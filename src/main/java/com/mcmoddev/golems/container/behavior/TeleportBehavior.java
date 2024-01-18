package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to teleport randomly,
 * when hurt, or when attacking.
 **/
@Immutable
public class TeleportBehavior extends GolemBehavior {

	/**
	 * The maximum distance the entity can teleport
	 **/
	protected final double range;
	/**
	 * The percent chance [0,1] to apply when the entity is doing nothing
	 **/
	protected final double chanceOnIdle;
	/**
	 * The percent chance [0,1] to apply when the entity is hurt
	 **/
	protected final double chanceOnHurt;
	/**
	 * The percent chance [0,1] to apply each tick that the entity has an attack target
	 **/
	protected final double chanceOnTarget;

	public TeleportBehavior(CompoundTag tag) {
		super(tag);
		range = tag.getDouble("range");
		chanceOnIdle = tag.getDouble("chance_on_idle");
		chanceOnHurt = tag.getDouble("chance_on_hurt");
		chanceOnTarget = tag.getDouble("chance_on_target");
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		entity.goalSelector.addGoal(1, new RandomTeleportGoal<>(entity, range, chanceOnIdle, chanceOnTarget));
	}

	@Override
	public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
		if (source.isIndirect()) {
			// if damage was projectile, remember the indirect entity and set as target
			if (source.getEntity() instanceof LivingEntity) {
				LivingEntity target = (LivingEntity) source.getEntity();
				entity.setLastHurtByMob(target);
				entity.setTarget(target);
			}
			// attempt random teleport
			for (int i = 0; i < 16; ++i) {
				if (entity.teleportRandomly(entity, range)) {
					return;
				}
			}
		} else {
			// if damage was something else, entity MIGHT teleport away if it passes a random chance OR has no attack target
			if (entity.getRandom().nextDouble() < chanceOnHurt || (entity.getTarget() == null && entity.getRandom().nextBoolean())
					|| (entity.getContainer().getAttributes().isHurtByWater() && source.is(DamageTypes.DROWN))) {
				// attempt random teleport
				for (int i = 0; i < 16; ++i) {
					if (entity.teleportRandomly(entity, range)) {
						return;
					}
				}
			}
		}
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(Component.translatable("entitytip.teleport").withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}
