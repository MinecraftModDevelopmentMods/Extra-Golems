package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public final class NetheriteGolem extends GolemBase {
  
  public static final String ALLOW_RESIST = "Allow Special: Resistance";
  
  private boolean allowResist;
  
  public NetheriteGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    allowResist = getConfigBool(ALLOW_RESIST);
  }
  
  @Override
  protected void damageEntity(DamageSource source, float amount) {
    // reduce all incoming damage
    float amt = amount;
    if(allowResist && !source.isDamageAbsolute()) {
      amt *= 0.64F;
    }
    super.damageEntity(source, amt);
  }
}
