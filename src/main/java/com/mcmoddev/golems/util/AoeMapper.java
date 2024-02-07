package com.mcmoddev.golems.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Used by {@link com.mcmoddev.golems.data.behavior.AoeBehavior}
 * and {@link GolemModifyBlocksEvent}
 * to replace block states in a large area
 */
@FunctionalInterface
public interface AoeMapper {
	/**
	 * Maps from one {@link BlockState} to another {@link BlockState}
	 *
	 * @param entity the entity that is causing the block modification
	 * @param pos    the BlockPos to modify
	 * @param input  the original BlockState
	 * @return the BlockState to replace the input BlockState
	 */
	BlockState map(@Nullable LivingEntity entity, final BlockPos pos, final BlockState input);
}
