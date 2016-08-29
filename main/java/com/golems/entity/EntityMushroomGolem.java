package com.golems.entity;

import java.util.List;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.util.WeightedItem;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityMushroomGolem extends GolemMultiTextured
{	
	public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
	public static final String FREQUENCY = "Mushroom Frequency";
	
	public static final String shroomPrefix = "shroom";
	public static final String[] shroomTypes = {"red","brown"};
	public final IBlockState[] mushrooms = {Blocks.BROWN_MUSHROOM.getDefaultState(), Blocks.RED_MUSHROOM.getDefaultState()};
	public final Block[] soils = {Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM};

	public EntityMushroomGolem(World world) 
	{
		super(world, Config.MUSHROOM.getBaseAttack(), new ItemStack(Blocks.RED_MUSHROOM_BLOCK), shroomPrefix, shroomTypes);
		this.setCanSwim(true);
		int freq = Config.MUSHROOM.getInt(FREQUENCY);
		boolean allowed = Config.MUSHROOM.getBoolean(ALLOW_SPECIAL);
		this.tasks.addTask(2, new EntityAIPlaceRandomBlocksStrictly(this, freq, mushrooms, soils, allowed));
	}
	
	@Override
	public String getModId() 
	{
		return ExtraGolems.MODID;
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.MUSHROOM.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int size = 4 + this.rand.nextInt(6 + lootingLevel * 2);
		Block shroom = rand.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
		this.addDrop(dropList, new ItemStack(shroom, size), 100);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_GRASS_STEP;
	}
}
