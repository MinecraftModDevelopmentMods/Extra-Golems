package com.golems.entity.ai;

import java.util.function.Predicate;

import com.golems.entity.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIPlaceRandomBlocks extends EntityAIBase
{
	public final GolemBase golem;
	public final int tickDelay;
	public final IBlockState[] plantables;
	public final Block[] plantSupports;
	public final boolean checkSupports;
	public final Predicate<EntityAIPlaceRandomBlocks> canExecute;
	
	public EntityAIPlaceRandomBlocks(GolemBase golemBase, int ticksBetweenPlanting, IBlockState[] plants, Block[] soils, Predicate<EntityAIPlaceRandomBlocks> pred)
	{
		this.setMutexBits(8);
		this.golem = golemBase;
		this.tickDelay = ticksBetweenPlanting;
		this.plantables = plants;
		this.plantSupports = soils;
		this.canExecute = pred;
		this.checkSupports = (soils != null);
	}
	
	public EntityAIPlaceRandomBlocks(GolemBase golemBase, int ticksBetweenPlanting, IBlockState[] plants, Predicate<EntityAIPlaceRandomBlocks> p)
	{
		this(golemBase, ticksBetweenPlanting, plants, null, p);
	}
	
	@Override
	public boolean shouldExecute() 
	{
		return golem.world.rand.nextInt(tickDelay) == 0 && this.canExecute.test(this);
	}
	
	@Override
	public void startExecuting()
	{
		int x = MathHelper.floor(golem.posX);
		int y = MathHelper.floor(golem.posY - 0.20000000298023224D - (double)golem.getYOffset());
		int z = MathHelper.floor(golem.posZ);
		BlockPos below = new BlockPos(x, y, z);
		golem.world.getBlockState(below).getBlock();
		
		if(golem.world.isAirBlock(below.up(1)) && isPlantSupport(golem.world, below))
		{
			setToPlant(golem.world, below.up(1));
			return;
		}
	}
	
	@Override
	public boolean shouldContinueExecuting() 
	{
		return false;
	}
	
	public boolean setToPlant(World world, BlockPos pos)
	{
		IBlockState state = this.plantables[world.rand.nextInt(this.plantables.length)];
		return world.setBlockState(pos, state, 2);	
	}
	
	public boolean isPlantSupport(World world, BlockPos pos)
	{
		if(!this.checkSupports) return true;
		
		Block at = world.getBlockState(pos).getBlock();
		if(this.plantSupports != null && this.plantSupports.length > 0)
		{
			for(Block b : this.plantSupports)
			{
				if(at == b) return true;
			}
		}
		
		return false;
	}
}