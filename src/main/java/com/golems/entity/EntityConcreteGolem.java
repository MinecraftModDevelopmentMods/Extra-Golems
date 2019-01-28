package com.golems.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityConcreteGolem extends GolemColorizedMultiTextured {

	public static final String PREFIX = "concrete";
	public static final int[] COLOR_ARRAY = ItemDye.DYE_COLORS;
	
	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeGolemTexture(PREFIX + "_base");
		private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeGolemTexture(PREFIX + "_grayscale");
	
	public EntityConcreteGolem(final World world) {
		super(world, TEXTURE_BASE, TEXTURE_OVERLAY, COLOR_ARRAY);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block metadata to give this golem the right texture
		final int meta = body.getBlock().getMetaFromState(body)
			% this.getColorArray().length;
		this.setTextureNum((byte) (this.getColorArray().length - meta - 1));
	}
}
