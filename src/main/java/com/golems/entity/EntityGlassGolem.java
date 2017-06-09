package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityGlassGolem extends GolemBase 
{			
	public EntityGlassGolem(World world) 
	{
		super(world, Config.GLASS.getBaseAttack(), Blocks.GLASS);
		this.setCanTakeFallDamage(true);
	}
	
	protected ResourceLocation applyTexture()
	{
		return GolemBase.makeGolemTexture("glass");
	}
		
	@Override
	protected void applyAttributes() 
	{
	 	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.GLASS.getMaxHealth());
	  	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}
	
	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		this.addDrop(dropList, Blocks.GLASS, 0, lootingLevel, lootingLevel + 1, 90);
	}
	
	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_GLASS_STEP;
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.BLOCK_GLASS_BREAK;
	}
}
