package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
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
  public void livingTick() {
    super.livingTick();
    // TODO: getAttackTarget keeps returning null?
    if(this.rand.nextInt(40) == 0 && this.getAttackTarget() != null) {
      // find nearby bees and make them angry at this entity
      List<BeeEntity> beeList = this.world.getEntitiesWithinAABB(BeeEntity.class, this.getBoundingBox().grow(10.0D));
      for(final BeeEntity bee : beeList) {
        bee.setAttackTarget(this.getAttackTarget());
      }
    }
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amount) {
    if (!this.isInvulnerableTo(source)) {
      super.damageEntity(source, amount);
      // summons a bee and makes it angry at the target
      if (!this.world.isRemote && this.rand.nextInt(100) < summonBeeChance && source.getImmediateSource() != null) {
        BeeEntity bee = EntityType.BEE.create(this.world);
        bee.copyLocationAndAnglesFrom(this);
        if(this.getAttackTarget() != null) {
          // TODO: getAttackTarget keeps returning null?
          bee.setAttackTarget(this.getAttackTarget());
        }
        this.world.addEntity(bee);
      }
    }
  }
  
}
