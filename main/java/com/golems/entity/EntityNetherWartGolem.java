package com.golems.entity;

import java.util.List;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityNetherWartGolem extends GolemBase 
{	
	public static final Block NETHERWART = Blocks.field_189878_dg;
	
	public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
	public static final String FREQUENCY = "Netherwart Frequency";
	public static final String DROP_NETHERWART_BLOCK = "Drop Netherwart Blocks";

	public EntityNetherWartGolem(World world) 
	{
		super(world, Config.NETHERWART.getBaseAttack(), NETHERWART);	
		this.setCanSwim(true);
	}

	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();
		IBlockState[] flowers = 
		{ 
			Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 0),
			Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 1),
			Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 2)
		};
		Block[] soils = {Blocks.SOUL_SAND};
		boolean spawn = Config.NETHERWART.getBoolean(ALLOW_SPECIAL);
		int freq = Config.NETHERWART.getInt(FREQUENCY);
		this.tasks.addTask(2, new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, spawn));	
	}

	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("nether_wart");
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.NETHERWART.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		Item netherwart;
		int min, max;
		if(Config.NETHERWART.getBoolean(DROP_NETHERWART_BLOCK))
		{
			netherwart = Item.getItemFromBlock(NETHERWART);
			min = 0;
			max = 4;
		}
		else
		{
			netherwart = Items.NETHER_WART;
			min = 1;
			max = 9;
		}
		this.addDrop(dropList, netherwart, 0, min, max, 90 + lootingLevel * 2);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
