package com.mcmoddev.golems.blocks;

import java.util.List;
import java.util.Random;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockUtilityGlow extends BlockUtility {

  public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light", 0, 15);
  /* Default value for TICK_RATE. Not necessary to define through config. */
  public static final int UPDATE_TICKS = 6;

  public BlockUtilityGlow(final Material m, final float defaultLight) {
    super(Properties.of(m).randomTicks().lightLevel(state -> state.getValue(LIGHT_LEVEL)), UPDATE_TICKS);
    int light = (int) (defaultLight * 15.0F);
    this.registerDefaultState(this.defaultBlockState().setValue(LIGHT_LEVEL, light));
  }

  @Override
  public void tick(final BlockState state, final ServerLevel worldIn, final BlockPos pos, final Random random) {
    // make a slightly expanded AABB to check for the golem
    final AABB toCheck = new AABB(pos).inflate(0.5D);
    // we'll probably only ever get one golem, but it doesn't hurt to be safe and
    // check them all
    final List<GolemBase> list = worldIn.getEntitiesOfClass(GolemBase.class, toCheck);
    boolean hasLightGolem = !list.isEmpty() && hasLightGolem(list);

    if (hasLightGolem) {
      // light golem is nearby, schedule another update
      worldIn.getBlockTicks().scheduleTick(pos, state.getBlock(), this.tickRate);
    } else {
      this.remove(worldIn, state, pos, 3);
    }
  }

  @Override
  protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
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
