package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
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
		/* TODO use block metadata to give this golem the right texture */
//		if(body.getBlock() instanceof BlockNewLog || body.getBlock() instanceof BlockOldLog) {
//			final int meta = body.getBlock().getMetaFromState(
//			body.with(BlockLog.AXIS, EnumFacing.Axis.));
//			byte textureNum = body.getBlock() == Blocks.LOG2 ? (byte) (meta + 4) : (byte) meta;
//			textureNum %= this.getNumTextures();
//			this.setTextureNum(textureNum);
//		}

	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
