package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public final class LapisGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

  private static final Effect[] badEffects = { Effects.BLINDNESS, Effects.SLOWNESS, Effects.POISON, Effects.INSTANT_DAMAGE, Effects.WEAKNESS,
      Effects.WITHER, Effects.LEVITATION, Effects.GLOWING };

  public LapisGolem(final EntityType<? extends GolemBase> entityType, final World world) {
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
        final Effect potionID = entity.isEntityUndead() ? Effects.INSTANT_HEALTH : badEffects[rand.nextInt(badEffects.length)];
        final int len = potionID.isInstant() ? 1 : 20 * (5 + rand.nextInt(9));
        final int amp = potionID.isInstant() ? rand.nextInt(2) : rand.nextInt(3);
        entity.addPotionEffect(new EffectInstance(potionID, len, amp));
      }
      return true;
    }
    return false;
  }
}
