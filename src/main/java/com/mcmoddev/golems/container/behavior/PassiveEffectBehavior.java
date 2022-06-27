package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.PassiveEffectsGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;

/**
 * This behavior allows an entity to passively apply a mob effect
 **/
@Immutable
public class PassiveEffectBehavior extends GolemBehavior {

	/**
	 * True to only use the effect during night
	 **/
	protected final boolean nightOnly;
	/**
	 * An optional containing the mob effect parameter, if present
	 **/
	protected final Optional<MobEffectBehaviorParameter> effect;

	public PassiveEffectBehavior(CompoundTag tag) {
		super(tag);
		nightOnly = tag.getBoolean("night_only");
		effect = tag.contains("effect") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effect"))) : Optional.empty();
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		effect.ifPresent(e -> entity.goalSelector.addGoal(4, new PassiveEffectsGoal(entity, e.getEffects(), nightOnly, e.getTarget() == Target.SELF, (float) e.getChance())));
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		effect.ifPresent(p -> {
			if (p.getTarget() == Target.SELF && !list.contains(EFFECTS_SELF_DESC)) {
				list.add(EFFECTS_SELF_DESC);
			} else if (p.getTarget() == Target.ENEMY && !list.contains(EFFECTS_ENEMY_DESC)) {
				list.add(EFFECTS_ENEMY_DESC);
			}
		});
	}
}
