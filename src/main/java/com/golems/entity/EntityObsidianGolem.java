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

public class EntityObsidianGolem extends GolemBase 
{			
	public EntityObsidianGolem(World world) 
	{
		super(world, Config.OBSIDIAN.getBaseAttack(), Blocks.OBSIDIAN);
	}
	
	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("obsidian");
	}
		
	@Override
	protected void applyAttributes() 
	{
	 	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.OBSIDIAN.getMaxHealth());
	  	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
	}
	
	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 1 + this.rand.nextInt(2 + (lootingLevel > 2 ? 2 : lootingLevel));
		this.addDrop(dropList, new ItemStack(Blocks.OBSIDIAN, size), 100);
	}
   
	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
