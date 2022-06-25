package com.mcmoddev.golems.container.behavior.parameter;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public class SummonEntityBehaviorParameter extends BehaviorParameter {

	private final Target target;
	private final Target targetPos;
	private final ResourceLocation entityId;
	private final CompoundTag compoundTag;
	private final double chance;
	private final double bonusChanceInRain;
	private final Component component;

	public SummonEntityBehaviorParameter(final CompoundTag tag) {
		super();
		this.target = Target.getByName(tag.getString("target"));
		this.targetPos = tag.contains("summon_pos") ? Target.getByName(tag.getString("summon_pos")) : this.target;
		this.compoundTag = tag.getCompound("entity");
		this.entityId = new ResourceLocation(this.compoundTag.getString("id"));
		this.chance = tag.getDouble("chance");
		this.bonusChanceInRain = tag.getDouble("bonus_chance_in_rain");
		this.component = new TranslatableComponent("entitytip.summon_x",
				new TranslatableComponent("entity." + entityId.getNamespace() + "." + entityId.getPath()));
	}

	public Target getTarget() {
		return target;
	}

	public Target getSummonPos() {
		return targetPos;
	}

	public ResourceLocation getEntityId() {
		return entityId;
	}

	public double getChance() {
		return chance;
	}

	public double getBonusChanceInRain() {
		return bonusChanceInRain;
	}

	public CompoundTag getCompoundTag() {
		return compoundTag;
	}

	public Component getDescription() {
		return component;
	}

	public void apply(final GolemBase self, @Nullable final Entity angerTarget) {
		// determine the random chance of trigger, taking rain into account
		double chanceApply = chance;
		if (self.level.isRainingAt(self.blockPosition())) {
			chanceApply += bonusChanceInRain;
		}
		if (!self.level.isClientSide() && self.getRandom().nextFloat() < chanceApply) {
			// create an entity to add to the level
			Optional<Entity> entity = EntityType.create(compoundTag, self.level);
			if (entity.isPresent() && self.level instanceof ServerLevel) {
				// read entity from compound tag
				entity.get().load(compoundTag);
				// determine spawn position for the entity
				Vec3 pos = (targetPos == Target.SELF || angerTarget == null ? self.position() : angerTarget.position());
				entity.get().moveTo(pos.x, pos.y, pos.z);
				// add the entity to the level
				self.level.addFreshEntity(entity.get());
				// process mob entity
				if (entity.get() instanceof Mob) {
					((Mob) entity.get()).finalizeSpawn((ServerLevel) self.level, self.level.getCurrentDifficultyAt(new BlockPos(pos)), MobSpawnType.MOB_SUMMONED, null, null);
					if (target == Target.ENEMY && angerTarget != null) {
						((Mob) entity.get()).setTarget(self.getTarget());
					}
				}
				// process NeutralMob
				if (entity.get() instanceof NeutralMob && target == Target.ENEMY && angerTarget != null) {
					((NeutralMob) entity.get()).setPersistentAngerTarget(angerTarget.getUUID());
					((NeutralMob) entity.get()).startPersistentAngerTimer();
				}
			} else {
				ExtraGolems.LOGGER.warn("Failed to create entity of type " + entityId);
			}
		}
	}
}
