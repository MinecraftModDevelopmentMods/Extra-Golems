package com.mcmoddev.golems.blocks;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.init.Blocks;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
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
	private final int TICK_RATE;

	public BlockUtilityPower(final int powerLevel, final int tickRate) {
		super(Properties.create(Material.GLASS).hardnessAndResistance(-1F).doesNotBlockMovement()
			.needsRandomTick());
		this.setDefaultState(this.stateContainer.getBaseState().with(POWER_LEVEL, powerLevel));
		TICK_RATE = tickRate;
	}

	@Override
	public void randomTick(IBlockState state, World worldIn, BlockPos pos, Random random) {
		// make a slightly expanded AABB to check for the golem
		AxisAlignedBB toCheck = new AxisAlignedBB(pos).grow(0.5D);
		List<GolemBase> list = worldIn.getEntitiesWithinAABB(GolemBase.class, toCheck);
		boolean hasPowerGolem = !list.isEmpty();
		for (GolemBase g : list) {
			//TODO: Simplify
			hasPowerGolem |= isPowerGolem(g);
		}
		if (!hasPowerGolem) {
			// remove this block
			worldIn.setBlockState(pos, REPLACE_WITH, 3);
		} else {
			// schedule another update
			//TODO: Ensure this works properly
			worldIn.notifyNeighborsOfStateChange(pos, this);
			//worldIn.scheduleUpdate(pos, this, TICK_RATE);
		}
	}

	@Override
	public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
		worldIn.notifyNeighbors(pos, this);
	}

//	/**
//	 * Called after the block is set in the Chunk data, but before the Tile Entity is set
//	 */
//	@Override
//	public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
//		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
//	}

	@Override
	public int tickRate(IWorldReaderBase worldIn) {
		return TICK_RATE;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
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
	 * Search the golem's AI to determine if it is a light-providing golem
	 **/
	protected boolean isPowerGolem(GolemBase golem) {
		for (EntityAITaskEntry entry : golem.tasks.taskEntries) {
			if (entry.action instanceof EntityAIPlaceSingleBlock && ((EntityAIPlaceSingleBlock) entry.action
			).stateToPlace.getBlock() instanceof BlockUtilityPower) {
				return true;
			}
		}
		return false;
	}
}
