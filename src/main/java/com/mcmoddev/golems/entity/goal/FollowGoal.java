package com.mcmoddev.golems.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * This is copied from net.minecraft.entity.ai.goal.FollowMobGoal
 * but allows for custom follow predicate.
 **/
public class FollowGoal extends Goal {
  protected final MobEntity entity;
  protected final Predicate<LivingEntity> followPredicate;
  protected LivingEntity followingEntity;
  protected final double speedModifier;
  protected final PathNavigator navigation;
  protected int timeToRecalcPath;
  protected final float stopDistance;
  protected float oldWaterCost;
  protected final float areaSize;

  public FollowGoal(final MobEntity entityIn, final double speed, final float stopDistanceIn, final float areaSizeIn,
      final Predicate<LivingEntity> followPredicateIn) {
    setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    this.entity = entityIn;
    this.followPredicate = followPredicateIn;
    this.speedModifier = speed;
    this.navigation = entityIn.getNavigator();
    this.stopDistance = stopDistanceIn;
    this.areaSize = areaSizeIn;

    if (!(entityIn.getNavigator() instanceof GroundPathNavigator)
        && !(entityIn.getNavigator() instanceof FlyingPathNavigator)) {
      throw new IllegalArgumentException("Unsupported mob type for FollowGoal");
    }
  }

  @Override
  public boolean shouldExecute() {
    List<LivingEntity> mobEntityList = this.entity.world.getEntitiesWithinAABB(LivingEntity.class,
        this.entity.getBoundingBox().grow(this.areaSize), this.followPredicate);
    if (!mobEntityList.isEmpty()) {
      for (LivingEntity mobEntity : mobEntityList) {
        if (mobEntity.isInvisible()) {
          continue;
        }

        this.followingEntity = mobEntity;
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return (this.followingEntity != null && !this.navigation.noPath()
        && this.entity.getDistanceSq(this.followingEntity) > (this.stopDistance * this.stopDistance));
  }

  @Override
  public void startExecuting() {
    this.timeToRecalcPath = 0;
    this.oldWaterCost = this.entity.getPathPriority(PathNodeType.WATER);
    this.entity.setPathPriority(PathNodeType.WATER, 0.0F);
  }

  @Override
  public void resetTask() {
    this.followingEntity = null;
    this.navigation.clearPath();
    this.entity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
  }

  @Override
  public void tick() {
    if (this.followingEntity == null || this.entity.getLeashed()) {
      return;
    }

    this.entity.getLookController().setLookPositionWithEntity(this.followingEntity, 10.0F, this.entity.getHorizontalFaceSpeed());

    if (--this.timeToRecalcPath > 0) {
      return;
    }
    this.timeToRecalcPath = 10;

    double dX = this.entity.getPosX() - this.followingEntity.getPosX();
    double dY = this.entity.getPosY() - this.followingEntity.getPosY();
    double dZ = this.entity.getPosZ() - this.followingEntity.getPosZ();

    double distanceSq = dX * dX + dY * dY + dZ * dZ;
    if (distanceSq <= (this.stopDistance * this.stopDistance)) {
      this.navigation.clearPath();

//      if (distanceSq <= this.stopDistance) {
//        double dX2 = this.followingEntity.getPosX() - this.entity.getPosX();
//        double dZ2 = this.followingEntity.getPosZ() - this.entity.getPosZ();
//        this.navigation.tryMoveToXYZ(this.entity.getPosX() - dX2, this.entity.getPosY(), this.entity.getPosZ() - dZ2, this.speedModifier);
//      }

      return;
    }
    this.navigation.tryMoveToEntityLiving(this.followingEntity, this.speedModifier);
  }
}
