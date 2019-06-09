package com.mcmoddev.golems.blocks;

import java.util.List;
import java.util.Random;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockUtilityPower extends BlockUtility {
	public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power", 0, 15);
	/* Default value for TICK_RATE. Not necessary to define through config. */
	public static final int UPDATE_TICKS = 4;

	public BlockUtilityPower(final int powerLevel, final int tickRate) {
		super(Properties.create(Material.GLASS).needsRandomTick(), tickRate);
		this.setDefaultState(this.getDefaultState().with(POWER_LEVEL, powerLevel));
	}

	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
		// make a slightly expanded AABB to check for the golem
		AxisAlignedBB toCheck = new AxisAlignedBB(pos).grow(0.5D);
		List<GolemBase> list = worldIn.getEntitiesWithinAABB(GolemBase.class, toCheck);
		boolean hasPowerGolem = list != null && !list.isEmpty() && hasPowerGolem(list);

		if (hasPowerGolem) {
			// light golem is nearby, schedule another update
			worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
		} else {
			this.remove(worldIn, state, pos, 3);
		}	
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(POWER_LEVEL);
	}

	/**
	 * "Implementing/overriding is fine."
	 */
	@Override
	public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.get(POWER_LEVEL);
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.get(POWER_LEVEL);
	}
	
	/**
	 * @return if the given list contains any golems for whom 
	 * {@link GolemBase#isProvidingPower()} returns true
	 **/
	public static boolean hasPowerGolem(final List<GolemBase> golems) {
		for(GolemBase g : golems) {
			if(g.isProvidingPower()) {
				return true;
			}
		}
		return false;
	}
}
