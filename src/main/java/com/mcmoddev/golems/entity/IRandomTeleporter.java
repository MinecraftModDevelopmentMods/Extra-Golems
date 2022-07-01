package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.event.GolemTeleportEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public interface IRandomTeleporter {

	default boolean teleportRandomly(Mob mob, final double range) {
		final Vec3 pos = mob.position();
		final double d0 = pos.x + (mob.getRandom().nextDouble() - 0.5D) * range;
		final double d1 = pos.y + (mob.getRandom().nextDouble() - 0.5D) * range * 0.5D;
		final double d2 = pos.z + (mob.getRandom().nextDouble() - 0.5D) * range;
		return attemptTeleportTo(mob, d0, d1, d2);
	}

	/**
	 * Teleport the entity to another entity.
	 **/
	default boolean teleportToEntity(final Mob mob, final Entity entity, final double range) {
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
	 * Teleport the entity.
	 **/
	default boolean attemptTeleportTo(final Mob mob, final double x, final double y, final double z) {
		final GolemTeleportEvent event = new GolemTeleportEvent(mob, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		final boolean flag = mob.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

		if (flag) {
			mob.level.playSound(null, mob.xo, mob.yo, mob.zo, SoundEvents.ENDERMAN_TELEPORT, mob.getSoundSource(), 1.0F, 1.0F);
			mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}
}
