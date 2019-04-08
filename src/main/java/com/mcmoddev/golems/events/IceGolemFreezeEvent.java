package com.mcmoddev.golems.events;

import com.mcmoddev.golems.entity.EntityIceGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

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
	protected Function<IBlockState, IBlockState> freezeFunction;

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
				golem.getRNG(), golem.getConfigBool(EntityIceGolem.FROST), ICE_CHANCE, COBBLE_CHANCE));
	}

	public IceGolemFreezeEvent(final GolemBase golem, final BlockPos center, 
			final int radius, final Function<IBlockState, IBlockState> function) {
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
						final IBlockState state = this.iceGolem.world.getBlockState(currentPos);
						final IBlockState replace = this.freezeFunction.apply(state);
						if (replace != state) {
							this.affectedBlocks.add(currentPos);
						}
					}
				}
			}
		}
	}

	public Function<IBlockState, IBlockState> getFunction() {
		return this.freezeFunction;
	}

	/**
	 * Call this method to use a different function than the default one
	 * to determine which state should replace which blocks. 
	 * @param toSet the new {@code Function<IBlockState, IBlockState>}
	 * @param refresh when true, the event will call {@link #initAffectedBlockList(int)}
	 * to refresh the list of affected blocks.
	 * @see DefaultFreezeFunction
	 **/
	public void setFunction(final Function<IBlockState, IBlockState> toSet, final boolean refresh) {
		this.freezeFunction = toSet;
		if(refresh) {
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

	public static class DefaultFreezeFunction implements Function<IBlockState, IBlockState> {

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
		public IBlockState apply(final IBlockState input) {
			final IBlockState cobbleState = Blocks.COBBLESTONE.getDefaultState();
			final IBlockState iceState = this.frostedIce ? Blocks.FROSTED_ICE.getDefaultState() 
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
