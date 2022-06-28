package com.mcmoddev.golems.container.behavior.parameter;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public class MobEffectBehaviorParameter extends BehaviorParameter {

	private final Target target;
	private final double chance;
	private final double range;
	private final ListTag effectList;

	public MobEffectBehaviorParameter(final CompoundTag tag) {
		super();
		this.target = Target.getByName(tag.getString("target"));
		this.chance = tag.getDouble("chance");
		this.range = tag.getDouble("range");
		this.effectList = tag.getList("effects", Tag.TAG_COMPOUND);
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

	public MobEffectInstance[] getEffects() {
		return readEffectArray(getEffectTag());
	}

	public ListTag getEffectTag() {
		return effectList.copy();
	}

	/**
	 * Applies the behavior based on its parameters
	 * @param self the entity
	 * @param other the target entity, if any
	 */
	public void apply(GolemBase self, LivingEntity other) {
		if (effectList.size() > 0 && self.getRandom().nextFloat() < chance) {
			// choose random effect from list
			CompoundTag effectTag = effectList.getCompound(self.getRandom().nextInt(effectList.size())).copy();
			MobEffectInstance effect = readEffect(effectTag);
			// apply effect to target(s)
			switch (target) {
				case AREA:
					double inflate = range > 0 ? range : 2.0D;
					TargetingConditions condition = TargetingConditions.forNonCombat()
							.ignoreLineOfSight().ignoreInvisibilityTesting();
					List<LivingEntity> targets = self.level.getNearbyEntities(LivingEntity.class,
							condition, self, self.getBoundingBox().inflate(inflate));
					// apply to each entity in list
					for(LivingEntity target : targets) {
						applyEffect(target, effect);
					}
					break;
				case SELF:
					applyEffect(self, effect);
					break;
				case ENEMY:
					applyEffect(other, effect);
					break;
			}
		}
	}

	/**
	 * Applies a MobEffectInstance to the given entity
	 * @param target the entity
	 * @param effect the effect instance
	 */
	private void applyEffect(final LivingEntity target,final MobEffectInstance effect) {
		if(effect != null && target != null) {
			target.addEffect(effect);
		}
	}
}
