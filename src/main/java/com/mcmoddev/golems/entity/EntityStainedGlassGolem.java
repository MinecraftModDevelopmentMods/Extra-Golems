package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntityStainedGlassGolem extends GolemMultiColorized {

	public static final String PREFIX = "stained_glass";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM + "_grayscale");

	public EntityStainedGlassGolem(final World world) {
		super(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM, world, TEXTURE_BASE, TEXTURE_OVERLAY, DYE_COLORS);
		this.setCanTakeFallDamage(true);
	}

	/**
	 * Whether {@link overlay} should be rendered as transparent. Is not called for rendering
	 * {@link base}
	 **/
	@Override
	public boolean hasTransparency() {
		return true;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		// use block type to give this golem the right texture
		// defaults to random color.
		final Block b = body.getBlock();
		byte textureNum;
		// check each type of stained glass
		if(b instanceof StainedGlassBlock) {
			final DyeColor color = ((StainedGlassBlock)b).getColor();
			textureNum = (byte)color.getId();
		} else {
			textureNum = (byte)this.rand.nextInt(DYE_COLORS.length);
		}
		// actually set the texture
		this.setTextureNum(textureNum);
	}
}
