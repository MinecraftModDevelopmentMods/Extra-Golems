package com.mcmoddev.golems.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import net.minecraftforge.eventbus.api.Event.Result;

/**
 * This event exists for other mods or addons to handle and modify the Ice
 * Golem's behavior. It is not handled in Extra Golems.
 */
@Cancelable
public final class IceGolemFreezeEvent extends Event {

  private List<BlockPos> affectedBlocks;
  private Function<BlockState, BlockState> freezeFunction;

  public final GolemBase iceGolem;
  public final BlockPos iceGolemPos;
  public final int range;

  /**
   * This percentage of Packed Ice placed will become regular ice instead.
   **/
  public static final int ICE_CHANCE = 52;
  /**
   * This percentage of Obsidian placed will become cobblestone instead.
   **/
  public static final int COBBLE_CHANCE = 29;

  /**
   * This should be passed in World#setBlockState when using this event.
   **/
  public int updateFlag;

  public IceGolemFreezeEvent(final GolemBase golem, final BlockPos center, final int radius, final boolean frostedIce) {
    this(golem, center, radius, new DefaultFreezeFunction(golem.getRandom(), frostedIce, ICE_CHANCE, COBBLE_CHANCE));
  }

  public IceGolemFreezeEvent(final GolemBase golem, final BlockPos center, final int radius, final Function<BlockState, BlockState> function) {
    this.setResult(Result.ALLOW);
    this.iceGolem = golem;
    this.iceGolemPos = center;
    this.range = radius;
    this.updateFlag = 3;
    this.setFunction(function, true);
  }

  public void initAffectedBlockList(final int range) {
    this.affectedBlocks = new ArrayList<>(range * range * 2 * 4);
    final int maxDis = range * range;
    // check 3-layer circle around this golem (disc, not sphere) to add positions to
    // the map
    for (int i = -range; i <= range; i++) {
      for (int j = -1; j <= 1; j++) {
        for (int k = -range; k <= range; k++) {
          final BlockPos currentPos = this.iceGolemPos.offset(i, j, k);
          if (iceGolemPos.distSqr(currentPos) <= maxDis) {
            final BlockState state = this.iceGolem.level.getBlockState(currentPos);
            final BlockState replace = this.freezeFunction.apply(state);
            if (replace != state) {
              this.affectedBlocks.add(currentPos);
            }
          }
        }
      }
    }
  }

  public Function<BlockState, BlockState> getFunction() {
    return this.freezeFunction;
  }

  /**
   * Call this method to use a different enabled than the default one to
   * determine which state should replace which blocks.
   *
   * @param toSet   the new {@code Function<BlockState, BlockState>}
   * @param refresh when true, the event will call
   *                {@link #initAffectedBlockList(int)} to refresh the list of
   *                affected blocks.
   * @see DefaultFreezeFunction
   **/
  public void setFunction(final Function<BlockState, BlockState> toSet, final boolean refresh) {
    this.freezeFunction = toSet;
    if (refresh) {
      this.initAffectedBlockList(this.range);
    }
  }

  public List<BlockPos> getAffectedPositions() {
    return this.affectedBlocks;
  }

  public boolean add(final BlockPos pos) {
    return this.affectedBlocks.add(pos);
  }

  public boolean remove(final BlockPos toRemove) {
    return this.affectedBlocks.remove(toRemove);
  }

  public static class DefaultFreezeFunction implements Function<BlockState, BlockState> {

    /**
     * Random instance.
     **/
    public final Random random;
    /**
     * This percentage of Packed Ice placed will become regular ice instead.
     **/
    public final int iceChance;
    /**
     * This percentage of Obsidian placed will become cobblestone instead.
     **/
    public final int cobbleChance;
    /**
     * When true, all water will turn to Frosted Ice
     **/
    public final boolean frostedIce;

    public DefaultFreezeFunction(final Random randomIn, final boolean useFrost, final int iceChanceIn, final int cobbleChanceIn) {
      super();
      this.random = randomIn;
      this.frostedIce = useFrost;
      this.iceChance = iceChanceIn;
      this.cobbleChance = cobbleChanceIn;
    }

    @Override
    public BlockState apply(final BlockState input) {
      final BlockState cobbleState = Blocks.COBBLESTONE.defaultBlockState();
      final BlockState iceState = this.frostedIce ? Blocks.FROSTED_ICE.defaultBlockState() : Blocks.ICE.defaultBlockState();
      final Material material = input.getMaterial();
      if (material.isLiquid()) {
        final Block block = input.getBlock();

        if (block == Blocks.WATER) {
          final boolean isNotPacked = this.frostedIce || this.random.nextInt(100) < this.iceChance;
          return isNotPacked ? iceState : Blocks.PACKED_ICE.defaultBlockState();
        } else if (block == Blocks.LAVA) {
          final boolean isNotObsidian = this.random.nextInt(100) < this.cobbleChance;
          return isNotObsidian ? cobbleState : Blocks.OBSIDIAN.defaultBlockState();
        }
      }

      return input;
    }
  }
}
