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

public final class EntityStainedClayGolem extends GolemMultiColorized {

	public static final String PREFIX = "stained_clay";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDTERRACOTTA_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDTERRACOTTA_GOLEM + "_grayscale");

	public EntityStainedClayGolem(final World world) {
		super(EntityStainedClayGolem.class, world, TEXTURE_BASE, TEXTURE_OVERLAY, dyeColorArray);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block metadata to give this golem the right texture (defaults to last item of color array)
		// TODO
//		final int meta = body.getBlock().getMetaFromState(body)
//			% this.getColorArray().length;
//		this.setTextureNum((byte) (this.getColorArray().length - meta - 1));
	}
}
