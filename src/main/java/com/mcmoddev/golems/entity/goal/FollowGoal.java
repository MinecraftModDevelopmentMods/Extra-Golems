package com.mcmoddev.golems.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * This is copied from net.minecraft.entity.ai.goal.FollowMobGoal
 * but allows for custom follow predicate.
 **/
public class FollowGoal extends Goal {
	protected final Mob entity;
	protected final Predicate<LivingEntity> followPredicate;
	protected LivingEntity followingEntity;
	protected final double speedModifier;
	protected final PathNavigation navigation;
	protected int timeToRecalcPath;
	protected final float stopDistance;
	protected float oldWaterCost;
	protected final float areaSize;

	public FollowGoal(final Mob entityIn, final double speed, final float stopDistanceIn, final float areaSizeIn,
					  final Predicate<LivingEntity> followPredicateIn) {
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		this.entity = entityIn;
		this.followPredicate = followPredicateIn;
		this.speedModifier = speed;
		this.navigation = entityIn.getNavigation();
		this.stopDistance = stopDistanceIn;
		this.areaSize = areaSizeIn;

		if (!(entityIn.getNavigation() instanceof net.minecraft.world.entity.ai.navigation.GroundPathNavigation)
				&& !(entityIn.getNavigation() instanceof net.minecraft.world.entity.ai.navigation.FlyingPathNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowGoal");
		}
	}

	@Override
	public boolean canUse() {
		List<LivingEntity> mobEntityList = this.entity.level.getEntitiesOfClass(LivingEntity.class,
				this.entity.getBoundingBox().inflate(this.areaSize), this.followPredicate);
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
	public boolean canContinueToUse() {
		return (this.followingEntity != null && !this.navigation.isDone()
				&& this.entity.distanceToSqr(this.followingEntity) > (this.stopDistance * this.stopDistance));
	}

	@Override
	public void start() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.entity.getPathfindingMalus(BlockPathTypes.WATER);
		this.entity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
	}

	@Override
	public void stop() {
		this.followingEntity = null;
		this.navigation.stop();
		this.entity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
	}

	@Override
	public void tick() {
		if (this.followingEntity == null || this.entity.isLeashed()) {
			return;
		}

		this.entity.getLookControl().setLookAt(this.followingEntity, 10.0F, this.entity.getMaxHeadXRot());

		if (--this.timeToRecalcPath > 0) {
			return;
		}
		this.timeToRecalcPath = 10;

		double dX = this.entity.getX() - this.followingEntity.getX();
		double dY = this.entity.getY() - this.followingEntity.getY();
		double dZ = this.entity.getZ() - this.followingEntity.getZ();

		double distanceSq = dX * dX + dY * dY + dZ * dZ;
		if (distanceSq <= (this.stopDistance * this.stopDistance)) {
			this.navigation.stop();

//      if (distanceSq <= this.stopDistance) {
//        double dX2 = this.followingEntity.getPosX() - this.entity.getPosX();
//        double dZ2 = this.followingEntity.getPosZ() - this.entity.getPosZ();
//        this.navigation.tryMoveToXYZ(this.entity.getPosX() - dX2, this.entity.getPosY(), this.entity.getPosZ() - dZ2, this.speedModifier);
//      }

			return;
		}
		this.navigation.moveTo(this.followingEntity, this.speedModifier);
	}
}
