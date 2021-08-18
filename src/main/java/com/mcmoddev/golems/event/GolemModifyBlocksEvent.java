package com.mcmoddev.golems.event;

import java.util.ArrayList;
import java.util.List;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event exists for other mods or addons to handle and modify
 * when the entity modifies a large number of blocks
 */
@Cancelable
public final class GolemModifyBlocksEvent extends Event {

  private List<BlockPos> affectedBlocks;
  private AoeFunction function;

  public final GolemBase entity;
  public final BlockPos center;
  public final int range;
  public final boolean sphere;

  /** This should be passed in World#setBlockState when using this event **/
  public int updateFlag;

  public GolemModifyBlocksEvent(final GolemBase golem, final BlockPos center, final int radius, final boolean sphere, final AoeFunction function) {
    this.setResult(Result.ALLOW);
    this.entity = golem;
    this.center = center;
    this.range = Math.min(radius, 16);
    this.sphere = sphere;
    this.updateFlag = 3;
    this.setFunction(function, true);
  }

  public void initAffectedBlockList(final int range) {
    this.affectedBlocks = new ArrayList<>(range * range * 2 * 4);
    final int maxDis = range * range;
    // check 3-layer circle around this entity (disc, not sphere)
    int dY = sphere ? range : 1;
    // map the blockstates
    BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
    for (int i = -range; i <= range; i++) {
      for (int j = -dY; j <= dY; j++) {
        for (int k = -range; k <= range; k++) {
          currentPos.setWithOffset(this.center, i, j, k);
          if (center.distSqr(currentPos) <= maxDis) {
            final BlockState state = this.entity.level.getBlockState(currentPos);
            final BlockState replace = this.function.map(entity, currentPos, state);
            if (replace != state) {
              this.affectedBlocks.add(currentPos);
            }
          }
        }
      }
    }
  }

  /** @return the current blockstate mapping function **/
  public AoeFunction getFunction() {
    return this.function;
  }

  /**
   * Call this method to use a different enabled than the default one to
   * determine which state should replace which blocks.
   *
   * @param toSet   the new {@code Function<BlockState, BlockState>}
   * @param refresh when true, the event will call
   *                {@link #initAffectedBlockList(int)} to refresh the list of
   *                affected blocks.
   **/
  public void setFunction(final AoeFunction toSet, final boolean refresh) {
    this.function = toSet;
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

  
}
