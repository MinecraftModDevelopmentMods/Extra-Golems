package com.golems.entity;

import java.util.List;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityMushroomGolem extends GolemMultiTextured {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
	public static final String FREQUENCY = "Mushroom Frequency";

	public static final String SHROOM_PREFIX = "shroom";
	protected static final String[] SHROOM_TYPES = { "red", "brown" };
	public final IBlockState[] mushrooms = { Blocks.BROWN_MUSHROOM.getDefaultState(),
			Blocks.RED_MUSHROOM.getDefaultState() };
	protected static final Block[] soils = { Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM };

	public EntityMushroomGolem(final World world) {
		super(world, Config.MUSHROOM.getBaseAttack(), new ItemStack(Blocks.RED_MUSHROOM_BLOCK),
				SHROOM_PREFIX, SHROOM_TYPES);
		this.setCanSwim(true);
		final int freq = Config.MUSHROOM.getInt(FREQUENCY);
		final boolean allowed = Config.MUSHROOM.getBoolean(ALLOW_SPECIAL);
		this.tasks.addTask(2,
				new EntityAIPlaceRandomBlocksStrictly(this, freq, mushrooms, soils, allowed));
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.MUSHROOM.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 4 + this.rand.nextInt(6 + lootingLevel * 2);
//		final Block shroom = rand.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
//		this.addDrop(dropList, new ItemStack(shroom, size), 100);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRASS_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(Config.MUSHROOM.getBoolean(EntityMushroomGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.DARK_GREEN + trans("entitytip.plants_shrooms"));
		return list;
	}
}
