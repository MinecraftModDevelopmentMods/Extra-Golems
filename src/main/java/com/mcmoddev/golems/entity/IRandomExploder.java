package com.mcmoddev.golems.entity;

import java.util.Collection;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;

public interface IRandomExploder {
  
  static final String KEY_FUSE = "Fuse";
  static final String KEY_FUSE_LIT = "FuseLit";
  
  int getFuseLen();
  
  int getFuse();
  
  void setFuse(int fuseIn);
  
  void setFuseLit(final boolean litIn);
  
  boolean isFuseLit();
  
  default void resetFuse() {
    setFuse(getFuseLen());
  }

  default void lightFuse() {
    if (!isFuseLit()) {
      resetFuse();
      setFuseLit(true);
      // play sounds
//      if (!entity.isInWaterOrRain()) {
//        entity.playSound(SoundEvents.CREEPER_PRIMED, 0.9F, random.nextFloat());
//      }
    }
  }
  
  default void saveFuse(final CompoundTag tag) {
    tag.putInt(KEY_FUSE, getFuse());
    tag.putBoolean(KEY_FUSE_LIT, isFuseLit());
  }
  
  default void loadFuse(final CompoundTag tag) {
    setFuse(tag.getInt(KEY_FUSE));
    setFuseLit(tag.getBoolean(KEY_FUSE_LIT));
  }

  /** @return a number between 0.0 and 1.0 to indicate fuse progress **/
  default float getFusePercentage() {
    return (float) getFuse() / (float) getFuseLen();
  }
  
  default void resetFuseLit() {
    setFuseLit(false);
    resetFuse();
  }

  default void explode(Mob entity, float range) {
    if (!entity.level.isClientSide()) {
      final boolean flag = entity.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
      final Vec3 pos = entity.position();
      entity.level.explode(entity, pos.x, pos.y, pos.z, range, flag ? BlockInteraction.BREAK : BlockInteraction.NONE);
      entity.discard();
      spawnLingeringCloud(entity);
    }
  }

  static void spawnLingeringCloud(Mob entity) {
    Collection<MobEffectInstance> collection = entity.getActiveEffects();
    if (!collection.isEmpty()) {
      AreaEffectCloud areaeffectcloud = new AreaEffectCloud(entity.level, entity.getX(), entity.getY(), entity.getZ());
      areaeffectcloud.setRadius(2.5F);
      areaeffectcloud.setRadiusOnUse(-0.5F);
      areaeffectcloud.setWaitTime(10);
      areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
      areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());

      for (MobEffectInstance mobeffectinstance : collection) {
        areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
      }

      entity.level.addFreshEntity(areaeffectcloud);
    }
  }
}
