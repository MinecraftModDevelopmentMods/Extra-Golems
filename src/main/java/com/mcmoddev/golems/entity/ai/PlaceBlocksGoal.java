package com.mcmoddev.golems.entity.ai;

import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @see EntityAIPlaceRandomBlocksStrictly
 **/
public class PlaceBlocksGoal extends Goal {

	public final GolemBase golem;
	public final int tickDelay;
	public final BlockState[] plantables;
	public final Block[] plantSupports;
	public final boolean checkSupports;
	public final Predicate<PlaceBlocksGoal> canExecute;

	public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting,
			final BlockState[] plants, final Block[] soils, final Predicate<PlaceBlocksGoal> pred) {
		//this.setMutexFlags(EnumSet.of(Flag.MOVE));
		this.golem = golemBase;
		this.tickDelay = Math.max(1, ticksBetweenPlanting);
		this.plantables = plants;
		this.plantSupports = soils;
		this.canExecute = pred;
		this.checkSupports = (soils != null && soils.length > 0);
	}

	public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting,
			final BlockState[] plants, final Predicate<PlaceBlocksGoal> p) {
		this(golemBase, ticksBetweenPlanting, plants, null, p);
	}

	public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting,
			final BlockState[] plants, @Nullable final Block[] soils, final boolean configAllows) {
		this(golemBase, ticksBetweenPlanting, plants, soils, (t -> configAllows));
	}

	public PlaceBlocksGoal(final GolemBase golemBase, final int ticksBetweenPlanting,
			final BlockState[] plants, final boolean configAllows) {
		this(golemBase, ticksBetweenPlanting, plants, null, configAllows);
	}

	@Override
	public boolean shouldExecute() {
		return tickDelay > 0 && golem.getEntityWorld().rand.nextInt(tickDelay) == 0 && this.canExecute.test(this);
	}

	@Override
	public void startExecuting() {
		final int x = MathHelper.floor(golem.posX);
		final int y = MathHelper.floor(golem.posY - 0.20000000298023224D);
		final int z = MathHelper.floor(golem.posZ);
		final BlockPos below = new BlockPos(x, y, z);
		final BlockPos in = below.up(1);

		if (golem.world.isAirBlock(in) && isPlantSupport(golem.world, below)) {
			setToPlant(golem.world, in);
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return false;
	}

	protected boolean setToPlant(final World world, final BlockPos pos) {
		final BlockState state = this.plantables[world.rand.nextInt(this.plantables.length)];
		return world.setBlockState(pos, state, 2);
	}

	protected boolean isPlantSupport(final World world, final BlockPos pos) {
		if (!this.checkSupports) {
			return true;
		}

		final Block at = world.getBlockState(pos).getBlock();
		if (this.plantSupports != null && this.plantSupports.length > 0) {
			for (final Block b : this.plantSupports) {
				if (at == b) {
					return true;
				}
			}
		}

		return false;
	}

	public static Predicate<PlaceBlocksGoal> getGriefingPredicate() {
		return t -> t.golem.world.getGameRules().func_223586_b(GameRules.field_223599_b); //.getBoolean("mobGriefing");
	}
}
