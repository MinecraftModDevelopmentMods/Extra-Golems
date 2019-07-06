package com.mcmoddev.golems.entity.ai;

import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiPredicate;

import java.util.function.BiPredicate;

/**
 * Places a single BlockState every {@code tickDelay} ticks with certain conditions
 **/
public class PlaceUtilityBlockGoal extends Goal {

	public final GolemBase golem;
	public final BlockState stateToPlace;
	public final int tickDelay;
	public final boolean configAllows;
	public final BiPredicate<GolemBase, BlockState> predicate;

	/**
	 * @param golemIn        the GolemBase to use
	 * @param stateIn        the BlockState that will be placed every {@code interval} ticks
	 * @param interval       ticks between placing block
	 * @param cfgAllows      whether this AI is enabled by the config
	 * @param canReplacePred an optional BiPredicate to use when determining whether to place a Block.
	 *                       Defaults to replacing air only.
	 * @see #getDefaultBiPred(GolemBase, BlockState)
	 **/
	public PlaceUtilityBlockGoal(final GolemBase golemIn, final BlockState stateIn, final int interval,
			final boolean cfgAllows, final BiPredicate<GolemBase, BlockState> canReplacePred) {
		//this.setMutexFlags(EnumSet.of(Flag.MOVE));
		this.golem = golemIn;
		this.stateToPlace = stateIn;
		this.tickDelay = interval;
		this.configAllows = cfgAllows;
		this.predicate = canReplacePred;
	}

	/**
	 * Constructor that auto-generates a new Predicate where the only condition
	 * for replacing a block with this one is that the other block is air
	 *
	 * @param golemIn      the GolemBase to use
	 * @param stateIn      the BlockState that will be placed every {@code interval} ticks
	 * @param interval     ticks between placing block
	 * @param configAllows whether this AI is enabled by the config
	 **/
	public PlaceUtilityBlockGoal(final GolemBase golemIn, final BlockState stateIn, final int interval, boolean configAllows) {
		this(golemIn, stateIn, interval, configAllows, getDefaultBiPred(stateIn));
	}

	@Override
	public boolean shouldExecute() {
		return this.configAllows;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void tick() {
		long tickMod = this.golem.ticksExisted % this.tickDelay;
		if (this.configAllows && tickMod == (long) 0) {
			final int x = MathHelper.floor(golem.posX);
			final int y = MathHelper.floor(golem.posY - 0.20000000298023224D - golem.getYOffset());
			final int z = MathHelper.floor(golem.posZ);
			final BlockPos blockPosIn = new BlockPos(x, y, z);
			// test the predicate against each BlockPos in a vertical column around this golem
			// when it passes, place the block and return
			for (int i = 0; i < 3; i++) {
				BlockPos temp = blockPosIn.up(i);
				final BlockState cur = golem.getEntityWorld().getBlockState(temp);
				// if there's already a matching block, stop here
				if (cur.getBlock() == stateToPlace.getBlock()) {
					return;
				}
				if (this.predicate.test(golem, cur)) {
					this.golem.getEntityWorld().setBlockState(temp, getStateToPlace(cur), 2 | 4);
					return;
				}
			}
		}
	}

	@Override
	public void startExecuting() {
		this.tick();
	}

	public static boolean canBeWaterlogged(final BlockState stateIn) {
		return stateIn.getProperties().contains(BlockStateProperties.WATERLOGGED);
	}

	/**
	 * @return a state with Waterlogged set to True if applicable, otherwise returns the given state
	 **/
	public static BlockState getStateWaterlogged(final BlockState stateIn) {
		return canBeWaterlogged(stateIn) ? stateIn.with(BlockStateProperties.WATERLOGGED, true) : stateIn;
	}

	/**
	 * Builds a BiPredicate that returns True for either of two conditions:
	 * <br>1. If the current BlockState is Air
	 * <br>2. If the current BlockState is Water AND the replacement state
	 * can be Waterlogged
	 *
	 * @param stateIn the state that will replace the given one if possible
	 **/
	public static BiPredicate<GolemBase, BlockState> getDefaultBiPred(final BlockState stateIn) {
		final boolean canBeWaterlogged = canBeWaterlogged(stateIn);
		return (golem, toReplace) -> toReplace.getBlock() == Blocks.AIR
				|| (toReplace.getBlock() == Blocks.WATER && canBeWaterlogged
				&& toReplace.get(FlowingFluidBlock.LEVEL) == 0);
	}

	protected BlockState getStateToPlace(final BlockState toReplace) {
		return toReplace.getBlock() == Blocks.WATER ? getStateWaterlogged(stateToPlace) : stateToPlace;
	}
}
