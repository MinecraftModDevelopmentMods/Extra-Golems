package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to teleport randomly,
 * when hurt, or when attacking.
 **/
@Immutable
public class TeleportBehavior extends Behavior<GolemBase> {

	public static final Codec<TeleportBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.doubleRange(0.0D, 128.0D).optionalFieldOf("radius", 0.0D).forGetter(TeleportBehavior::getRadius))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("idle_chance", 0.0D).forGetter(TeleportBehavior::getChanceOnIdle))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("hurt_chance", 0.0D).forGetter(TeleportBehavior::getChanceOnHurt))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("target_chance", 0.0D).forGetter(TeleportBehavior::getChanceOnTarget))
			.apply(instance, TeleportBehavior::new));

	/** The maximum distance the entity can teleport **/
	private final double radius;
	/** The percent chance [0,1] to apply when the entity is doing nothing **/
	private final double chanceOnIdle;
	/** The percent chance [0,1] to apply when the entity is hurt **/
	private final double chanceOnHurt;
	/** The percent chance [0,1] to apply each tick that the entity has an attack target **/
	private final double chanceOnTarget;

	public TeleportBehavior(MinMaxBounds.Ints variant, double radius, double chanceOnIdle, double chanceOnHurt, double chanceOnTarget) {
		super(variant);
		this.radius = radius;
		this.chanceOnIdle = chanceOnIdle;
		this.chanceOnHurt = chanceOnHurt;
		this.chanceOnTarget = chanceOnTarget;
	}

	//// GETTERS ////

	public double getRadius() {
		return radius;
	}

	public double getChanceOnIdle() {
		return chanceOnIdle;
	}

	public double getChanceOnHurt() {
		return chanceOnHurt;
	}

	public double getChanceOnTarget() {
		return chanceOnTarget;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.TELEPORT.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// TODO adjust goal to account for entity variant
		entity.goalSelector.addGoal(1, new RandomTeleportGoal<>(entity, radius, chanceOnIdle, chanceOnTarget));
	}

	@Override
	public void onTick(GolemBase entity) {
		if(entity.isBaby()) {
			return;
		}
		// teleport to target
		final LivingEntity target = entity.getTarget();
		if(target != null && entity.getRandom().nextFloat() < chanceOnTarget) {
			entity.lookAt(target, 100.0F, 100.0F);
			if(target.position().closerThan(entity.position(), radius)) {
				// when within radius, teleport near the target
				entity.teleportNear(entity, target.blockPosition(), 20, 4, 1, 4);
			} else {
				// otherwise, teleport toward the target
				entity.teleportToEntity(entity, target, radius);
			}
			return;
		}
		// teleport randomly
		if(null == target && entity.getRandom().nextFloat() < chanceOnIdle) {
			entity.teleportRandomly(entity, radius);
		}
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
				if (entity.teleportRandomly(entity, radius)) {
					return;
				}
			}
		} else {
			// if damage was something else, entity MIGHT teleport away if it passes a random chance OR has no attack target
			if (entity.getRandom().nextDouble() < chanceOnHurt || (entity.getTarget() == null && entity.getRandom().nextBoolean())
					|| (entity.getContainer().getAttributes().isHurtByWater() && source.is(DamageTypes.DROWN))) {
				// attempt random teleport
				for (int i = 0; i < 16; ++i) {
					if (entity.teleportRandomly(entity, radius)) {
						return;
					}
				}
			}
		}
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.teleport").withStyle(ChatFormatting.LIGHT_PURPLE));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TeleportBehavior)) return false;
		if (!super.equals(o)) return false;
		TeleportBehavior that = (TeleportBehavior) o;
		return Double.compare(that.radius, radius) == 0 && Double.compare(that.chanceOnIdle, chanceOnIdle) == 0 && Double.compare(that.chanceOnHurt, chanceOnHurt) == 0 && Double.compare(that.chanceOnTarget, chanceOnTarget) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), radius, chanceOnIdle, chanceOnHurt, chanceOnTarget);
	}
}
