package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class PlaceBlocksGoal extends Goal {

  protected final GolemBase golem;
  protected final int interval;
  protected final Block[] blocks;

  public PlaceBlocksGoal(final GolemBase golemBase, final int interval, final Block[] blocks) {
    this.golem = golemBase;
    this.interval = Math.max(1, interval);
    this.blocks = blocks;
  }

  @Override
  public boolean shouldExecute() {
    return !golem.isChild() && interval > 0 && golem.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)
        && golem.world.getRandom().nextInt(interval) == 0;
  }

  @Override
  public void startExecuting() {
    final BlockPos in = golem.getBlockBelow().up(1);
    final BlockState replace = golem.world.getBlockState(in);
    final BlockState below = golem.world.getBlockState(in.down());
    // only replace air or water-source above a solid face
    if ((replace.isAir() || (replace.getBlock() == Blocks.WATER  && replace.get(FlowingFluidBlock.LEVEL) == 0))
        && below.isSolidSide(golem.world, in.down(), Direction.UP)) {
      place(golem.world, replace, in);
    }
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }

  protected boolean place(final World world, final BlockState replace, final BlockPos pos) {
    BlockState state = this.blocks[world.getRandom().nextInt(this.blocks.length)].getDefaultState();
    // add waterlogged property if replacing water
    if(replace.getBlock() == Blocks.WATER && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
      state = state.with(BlockStateProperties.WATERLOGGED, true);
    }
    // check if the selected state is valid for this position
    if(state.isValidPosition(world, pos)) {
      return world.setBlockState(pos, state, 2);
    }
    return false;
  }
}
