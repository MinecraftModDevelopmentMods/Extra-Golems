package com.mcmoddev.golems.entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public interface IRandomExploder {

	/**
	 * @return the minimum fuse length
	 **/
	int getMinFuse();

	/**
	 * @return the current fuse length
	 **/
	int getFuse();

	/**
	 * @param fuseIn the updated fuse length
	 **/
	void setFuse(int fuseIn);

	/**
	 * @param litIn TRUE if the fuse is ignited
	 **/
	void setFuseLit(final boolean litIn);

	/**
	 * @return TRUE if the fuse is ignited
	 **/
	boolean isFuseLit();

	/**
	 * Sets the fuse to the minimum fuse length
	 **/
	default void resetFuse() {
		setFuse(getMinFuse());
	}

	/**
	 * Lights the fuse
	 **/
	default void lightFuse(final LivingEntity entity) {
		if (!isFuseLit() && !entity.isInWaterOrRain()) {
			resetFuse();
			setFuseLit(true);
			// play sounds
			entity.playSound(SoundEvents.CREEPER_PRIMED, 0.9F, 0.5F + entity.getRandom().nextFloat() * 0.5F);
		}
	}

	/**
	 * @return a number between 0.0 and 1.0 to indicate fuse progress
	 **/
	default float getFusePercentage() {
		return (float) getFuse() / (float) getMinFuse();
	}

	/**
	 * Sets fuse to 0 and fuseLit to false
	 **/
	default void resetFuseLit() {
		setFuseLit(false);
		resetFuse();
	}

	/**
	 * Creates an explosion at the entity location
	 *
	 * @param range the explosion size
	 */
	default void explode(LivingEntity entity, float range) {
		if (!entity.level().isClientSide()) {
			final Vec3 pos = entity.position();
			entity.level().explode(entity, pos.x, pos.y, pos.z, range, Level.ExplosionInteraction.MOB);
			entity.discard();
			spawnLingeringCloud(entity);
		}
	}

	/**
	 * Creates an AreaEffectCloud if applicable
	 **/
	default void spawnLingeringCloud(LivingEntity entity) {
		Collection<MobEffectInstance> collection = entity.getActiveEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloud areaeffectcloud = new AreaEffectCloud(entity.level(), entity.getX(), entity.getY(), entity.getZ());
			areaeffectcloud.setRadius(2.5F);
			areaeffectcloud.setRadiusOnUse(-0.5F);
			areaeffectcloud.setWaitTime(10);
			areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
			areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());

			for (MobEffectInstance mobeffectinstance : collection) {
				areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
			}

			entity.level().addFreshEntity(areaeffectcloud);
		}
	}
}
