package com.golems.entity;

import java.util.HashSet;

import com.golems.blocks.BlockLightProvider;
import com.golems.main.GolemItems;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GolemLightProvider extends GolemBase 
{	
	protected LightLevel lightLevel;
	protected int tickDelay;
	protected int updateFlag;
	
	public GolemLightProvider(World world, float attack, ItemStack pick, LightLevel light)
	{
		super(world, attack, pick);
		this.lightLevel = light;
		this.tickDelay = 2;
		this.updateFlag = 2;
	}
	
	public GolemLightProvider(World world, float attack, LightLevel light)
	{
		this(world, attack, new ItemStack(GolemItems.golemHead), light);
	}
	
	/**
	 * @deprecated Use {@link #GolemLightProvider(World, float, ItemStack, LightLevel)}
	 * REMOVE THIS CONSTRUCTOR IN 1.11 UPDATE
	 **/
	public GolemLightProvider(World world, float attack, Block pick, EnumLightLevel light)
	{
		this(world, attack, new ItemStack(pick), new LightLevel(light.getBrightness(), light.getMaterialToReplace()));
	}
	
	/**
	 * @deprecated Use {@link #GolemLightProvider(World, float, LightLevel)}
	 * REMOVE THIS CONSTRUCTOR IN 1.11 UPDATE
	 **/
	public GolemLightProvider(World world, float attack, EnumLightLevel light)
	{
		this(world, attack, GolemItems.golemHead, light);
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		// only try to place light blocks every other tick -- reduces lag by 50%
		if(this.tickDelay <= 1 || this.ticksExisted % this.tickDelay == 0)
		{
			this.placeLightBlock();
		}
	}

	private boolean placeLightBlock() 
	{
		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.posY - 0.20000000298023224D);
		int z = MathHelper.floor_double(this.posZ);
		int[][] validPos = {{x,z},{x+1,z},{x-1,z},{x,z+1},{x,z-1},{x+1,z+1},{x-1,z+1},{x+1,z-1},{x-1,z-1}};
		for(int[] coord : validPos)
		{
			int xPos = coord[0];
			int zPos = coord[1];
			for(int k = 0; k < 3; ++k)
			{	
				int yPos = y + k + 1;
				BlockPos pos = new BlockPos(xPos, yPos, zPos);
				IBlockState state = this.worldObj.getBlockState(pos);
				if(state.getBlock() instanceof BlockLightProvider)
				{
					return false;
				}
				else if(this.lightLevel.canReplace(state))
				{
					return this.worldObj.setBlockState(pos, this.lightLevel.getLightState(), this.updateFlag);
				}
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float f)
	{
		return 15728880;
	}

	/** Gets how bright this entity is **/
	@Override
	public float getBrightness(float f)
	{
		return this.lightLevel.getBrightness();
	}
	
	public static class LightLevel
	{
		public static final LightLevel FULL = LightLevel.of(1.0F);
		public static final LightLevel HALF = LightLevel.of(0.5F);
		
		private final int lightRange;
		private final float light;
		private final HashSet<Material> replaceable;
		
		public LightLevel(float brightness, Material... canReplace)
		{
			this.lightRange = (int)(15.0F * brightness);
			this.light = brightness;
			if(canReplace != null && canReplace.length > 0)
			{
				this.replaceable = Sets.newHashSet(canReplace);
			}
			else this.replaceable = Sets.newHashSet(Material.AIR);
		}
		
		public IBlockState getLightState()
		{
			return GolemItems.blockLightSource.getDefaultState().withProperty(BlockLightProvider.LIGHT, this.lightRange);
		}
		
		public int getLightValue()
		{
			return this.lightRange;
		}
		
		public float getBrightness()
		{
			return this.light;
		}
		
		public boolean canReplace(IBlockState state)
		{
			return this.replaceable.contains(state.getMaterial());
		}
		
		public static LightLevel of(float brightness, Material... materials)
		{
			return new LightLevel(brightness, materials);
		}
	}

	/** 
	 * Allows the golem to emit different levels of light.
	 * @deprecated use {@link LightLevel}
	 * REMOVE THIS ENUM IN 1.11 UPDATE
	 **/
	@Deprecated
	public static enum EnumLightLevel
	{
		HALF(0.5F, Material.AIR),
		FULL(1.0F, Material.AIR),
		WATER_HALF(0.5F, Material.WATER),
		WATER_FULL(1.0F, Material.WATER);
		
		private final int lightRange;
		private final float light;
		private final Material replaceable;
		
		private EnumLightLevel(float brightness, Material canReplace)
		{
			this.lightRange = (int)(15.0F * brightness);
			this.light = brightness;
			this.replaceable = canReplace;
		}
		
		public IBlockState getLightBlock()
		{
			return GolemItems.blockLightSource.getDefaultState().withProperty(BlockLightProvider.LIGHT, this.lightRange);
		}
		
		public int getLightValue()
		{
			return this.lightRange;
		}
		
		public float getBrightness()
		{
			return this.light;
		}
		
		public Material getMaterialToReplace()
		{
			return this.replaceable;
		}
	}
}
