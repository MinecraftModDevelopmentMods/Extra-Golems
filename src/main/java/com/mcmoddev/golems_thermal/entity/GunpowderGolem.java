package com.mcmoddev.golems_thermal.entity;

import com.mcmoddev.golems.entity.TNTGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class GunpowderGolem extends TNTGolem {
  
  public GunpowderGolem(EntityType<? extends GolemBase> type, World world) {
    super(type, world, 6, 10, 40, 5);
    this.allowedToExplode = this.getConfigBool(ALLOW_SPECIAL);
  }
}
