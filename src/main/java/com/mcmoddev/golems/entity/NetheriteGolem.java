package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public final class NetheriteGolem extends GolemBase {
  
  private static final float DAMAGE_RESIST = 0.6F;

  public NetheriteGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
  }
  
  @Override
  protected void damageEntity(DamageSource source, float amount) {
    // reduce all incoming damage
    super.damageEntity(source, amount * DAMAGE_RESIST);
  }
}
