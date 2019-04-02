package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityWoodenGolem extends GolemMultiTextured {

	public static final String WOOD_PREFIX = "wooden";
	public static final String[] woodTypes = {"oak", "spruce", "birch", "jungle", "acacia",
		"big_oak"};

	public EntityWoodenGolem(final World world) {
		super(EntityWoodenGolem.class, world, WOOD_PREFIX, woodTypes);
		this.setCanSwim(true);
	}

	@Override
	public ItemStack getCreativeReturn() {
		// try to return the same block of this golem's texture
		// TODO but low priority
		return new ItemStack(Blocks.OAK_LOG);
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// uses the top-middle building block of this golem to set texture.
		// defaults to a random texture.
		final Block b = body.getBlock();
		byte textureNum;
		// check the following block tags for matches
		if(b.isIn(BlockTags.OAK_LOGS)) {
			textureNum = 0;
		} else if(b.isIn(BlockTags.SPRUCE_LOGS)) {
			textureNum = 1;
		} else if(b.isIn(BlockTags.BIRCH_LOGS)) {
			textureNum = 2;
		} else if(b.isIn(BlockTags.JUNGLE_LOGS)) {
			textureNum = 3;
		} else if(b.isIn(BlockTags.ACACIA_LOGS)) {
			textureNum = 4;
		} else if(b.isIn(BlockTags.DARK_OAK_LOGS)) {
			textureNum = 5;
		} else {
			textureNum = (byte)this.rand.nextInt(woodTypes.length);
		}
		// set the texture num based on above
		this.setTextureNum(textureNum);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
