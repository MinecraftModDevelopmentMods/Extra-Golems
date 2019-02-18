package com.mcmoddev.golems.events;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This event exists for other mods or addons to handle and modify the Sponge Golem's behavior. It
 * is not handled in Extra Golems. To modify which blocks count as 'water' you must call
 * {@link #setWaterPredicate(Predicate)} and {@link #initAffectedBlockList(int)}, in that order. You
 * can 'add' your liquid to the current predicate by passing
 * {@code SpongeGolemSoakEvent#getWaterPredicate().and(yourPredicate)} to
 * {@link #setWaterPredicate(Predicate)}
 */
@Cancelable
public final class SpongeGolemSoakEvent extends Event {

	protected List<BlockPos> affectedBlocks;
	protected Predicate<IBlockState> waterPredicate;

	public final GolemBase spongeGolem;
	public final BlockPos spongeGolemPos;
	public final int range;

	protected IBlockState replacesWater;
	/**
	 * This will be passed in World#setBlockState.
	 **/
	public int updateFlag = 3;

	public SpongeGolemSoakEvent(final GolemBase golem, final BlockPos center, final int radius) {
		this.setResult(Event.Result.ALLOW);
		this.spongeGolem = golem;
		this.spongeGolemPos = center;
		this.range = radius;
		this.setReplacementState(Blocks.AIR.getDefaultState());
		this.setWaterPredicate(state -> state.getMaterial() == Material.WATER
			|| state.getBlock() == Blocks.WATER);

		initAffectedBlockList(radius);
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
						final IBlockState state = this.spongeGolem.world.getBlockState(current);
						if (this.waterPredicate.test(state)) {
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

	public Predicate<IBlockState> getWaterPredicate() {
		return this.waterPredicate;
	}

	public void setWaterPredicate(final Predicate<IBlockState> waterPred) {
		this.waterPredicate = waterPred;
	}

	/**
	 * Sets the IBlockState that will replace water when this event is finalized.
	 **/
	public void setReplacementState(final IBlockState toReplaceWater) {
		this.replacesWater = toReplaceWater;
	}

	public IBlockState getReplacementState() {
		return this.replacesWater;
	}

	public boolean removeBlockPos(final BlockPos toRemove) {
		return this.affectedBlocks.remove(toRemove);
	}
}
