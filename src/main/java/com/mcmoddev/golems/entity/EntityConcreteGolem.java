package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityConcreteGolem extends GolemMultiColorized {

	public static final String ALLOW_RESIST = "Allow Special: Resistance";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.CONCRETE_GOLEM + "_base");
		private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.CONCRETE_GOLEM + "_grayscale");

	public EntityConcreteGolem(final World world) {
		super(EntityConcreteGolem.class, world, TEXTURE_BASE, TEXTURE_OVERLAY, dyeColorArray);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2D);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	@Override
	protected void damageEntity(DamageSource source, float amount) {
		if(this.getConfigBool(ALLOW_RESIST)) {
			amount *= 3.0F / 5.0F;
		}
		super.damageEntity(source, amount);
	}

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block type to give this golem the right texture.
		// defaults to random color.
		final Block b = body.getBlock();
		byte textureNum;
		// check each type of stained glass
		if(b == Blocks.WHITE_CONCRETE) textureNum = 0;
		else if(b == Blocks.ORANGE_CONCRETE) textureNum = 1;
		else if(b == Blocks.MAGENTA_CONCRETE) textureNum = 2;
		else if(b == Blocks.LIGHT_BLUE_CONCRETE) textureNum = 3;
		else if(b == Blocks.YELLOW_CONCRETE) textureNum = 4;
		else if(b == Blocks.LIME_CONCRETE) textureNum = 5;
		else if(b == Blocks.PINK_CONCRETE) textureNum = 6;
		else if(b == Blocks.GRAY_CONCRETE) textureNum = 7;
		else if(b == Blocks.LIGHT_GRAY_CONCRETE) textureNum = 8;
		else if(b == Blocks.CYAN_CONCRETE) textureNum = 9;
		else if(b == Blocks.PURPLE_CONCRETE) textureNum = 10;
		else if(b == Blocks.BLUE_CONCRETE) textureNum = 11;
		else if(b == Blocks.BROWN_CONCRETE) textureNum = 12;
		else if(b == Blocks.GREEN_CONCRETE) textureNum = 13;
		else if(b == Blocks.RED_CONCRETE) textureNum = 14;
		else if(b == Blocks.BLACK_CONCRETE) textureNum = 15;
		else textureNum = (byte)this.rand.nextInt(this.dyeColorArray.length);
		// actually set the texture
		this.setTextureNum(textureNum);
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(this.getConfigBool(ALLOW_RESIST)) {
			String sResist = TextFormatting.DARK_GRAY + trans("effect.minecraft.resistance");
			list.add(sResist);
		}
		return list;
	}
}
