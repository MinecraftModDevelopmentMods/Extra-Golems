package com.golems.entity;

import com.golems.blocks.BlockLightProvider;
import com.golems.main.GolemItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GolemLightProvider extends GolemBase 
{	
	protected EnumLightLevel lightLevel;
	protected int tickDelay;
	
	public GolemLightProvider(World world, float attack, Block pick, EnumLightLevel light)
	{
		super(world, attack, pick);
		this.lightLevel = light;
		this.tickDelay = 2;
	}
	
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
				Block at = state.getBlock();
				if(this.worldObj.isAirBlock(pos) || state.getMaterial() == this.lightLevel.getMaterialToReplace())
				{
					return this.worldObj.setBlockState(pos, this.lightLevel.getLightBlock().getDefaultState(), 2);
				}
				else if(at instanceof BlockLightProvider)
				{
					return false;
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

	/** Allows the golem to emit different levels of light **/
	public static enum EnumLightLevel
	{
		HALF(0.5F, Material.AIR),
		FULL(1.0F, Material.AIR),
		WATER_HALF(0.5F, Material.WATER),
		WATER_FULL(1.0F, Material.WATER);
		
		private final float light;
		private final Material replaceable;
		
		private EnumLightLevel(float brightness, Material canReplace)
		{
			this.light = brightness;
			this.replaceable = canReplace;
		}
		
		public Block getLightBlock()
		{
			switch(this)
			{
			case FULL: case WATER_FULL:	return GolemItems.blockLightSourceFull;
			case HALF: case WATER_HALF:	return GolemItems.blockLightSourceHalf;
			}
			return Blocks.AIR;
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
