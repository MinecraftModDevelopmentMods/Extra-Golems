package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.FollowGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SplitBehavior extends Behavior<GolemBase> {

	public static final Codec<SplitBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(0, 255).fieldOf("children").forGetter(SplitBehavior::getChildren))
			.apply(instance, SplitBehavior::new));

	/** The number of children to spawn **/
	private final int children;

	public SplitBehavior(MinMaxBounds.Ints variant, int children) {
		super(variant);
		this.children = children;
	}

	//// GETTERS ////

	public int getChildren() {
		return children;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.FOLLOW.get();
	}

	//// METHODS ////


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
		if (entity.level() instanceof ServerLevelAccessor serverLevel && !entity.isBaby() && count > 0) {
			final DifficultyInstance diff = entity.level().getCurrentDifficultyAt(entity.blockPosition());
			final Component name = entity.getCustomName();
			for (int i = 0; i < count; i++) {
				GolemBase child = GolemBase.create(entity.level(), entity.getMaterial());
				child.setBaby(true);
				if (entity.getTarget() != null) {
					child.setTarget(entity.getTarget());
				}
				// copy location and attributes
				child.copyPosition(entity);
				child.setCustomName(name);
				child.setNoAi(entity.isNoAi());
				entity.setInvulnerable(entity.isInvulnerable());
				// spawn the entity
				serverLevel.addFreshEntityWithPassengers(child);
				child.finalizeSpawn(serverLevel, diff, MobSpawnType.MOB_SUMMONED, null, null);
				// add to the list
				children.add(child);
			}
		}
		return children;
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.split_on_death").withStyle(ChatFormatting.GREEN));
	}
}
