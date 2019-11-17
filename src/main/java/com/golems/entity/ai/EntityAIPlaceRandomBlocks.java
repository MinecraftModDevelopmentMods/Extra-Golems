package com.golems.entity.ai;

import java.util.function.Predicate;

import com.golems.entity.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * @see EntityAIPlaceRandomBlocksStrictly
 **/
public class EntityAIPlaceRandomBlocks extends EntityAIBase {

	public final GolemBase golem;
	public final int tickDelay;
	public final IBlockState[] plantables;
	public final Block[] plantSupports;
	public final boolean checkSupports;
	public final Predicate<EntityAIPlaceRandomBlocks> canExecute;

	public EntityAIPlaceRandomBlocks(final GolemBase golemBase, final int ticksBetweenPlanting,
					 final IBlockState[] plants, final Block[] soils, final Predicate<EntityAIPlaceRandomBlocks> pred) {
		this.setMutexBits(8);
		this.golem = golemBase;
		this.tickDelay = ticksBetweenPlanting;
		this.plantables = plants;
		this.plantSupports = soils;
		this.canExecute = pred;
		this.checkSupports = (soils != null);
	}

	public EntityAIPlaceRandomBlocks(final GolemBase golemBase, final int ticksBetweenPlanting,
					 final IBlockState[] plants, final Predicate<EntityAIPlaceRandomBlocks> p) {
		this(golemBase, ticksBetweenPlanting, plants, null, p);
	}

	@Override
	public boolean shouldExecute() {
		return golem.worldObj.rand.nextInt(tickDelay) == 0 && this.canExecute.test(this);
	}

	@Override
	public void startExecuting() {
		final BlockPos below = golem.getBlockBelow();
		final BlockPos in = below.up(1);
		
		if (golem.worldObj.isAirBlock(in) && isPlantSupport(golem.worldObj, below)) {
			setToPlant(golem.worldObj, in);
		}
	}

	@Override
	public boolean continueExecuting() {
		return false;
	}

	public boolean setToPlant(final World world, final BlockPos pos) {
		final IBlockState state = this.plantables[world.rand.nextInt(this.plantables.length)];
		return world.setBlockState(pos, state, 2);
	}

	public boolean isPlantSupport(final World world, final BlockPos pos) {
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
}
