package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class WoodenGolem extends GolemMultiTextured {

	public static final String[] WOOD_TYPES = { "oak", "spruce", "birch", "jungle", "acacia",
			"big_oak" };

	public WoodenGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, WOOD_TYPES);
	}

	@Override
	public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
		// uses the top-middle building block of this golem to set texture.
		// defaults to a random texture.
		final Block b = body.getBlock();
		byte textureNum;
		// check the following block tags for matches
		if (b.isIn(BlockTags.OAK_LOGS)) {
			textureNum = 0;
		} else if (b.isIn(BlockTags.SPRUCE_LOGS)) {
			textureNum = 1;
		} else if (b.isIn(BlockTags.BIRCH_LOGS)) {
			textureNum = 2;
		} else if (b.isIn(BlockTags.JUNGLE_LOGS)) {
			textureNum = 3;
		} else if (b.isIn(BlockTags.ACACIA_LOGS)) {
			textureNum = 4;
		} else if (b.isIn(BlockTags.DARK_OAK_LOGS)) {
			textureNum = 5;
		} else {
			this.randomizeTexture(this.world, this.getPosition());
			return;
		}
		// set the texture num based on above
		this.setTextureNum(textureNum);
	}

	@Override
	public ItemStack getCreativeReturn(final RayTraceResult target) {
		switch(this.getTextureNum()) {
		case 0: return new ItemStack(Blocks.OAK_LOG);
		case 1: return new ItemStack(Blocks.SPRUCE_LOG);
		case 2: return new ItemStack(Blocks.BIRCH_LOG);
		case 3: return new ItemStack(Blocks.JUNGLE_LOG);
		case 4: return new ItemStack(Blocks.ACACIA_LOG);
		case 5: return new ItemStack(Blocks.DARK_OAK_LOG);
		default: return ItemStack.EMPTY;
		}
	}
	
	@Override
	public void randomizeTexture(final World world, final BlockPos pos) {
		// use the location to select a biome-appropriate texture
		final boolean useBiome = world.getRandom().nextBoolean();
		if(useBiome) {
			byte texture = 0;
			// final Biome biome = world.getBiome(pos);
			// TODO find an easy way to get the preferred log type from the biome
			
			setTextureNum(texture);
			// return;
		}
		super.randomizeTexture(world, pos);
	}	

	@Override
	public Map<Block, Byte> getTextureBytes() {
		return new HashMap<>();
	}
}
