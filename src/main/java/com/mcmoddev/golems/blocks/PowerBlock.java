package com.mcmoddev.golems.blocks;

import java.util.List;
import java.util.Random;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;

public class PowerBlock extends UtilityBlock {
  public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power", 0, 15);
  /* Default value for TICK_RATE. Not necessary to define through config. */
  public static final int UPDATE_TICKS = 4;

  public PowerBlock(final int powerLevel) {
    super(Properties.of(Material.GLASS).randomTicks(), UPDATE_TICKS);
    this.registerDefaultState(this.defaultBlockState().setValue(POWER_LEVEL, powerLevel));
  }

  @Override
  public void tick(final BlockState state, final ServerLevel worldIn, final BlockPos pos, final Random random) {
    // make a slightly expanded AABB to check for the entity
    AABB toCheck = new AABB(pos).inflate(0.25D);
    List<GolemBase> list = worldIn.getEntitiesOfClass(GolemBase.class, toCheck);
    boolean hasPowerGolem = !list.isEmpty() && hasPowerGolem(list);

    if (hasPowerGolem) {
      // power entity is nearby, schedule another update
      worldIn.getBlockTicks().scheduleTick(pos, this, this.tickRate);
    } else {
      this.remove(worldIn, state, pos, 3);
    }
  }

  @Override
  protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(POWER_LEVEL);
  }

  /**
   * "Implementing/overriding is fine."
   */
  @Override
  public int getSignal(final BlockState blockState, final BlockGetter blockAccess, final BlockPos pos, final Direction side) {
    return blockState.getValue(POWER_LEVEL);
  }

  @Override
  public int getDirectSignal(final BlockState blockState, final BlockGetter blockAccess, final BlockPos pos, final Direction side) {
    return blockState.getValue(POWER_LEVEL);
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
