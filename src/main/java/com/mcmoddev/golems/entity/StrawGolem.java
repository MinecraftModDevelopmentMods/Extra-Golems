package com.mcmoddev.golems.entity;

import java.util.Random;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.StemBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class StrawGolem extends GolemBase {
	
	public static final String ALLOW_SPECIAL = "Allow Special: Crop Boost";
	public static final String SPECIAL_FREQ = "Crop Boost Frequency";

	public StrawGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		if(this.getConfigBool(ALLOW_SPECIAL)) {
			this.goalSelector.addGoal(3, new BoostCropGoal(this, 4, this.getConfigInt(SPECIAL_FREQ)));
		}
	}
	
	public static class BoostCropGoal extends Goal {
		protected final GolemBase golem;
		final int range;
		final int frequency;
		
		public BoostCropGoal(final GolemBase golemIn, final int rangeIn, final int freq) {
			golem = golemIn;
			range = rangeIn;
			frequency = freq + golem.getEntityWorld().getRandom().nextInt(Math.max(10, freq / 2));
		}

		@Override
		public boolean shouldExecute() {
			return golem.getEntityWorld().getRandom().nextInt(frequency) == 0;
		}
		
		@Override
		public void startExecuting() {
			tryBoostCrop();
		}
		
		/**
		 * Checks random blocks in a radius until
		 * either a growable crop has been found and
		 * boosted, or no crops were found in a limited
		 * number of attempts.
		 *
		 * @return if a crop was grown
		 **/
		private boolean tryBoostCrop() {
			final Random rand = this.golem.getEntityWorld().getRandom();
			final int maxAttempts = 25;
			final int variationY = 2;
			int attempts = 0;
			while (attempts <= maxAttempts) {
				// increment attempts
				++attempts;
				// get random block in radius
				final int x = MathHelper.floor(golem.posX);
				final int y = MathHelper.floor(golem.posY);
				final int z = MathHelper.floor(golem.posZ);
				final int x1 = rand.nextInt(this.range * 2) - this.range;
				final int y1 = rand.nextInt(variationY * 2) - variationY;
				final int z1 = rand.nextInt(this.range * 2) - this.range;
				final BlockPos blockpos = new BlockPos(x + x1, y + y1, z + z1);
				final BlockState state = golem.getEntityWorld().getBlockState(blockpos);
				// if the block can be grown, grow it and return
				if (state.getBlock() instanceof CropsBlock || state.getBlock() instanceof StemBlock) {
					IGrowable crop = (IGrowable) state.getBlock();
					if (crop.canGrow(golem.getEntityWorld(), blockpos, state, golem.getEntityWorld().isRemote)) {
						// grow the crop!
						crop.grow(golem.getEntityWorld(), rand, blockpos, state);
						// spawn particles
						//if (golem.getEntityWorld().isRemote) {
						//	BoneMealItem.spawnBonemealParticles(golem.getEntityWorld(), blockpos, 0);
						//}
						return true;
					}
				}
			}
			return false;
		}
	}
}
