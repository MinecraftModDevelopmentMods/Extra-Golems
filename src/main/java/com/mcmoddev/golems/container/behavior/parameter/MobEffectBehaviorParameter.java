package com.mcmoddev.golems.container.behavior.parameter;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.concurrent.Immutable;

@Immutable
public class MobEffectBehaviorParameter extends BehaviorParameter {

	private final Target target;
	private final double chance;
	private final ListTag effectList;

	public MobEffectBehaviorParameter(final CompoundTag tag) {
		super();
		this.target = Target.getByName(tag.getString("target"));
		this.chance = tag.getDouble("chance");
		this.effectList = tag.getList("effects", Tag.TAG_COMPOUND);
	}

	public Target getTarget() {
		return target;
	}

	public double getChance() {
		return chance;
	}

	public MobEffectInstance[] getEffects() {
		return readEffectArray(getEffectTag());
	}

	public ListTag getEffectTag() {
		return effectList.copy();
	}

	public void apply(GolemBase self, LivingEntity other) {
		if (effectList.size() > 0 && self.getRandom().nextFloat() < chance) {
			LivingEntity effectTarget = (target == Target.SELF) ? self : other;
			if (effectTarget != null) {
				// choose random effect from list
				CompoundTag effectTag = effectList.getCompound(self.getRandom().nextInt(effectList.size())).copy();
				MobEffectInstance effect = readEffect(effectTag);
				// apply the mob effect instance
				if(effect != null) {
					effectTarget.addEffect(effect);
				}
			}
		}
	}
}
