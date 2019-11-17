package com.golems.entity;

import java.util.List;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityStrawGolem extends GolemBase {
	
	public static final String ALLOW_SPECIAL = "Allow Special: Crop Boost";
	public static final String SPECIAL_FREQ = "Crop Boost Frequency";
	private int range;
	private int boostFreq;
	private boolean allowed;

	public EntityStrawGolem(final World world) {
		super(world);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.STRAW_GOLEM);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
		this.boostFreq = getConfig(this).getInt(SPECIAL_FREQ);
		this.boostFreq += this.rand.nextInt(Math.max(10, this.boostFreq / 2));
		this.range = 4;
		this.allowed = getConfig(this).getBoolean(ALLOW_SPECIAL);
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		// look for crops to boost
		if(this.allowed && this.rand.nextInt(boostFreq) == 0) {
			tryBoostCrop();
		}
	}
	
	/**
	 * Checks random blocks in a radius until
	 * either a growable crop has been found and
	 * boosted, or no crops were found in a limited
	 * number of attempts.
	 * @return
	 **/
	private boolean tryBoostCrop() {
		final int maxAttempts = 25;
		final int variationY = 2;
		final BlockPos below = getBlockBelow();
		int attempts = 0;
		while(attempts <= maxAttempts) {
			// increment attempts
			++attempts;
			// get random block in radius
			final int x1 = this.rand.nextInt(this.range * 2) - this.range;
			final int y1 = this.rand.nextInt(variationY * 2) - variationY;
			final int z1 = this.rand.nextInt(this.range * 2) - this.range;
			final BlockPos blockpos = below.add(x1, y1, z1);
			final IBlockState state = this.getEntityWorld().getBlockState(blockpos);
			// if the block can be grown, grow it and return
			if(state.getBlock() instanceof BlockCrops || state.getBlock() instanceof BlockStem) {
				IGrowable crop = (IGrowable)state.getBlock();
				if(crop.canGrow(this.worldObj, blockpos, state, this.worldObj.isRemote)) {
					// grow the crop!
					crop.grow(this.worldObj, rand, blockpos, state);
					// spawn particles
					if(this.worldObj.isRemote) {
						ItemDye.spawnBonemealParticles(this.worldObj, blockpos, 0);
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
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(this.allowed) {
			String sCrops = TextFormatting.GREEN + trans("entitytip.grows_crops");
			list.add(sCrops);
		}
		return list;
	}
}
