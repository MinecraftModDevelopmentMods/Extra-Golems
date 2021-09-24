package com.mcmoddev.golems.entity;

import java.util.Collection;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.GameRules;

public interface IRandomExploder {
  
  static final String KEY_FUSE = "Fuse";
  static final String KEY_FUSE_LIT = "FuseLit";
  
  /** @return the minimum fuse length **/
  int getFuseLen();
  /** @return the current fuse length **/
  int getFuse();
  /** @param fuseIn the updated fuse length **/
  void setFuse(int fuseIn);
  /** @param litIn TRUE if the fuse is ignited **/
  void setFuseLit(final boolean litIn);
  /** @return TRUE if the fuse is ignited **/
  boolean isFuseLit();
  /** @return the Golem **/
  GolemBase getGolemEntity();
  
  /** Sets the fuse to the minimum fuse length **/
  default void resetFuse() {
    setFuse(getFuseLen());
  }

  /** Lights the fuse **/
  default void lightFuse() {
    if (!isFuseLit() && !getGolemEntity().isInWaterRainOrBubbleColumn()) {
      resetFuse();
      setFuseLit(true);
      // play sounds
      getGolemEntity().playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 0.9F, getGolemEntity().world.getRandom().nextFloat());
    }
  }
  
  /** @param tag the CompoundNBT to write to **/
  default void saveFuse(final CompoundNBT tag) {
    tag.putInt(KEY_FUSE, getFuse());
    tag.putBoolean(KEY_FUSE_LIT, isFuseLit());
  }
  
  /** @param tag the CompoundNBT to read from **/
  default void loadFuse(final CompoundNBT tag) {
    setFuse(tag.getInt(KEY_FUSE));
    setFuseLit(tag.getBoolean(KEY_FUSE_LIT));
  }

  /** @return a number between 0.0 and 1.0 to indicate fuse progress **/
  default float getFusePercentage() {
    return (float) getFuse() / (float) getFuseLen();
  }
  
  /** Sets fuse to 0 and fuseLit to false **/
  default void resetFuseLit() {
    setFuseLit(false);
    resetFuse();
  }

  /**
   * Creates an explosion at the entity location
   * @param range the explosion size
   */
  default void explode(float range) {
    final GolemBase entity = getGolemEntity();
    if (!entity.world.isRemote()) {
      final boolean flag = entity.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
      final Vector3d pos = entity.getPositionVec();
      entity.world.createExplosion(entity, pos.x, pos.y, pos.z, range, flag ? Explosion.Mode.BREAK : Explosion.Mode.NONE);
      entity.remove();
      spawnLingeringCloud();
    }
  }

  /** Creates an AreaEffectCloud if applicable **/
  default void spawnLingeringCloud() {
	final GolemBase entity = getGolemEntity();
	Collection<EffectInstance> collection = entity.getActivePotionEffects();
	if (!collection.isEmpty()) {
	  AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ());
	  areaeffectcloudentity.setRadius(2.5F);
	  areaeffectcloudentity.setRadiusOnUse(-0.5F);
	  areaeffectcloudentity.setWaitTime(10);
	  areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
	  areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());

	  for(EffectInstance effectinstance : collection) {
		areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
	  }

	  entity.world.addEntity(areaeffectcloudentity);
	}
  }
}
