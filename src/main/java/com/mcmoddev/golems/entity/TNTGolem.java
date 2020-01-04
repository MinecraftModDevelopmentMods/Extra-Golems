package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class TNTGolem extends GolemBase {

	protected static final DataParameter<Boolean> DATA_IGNITED = EntityDataManager
		.<Boolean>createKey(TNTGolem.class, DataSerializers.BOOLEAN);
	public static final String ALLOW_SPECIAL = "Allow Special: Explode";

	protected final int minExplosionRad;
	protected final int maxExplosionRad;
	protected final int fuseLen;
	/**
	 * Percent chance to explode while attacking a mob.
	 **/
	protected final int chanceToExplodeWhenAttacking;
	protected boolean allowedToExplode = false;

	protected boolean willExplode;
	protected int fuseTimer;

	/** Default constructor for TNT golem. **/
	public TNTGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		this(entityType, world, 6, 10, 50, 10);
		this.allowedToExplode = this.getConfigBool(ALLOW_SPECIAL);
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 *
	 * @param entityType
	 * @param world
	 * @param minExplosionRange
	 * @param maxExplosionRange
	 * @param minFuseLength
	 * @param randomExplosionChance
	 */
	public TNTGolem(final EntityType<? extends GolemBase> entityType, final World world, final int minExplosionRange,
			      final int maxExplosionRange, final int minFuseLength, final int randomExplosionChance) {
		super(entityType, world);
		this.minExplosionRad = minExplosionRange;
		this.maxExplosionRad = maxExplosionRange;
		this.fuseLen = minFuseLength;
		this.chanceToExplodeWhenAttacking = randomExplosionChance;
		this.resetIgnite();
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(DATA_IGNITED, false);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();

		if (this.isBurning()) {
			this.ignite();
		}

		if (this.isWet() || (this.getAttackTarget() != null
				&& this.getDistanceSq(this.getAttackTarget()) > this.minExplosionRad
				* this.maxExplosionRad)) {
			this.resetIgnite();
		}

		if (this.isIgnited()) {
			this.setMotion(0.0D, this.getMotion().getY(), 0.0D);
			this.fuseTimer--;
			final Vec3d pos = this.getPositionVec();
			ItemBedrockGolem.spawnParticles(this.world, pos.x, pos.y + 1.0D,
					pos.z, 0.21D, ParticleTypes.SMOKE, 6);
			if (this.fuseTimer <= 0) {
				this.willExplode = true;
			}
		}

		if (this.willExplode) {
			this.explode();
		}
	}

	@Override
	public void onDeath(final DamageSource source) {
		super.onDeath(source);
		this.explode();
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		boolean flag = super.attackEntityAsMob(entity);

		if (flag && entity.isAlive() && rand.nextInt(100) < this.chanceToExplodeWhenAttacking
				&& this.getDistanceSq(entity) <= this.minExplosionRad * this.minExplosionRad) {
			this.ignite();
		}

		return flag;
	}

	@Override
	protected boolean processInteract(final PlayerEntity player, final Hand hand) {
		final ItemStack itemstack = player.getHeldItem(hand);
		if (!itemstack.isEmpty() && itemstack.getItem() == Items.FLINT_AND_STEEL) {
			final Vec3d pos = this.getPositionVec();
			this.world.playSound(player, pos.x, pos.y, pos.z,
					SoundEvents.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0F,
					this.rand.nextFloat() * 0.4F + 0.8F);
			player.swingArm(hand);

			if (!this.world.isRemote) {
				this.setFire(Math.floorDiv(this.fuseLen, 20));
				this.ignite();
				itemstack.damageItem(1, player, c -> c.sendBreakAnimation(hand));
			}
		}

		return super.processInteract(player, hand);
	}

	protected void resetFuse() {
		this.fuseTimer = this.fuseLen + rand.nextInt(Math.floorDiv(fuseLen, 2) + 1);
	}

	protected void setIgnited(final boolean toSet) {
		this.getDataManager().set(DATA_IGNITED, Boolean.valueOf(toSet));
	}

	protected boolean isIgnited() {
		return this.getDataManager().get(DATA_IGNITED).booleanValue();
	}

	protected void ignite() {
		if (!this.isIgnited()) {
			// update info
			this.setIgnited(true);
			this.resetFuse();
			// play sounds
			if (!this.isWet()) {
				this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 0.9F, rand.nextFloat());
			}
		}
	}

	protected void resetIgnite() {
		this.setIgnited(false);
		this.resetFuse();
		this.willExplode = false;
	}

	protected void explode() {
		if (this.allowedToExplode) {
			if (!this.world.isRemote) {
				final boolean flag = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
				final float range = this.maxExplosionRad > this.minExplosionRad
						? (minExplosionRad + rand.nextInt(maxExplosionRad - minExplosionRad))
						: this.minExplosionRad;
				final Vec3d pos = this.getPositionVec();
				this.world.createExplosion(this, pos.x, pos.y, pos.z, range, flag ? Mode.BREAK : Mode.NONE);
				this.remove();
			}
		} else {
			resetIgnite();
		}
	}
}
