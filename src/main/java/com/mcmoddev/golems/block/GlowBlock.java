package com.mcmoddev.golems.block;

import java.util.List;
import java.util.Random;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class GlowBlock extends UtilityBlock {

  public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light", 0, 15);
  /* Default value for TICK_RATE. Not necessary to define through config. */
  public static final int UPDATE_TICKS = 6;

  public GlowBlock(final Material m, final float defaultLight) {
	super(AbstractBlock.Properties.create(m).tickRandomly().setLightLevel(state -> state.get(LIGHT_LEVEL)), UPDATE_TICKS);
	int light = (int) (defaultLight * 15.0F);
	this.setDefaultState(this.getDefaultState().with(LIGHT_LEVEL, light));
  }

  @Override
  public void tick(final BlockState state, final ServerWorld worldIn, final BlockPos pos, final Random random) {
	// make a slightly expanded AABB to check for the golem
	final AxisAlignedBB toCheck = new AxisAlignedBB(pos).grow(0.5D);
	// we'll probably only ever get one golem, but it doesn't hurt to be safe and
	// check them all
	final List<GolemBase> list = worldIn.getEntitiesWithinAABB(GolemBase.class, toCheck);
	boolean hasLightGolem = !list.isEmpty() && hasLightGolem(list);

	if (hasLightGolem) {
	  // light golem is nearby, schedule another update
	  worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), this.tickRate);
	} else {
	  this.remove(worldIn, state, pos, 3);
	}
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
	super.fillStateContainer(builder);
	builder.add(LIGHT_LEVEL);
  }

  /**
   * @return if the given list contains any golems for whom
   *         {@link GolemBase#isProvidingLight()} returns true
   **/
  public static boolean hasLightGolem(final List<GolemBase> golems) {
	for (GolemBase g : golems) {
	  if (g.isProvidingLight()) {
		return true;
	  }
	}
	return false;
  }
}
