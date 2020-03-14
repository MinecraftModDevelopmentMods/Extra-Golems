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

  public static final String ALLOW_HONEY = "Allow Special: Honey";
  public static final String SPLITTING_CHILDREN = "Splitting Factor";
  
  private boolean allowHoney;

  public HoneyGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    allowHoney = this.getConfigBool(ALLOW_HONEY);
  }

  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      if(entityIn instanceof LivingEntity) {
        applyHoney((LivingEntity)entityIn);
      }
      return true;
    }
    return false;
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amount) {
    if (!this.isInvulnerableTo(source)) {
      super.damageEntity(source, amount);
      // slows the entity that attacked it
      if(source.getImmediateSource() instanceof LivingEntity) {
        applyHoney((LivingEntity)source.getImmediateSource());
      }
    }
  }

  @Override
  public void onDeath(final DamageSource source) {
    int children = this.getConfigInt(SPLITTING_CHILDREN);
    if (children > 0) {
      trySpawnChildren(children);
    }
    super.onDeath(source);
  }
  
  @Override
  public void setChild(final boolean isChild) {
    super.setChild(isChild);
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
    if (!this.isChild() && allowHoney) {
      final int len = 20 * (2 + rand.nextInt(2));
      final int amp = 1 + rand.nextInt(2);
      return entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, len, amp));
    }
    return false;
  }
  
}
