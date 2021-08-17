package com.mcmoddev.golems.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Used by {@link com.mcmoddev.golems.entity.goal.AoeBlocksGoal}
 * and {@link com.mcmoddev.golems.events.GolemModifyBlocksEvent}
 * to replace block states in a large area
 */
@FunctionalInterface
public interface AoeFunction {
  /**
   * Maps from one BlockState to another BlockState
   * @param entity the entity that owns the AoeBlocksGoal
   * @param pos the BlockPos to modify
   * @param input the original BlockState
   * @return the BlockState to replace the given input
   */
  public BlockState map(final LivingEntity entity, final BlockPos pos, final BlockState input);
}
