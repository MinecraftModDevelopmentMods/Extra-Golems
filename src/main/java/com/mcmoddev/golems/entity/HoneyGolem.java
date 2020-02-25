package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public final class HoneyGolem extends GolemBase {

  public static final String ALLOW_SPLITTING = "Allow Special: Split";

  public HoneyGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
  }
  
  /**
   * Attack by adding potion effect as well.
   */
  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn) && entityIn instanceof LivingEntity) {
      final LivingEntity entity = (LivingEntity) entityIn;
     // TODO if (!this.isChild() && this.getConfigBool(ALLOW_SPECIAL)) {
        final int len = 20 * (2 + rand.nextInt(5));
        final int amp = 2 + rand.nextInt(2);
        entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, len, amp));
     // }
      return true;
    }
    return false;
  }

  @Override
  public void onDeath(final DamageSource source) {
    if (this.getConfigBool(ALLOW_SPLITTING)) {
      trySpawnChildren(2);
    }
    super.onDeath(source);
  }

  
}
