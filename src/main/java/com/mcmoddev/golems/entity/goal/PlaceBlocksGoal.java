package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PlaceBlocksGoal extends Goal {

  protected final GolemBase golem;
  protected final int interval;
  protected final Block[] blocks;
  protected final Block[] supports;
  protected final boolean checkSupports;

  public PlaceBlocksGoal(final GolemBase golemBase, final int interval, final Block[] blocks, final Block[] supports) {
    this.golem = golemBase;
    this.interval = Math.max(1, interval);
    this.blocks = blocks;
    this.supports = supports;
    this.checkSupports = (supports != null && supports.length > 0);
  }

  @Override
  public boolean canUse() {
    return interval > 0 && golem.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) 
        && golem.getCommandSenderWorld().random.nextInt(interval) == 0;
  }

  @Override
  public void start() {
    final BlockPos below = golem.getBlockBelow();
    final BlockPos in = below.above(1);

    if (golem.level.isEmptyBlock(in) && isSupport(golem.level, below)) {
      place(golem.level, in);
    }
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }

  protected boolean place(final Level world, final BlockPos pos) {
    BlockState state = this.blocks[world.random.nextInt(this.blocks.length)].defaultBlockState();
    if(world.getBlockState(pos).getBlock() == Blocks.WATER) {
      state = getStateWaterlogged(state);
    }
    return world.setBlock(pos, state, 2);
  }

  protected boolean isSupport(final Level world, final BlockPos pos) {
    if (!this.checkSupports) {
      return true;
    }

    final Block at = world.getBlockState(pos).getBlock();
    if (this.supports != null && this.supports.length > 0) {
      for (final Block b : this.supports) {
        if (at == b) {
          return true;
        }
      }
    }

    return false;
  }
  
  /**
   * @param stateIn the original BlockState
   * @return true if the state can be waterlogged
   */
  public static boolean canBeWaterlogged(final BlockState stateIn) {
    return stateIn.hasProperty(BlockStateProperties.WATERLOGGED);
  }

  /**
   * @param stateIn the original BlockState
   * @return a state with Waterlogged set to True if applicable, otherwise returns the given state
   **/
  public static BlockState getStateWaterlogged(final BlockState stateIn) {
    return canBeWaterlogged(stateIn) ? stateIn.setValue(BlockStateProperties.WATERLOGGED, true) : stateIn;
  }
}
