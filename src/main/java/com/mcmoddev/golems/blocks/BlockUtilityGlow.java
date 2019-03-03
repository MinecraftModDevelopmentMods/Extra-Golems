package com.mcmoddev.golems.blocks;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockUtilityGlow extends BlockUtility {
	public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light", 0, 15);
	private final IBlockState REPLACE_WITH;
	private final int TICK_RATE;

	public BlockUtilityGlow(Material m, final float defaultLight, final int tickRate, final IBlockState replaceWith) {
		super(Properties.create(m).hardnessAndResistance(-1F).doesNotBlockMovement().needsRandomTick()
			.lightValue((int)defaultLight));
		int light = (int) (defaultLight * 15.0F);
		this.setDefaultState(this.stateContainer.getBaseState().with(LIGHT_LEVEL, light));
		this.TICK_RATE = tickRate;
		this.REPLACE_WITH = replaceWith;
	}
	//TODO: see BlockUtilityPower
	@Override
	public void randomTick(IBlockState state, World worldIn, BlockPos pos, Random random) {
		// make a slightly expanded AABB to check for the golem
		AxisAlignedBB toCheck = new AxisAlignedBB(pos).grow(0.5D);
		List<GolemBase> list = worldIn.getEntitiesWithinAABB(GolemBase.class, toCheck);
		boolean hasLightGolem = list != null && !list.isEmpty();
		for (GolemBase g : list) {
			hasLightGolem |= isLightGolem(g);
		}

		if (!hasLightGolem) {
			// remove this block
			worldIn.setBlockState(pos, REPLACE_WITH, 3);
		} else {
			// schedule another update
			worldIn.notifyNeighbors(pos, this);
		}
	}

	@Override
	public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
		worldIn.notifyNeighbors(pos, this);
	}

	@Override
	public int tickRate(IWorldReaderBase worldIn) {
		return TICK_RATE;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(LIGHT_LEVEL);
	}



	/**
	 * Search the golem's AI to determine if it is a light-providing golem
	 **/
	protected boolean isLightGolem(GolemBase golem) {
		for (EntityAITaskEntry entry : golem.tasks.taskEntries) {
			if (entry.action instanceof EntityAIPlaceSingleBlock &&
				((EntityAIPlaceSingleBlock) entry.action).stateToPlace.getBlock()
					instanceof BlockUtilityGlow /*TODO: CLEANUP!*/) {
				return true;
			}
		}
		return false;
	}
}
