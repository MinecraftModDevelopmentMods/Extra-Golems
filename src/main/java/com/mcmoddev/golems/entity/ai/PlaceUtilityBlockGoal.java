package com.mcmoddev.golems.entity.ai;

import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;

import java.util.function.BiPredicate;

import javax.annotation.Nullable;

/**
 * Places a single BlockState every {@code tickDelay} ticks with certain
 * conditions
 **/
public class PlaceUtilityBlockGoal extends Goal {

  public final GolemBase golem;
  public final BlockState stateToPlace;
  public final int tickDelay;
  public final BiPredicate<GolemBase, BlockPos> predicate;
  
  public static final BiPredicate<GolemBase, BlockPos> ABOVE_AIR_PRED = 
      (g, pos) -> g.getCommandSenderWorld().isEmptyBlock(pos.below());
  public static final BiPredicate<GolemBase, BlockPos> ABOVE_WATER_PRED = 
      (g, pos) -> g.getCommandSenderWorld().getBlockState(pos.below()).getBlock() == Blocks.WATER;


  /**
   * @param golemIn        the GolemBase to use
   * @param stateIn        the BlockState that will be placed every
   *                       {@code interval} ticks
   * @param interval       ticks between placing block
   * @param cfgAllows      whether this AI is enabled by the config
   * @param onlyAboveEmpty whether the utility block should not be placed directly on top of blocks
   * @param otherPredicate an optional BiPredicate to use when determining whether
   *                       to place a Block. Defaults to replacing air only.
   * @see #makeBiPred(BlockState, boolean)
   **/
  public PlaceUtilityBlockGoal(final GolemBase golemIn, final BlockState stateIn, 
      final int interval, final boolean onlyAboveEmpty,
      @Nullable final BiPredicate<GolemBase, BlockPos> otherPredicate) {
    // this.setMutexFlags(EnumSet.of(Flag.MOVE));
    this.golem = golemIn;
    this.stateToPlace = stateIn;
    this.tickDelay = interval;
    // build the predicate that will be used to verify block placement
    final BiPredicate<GolemBase, BlockPos> pred = makeBiPred(stateIn, onlyAboveEmpty);
    this.predicate = otherPredicate != null ? pred.and(otherPredicate) : pred;
  }

  /**
   * Constructor that auto-generates a new Predicate where the only condition for
   * replacing a block with this one is that the other block is air
   *
   * @param golemIn      the GolemBase to use
   * @param stateIn      the BlockState that will be placed every {@code interval}
   *                     ticks
   * @param interval     ticks between placing block
   * @param configAllows whether this AI is enabled by the config
   **/
  public PlaceUtilityBlockGoal(final GolemBase golemIn, final BlockState stateIn, final int interval) {
    this(golemIn, stateIn, interval, false, null);
  }

  @Override
  public boolean canUse() {
    return true;
  }

  /**
   * Keep ticking a continuous task that has already been started
   */
  @Override
  public void tick() {
    if ((this.golem.tickCount % this.tickDelay) == 0) {
      final BlockPos blockPosIn = golem.getBlockBelow().above();
      // test the predicate against each BlockPos in a vertical column
      // when it passes, place the block and return
      for (int i = 0; i < 4; i++) {
        BlockPos pos = blockPosIn.above(i);
        final BlockState cur = golem.getCommandSenderWorld().getBlockState(pos);
        // if there's already a matching block, stop here
        if (cur.getBlock() == stateToPlace.getBlock()) {
          return;
        }
        if (this.predicate.test(golem, pos)) {
          this.golem.getCommandSenderWorld().setBlock(pos, getStateToPlace(cur), 2 | 4);
          return;
        }
      }
    }
  }

  @Override
  public void start() {
    this.tick();
  }

  public static boolean canBeWaterlogged(final BlockState stateIn) {
    return stateIn.hasProperty(BlockStateProperties.WATERLOGGED);
  }

  /**
   * @return a state with Waterlogged set to True if applicable, otherwise returns
   *         the given state
   **/
  public static BlockState getStateWaterlogged(final BlockState stateIn) {
    return canBeWaterlogged(stateIn) ? stateIn.setValue(BlockStateProperties.WATERLOGGED, true) : stateIn;
  }

  /**
   * Builds a BiPredicate that returns True for either of two conditions: <br>
   * 1. If the current BlockState is Air <br>
   * 2. If the current BlockState is Water AND the replacement state can be
   * Waterlogged
   *
   * @param stateIn the state that will replace the given one if possible
   * @param onlyAboveEmpty if there should always be an air/water block underneath
   * the placed block
   * @return a new BiPredicate<GolemBase, BlockPos>
   **/
  public static BiPredicate<GolemBase, BlockPos> makeBiPred(final BlockState stateIn, final boolean onlyAboveEmpty) {
    // whether or not the utility block can be waterlogged
    final boolean canBeWaterlogged = canBeWaterlogged(stateIn);
    // the main purpose of the predicate is to make sure the block can be replaced
    BiPredicate<GolemBase, BlockPos> pred = (golem, pos) -> golem.getCommandSenderWorld().isEmptyBlock(pos);
    // if the utility block can be waterlogged, allow replacing water as well as air
    if(canBeWaterlogged) {
      pred = pred.or((golem, pos) -> {
        final BlockState toReplace = golem.getCommandSenderWorld().getBlockState(pos);
        return toReplace.getBlock() == Blocks.WATER && toReplace.getValue(LiquidBlock.LEVEL) == 0;
      });
    }
    // if there must be an empty (air or water) block below the utility block, check for that
    if(onlyAboveEmpty) {
      pred = canBeWaterlogged ? pred.and(ABOVE_WATER_PRED.or(ABOVE_AIR_PRED)) : pred.and(ABOVE_AIR_PRED);
    }
    return pred;
  }

  protected BlockState getStateToPlace(final BlockState toReplace) {
    return toReplace.getBlock() == Blocks.WATER ? getStateWaterlogged(stateToPlace) : stateToPlace;
  }
}
