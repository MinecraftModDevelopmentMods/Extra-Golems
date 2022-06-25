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
import java.util.Optional;

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
	protected final Optional<FireBehaviorParameter> fire;
	/**
	 * An optional containing the mob effect parameter, if present
	 **/
	protected final Optional<MobEffectBehaviorParameter> effect;
	/**
	 * An optional containing the summon entity parameter, if present
	 **/
	protected final Optional<SummonEntityBehaviorParameter> summon;

	public OnActuallyHurtBehavior(CompoundTag tag) {
		super(tag);
		fire = tag.contains("fire") ? Optional.of(new FireBehaviorParameter(tag.getCompound("fire"))) : Optional.empty();
		effect = tag.contains("effect") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effect"))) : Optional.empty();
		summon = tag.contains("summon") ? Optional.of(new SummonEntityBehaviorParameter(tag.getCompound("summon"))) : Optional.empty();
	}

	@Override
	public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
		if (!entity.isBaby()) {
			fire.ifPresent(p -> p.apply(entity, source.getEntity()));
			if (source.getEntity() instanceof LivingEntity) {
				effect.ifPresent(p -> p.apply(entity, (LivingEntity) source.getEntity()));
			}
			summon.ifPresent(p -> p.apply(entity, source.getEntity()));
		}
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		fire.ifPresent(p -> {
			if (p.getTarget() == Target.ENEMY && !list.contains(FIRE_DESC)) {
				list.add(FIRE_DESC);
			}
		});
		effect.ifPresent(p -> {
			if (p.getTarget() == Target.SELF && !list.contains(EFFECTS_SELF_DESC)) {
				list.add(EFFECTS_SELF_DESC);
			} else if (p.getTarget() == Target.ENEMY && !list.contains(EFFECTS_ENEMY_DESC)) {
				list.add(EFFECTS_ENEMY_DESC);
			}
		});
		summon.ifPresent(p -> {
			if (!list.contains(p.getDescription())) {
				list.add(p.getDescription());
			}
		});
	}
}
