package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.container.behavior.parameter.FireBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.SummonEntityBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to react when actually hurt.
 * The entity may apply a fire parameter, mob effect parameter,
 * or summon entity parameter
 **/
@Immutable
public class OnActuallyHurtBehavior extends GolemBehavior {

	/**
	 * An optional containing the fire parameter, if present
	 **/
	protected final FireBehaviorParameter fire;
	/**
	 * An optional containing the mob effect parameter, if present
	 **/
	protected final MobEffectBehaviorParameter effect;
	/**
	 * An optional containing the summon entity parameter, if present
	 **/
	protected final SummonEntityBehaviorParameter summon;

	public OnActuallyHurtBehavior(CompoundTag tag) {
		super(tag);
		fire = tag.contains("fire") ? new FireBehaviorParameter(tag.getCompound("fire")) : null;
		effect = tag.contains("effect") ? new MobEffectBehaviorParameter(tag.getCompound("effect")) : null;
		summon = tag.contains("summon") ? new SummonEntityBehaviorParameter(tag.getCompound("summon")) : null;
	}

	@Override
	public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
		if (!entity.isBaby()) {
			// apply fire
			if (fire != null) {
				fire.apply(entity, source.getEntity());
			}
			// apply effect
			if (effect != null && source.getEntity() instanceof LivingEntity) {
				effect.apply(entity, (LivingEntity) source.getEntity());
			}
			// apply summon
			if (summon != null) {
				summon.apply(entity, source.getEntity());
			}
		}
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		// add fire description
		if (fire != null && fire.getTarget() != Target.SELF && !list.contains(FIRE_DESC)) {
			list.add(FIRE_DESC);
		}
		// add effect description
		if (effect != null) {
			if (effect.getTarget() == Target.SELF && !list.contains(EFFECTS_SELF_DESC)) {
				list.add(EFFECTS_SELF_DESC);
			} else if (effect.getTarget() != Target.SELF && !list.contains(EFFECTS_ENEMY_DESC)) {
				list.add(EFFECTS_ENEMY_DESC);
			}
		}
		// add summon description
		if (summon != null) {
			Component desc = summon.getDescription();
			if (!list.contains(desc)) {
				list.add(desc);
			}
		}
	}
}
