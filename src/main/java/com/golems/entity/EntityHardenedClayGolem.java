package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityHardenedClayGolem extends GolemBase 
{			
	public EntityHardenedClayGolem(World world) 
	{
		super(world, Config.HARD_CLAY.getBaseAttack(), Blocks.HARDENED_CLAY);
	}

	protected ResourceLocation applyTexture()
	{
		return GolemBase.makeGolemTexture("hardened_clay");
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.HARD_CLAY.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.18D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 1 + this.rand.nextInt(2 + lootingLevel);
		this.addDrop(dropList, new ItemStack(Blocks.HARDENED_CLAY, size), 100);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
