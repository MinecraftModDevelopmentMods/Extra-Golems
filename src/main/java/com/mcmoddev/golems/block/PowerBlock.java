package com.mcmoddev.golems.block;

import java.util.List;
import java.util.Random;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class PowerBlock extends UtilityBlock {
  public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power", 0, 15);
  /* Default value for TICK_RATE. Not necessary to define through config. */
  public static final int UPDATE_TICKS = 4;

  public PowerBlock(final int powerLevel) {
	super(Properties.create(Material.GLASS).tickRandomly(), UPDATE_TICKS);
	this.setDefaultState(this.getDefaultState().with(POWER_LEVEL, powerLevel));
  }

  @Override
  public void tick(final BlockState state, final ServerWorld worldIn, final BlockPos pos, final Random random) {
	// make a slightly expanded AABB to check for the golem
	AxisAlignedBB toCheck = new AxisAlignedBB(pos).grow(0.25D);
	List<GolemBase> list = worldIn.getEntitiesWithinAABB(GolemBase.class, toCheck);
	boolean hasPowerGolem = !list.isEmpty() && hasPowerGolem(list);

	if (hasPowerGolem) {
	  // power golem is nearby, schedule another update
	  worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate);
	} else {
	  this.remove(worldIn, state, pos, 3);
	}
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
	super.fillStateContainer(builder);
	builder.add(POWER_LEVEL);
  }

  /**
   * "Implementing/overriding is fine."
   */
  @Override
  public int getWeakPower(final BlockState blockState, final IBlockReader blockAccess, final BlockPos pos, final Direction side) {
	return blockState.get(POWER_LEVEL);
  }

  @Override
  public int getStrongPower(final BlockState blockState, final IBlockReader blockAccess, final BlockPos pos, final Direction side) {
	return blockState.get(POWER_LEVEL);
  }

  /**
   * @return if the given list contains any golems for whom
   *         {@link GolemBase#isProvidingPower()} returns true
   **/
  public static boolean hasPowerGolem(final List<GolemBase> golems) {
	for (GolemBase g : golems) {
	  if (g.isProvidingPower()) {
		return true;
	  }
	}
	return false;
  }
}
