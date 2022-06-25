package com.mcmoddev.golems.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public interface IRandomExploder {

	String KEY_FUSE = "Fuse";
	String KEY_FUSE_LIT = "FuseLit";

	/**
	 * @return the minimum fuse length
	 **/
	int getFuseLen();

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
	 * @return the Golem
	 **/
	GolemBase getGolemEntity();

	/**
	 * Sets the fuse to the minimum fuse length
	 **/
	default void resetFuse() {
		setFuse(getFuseLen());
	}

	/**
	 * Lights the fuse
	 **/
	default void lightFuse() {
		if (!isFuseLit() && !getGolemEntity().isInWaterOrRain()) {
			resetFuse();
			setFuseLit(true);
			// play sounds
			getGolemEntity().playSound(SoundEvents.CREEPER_PRIMED, 0.9F, getGolemEntity().getRandom().nextFloat());
		}
	}

	/**
	 * @param tag the CompoundTag to write to
	 **/
	default void saveFuse(final CompoundTag tag) {
		tag.putInt(KEY_FUSE, getFuse());
		tag.putBoolean(KEY_FUSE_LIT, isFuseLit());
	}

	/**
	 * @param tag the CompoundTag to read from
	 **/
	default void loadFuse(final CompoundTag tag) {
		setFuse(tag.getInt(KEY_FUSE));
		setFuseLit(tag.getBoolean(KEY_FUSE_LIT));
	}

	/**
	 * @return a number between 0.0 and 1.0 to indicate fuse progress
	 **/
	default float getFusePercentage() {
		return (float) getFuse() / (float) getFuseLen();
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
	default void explode(float range) {
		final GolemBase entity = getGolemEntity();
		if (!entity.level.isClientSide()) {
			final boolean flag = entity.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
			final Vec3 pos = entity.position();
			entity.level.explode(entity, pos.x, pos.y, pos.z, range, flag ? BlockInteraction.BREAK : BlockInteraction.NONE);
			entity.discard();
			spawnLingeringCloud();
		}
	}

	/**
	 * Creates an AreaEffectCloud if applicable
	 **/
	default void spawnLingeringCloud() {
		final GolemBase entity = getGolemEntity();
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
