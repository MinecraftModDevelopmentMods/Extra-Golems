package com.mcmoddev.golems.entity.ai;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class PlaceBlocksGoal extends Goal {

  public final GolemBase golem;
  public final int tickDelay;
  public final BlockState[] plantables;
  public final Block[] plantSupports;
  public final boolean checkSupports;
  public final Predicate<PlaceBlocksGoal> canExecute;

  public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting, final BlockState[] plants, final Block[] soils,
      final Predicate<PlaceBlocksGoal> pred) {
    this.golem = golemBase;
    this.tickDelay = Math.max(1, ticksBetweenPlanting);
    this.plantables = plants;
    this.plantSupports = soils;
    this.canExecute = pred;
    this.checkSupports = (soils != null && soils.length > 0);
  }

  public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting, final BlockState[] plants, final Predicate<PlaceBlocksGoal> p) {
    this(golemBase, ticksBetweenPlanting, plants, null, p);
  }

  public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting, final BlockState[] plants, @Nullable final Block[] soils,
      final boolean configAllows) {
    this(golemBase, ticksBetweenPlanting, plants, soils, (t -> configAllows));
  }

  public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting, final BlockState[] plants, final boolean configAllows) {
    this(golemBase, ticksBetweenPlanting, plants, null, configAllows);
  }

  @Override
  public boolean canUse() {
    return tickDelay > 0 && golem.getCommandSenderWorld().random.nextInt(tickDelay) == 0 && this.canExecute.test(this);
  }

  @Override
  public void start() {
    final BlockPos below = golem.getBlockBelow();
    final BlockPos in = below.above(1);

    if (golem.level.isEmptyBlock(in) && isPlantSupport(golem.level, below)) {
      setToPlant(golem.level, in);
    }
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }

  protected boolean setToPlant(final Level world, final BlockPos pos) {
    final BlockState state = this.plantables[world.random.nextInt(this.plantables.length)];
    return world.setBlock(pos, state, 2);
  }

  protected boolean isPlantSupport(final Level world, final BlockPos pos) {
    if (!this.checkSupports) {
      return true;
    }

    final Block at = world.getBlockState(pos).getBlock();
    if (this.plantSupports != null && this.plantSupports.length > 0) {
      for (final Block b : this.plantSupports) {
        if (at == b) {
          return true;
        }
      }
    }

    return false;
  }

  public static Predicate<PlaceBlocksGoal> getGriefingPredicate() {
    return t -> t.golem.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
  }
}
