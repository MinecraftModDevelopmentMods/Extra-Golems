package com.mcmoddev.golems.data.behavior.data;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.ExplodeBehavior;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Used by {@link ExplodeBehavior} to manage fuse length, lit status, and explosions
 */
public class ExplodeBehaviorData implements IBehaviorData {

	private final IExtraGolem entity;
	private int minFuse;
	private double explosionRadius;
	private int fuse;
	private boolean fuseLit;

	public ExplodeBehaviorData(final IExtraGolem entity) {
		this.entity = entity;
		processBehaviors();
	}

	//// METHODS ////

	/** Loads the relevant behaviors from the IExtraGolem to initialize fields **/
	private void processBehaviors() {
		final Optional<GolemContainer> oContainer = entity.getContainer(entity.asMob().level().registryAccess());
		if(oContainer.isEmpty()) {
			return;
		}
		final List<ExplodeBehavior> behaviors = oContainer.get().getBehaviors().getActiveBehaviors(ExplodeBehavior.class, entity);
		if(behaviors.isEmpty()) {
			return;
		}
		this.minFuse = behaviors.get(0).getMinFuse();
		this.explosionRadius = behaviors.get(0).getRadius();
	}

	/** Decrements the fuse if it is lit, then explodes when it reaches zero **/
	public void updateFuse() {
		final Mob mob = this.entity.asMob();
		if(this.fuseLit) {
			// update fuse
			if(!mob.level().isClientSide() && --this.fuse < 0) {
				this.explode();
			}
		}
	}

	/** @param fuse the updated fuse length **/
	public void setFuse(int fuse) {
		this.fuse = fuse;
	}

	/** @param fuseLit {@code true} if the fuse is ignited **/
	public void setFuseLit(final boolean fuseLit) {
		final boolean changed = this.fuseLit != fuseLit;
		this.fuseLit = fuseLit;
		final Mob mob = entity.asMob();
		if(changed && !mob.level().isClientSide()) {
			if(fuseLit) {
				mob.playSound(SoundEvents.CREEPER_PRIMED, 0.9F, 0.5F + this.entity.asMob().getRandom().nextFloat() * 0.5F);
			} else {
				mob.playSound(SoundEvents.FIRE_EXTINGUISH, 0.9F, 0.5F + this.entity.asMob().getRandom().nextFloat() * 0.5F);
			}
		}
	}

	/** Sets the fuse to the minimum fuse length **/
	public void resetFuse() {
		setFuse(getMinFuse());
	}

	/** Lights the fuse **/
	public void lightFuse() {
		if (!isFuseLit() && !entity.asMob().isInWaterOrRain()) {
			resetFuse();
			setFuseLit(true);
		}
	}

	/** Sets fuse to {@code 0} and fuseLit to {@code false} **/
	public void resetFuseLit() {
		setFuseLit(false);
		resetFuse();
	}

	/** Creates an explosion at the entity location **/
	public void explode() {
		final Mob mob = entity.asMob();
		if (!mob.level().isClientSide() && this.explosionRadius > 0) {
			final Vec3 pos = mob.position();
			mob.level().explode(mob, pos.x, pos.y, pos.z, (float) this.explosionRadius, Level.ExplosionInteraction.MOB);
			mob.discard();
			spawnLingeringCloud();
		}
	}

	/**
	 * Creates an AreaEffectCloud if applicable
	 **/
	public void spawnLingeringCloud() {
		final Mob mob = entity.asMob();
		Collection<MobEffectInstance> collection = mob.getActiveEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloud areaeffectcloud = new AreaEffectCloud(mob.level(), mob.getX(), mob.getY(), mob.getZ());
			areaeffectcloud.setRadius(2.5F);
			areaeffectcloud.setRadiusOnUse(-0.5F);
			areaeffectcloud.setWaitTime(10);
			areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
			areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());

			for (MobEffectInstance mobeffectinstance : collection) {
				areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
			}

			mob.level().addFreshEntity(areaeffectcloud);
		}
	}

	//// GETTERS ////

	/** @return the minimum fuse length **/
	public int getMinFuse() {
		return this.minFuse;
	}

	/** @return the current fuse length **/
	public int getFuse() {
		return this.fuse;
	}

	/** @return {@code true} if the fuse is ignited **/
	public boolean isFuseLit() {
		return this.fuseLit;
	}

	/** @return the explosion radius **/
	public double getExplosionRadius() {
		return this.explosionRadius;
	}

	/** @return a number between 0.0 and 1.0 to indicate fuse progress **/
	public float getFusePercentage() {
		if(this.minFuse <= 0) {
			return 0;
		}
		return (float) this.fuse / (float) this.minFuse;
	}

	//// NBT ////

	private static final String KEY_FUSE = "Fuse";
	private static final String KEY_FUSE_LIT = "FuseLit";

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag tag = new CompoundTag();
		tag.putInt(KEY_FUSE, fuse);
		tag.putBoolean(KEY_FUSE_LIT, fuseLit);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.fuse = tag.getInt(KEY_FUSE);
		this.fuseLit = tag.getBoolean(KEY_FUSE_LIT);
	}
}
