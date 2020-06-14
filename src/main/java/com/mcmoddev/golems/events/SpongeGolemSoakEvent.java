package com.mcmoddev.golems.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event exists for other mods or addons to handle and modify the Sponge
 * Golem's behavior. It is not handled in Extra Golems. To modify which blocks
 * count as 'water' you must call {@link #setAbsorbFunction(Function, boolean)}
 * and {@link #initAffectedBlockList(int)}, in that order.
 */
@Cancelable
public final class SpongeGolemSoakEvent extends Event {

  protected List<BlockPos> affectedBlocks;
  protected Function<BlockState, BlockState> absorbFunction;

  public final GolemBase spongeGolem;
  public final BlockPos spongeGolemPos;
  public final int range;

  /**
   * This will be passed in World#setBlockState.
   **/
  public int updateFlag = 3;

  public SpongeGolemSoakEvent(final GolemBase golem, final BlockPos center, final int radius) {
    this(golem, center, radius,
        (state) -> state.has(BlockStateProperties.WATERLOGGED) ? state.with(BlockStateProperties.WATERLOGGED, false)
            : (state.getMaterial() == Material.WATER || state.getBlock() == Blocks.KELP || state.getBlock() == Blocks.KELP_PLANT
                || state.getBlock() == Blocks.SEAGRASS || state.getBlock() == Blocks.TALL_SEAGRASS ? Blocks.AIR.getDefaultState() : state));
  }

  public SpongeGolemSoakEvent(final GolemBase golem, final BlockPos center, final int radius, final Function<BlockState, BlockState> function) {
    this.setResult(Event.Result.ALLOW);
    this.spongeGolem = golem;
    this.spongeGolemPos = center;
    this.range = radius;
    this.setAbsorbFunction(function, true);
  }

  public void initAffectedBlockList(final int range) {
    this.affectedBlocks = new ArrayList<>(range * range * range * 4);
    final int MAX_DIS = range * range;
    // check sphere around golem to absorb water
    for (int i = -range; i <= range; i++) {
      for (int j = -range; j <= range; j++) {
        for (int k = -range; k <= range; k++) {
          final BlockPos current = this.spongeGolemPos.add(i, j, k);
          if (spongeGolemPos.distanceSq(current) <= MAX_DIS) {
            final BlockState state = this.spongeGolem.world.getBlockState(current);
            final BlockState replace = this.absorbFunction.apply(state);
            if (replace != null && replace != state) {
              this.affectedBlocks.add(current);
            }
          }
        }
      }
    }
  }

  public List<BlockPos> getPositionList() {
    return this.affectedBlocks;
  }

  public Function<BlockState, BlockState> getAbsorbFunction() {
    return this.absorbFunction;
  }

  /**
   * Call this method to use a different enabled than the default one to
   * determine which state should replace which blocks.
   *
   * @param function the new {@code Function<BlockState, BlockState>}
   * @param refresh  when true, the event will call
   *                 {@link #initAffectedBlockList(int)} to refresh the list of
   *                 affected blocks.
   **/
  public void setAbsorbFunction(final Function<BlockState, BlockState> function, final boolean refresh) {
    this.absorbFunction = function;
    if (refresh) {
      this.initAffectedBlockList(this.range);
    }
  }

  public boolean removeBlockPos(final BlockPos toRemove) {
    return this.affectedBlocks.remove(toRemove);
  }
}
