package com.golems.blocks;

import java.util.List;
import java.util.Random;

import com.golems.entity.EntityRedstoneGolem;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUtilityPower extends BlockUtility
{
	public static final PropertyInteger POWER_LEVEL = PropertyInteger.create("power", 0, 15);
	private static final IBlockState REPLACE_WITH = Blocks.AIR.getDefaultState();
	private final int TICK_RATE;
	
	public BlockUtilityPower(final int powerLevel, final int tickRate) {
		super();
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(POWER_LEVEL, powerLevel));
		TICK_RATE = tickRate;
	}
	
	@Override
	public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
		// make a slightly expanded AABB to check for the golem
		AxisAlignedBB toCheck = new AxisAlignedBB(pos).grow(0.5D);
		List<EntityRedstoneGolem> list = worldIn.getEntitiesWithinAABB(EntityRedstoneGolem.class, toCheck);
		if(list == null || list.isEmpty()) {
			// remove this block
			worldIn.setBlockState(pos, REPLACE_WITH, 3);
		}
		else {
			// schedule another update
			worldIn.scheduleUpdate(pos, this, TICK_RATE);
		}
    }
	
	/**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
	@Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
    	worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
	
    @Override
	public int tickRate(final World worldIn) {
        return TICK_RATE;
    }
	
	@Override
	public boolean requiresUpdates() {
		return true;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { BlockUtilityPower.POWER_LEVEL } );
	}
	
	/** Convert the given metadata into a BlockState for this Block **/
    public IBlockState getStateFromMeta(final int metaIn) {
    	int meta = metaIn;
    	if(meta < 0)
    		meta = 0;
    	if(meta > 15)
    		meta = 15;
        return this.getDefaultState().withProperty(POWER_LEVEL, meta);
    }

    /** Convert the BlockState into the correct metadata value **/
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(POWER_LEVEL).intValue();
    }
	
	@Override
	public int getWeakPower(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos pos, final EnumFacing side) {
        return blockState.getValue(POWER_LEVEL);
    }
	
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(POWER_LEVEL);
    }
}
