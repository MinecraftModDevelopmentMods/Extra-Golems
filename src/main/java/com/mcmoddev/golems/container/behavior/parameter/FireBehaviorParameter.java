package com.mcmoddev.golems.container.behavior.parameter;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public class FireBehaviorParameter extends BehaviorParameter {

	private final Target target;
	private final double chance;
	private final double range;
	private final int time;

	public FireBehaviorParameter(final CompoundTag tag) {
		super();
		this.target = Target.getByName(tag.getString("target"));
		this.chance = tag.getDouble("chance");
		this.time = tag.getInt("time");
		this.range = tag.getDouble("range");
	}

	public Target getTarget() {
		return target;
	}

	public double getChance() {
		return chance;
	}

	public double getRange() {
		return range;
	}

	public int getTime() {
		return time;
	}

	public void apply(final GolemBase self, final Entity other) {
		if (self.getRandom().nextFloat() < chance) {
			switch (target) {
				case AREA:
					double inflate = range > 0 ? range : 2.0D;
					TargetingConditions condition = TargetingConditions.forNonCombat()
							.ignoreLineOfSight().ignoreInvisibilityTesting();
					List<LivingEntity> targets = self.level().getNearbyEntities(LivingEntity.class,
							condition, self, self.getBoundingBox().inflate(inflate));
					// apply to each entity in list
					for (LivingEntity target : targets) {
						target.setSecondsOnFire(time);
					}
					break;
				case ENEMY:
					if (other != null) {
						other.setSecondsOnFire(time);
					}
					break;
				case SELF:
					self.setSecondsOnFire(time);
					break;
			}
		}
	}
}
