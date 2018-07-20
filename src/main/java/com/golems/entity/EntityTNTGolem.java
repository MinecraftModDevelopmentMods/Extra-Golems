package com.golems.entity;

import com.golems.main.Config;
import com.golems.util.WeightedItem;
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
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class EntityTNTGolem extends GolemBase 
{	
	protected static final DataParameter<Boolean> DATA_IGNITED = EntityDataManager.<Boolean>createKey(EntityTNTGolem.class, DataSerializers.BOOLEAN);
	public static final String ALLOW_SPECIAL = "Allow Special: Explode";

	protected final int MIN_EXPLOSION_RAD;
	protected final int MAX_EXPLOSION_RAD;
	protected final int FUSE_LEN;
	/** Percent chance to explode while attacking a mob **/
	protected final int CHANCE_TO_EXPLODE_WHEN_ATTACKING;
	protected boolean allowedToExplode;

	protected boolean willExplode;
	protected int fuseTimer;

	/** Default constructor for TNT golem **/
	public EntityTNTGolem(World world) 
	{
		this(world, Config.TNT.getBaseAttack(), new ItemStack(Blocks.TNT), 3, 6, 50, 10, Config.TNT.getBoolean(ALLOW_SPECIAL));
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 **/
	public EntityTNTGolem(World world, float attack, ItemStack pick, int minExplosionRange, int maxExplosionRange, int minFuseLength, int randomExplosionChance, boolean configAllowsExplode)
	{
		super(world, attack, pick);
		this.MIN_EXPLOSION_RAD = minExplosionRange;
		this.MAX_EXPLOSION_RAD = maxExplosionRange;
		this.FUSE_LEN = minFuseLength;
		this.CHANCE_TO_EXPLODE_WHEN_ATTACKING = randomExplosionChance;
		this.allowedToExplode = configAllowsExplode;
		this.resetIgnite();	
	}
	
	@Override
	protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DATA_IGNITED, Boolean.valueOf(false));
    }

	@Override
	protected ResourceLocation applyTexture()
	{
		return makeGolemTexture("tnt");
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if(this.isBurning())
		{
			this.ignite();
		}

		if(this.isWet() || (this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) > this.MAX_EXPLOSION_RAD * this.MAX_EXPLOSION_RAD))
		{
			this.resetIgnite();
		}

		if(this.isIgnited())
		{
			this.motionX = this.motionZ = 0;
			this.fuseTimer--;
			if(this.world instanceof WorldServer)
			{
				for (int i = 0; i < 2; i++)
				{
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 2.0D, this.posZ, 0.0D, 0.0D, 0.0D);
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + 0.75D, this.posY + 1.0D + rand.nextDouble() * 2, this.posZ + 0.75D, 0.5 * (0.5D - rand.nextDouble()), 0.0D, 0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + 0.75D, this.posY + 1.0D + rand.nextDouble() * 2, this.posZ - 0.75D, 0.5 * (0.5D - rand.nextDouble()), 0.0D, 0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX - 0.75D, this.posY + 1.0D + rand.nextDouble() * 2, this.posZ + 0.75D, 0.5 * (0.5D - rand.nextDouble()), 0.0D, 0.5 * (0.5D - rand.nextDouble()));
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX - 0.75D, this.posY + 1.0D + rand.nextDouble() * 2, this.posZ - 0.75D, 0.5 * (0.5D - rand.nextDouble()), 0.0D, 0.5 * (0.5D - rand.nextDouble()));
				}
			}
			if(this.fuseTimer <= 0)
			{
				this.willExplode = true;
			}
		}

		if(this.willExplode)
		{
			this.explode();
		}
	}

	@Override
	public void onDeath(DamageSource source)
	{
		super.onDeath(source);
		this.explode();
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		boolean flag = super.attackEntityAsMob(entity);

		if (flag && !entity.isDead && rand.nextInt(100) < this.CHANCE_TO_EXPLODE_WHEN_ATTACKING && this.getDistanceSq(entity) <= this.MIN_EXPLOSION_RAD * this.MIN_EXPLOSION_RAD)
		{
			this.ignite();
		}

		return flag;
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		ItemStack itemstack = player.getHeldItem(hand);
		if (!itemstack.isEmpty() && itemstack.getItem() == Items.FLINT_AND_STEEL)
		{	
			this.world.playSound(player, this.posX, this.posY, this.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
			player.swingArm(hand);

			if(!this.world.isRemote)
			{
				this.setFire(Math.floorDiv(this.FUSE_LEN, 20));
				this.ignite();
				itemstack.damageItem(1, player);
			}
		}

		return super.processInteract(player, hand);
	}

	protected void resetFuse()
	{
		this.fuseTimer = this.FUSE_LEN + rand.nextInt(Math.floorDiv(FUSE_LEN, 2) + 1);
	}
	
	protected void setIgnited(boolean toSet)
	{
		this.getDataManager().set(DATA_IGNITED, Boolean.valueOf(toSet));
	}
	
	protected boolean isIgnited()
	{
		return this.getDataManager().get(DATA_IGNITED).booleanValue();
	}

	protected void ignite()
	{
		if(!this.isIgnited())
		{
			// update info
			this.setIgnited(true);
			this.resetFuse();
			// play sounds
			if(!this.isWet())
			{
				this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 0.9F, rand.nextFloat());
			}
		}
	}

	protected void resetIgnite()
	{
		this.setIgnited(false);
		this.resetFuse();
		this.willExplode = false;
	}

	protected void explode()
	{
		if(this.allowedToExplode)
		{
			if(!this.world.isRemote)
			{
				boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
				float range = this.MAX_EXPLOSION_RAD > this.MIN_EXPLOSION_RAD ? rand.nextInt(MAX_EXPLOSION_RAD - MIN_EXPLOSION_RAD) : this.MIN_EXPLOSION_RAD;
				this.world.createExplosion(this, this.posX, this.posY, this.posZ, range, flag);
				this.setDead();
			}
		}
		else
		{
			resetIgnite();
		}
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.TNT.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 2 + this.rand.nextInt(4);
		this.addDrop(dropList, new ItemStack(Items.GUNPOWDER, size), 100);
		this.addDrop(dropList, Blocks.TNT, 0, 1, 1, lootingLevel * 30);
		this.addDrop(dropList, Blocks.SAND, 0, 1, 4, 5 + lootingLevel * 10);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
}
