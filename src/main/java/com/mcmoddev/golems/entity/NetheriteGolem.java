package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public final class NetheriteGolem extends GolemBase {
  
  public static final String ALLOW_RESIST = "Allow Special: Resistance";
  
  private boolean allowResist;
  
  public NetheriteGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    allowResist = getConfigBool(ALLOW_RESIST);
  }
  
  @Override
  protected void actuallyHurt(DamageSource source, float amount) {
    // reduce all incoming damage
    float amt = amount;
    if(allowResist && !source.isBypassMagic()) {
      amt *= 0.64F;
    }
    super.actuallyHurt(source, amt);
  }
}
