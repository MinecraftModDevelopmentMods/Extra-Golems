package com.mcmoddev.golems.entity.modded;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ElectrumGolem extends GolemBase {
  
  public static final String IMMUNE_TO_MAGIC = "Immune to Magic";
  
  private boolean immuneToMagic;

  public ElectrumGolem(EntityType<? extends GolemBase> type, World world) {
    super(type, world);
    immuneToMagic = getConfigBool(IMMUNE_TO_MAGIC);
  }
  
  @Override
  public boolean isInvulnerableTo(DamageSource source) {
    return (immuneToMagic && source.isMagicDamage()) || super.isInvulnerableTo(source);
  }

}
