package com.mcmoddev.golems.entity.ai;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class EntityAIPlaceRandomBlocksStrictly extends EntityAIPlaceRandomBlocks {

	public EntityAIPlaceRandomBlocksStrictly(final GolemBase golemBase, final int ticksBetweenPlanting,
						 final BlockState[] plants, @Nullable final Block[] soils, final boolean configAllows) {
		super(golemBase, ticksBetweenPlanting, plants, soils, (t -> configAllows));
	}

	public EntityAIPlaceRandomBlocksStrictly(final GolemBase golemBase, final int ticksBetweenPlanting,
						 final BlockState[] plants, final boolean configAllows) {
		this(golemBase, ticksBetweenPlanting, plants, null, configAllows);
	}

	public static Predicate<EntityAIPlaceRandomBlocks> getGriefingPredicate() {
		return t -> t.golem.world.getGameRules().getBoolean("mobGriefing");
	}
}
