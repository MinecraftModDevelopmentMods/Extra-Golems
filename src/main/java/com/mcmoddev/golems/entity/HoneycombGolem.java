package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public final class HoneycombGolem extends GolemBase {
  
  public static final String SUMMON_BEE_CHANCE = "Summon Bee Chance";
  
  private final int summonBeeChance;

  public HoneycombGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    summonBeeChance = this.getConfigInt(SUMMON_BEE_CHANCE);
  }
 
  @Override
  public boolean attackEntityFrom(final DamageSource source, final float amount) {
    boolean flag = super.attackEntityFrom(source, amount);
    if (flag && source.getImmediateSource() instanceof LivingEntity) {
      // chance to summon a bee when attacked
      if (!this.world.isRemote && this.rand.nextInt(100) < summonBeeChance) {
        summonBees(1);
      }
      // anger other nearby bees
      angerBees((LivingEntity)source.getImmediateSource(), 16.0D);
    }

    return flag;
  }
  
  @Override
  public void onDeath(final DamageSource source) {
    // summon bees upon death
    summonBees(2 + rand.nextInt(4));
    // anger nearby bees
    final LivingEntity target = source.getTrueSource() instanceof LivingEntity 
        ? (LivingEntity)source.getTrueSource() 
        : this.getRevengeTarget();
    angerBees(target, 16.0D);
    super.onDeath(source);
  }
  
  // summon a bee and makes it angry at the given entity
  private void summonBees(final int number) {
    for(int i = 0; i < number; i++) {
      BeeEntity bee = EntityType.BEE.create(this.world);
      bee.copyLocationAndAnglesFrom(this);
      // sometimes summon a baby bee instead
      if(this.rand.nextInt(3) == 0) {
        bee.setGrowingAge(-24000);
      }
      this.world.addEntity(bee);
    }
  }
  
  // find nearby bees and make them angry at the given entity
  private boolean angerBees(final LivingEntity target, final double range) {
    if(target != null) {
      List<BeeEntity> beeList = this.world.getEntitiesWithinAABB(BeeEntity.class, this.getBoundingBox().grow(range));
      for(final BeeEntity bee : beeList) {
        bee.setRevengeTarget(target);
        bee.setAttackTarget(target);
      }
      return !beeList.isEmpty();
    }
    return false;
  }  
}
