package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class NetherBrickGolem extends GolemBase {

  public static final String ALLOW_FIRE_SPECIAL = "Allow Special: Burn Enemies";

  public NetherBrickGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  /**
   * Attack by lighting on fire as well.
   */
  @Override
  public boolean doHurtTarget(final Entity entity) {
    if (super.doHurtTarget(entity)) {
      if (this.getConfigBool(ALLOW_FIRE_SPECIAL)) {
        entity.setSecondsOnFire(2 + random.nextInt(5));
      }
      return true;
    }
    return false;
  }
}
