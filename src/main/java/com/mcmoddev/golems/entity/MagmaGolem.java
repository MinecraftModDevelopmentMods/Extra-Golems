package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class MagmaGolem extends GolemBase {

  public static final String ALLOW_FIRE_SPECIAL = "Allow Special: Burn Enemies";
  public static final String ALLOW_LAVA_SPECIAL = "Allow Special: Melt Cobblestone";
  public static final String ALLOW_WATER_DAMAGE = "Enable Water Damage";
  public static final String SPLITTING_CHILDREN = "Splitting Factor";
  public static final String MELT_DELAY = "Melting Delay";

  /**
   * Golem should stand in one spot for number of ticks before affecting the block
   * below it.
   */
  private int ticksStandingStill;
  /**
   * Helpers for "Standing Still" code
   */
  private int stillX;
  private int stillZ;
  /**
   * Whether this golem is hurt by water
   */
  private boolean isHurtByWater;
  
  private boolean allowFire;
  private boolean allowMelting;
  private int meltDelay;

  public MagmaGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    this.isHurtByWater = this.getConfigBool(ALLOW_WATER_DAMAGE);
    this.allowFire = this.getConfigBool(ALLOW_FIRE_SPECIAL);
    this.allowMelting = this.getConfigBool(ALLOW_LAVA_SPECIAL);
    this.meltDelay = this.getConfigInt(MELT_DELAY);
    this.ticksStandingStill = 0;
    if (isHurtByWater) {
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }
  }

  @Override
  public boolean isUnderWater() {
    return isHurtByWater;
  }
  
  @Override
  public void setBaby(final boolean isChild) {
    super.setBaby(isChild);
    if(isChild) {
      allowMelting = false;
      allowFire = false;
    }
  }

  /**
   * Attack by lighting on fire as well.
   */
  @Override
  public boolean doHurtTarget(final Entity entity) {
    if (super.doHurtTarget(entity)) {
      if (!this.isBaby() && allowFire) {
        entity.setSecondsOnFire(2 + random.nextInt(5));
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
    // take damage from water/rain
    if (this.isHurtByWater && this.isInWaterOrRain()) {
      this.hurt(DamageSource.DROWN, 0.5F);
    }
    // check the cobblestone-melting math
    if (this.allowMelting && !this.isBaby()) {
      final BlockPos below = this.getBlockBelow();
      final Block b1 = this.level.getBlockState(below).getBlock();

      if (below.getX() == this.stillX && below.getZ() == this.stillZ) {
        // check if it's been holding still long enough AND on top of cobblestone
        if (++this.ticksStandingStill >= this.meltDelay && b1 == Blocks.COBBLESTONE && random.nextInt(16) == 0) {
          BlockState replace = Blocks.MAGMA_BLOCK.defaultBlockState();
          this.level.setBlock(below, replace, 3);
          this.ticksStandingStill = 0;
        }
      } else {
        this.ticksStandingStill = 0;
        this.stillX = below.getX();
        this.stillZ = below.getZ();
      }
    }
  }

  @Override
  protected SoundEvent getHurtSound(final DamageSource ignored) {
    return ignored == DamageSource.DROWN ? SoundEvents.LAVA_EXTINGUISH : this.getGolemSound();
  }

  @Override
  public void die(final DamageSource source) {
    int children = this.getConfigInt(SPLITTING_CHILDREN);
    if (children > 0) {
      trySpawnChildren(children);
    }
    super.die(source);
  }
}
