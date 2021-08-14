package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class PurpurGolem extends EndstoneGolem {

  public PurpurGolem(EntityType<? extends GolemBase> entityType, Level world) {
    super(entityType, world, 16.0D, true);
    this.isHurtByWater = false;
    this.allowTeleport = this.getConfigBool(ALLOW_SPECIAL);
  }  
}
