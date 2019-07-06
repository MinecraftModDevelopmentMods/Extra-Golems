package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class StainedTerracottaGolem extends GolemMultiColorized {

	public static final String PREFIX = "stained_clay";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDTERRACOTTA_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDTERRACOTTA_GOLEM + "_grayscale");

	public StainedTerracottaGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, TEXTURE_BASE, TEXTURE_OVERLAY, DYE_COLORS);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2D);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	@Override
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		// use block type to give this golem the right texture
		// defaults to random color.
		final Block b = body.getBlock();
		byte textureNum;
		// check each type of stained glass
		if (b == Blocks.WHITE_TERRACOTTA) textureNum = 0;
		else if (b == Blocks.ORANGE_TERRACOTTA) textureNum = 1;
		else if (b == Blocks.MAGENTA_TERRACOTTA) textureNum = 2;
		else if (b == Blocks.LIGHT_BLUE_TERRACOTTA) textureNum = 3;
		else if (b == Blocks.YELLOW_TERRACOTTA) textureNum = 4;
		else if (b == Blocks.LIME_TERRACOTTA) textureNum = 5;
		else if (b == Blocks.PINK_TERRACOTTA) textureNum = 6;
		else if (b == Blocks.GRAY_TERRACOTTA) textureNum = 7;
		else if (b == Blocks.LIGHT_GRAY_TERRACOTTA) textureNum = 8;
		else if (b == Blocks.CYAN_TERRACOTTA) textureNum = 9;
		else if (b == Blocks.PURPLE_TERRACOTTA) textureNum = 10;
		else if (b == Blocks.BLUE_TERRACOTTA) textureNum = 11;
		else if (b == Blocks.BROWN_TERRACOTTA) textureNum = 12;
		else if (b == Blocks.GREEN_TERRACOTTA) textureNum = 13;
		else if (b == Blocks.RED_TERRACOTTA) textureNum = 14;
		else if (b == Blocks.BLACK_TERRACOTTA) textureNum = 15;
		else textureNum = (byte) this.rand.nextInt(DYE_COLORS.length);
		// actually set the texture
		this.setTextureNum(textureNum);
	}
}
