package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityTNTGolem extends GolemBase {

	protected static final DataParameter<Boolean> DATA_IGNITED = EntityDataManager
		.<Boolean>createKey(EntityTNTGolem.class, DataSerializers.BOOLEAN);
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
	public EntityTNTGolem(final World world) {
		this(EntityTNTGolem.class, world, 4, 8, 50, 10);
		this.setLootTableLoc(GolemNames.TNT_GOLEM);
		this.allowedToExplode = this.getConfigBool(ALLOW_SPECIAL);
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 * 
	 * @param clazz
	 * @param world
	 * @param minExplosionRange
	 * @param maxExplosionRange
	 * @param minFuseLength
	 * @param randomExplosionChance
	 */
	public EntityTNTGolem(final Class<? extends EntityTNTGolem> clazz, final World world, final int minExplosionRange,
			      final int maxExplosionRange, final int minFuseLength, final int randomExplosionChance) {
		super(clazz, world);
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

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.TNT_GOLEM);
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
			this.motionX = this.motionZ = 0;
			this.fuseTimer--;
			if (!this.world.isRemote) {
				for (int i = 0; i < 2; i++) {
					this.world.spawnParticle(Particles.LARGE_SMOKE, this.posX,
						this.posY + 2.0D, this.posZ, 0.0D, 0.0D, 0.0D);
					this.world.spawnParticle(Particles.LARGE_SMOKE, this.posX + 0.75D,
						this.posY + 1.0D + rand.nextDouble() * 2, this.posZ + 0.75D,
						0.5 * (0.5D - rand.nextDouble()), 0.0D,
						0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(Particles.LARGE_SMOKE, this.posX + 0.75D,
						this.posY + 1.0D + rand.nextDouble() * 2, this.posZ - 0.75D,
						0.5 * (0.5D - rand.nextDouble()), 0.0D,
						0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(Particles.LARGE_SMOKE, this.posX - 0.75D,
						this.posY + 1.0D + rand.nextDouble() * 2, this.posZ + 0.75D,
						0.5 * (0.5D - rand.nextDouble()), 0.0D,
						0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(Particles.LARGE_SMOKE, this.posX - 0.75D,
						this.posY + 1.0D + rand.nextDouble() * 2, this.posZ - 0.75D,
						0.5 * (0.5D - rand.nextDouble()), 0.0D,
						0.5 * (0.5D - rand.nextDouble()));
				}
			}
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
	protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
		final ItemStack itemstack = player.getHeldItem(hand);
		if (!itemstack.isEmpty() && itemstack.getItem() == Items.FLINT_AND_STEEL) {
			this.world.playSound(player, this.posX, this.posY, this.posZ,
				SoundEvents.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0F,
				this.rand.nextFloat() * 0.4F + 0.8F);
			player.swingArm(hand);

			if (!this.world.isRemote) {
				this.setFire(Math.floorDiv(this.fuseLen, 20));
				this.ignite();
				itemstack.damageItem(1, player);
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
				final boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
				final float range = this.maxExplosionRad > this.minExplosionRad
					? rand.nextInt(maxExplosionRad - minExplosionRad)
					: this.minExplosionRad;
				this.world.createExplosion(this, this.posX, this.posY, this.posZ, range, flag);
				this.remove();
			}
		} else {
			resetIgnite();
		}
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
}
