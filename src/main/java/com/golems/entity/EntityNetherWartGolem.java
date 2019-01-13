package com.golems.entity;

import java.util.List;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityNetherWartGolem extends GolemBase {

	public static final Block NETHERWART = Blocks.NETHER_WART_BLOCK;

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
	public static final String FREQUENCY = "Netherwart Frequency";
	public static final String DROP_NETHERWART_BLOCK = "Drop Netherwart Blocks";

	public EntityNetherWartGolem(final World world) {
		super(world, Config.NETHERWART.getBaseAttack(), NETHERWART);
		this.setCanSwim(true);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		final IBlockState[] flowers = {
				Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 0),
				Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 1),
				Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 2) };
		final Block[] soils = { Blocks.SOUL_SAND };
		final boolean spawn = Config.NETHERWART.getBoolean(ALLOW_SPECIAL);
		final int freq = Config.NETHERWART.getInt(FREQUENCY);
		this.tasks.addTask(2,
				new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, spawn));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("nether_wart");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.NETHERWART.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		Item netherwart;
//		int min;
//		int max;
//		if (Config.NETHERWART.getBoolean(DROP_NETHERWART_BLOCK)) {
//			netherwart = Item.getItemFromBlock(NETHERWART);
//			min = 0;
//			max = 4;
//		} else {
//			netherwart = Items.NETHER_WART;
//			min = 1;
//			max = 9;
//		}
//		this.addDrop(dropList, netherwart, 0, min, max, 90 + lootingLevel * 2);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(Config.NETHERWART.getBoolean(EntityNetherWartGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.RED + trans("entitytip.plants_warts"));
		return list;
	}
}
