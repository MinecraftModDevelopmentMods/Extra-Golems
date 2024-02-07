package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IVariantProvider;
import net.minecraft.advancements.critereon.MinMaxBounds;
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
public class  FollowGoal<T extends Mob & IVariantProvider> extends Goal implements IVariantPredicate {
	protected final T entity;
	protected final Predicate<LivingEntity> followPredicate;
	protected LivingEntity followingEntity;
	protected final double speedModifier;
	protected final PathNavigation navigation;
	protected int timeToRecalcPath;
	protected final float stopDistance;
	protected float oldWaterCost;
	protected final float areaSize;
	private final MinMaxBounds.Ints variant;

	public FollowGoal(final T entityIn, final double speed, final float stopDistanceIn, final float areaSizeIn,
					  final Predicate<LivingEntity> followPredicateIn, final MinMaxBounds.Ints variant) {
		this.variant = variant;
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

	//// VARIANT PREDICATE ////

	@Override
	public MinMaxBounds.Ints getVariantBounds() {
		return this.variant;
	}

	//// GOAL ////

	@Override
	public boolean canUse() {
		// validate variant
		if(!isVariantInBounds(entity)) {
			return false;
		}
		// validate target mob exists
		List<LivingEntity> mobEntityList = this.entity.level().getEntitiesOfClass(LivingEntity.class,
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
		return (isVariantInBounds(this.entity)
				&& this.followingEntity != null && !this.navigation.isDone()
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

		if(this.entity.position().closerThan(this.followingEntity.position(), this.stopDistance)) {
			this.navigation.stop();
			return;
		}

		this.navigation.moveTo(this.followingEntity, this.speedModifier);
	}
}
