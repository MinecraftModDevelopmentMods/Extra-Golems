package com.mcmoddev.golems.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.mcmoddev.golems.entity.IceGolem;

import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * This event exists for other mods or addons to handle and modify the Ice Golem's behavior. It
 * is not handled in Extra Golems.
 */
@Cancelable
public final class IceGolemFreezeEvent extends Event {

	protected List<BlockPos> affectedBlocks;
	protected Function<BlockState, BlockState> freezeFunction;

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

	public IceGolemFreezeEvent(final GolemBase golem, final BlockPos center, final int radius) {
		this(golem, center, radius, new DefaultFreezeFunction(
				golem.getRNG(), golem.getConfigBool(IceGolem.FROST), ICE_CHANCE, COBBLE_CHANCE));
	}

	public IceGolemFreezeEvent(final GolemBase golem, final BlockPos center,
			final int radius, final Function<BlockState, BlockState> function) {
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
		// check 3-layer circle around this golem (disc, not sphere) to add positions to the map
		for (int i = -range; i <= range; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -range; k <= range; k++) {
					final BlockPos currentPos = this.iceGolemPos.add(i, j, k);
					if (iceGolemPos.distanceSq(currentPos) <= maxDis) {
						final BlockState state = this.iceGolem.world.getBlockState(currentPos);
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
	 * Call this method to use a different function than the default one
	 * to determine which state should replace which blocks.
	 *
	 * @param toSet   the new {@code Function<BlockState, BlockState>}
	 * @param refresh when true, the event will call {@link #initAffectedBlockList(int)}
	 *                to refresh the list of affected blocks.
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

		public DefaultFreezeFunction(final Random randomIn, final boolean useFrost,
				final int iceChanceIn, final int cobbleChanceIn) {
			super();
			this.random = randomIn;
			this.frostedIce = useFrost;
			this.iceChance = iceChanceIn;
			this.cobbleChance = cobbleChanceIn;
		}

		@Override
		public BlockState apply(final BlockState input) {
			final BlockState cobbleState = Blocks.COBBLESTONE.getDefaultState();
			final BlockState iceState = this.frostedIce ? Blocks.FROSTED_ICE.getDefaultState()
					: Blocks.ICE.getDefaultState();
			final Material material = input.getMaterial();
			if (material.isLiquid()) {
				final Block block = input.getBlock();

				if (block == Blocks.WATER) {
					final boolean isNotPacked = this.frostedIce || this.random.nextInt(100) < this.iceChance;
					return isNotPacked ? iceState : Blocks.PACKED_ICE.getDefaultState();
				} else if (block == Blocks.LAVA) {
					final boolean isNotObsidian = this.random.nextInt(100) < this.cobbleChance;
					return isNotObsidian ? cobbleState : Blocks.OBSIDIAN.getDefaultState();
				} else if (block == Blocks.WATER) {
					return iceState;
				} else if (block == Blocks.LAVA) {
					return cobbleState;
				}
			}

			return input;
		}
	}
}
