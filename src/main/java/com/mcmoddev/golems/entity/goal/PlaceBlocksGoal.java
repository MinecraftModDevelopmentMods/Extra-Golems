package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
  public boolean canUse() {
    return !golem.isBaby() && interval > 0 && golem.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) 
        && golem.getRandom().nextInt(interval) == 0;
  }

  @Override
  public void start() {
    final BlockPos in = golem.getBlockBelow().above(1);
    final BlockState replace = golem.level.getBlockState(in);
    final BlockState below = golem.level.getBlockState(in.below());
    // only replace air or water-source above a solid face
    if ((replace.isAir() || (replace.getBlock() == Blocks.WATER  && replace.getValue(LiquidBlock.LEVEL) == 0)) 
        && below.isFaceSturdy(golem.level, in.below(), Direction.UP, SupportType.FULL)) {
      place(golem.level, replace, in);
    }
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }

  protected boolean place(final Level world, final BlockState replace, final BlockPos pos) {
    BlockState state = this.blocks[world.random.nextInt(this.blocks.length)].defaultBlockState();
    // add waterlogged property if replacing water
    if(replace.getBlock() == Blocks.WATER && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
      state = state.setValue(BlockStateProperties.WATERLOGGED, true);
    }
    // check if the selected state is valid for this position
    if(state.canSurvive(world, pos)) {
      return world.setBlock(pos, state, 2);
    }
    return false;
  }
}
