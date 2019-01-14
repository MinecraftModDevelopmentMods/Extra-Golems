package com.golems.entity;

import java.util.List;

import com.golems.main.Config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class EntityTNTGolem extends GolemBase {

	protected static final DataParameter<Boolean> DATA_IGNITED = EntityDataManager
			.<Boolean>createKey(EntityTNTGolem.class, DataSerializers.BOOLEAN);
	public static final String ALLOW_SPECIAL = "Allow Special: Explode";

	protected final int minExplosionRad;
	protected final int maxExplosionRad;
	protected final int fuseLen;
	/** Percent chance to explode while attacking a mob. **/
	protected final int chanceToExplodeWhenAttacking;
	protected boolean allowedToExplode;

	protected boolean willExplode;
	protected int fuseTimer;

	/** Default constructor for TNT golem. **/
	public EntityTNTGolem(final World world) {
		this(world, Config.TNT.getBaseAttack(), new ItemStack(Blocks.TNT), 3, 6, 50, 10,
				Config.TNT.getBoolean(ALLOW_SPECIAL));
		this.setLootTableLoc("golem_tnt");
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 * 
	 * @param world
	 * @param attack
	 * @param pick
	 * @param minExplosionRange
	 * @param maxExplosionRange
	 * @param minFuseLength
	 * @param randomExplosionChance
	 * @param configAllowsExplode
	 */
	public EntityTNTGolem(final World world, final float attack, final ItemStack pick, final int minExplosionRange,
			final int maxExplosionRange, final int minFuseLength, final int randomExplosionChance,
			final boolean configAllowsExplode) {
		super(world, attack, pick);
		this.minExplosionRad = minExplosionRange;
		this.maxExplosionRad = maxExplosionRange;
		this.fuseLen = minFuseLength;
		this.chanceToExplodeWhenAttacking = randomExplosionChance;
		this.allowedToExplode = configAllowsExplode;
		this.resetIgnite();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(DATA_IGNITED, Boolean.valueOf(false));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("tnt");
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

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
			if (this.world instanceof WorldServer) {
				for (int i = 0; i < 2; i++) {
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX,
							this.posY + 2.0D, this.posZ, 0.0D, 0.0D, 0.0D);
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + 0.75D,
							this.posY + 1.0D + rand.nextDouble() * 2, this.posZ + 0.75D,
							0.5 * (0.5D - rand.nextDouble()), 0.0D,
							0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + 0.75D,
							this.posY + 1.0D + rand.nextDouble() * 2, this.posZ - 0.75D,
							0.5 * (0.5D - rand.nextDouble()), 0.0D,
							0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX - 0.75D,
							this.posY + 1.0D + rand.nextDouble() * 2, this.posZ + 0.75D,
							0.5 * (0.5D - rand.nextDouble()), 0.0D,
							0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX - 0.75D,
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

		if (flag && !entity.isDead && rand.nextInt(100) < this.chanceToExplodeWhenAttacking
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
				this.setDead();
			}
		} else {
			resetIgnite();
		}
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.TNT.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 2 + this.rand.nextInt(4);
//		this.addDrop(dropList, new ItemStack(Items.GUNPOWDER, size), 100);
//		this.addDrop(dropList, Blocks.TNT, 0, 1, 1, lootingLevel * 30);
//		this.addDrop(dropList, Blocks.SAND, 0, 1, 4, 5 + lootingLevel * 10);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		// only fires for this golem, not child classes
		if (this.getClass() == EntityTNTGolem.class && Config.TNT.getBoolean(EntityTNTGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.RED + trans("entitytip.explodes"));
		return list;
	}
}
