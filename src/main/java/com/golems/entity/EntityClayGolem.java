package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityClayGolem extends GolemBase 
{			
	public EntityClayGolem(World world) 
	{
		super(world, Config.CLAY.getBaseAttack(), Blocks.CLAY);
	}
	
	@Override
	protected ResourceLocation applyTexture()
	{
		return GolemBase.makeGolemTexture("clay");
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.CLAY.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		this.addDrop(dropList, Items.CLAY_BALL, 0, 8, 10 + lootingLevel, 100);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
}
