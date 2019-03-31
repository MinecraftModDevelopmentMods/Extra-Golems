package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityStainedGlassGolem extends GolemMultiColorized {

	public static final String PREFIX = "stained_glass";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM + "_grayscale");

	public EntityStainedGlassGolem(final World world) {
		super(EntityStainedGlassGolem.class, world, TEXTURE_BASE, TEXTURE_OVERLAY, dyeColorArray);
		this.setCanTakeFallDamage(true);
		this.setLootTableLoc("golem_stained_glass");
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
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// TODO
		// use block metadata to give this golem the right texture (defaults to last item of color array)
//		final int meta = body.getBlock().getMetaFromState(body)
//			% this.getColorArray().length;
//		this.setTextureNum((byte) (this.getColorArray().length - meta - 1));
	}
}
