package com.mcmoddev.golems.entity.ai;

import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.function.Predicate;

/**
 * Places a single IBlockState every {@code tickDelay} ticks with certain conditions
 **/
public class EntityAIPlaceSingleBlock extends EntityAIBase {

	public final GolemBase golem;
	public final IBlockState stateToPlace;
	public final int tickDelay;
	public final boolean configAllows;
	public final Predicate<IBlockState> predicate;

	/**
	 * @param golemIn        the GolemBase to use
	 * @param stateIn        the IBlockState that will be placed every {@code interval} ticks
	 * @param interval       ticks between placing block
	 * @param canReplacePred a Predicate to determine if {@code stateIn} should replace a certain IBlockState
	 **/
	public EntityAIPlaceSingleBlock(final GolemBase golemIn, final IBlockState stateIn, final int interval, final boolean cfgAllows, final Predicate<IBlockState> canReplacePred) {
		this.setMutexBits(8);
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
	 * @param golemIn  the GolemBase to use
	 * @param stateIn  the IBlockState that will be placed every {@code interval} ticks
	 * @param interval ticks between placing block
	 **/
	public EntityAIPlaceSingleBlock(final GolemBase golemIn, final IBlockState stateIn, final int interval, boolean configAllows) {
		this(golemIn, stateIn, interval, configAllows, toReplace -> toReplace.getMaterial().equals(Material.AIR) && !toReplace.getBlock().equals(stateIn.getBlock()));
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
		long tickMod = this.golem.getEntityWorld().getGameTime() % this.tickDelay;
		if (this.configAllows && tickMod == (long) 0) {
			final int x = MathHelper.floor(golem.posX);
			final int y = MathHelper.floor(golem.posY - 0.20000000298023224D - golem.getYOffset());
			final int z = MathHelper.floor(golem.posZ);
			final BlockPos blockPosIn = new BlockPos(x, y, z);
			// test the predicate against each BlockPos in a vertical column around this golem
			// when it passes, place the block and return
			for (int i = 0; i < 3; i++) {
				BlockPos temp = blockPosIn.up(i);
				if (this.predicate.test(golem.getEntityWorld().getBlockState(temp))) {
					this.golem.getEntityWorld().setBlockState(temp, this.stateToPlace, 2);
					return;
				}
			}
		}
	}

	@Override
	public void startExecuting() {
		this.tick();
	}
}
