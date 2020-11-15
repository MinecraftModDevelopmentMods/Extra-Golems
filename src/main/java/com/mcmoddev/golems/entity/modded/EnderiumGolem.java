package com.mcmoddev.golems.entity.modded;

import com.mcmoddev.golems.entity.EndstoneGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public final class EnderiumGolem extends EndstoneGolem {

  public EnderiumGolem(EntityType<? extends GolemBase> entityType, World world) {
    super(entityType, world, 48.0D, true);
    this.allowTeleport = this.getConfigBool(ALLOW_SPECIAL);
  }  
}
