package com.mcmoddev.golems_thermal.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class HardenedGlassGolem extends GolemBase {
  
  public static final String ALLOW_RESIST = "Allow Special: Resistance";
  
  private boolean resist;

  public HardenedGlassGolem(EntityType<? extends GolemBase> type, World world) {
    super(type, world);
    resist = getConfigBool(ALLOW_RESIST);
  }
  
  @Override
  protected void damageEntity(DamageSource source, float amount) {
    if (resist && !source.isDamageAbsolute()) {
      amount *= 0.6F;
      if (source.isFireDamage()) {
        // additional fire resistance
        amount *= 0.85F;
      }
    }
    super.damageEntity(source, amount);
  }
}
