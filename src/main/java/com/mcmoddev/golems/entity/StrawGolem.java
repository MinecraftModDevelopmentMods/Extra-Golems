package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.StemBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.BoneMealItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class StrawGolem extends GolemBase {
	
	public static final String ALLOW_SPECIAL = "Allow Special: Crop Boost";
	public static final String SPECIAL_FREQ = "Crop Boost Frequency";
	private int range;
	private int boostFreq;
	private boolean allowed;

	public StrawGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.enableSwim();
		this.boostFreq = this.getConfigInt(SPECIAL_FREQ);
		this.boostFreq += this.rand.nextInt(Math.max(10, this.boostFreq / 2));
		this.range = 4;
		this.allowed = this.getConfigBool(ALLOW_SPECIAL);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// look for crops to boost
		if (this.allowed && this.rand.nextInt(boostFreq) == 0) {
			tryBoostCrop();
		}
	}

	/**
	 * Checks random blocks in a radius until
	 * either a growable crop has been found and
	 * boosted, or no crops were found in a limited
	 * number of attempts.
	 *
	 * @return
	 **/
	private boolean tryBoostCrop() {
		final int maxAttempts = 25;
		final int variationY = 2;
		int attempts = 0;
		while (attempts <= maxAttempts) {
			// increment attempts
			++attempts;
			// get random block in radius
			final int x = MathHelper.floor(this.posX);
			final int y = MathHelper.floor(this.posY);
			final int z = MathHelper.floor(this.posZ);
			final int x1 = this.rand.nextInt(this.range * 2) - this.range;
			final int y1 = this.rand.nextInt(variationY * 2) - variationY;
			final int z1 = this.rand.nextInt(this.range * 2) - this.range;
			final BlockPos blockpos = new BlockPos(x + x1, y + y1, z + z1);
			final BlockState state = this.getEntityWorld().getBlockState(blockpos);
			// if the block can be grown, grow it and return
			if (state.getBlock() instanceof CropsBlock || state.getBlock() instanceof StemBlock) {
				IGrowable crop = (IGrowable) state.getBlock();
				if (crop.canGrow(this.world, blockpos, state, this.world.isRemote)) {
					// grow the crop!
					crop.grow(this.world, rand, blockpos, state);
					// spawn particles
					if (this.world.isRemote) {
						BoneMealItem.spawnBonemealParticles(this.world, blockpos, 0);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.STRAW_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
}
