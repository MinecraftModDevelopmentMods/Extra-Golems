package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityMagmaGolem extends GolemLightProvider
{		
	public static final String ALLOW_FIRE_SPECIAL = "Allow Special: Burn Enemies";
	public static final String ALLOW_LAVA_SPECIAL = "Allow Special: Melt Cobblestone";
	public static final String MELT_DELAY = "Melting Delay";
	public static Block MAGMA = Blocks.field_189877_df;
	
	/** Golem should stand in one spot for number of ticks before affecting the block below it */
	private int ticksStandingStill;

	public EntityMagmaGolem(World world) 
	{
		super(world, Config.MAGMA.getBaseAttack(), new ItemStack(MAGMA), LightLevel.HALF);
		this.setImmuneToFire(true);
		this.ticksStandingStill = 0;
		this.stepHeight = 1.0F;
		this.tasks.addTask(0, this.swimmingAI);
	}
	
	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("magma");
	}
	
	/** Attack by lighting on fire as well */
	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if(super.attackEntityAsMob(entity))
		{
			if(Config.MAGMA.getBoolean(ALLOW_FIRE_SPECIAL))
			{
				entity.setFire(2 + rand.nextInt(5));
			}
			return true;
		}
		return false;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if(Config.MAGMA.getBoolean(ALLOW_LAVA_SPECIAL))
		{
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY - 0.20000000298023224D);
			int z = MathHelper.floor_double(this.posZ);
			BlockPos below = new BlockPos(x,y,z);
			Block b1 = this.worldObj.getBlockState(below).getBlock();
			// debug:
			//System.out.println("below=" + below + "; lastPos = " + new BlockPos(MathHelper.floor_double(this.lastTickPosX), this.lastTickPosY, MathHelper.floor_double(this.lastTickPosZ)));
			//System.out.println("block on= " + b1.getUnlocalizedName() + "; ticksStandingStill=" + ticksStandingStill);
			
			if(x == MathHelper.floor_double(this.lastTickPosX) && z == MathHelper.floor_double(this.lastTickPosZ))
			{
				if(++this.ticksStandingStill >= Config.MAGMA.getInt(MELT_DELAY) && b1 == Blocks.COBBLESTONE && rand.nextInt(16) == 0)
				{
					this.worldObj.setBlockState(below, Blocks.LAVA.getDefaultState(), 3);
					this.ticksStandingStill = 0;
				}
			}
			else
			{
				this.ticksStandingStill = 0;
			}
		}
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.MAGMA.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = lootingLevel + this.rand.nextInt(4);
		this.addDrop(dropList, new ItemStack(MAGMA, size > 4 ? 4 : size), 90 + lootingLevel * 2);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
