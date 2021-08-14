package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public final class CoalGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Blindness";

  public CoalGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  /**
   * Attack by adding potion effect as well.
   */
  @Override
  public boolean doHurtTarget(final Entity entity) {
    if (super.doHurtTarget(entity)) {
      final int BLIND_CHANCE = 2;
      if (entity instanceof LivingEntity && this.getConfigBool(ALLOW_SPECIAL) && this.random.nextInt(BLIND_CHANCE) == 0) {
        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * (3 + random.nextInt(5)), 0));
      }
      return true;
    }
    return false;
  }

  /**
   * Called frequently so the entity can update its state every tick as required.
   * For example, zombies and skeletons use this to react to sunlight and start to
   * burn.
   */
  @Override
  public void aiStep() {
    super.aiStep();
    // if burning, the fire never goes out on its own
    if (this.isOnFire() && !this.isInWaterOrRain()) {
      this.setSecondsOnFire(2);
    }
  }
}
