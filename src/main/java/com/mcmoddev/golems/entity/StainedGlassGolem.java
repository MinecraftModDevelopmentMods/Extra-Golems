package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class StainedGlassGolem extends GolemMultiColorized {

	public static final String PREFIX = "stained_glass";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM + "_grayscale");

	public StainedGlassGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, TEXTURE_BASE, TEXTURE_OVERLAY, DYE_COLORS);
		this.enableFallDamage();
	}

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
		// uses HashMap to determine which texture this golem should apply
		// based on the top-middle building block. Defaults to a random texture.
		byte textureNum = GolemTextureBytes.getByBlock(GolemTextureBytes.GLASS, body.getBlock());
		this.setTextureNum(textureNum);
	}
	
	@Override
	public ItemStack getCreativeReturn(final RayTraceResult target) {
		return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.GLASS, (byte)this.getTextureNum()));
	}
}
