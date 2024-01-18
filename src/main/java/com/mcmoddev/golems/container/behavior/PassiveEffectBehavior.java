package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.annotation.concurrent.Immutable;
import java.util.List;

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
	protected final MobEffectBehaviorParameter effect;

	public PassiveEffectBehavior(CompoundTag tag) {
		super(tag);
		nightOnly = tag.getBoolean("night_only");
		effect = tag.contains("effect") ? new MobEffectBehaviorParameter(tag.getCompound("effect")) : null;
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		if (effect != null) {
			entity.goalSelector.addGoal(4, new PassiveEffectsGoal(entity, effect.getEffects(), nightOnly, effect.getTarget(), (float) effect.getChance(), effect.getRange()));
		}
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		if (effect != null) {
			if (effect.getTarget() == Target.SELF && !list.contains(EFFECTS_SELF_DESC)) {
				list.add(EFFECTS_SELF_DESC);
			} else if (effect.getTarget() != Target.SELF && !list.contains(EFFECTS_ENEMY_DESC)) {
				list.add(EFFECTS_ENEMY_DESC);
			}
		}
	}
}
