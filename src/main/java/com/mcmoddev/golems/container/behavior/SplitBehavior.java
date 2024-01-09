package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This behavior allows an entity to create a number of
 * baby entities upon death.
 **/
@Immutable
public class SplitBehavior extends GolemBehavior {

	protected final int children;

	public SplitBehavior(CompoundTag tag) {
		super(tag);
		children = tag.getInt("children");
	}

	public int getChildren() {
		return children;
	}

	@Override
	public void onDie(final GolemBase entity, final DamageSource source) {
		attemptAddChildren(entity, children);
	}

	/**
	 * Attempts to spawn the given number of "mini" golems
	 *
	 * @param entity the parent Golem
	 * @param count  the number of children to spawn
	 * @return a collection containing the entities that were spawned
	 **/
	protected Collection<GolemBase> attemptAddChildren(final GolemBase entity, final int count) {
		final List<GolemBase> children = new ArrayList<>();
		if (entity.level() instanceof ServerLevelAccessor && !entity.isBaby() && count > 0) {
			final DifficultyInstance diff = entity.level().getCurrentDifficultyAt(entity.blockPosition());
			for (int i = 0; i < count; i++) {
				GolemBase child = GolemBase.create(entity.level(), entity.getMaterial());
				child.setBaby(true);
				if (entity.getTarget() != null) {
					child.setTarget(entity.getTarget());
				}
				// set location
				child.copyPosition(entity);
				// spawn the entity
				entity.level().addFreshEntity(child);
				child.finalizeSpawn((ServerLevelAccessor) entity.level(), diff, MobSpawnType.MOB_SUMMONED, null, null);
				// add to the list
				children.add(child);
			}
		}
		return children;
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(Component.translatable("entitytip.split_on_death").withStyle(ChatFormatting.GREEN));
	}
}
