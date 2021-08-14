package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class GenericGolem extends GolemBase {

  public GenericGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }
}
