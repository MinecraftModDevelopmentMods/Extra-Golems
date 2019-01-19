package com.golems.entity;

import java.util.List;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityMushroomGolem extends GolemMultiTextured {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
	public static final String FREQUENCY = "Mushroom Frequency";

	public static final String SHROOM_PREFIX = "shroom";
	public static final String[] SHROOM_TYPES = { "red", "brown" };
	public final IBlockState[] mushrooms = { Blocks.BROWN_MUSHROOM.getDefaultState(),
			Blocks.RED_MUSHROOM.getDefaultState() };
	protected static final Block[] soils = { Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM };

	public EntityMushroomGolem(final World world) {
		super(world, SHROOM_PREFIX, SHROOM_TYPES);
		this.setCanSwim(true);
		GolemConfigSet cfg = getConfig(this);
		final boolean allowed = cfg.getBoolean(ALLOW_SPECIAL);
		final int freq = allowed ? cfg.getInt(FREQUENCY) : Integer.MAX_VALUE;
		this.tasks.addTask(2,
				new EntityAIPlaceRandomBlocksStrictly(this, freq, mushrooms, soils, allowed));
		this.setBaseMoveSpeed(0.30D);
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRASS_STEP;
	}
	
	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block type to give this golem the right texture
		byte textureNum = body == Blocks.RED_MUSHROOM_BLOCK ? (byte) 0
				: (byte) 1;
		textureNum %= this.getNumTextures();
		this.setTextureNum(textureNum);
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(getConfig(this).getBoolean(EntityMushroomGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.DARK_GREEN + trans("entitytip.plants_shrooms"));
		return list;
	}
}
