package com.mcmoddev.golems.blocks;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockUtilityPower extends BlockUtility {
	public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power", 0, 15);
	private static final IBlockState REPLACE_WITH = Blocks.AIR.getDefaultState();
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
	 * {@link GolemBase#doesProvidePower()} returns true
	 **/
	protected boolean hasPowerGolem(final List<GolemBase> golems) {
		for(GolemBase g : golems) {
			if(g.doesProvidePower()) {
				return true;
			}
		}
		return false;
	}
}
