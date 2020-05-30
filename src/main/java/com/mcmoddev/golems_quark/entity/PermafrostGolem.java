package com.mcmoddev.golems_quark.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public final class PermafrostGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Slowness";

  public PermafrostGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
  }

  /**
   * Attack by adding potion effect as well.
   */
  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn) && entityIn instanceof LivingEntity) {
      final LivingEntity entity = (LivingEntity) entityIn;
      if (this.getConfigBool(ALLOW_SPECIAL)) {
        final int len = 20 * (5 + rand.nextInt(9));
        final int amp = 2;
        entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, len, amp));
      }
      return true;
    }
    return false;
  }
}
