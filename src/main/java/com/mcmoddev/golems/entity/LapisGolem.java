package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public final class LapisGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

  private static final MobEffect[] badEffects = { MobEffects.BLINDNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.POISON, MobEffects.HARM, MobEffects.WEAKNESS,
      MobEffects.WITHER, MobEffects.LEVITATION, MobEffects.GLOWING };

  public LapisGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  /**
   * Attack by adding potion effect as well.
   */
  @Override
  public boolean doHurtTarget(final Entity entityIn) {
    if (super.doHurtTarget(entityIn) && entityIn instanceof LivingEntity) {
      final LivingEntity entity = (LivingEntity) entityIn;
      if (this.getConfigBool(ALLOW_SPECIAL)) {
        final MobEffect potionID = entity.isInvertedHealAndHarm() ? MobEffects.HEAL : badEffects[random.nextInt(badEffects.length)];
        final int len = potionID.isInstantenous() ? 1 : 20 * (5 + random.nextInt(9));
        final int amp = potionID.isInstantenous() ? random.nextInt(2) : random.nextInt(3);
        entity.addEffect(new MobEffectInstance(potionID, len, amp));
      }
      return true;
    }
    return false;
  }
}
