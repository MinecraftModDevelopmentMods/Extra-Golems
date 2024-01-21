package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to teleport randomly,
 * when hurt, or when attacking.
 **/
@Immutable
public class TeleportBehavior extends Behavior {

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
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.TELEPORT.get();
	}

	//// METHODS ////

	@Override
	public void onTick(IExtraGolem entity) {
		final Mob mob = entity.asMob();
		if(mob.isBaby()) {
			return;
		}
		// teleport to target
		final LivingEntity target = mob.getTarget();
		if(target != null && mob.getRandom().nextFloat() < chanceOnTarget) {
			mob.lookAt(target, 100.0F, 100.0F);
			if(target.position().closerThan(mob.position(), radius)) {
				// when within radius, teleport near the target
				teleportNear(mob, target.blockPosition(), 20, 4, 1, 4);
			} else {
				// otherwise, teleport toward the target
				teleportToEntity(mob, target, radius);
			}
			return;
		}
		// teleport randomly
		if(null == target && mob.getRandom().nextFloat() < chanceOnIdle) {
			teleportRandomly(mob, radius);
		}
	}

	@Override
	public void onActuallyHurt(final IExtraGolem entity, final DamageSource source, final float amount) {
		final Mob mob = entity.asMob();
		if (source.isIndirect()) {
			// if damage was projectile, remember the indirect entity and set as target
			if (source.getEntity() instanceof LivingEntity) {
				LivingEntity target = (LivingEntity) source.getEntity();
				mob.setLastHurtByMob(target);
				mob.setTarget(target);
			}
			// attempt random teleport
			for (int i = 0; i < 16; ++i) {
				if (teleportRandomly(mob, radius)) {
					return;
				}
			}
		} else {
			// if damage was something else, entity MIGHT teleport away if it passes a random chance OR has no attack target
			if (mob.getRandom().nextDouble() < chanceOnHurt || (mob.getTarget() == null && mob.getRandom().nextBoolean())) {
				// attempt random teleport
				for (int i = 0; i < 16; ++i) {
					if (teleportRandomly(mob, radius)) {
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

	//// HELPER METHODS ////

	private boolean teleportRandomly(Mob mob, final double range) {
		final Vec3 pos = mob.position();
		final double d0 = pos.x + (mob.getRandom().nextDouble() - 0.5D) * range;
		final double d1 = pos.y + (mob.getRandom().nextDouble() - 0.5D) * range * 0.5D;
		final double d2 = pos.z + (mob.getRandom().nextDouble() - 0.5D) * range;
		return attemptTeleportTo(mob, d0, d1, d2);
	}

	/**
	 * Teleport the entity to another entity.
	 **/
	private boolean teleportToEntity(final Mob mob, final Entity entity, final double range) {
		Vec3 vec3d = new Vec3(mob.getX() - entity.getX(), mob.getY(0.5D) - entity.getEyeY(), mob.getZ() - entity.getZ());
		vec3d = vec3d.normalize();
		double d = range * 0.25D;
		double d0 = range * 0.5D;
		double d1 = mob.getX() + (mob.getRandom().nextDouble() - 0.5D) * d - vec3d.x * d0;
		double d2 = mob.getY() + (mob.getRandom().nextDouble() - 0.5D) * d - vec3d.y * d0;
		double d3 = mob.getZ() + (mob.getRandom().nextDouble() - 0.5D) * d - vec3d.z * d0;
		return attemptTeleportTo(mob, d1, d2, d3);
	}

	/**
	 * Teleport the entity to a random position near the given position
	 **/
	private boolean teleportNear(final Mob mob, final BlockPos pos, final int attempts, final int dx, final int dy, final int dz) {
		BlockPos.MutableBlockPos target = pos.mutable();
		for(int i = attempts; i > 0; i--) {
			target.setWithOffset(pos, mob.getRandom().nextInt(dx * 2) - dx,
					mob.getRandom().nextInt(dy * 2) - dy,
					mob.getRandom().nextInt(dz * 2) - dz);
			// attempt to teleport
			if(attemptTeleportTo(mob, pos.getX() + 0.5D, pos.getY() + 0.01D, pos.getZ())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Teleport the position
	 **/
	private boolean attemptTeleportTo(final Mob mob, final double x, final double y, final double z) {
		// create vent
		final EntityTeleportEvent event = new EntityTeleportEvent.EnderEntity(mob, x, y, z);
		// fire event
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		Vec3 target = event.getTarget();
		// random teleport (checks for collisions)
		final boolean flag = mob.randomTeleport(target.x(), target.y(), target.z(), true);
		if (flag) {
			mob.level().playSound(null, mob.xo, mob.yo, mob.zo, SoundEvents.ENDERMAN_TELEPORT, mob.getSoundSource(), 1.0F, 1.0F);
			mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
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
