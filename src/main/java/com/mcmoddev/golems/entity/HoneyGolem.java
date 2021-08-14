package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public final class HoneyGolem extends GolemBase {

  public static final String ALLOW_HONEY = "Allow Special: Honey";
  public static final String SPLITTING_CHILDREN = "Splitting Factor";
  
  private boolean allowHoney;

  public HoneyGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    allowHoney = this.getConfigBool(ALLOW_HONEY);
  }

  @Override
  public boolean doHurtTarget(final Entity entityIn) {
    if (super.doHurtTarget(entityIn)) {
      if(entityIn instanceof LivingEntity) {
        applyHoney((LivingEntity)entityIn);
      }
      return true;
    }
    return false;
  }
  
  @Override
  protected void actuallyHurt(final DamageSource source, final float amount) {
    if (!this.isInvulnerableTo(source)) {
      super.actuallyHurt(source, amount);
      // slows the entity that attacked it
      if(source.getDirectEntity() instanceof LivingEntity) {
        applyHoney((LivingEntity)source.getDirectEntity());
      }
    }
  }

  @Override
  public void die(final DamageSource source) {
    int children = this.getConfigInt(SPLITTING_CHILDREN);
    if (children > 0) {
      trySpawnChildren(children);
    }
    super.die(source);
  }
  
  @Override
  public void setBaby(final boolean isChild) {
    super.setBaby(isChild);
    if(isChild) {
      allowHoney = false;
    }
  }

  /**
   * Applies a "honey" effect to the entity (for now just slowness)
   * @param entity the target entity
   * @return the result of {@link LivingEntity#addPotionEffect(EffectInstance)},
   * or false if the honey effect is not enabled for the golem
   **/
  private boolean applyHoney(final LivingEntity entity) {
    if (!this.isBaby() && allowHoney) {
      final int len = 20 * (3 + random.nextInt(3));
      final int amp = 3 + random.nextInt(2);
      return entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, len, amp));
    }
    return false;
  }
  
}
