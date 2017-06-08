package com.golems.blocks;

import java.util.List;

import com.golems.entity.GolemLightProvider;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityMovingLightSource extends TileEntity implements ITickable
{    
	protected AxisAlignedBB aabb = Block.NULL_AABB;
	
	public TileEntityMovingLightSource() {}

	/**
	 * This controls whether the tile entity gets replaced whenever the block state 
	 * is changed. Normally only want this when block actually is replaced.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public void update()
	{
		List<GolemLightProvider> entityList = world.getEntitiesWithinAABB(GolemLightProvider.class, this.getAABBToCheck(this.world, this.getPos()));
		
		// if no golem was found, delete this tile entity and block
		if(entityList.isEmpty())
		{
			if(world.getBlockState(getPos()).getBlock() instanceof BlockLightProvider)
			{
				selfDestruct();
			}
		}
	} 
	
	protected void selfDestruct()
	{
		world.removeTileEntity(getPos());
		world.setBlockState(getPos(), Blocks.AIR.getDefaultState(), 3);
	}

	protected AxisAlignedBB getAABBToCheck(World worldIn, BlockPos pos)
	{
		if(this.aabb == Block.NULL_AABB)
    	{
    		this.aabb = new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)pos.getX() + 1D, (double)pos.getY() + 1D, (double)pos.getZ() + 1D);
    	}
    	return this.aabb;	
    }
}
