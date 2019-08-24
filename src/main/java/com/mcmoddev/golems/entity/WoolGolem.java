package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class WoolGolem extends GolemMultiTextured {

	public static final String WOOL_PREFIX = "wool";
	public static final String[] coloredWoolTypes = { "black", "orange", "magenta", "light_blue",
		"yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green",
		"red", "white" };

	public WoolGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, WOOL_PREFIX, coloredWoolTypes);
	}

	@Override
	public void setTextureNum(byte toSet, final boolean updateInstantly) {
		//  note: skips texture for 'white'
		toSet %= (byte) (coloredWoolTypes.length - 1);
		super.setTextureNum(toSet, updateInstantly);
	}

	@Override
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		// uses HashMap to determine which texture this golem should apply
		// based on the top-middle building block. Defaults to a random texture.
		byte textureNum = GolemTextureBytes.getByBlock(GolemTextureBytes.WOOL, body.getBlock());
		this.setTextureNum(textureNum);
	}
	
	@Override
	public ItemStack getCreativeReturn(final RayTraceResult target) {
		return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.WOOL, (byte)this.getTextureNum()));
	}
}
