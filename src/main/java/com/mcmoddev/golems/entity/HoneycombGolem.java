package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public final class HoneycombGolem extends GolemBase {
  
  public static final String SUMMON_BEE_CHANCE = "Summon Bee Chance";
  
  private final int summonBeeChance;

  public HoneycombGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    summonBeeChance = this.getConfigInt(SUMMON_BEE_CHANCE);
  }
 
  @Override
  public boolean hurt(final DamageSource source, final float amount) {
    boolean flag = super.hurt(source, amount);
    if (flag && source.getDirectEntity() instanceof LivingEntity) {
      // chance to summon a bee when attacked
      if (!this.level.isClientSide && this.random.nextInt(100) < summonBeeChance) {
        summonBees(1, (LivingEntity)source.getDirectEntity());
      }
      // anger other nearby bees
      angerBees((LivingEntity)source.getDirectEntity(), 16.0D);
    }

    return flag;
  }
  
  @Override
  public void die(final DamageSource source) {
    // determine if the golem was killed by an entity
    final LivingEntity target = source.getEntity() instanceof LivingEntity 
        ? (LivingEntity)source.getEntity() 
        : this.getLastHurtByMob();
    // summon bees upon death
    summonBees(2 + random.nextInt(4), target);
    angerBees(target, 16.0D);
    super.die(source);
  }
  
  // summon a bee and makes it angry at the given entity
  private void summonBees(final int number, final LivingEntity target) {
    for(int i = 0; i < number; i++) {
      Bee bee = EntityType.BEE.create(this.level);
      bee.copyPosition(this);
      // sometimes summon a baby bee instead
      if(this.random.nextInt(3) == 0) {
        bee.setAge(-24000);
      }
      if(target != null) {
        bee.setLastHurtByMob(target);
        bee.setTarget(target);
      }
      this.level.addFreshEntity(bee);
    }
  }
  
  // find nearby bees and make them angry at the given entity
  private boolean angerBees(final LivingEntity target, final double range) {
    if(target != null) {
      List<Bee> beeList = this.level.getEntitiesOfClass(Bee.class, this.getBoundingBox().inflate(range));
      for(final Bee bee : beeList) {
        bee.setLastHurtByMob(target);
        bee.setTarget(target);
      }
      return !beeList.isEmpty();
    }
    return false;
  }  
}
