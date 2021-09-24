package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.event.GolemTeleportEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;

public interface IRandomTeleporter {
  
  default boolean teleportRandomly(MobEntity mob, final double range) {
    final Vector3d pos = mob.getPositionVec();
    final double d0 = pos.x + (mob.world.getRandom().nextDouble() - 0.5D) * range;
    final double d1 = pos.y + (mob.world.getRandom().nextDouble() - 0.5D) * range * 0.5D;
    final double d2 = pos.z + (mob.world.getRandom().nextDouble() - 0.5D) * range;
    return attemptTeleportTo(mob, d0, d1, d2);
  }

  /**
   * Teleport the entity to another entity.
   **/
  default boolean teleportToEntity(final MobEntity mob, final Entity entity, final double range) {
    Vector3d vec3d = new Vector3d(mob.getPosX() - entity.getPosX(), mob.getPosYHeight(0.5) - entity.getEyeHeight(), mob.getPosZ() - entity.getPosZ());
    vec3d = vec3d.normalize();
    double d = range * 0.25D;
    double d0 = range * 0.5D;
    double d1 = mob.getPosX() + (mob.world.getRandom().nextDouble() - 0.5D) * d - vec3d.x * d0;
    double d2 = mob.getPosY() + (mob.world.getRandom().nextDouble() - 0.5D) * d - vec3d.y * d0;
    double d3 = mob.getPosZ() + (mob.world.getRandom().nextDouble() - 0.5D) * d - vec3d.z * d0;
    return attemptTeleportTo(mob, d1, d2, d3);
  }

  /**
   * Teleport the entity.
   **/
  default boolean attemptTeleportTo(final MobEntity mob, final double x, final double y, final double z) {
    final GolemTeleportEvent event = new GolemTeleportEvent(mob, x, y, z, 0);
    if (MinecraftForge.EVENT_BUS.post(event)) {
      return false;
    }
    final boolean flag = mob.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

    if (flag) {
      mob.world.playSound((PlayerEntity) null, mob.getPosX(), mob.getPosY(), mob.getPosZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, mob.getSoundCategory(), 1.0F, 1.0F);
      mob.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    return flag;
  }
}
