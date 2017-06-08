package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntitySeaLanternGolem extends GolemLightProvider
{		
	public static final LightManager WATER_LIGHT = new LightManager(0.987F, 2, Material.AIR, Material.WATER);
	
	public EntitySeaLanternGolem(World world) 
	{
		super(world, Config.SEA_LANTERN.getBaseAttack(), new ItemStack(Blocks.SEA_LANTERN), WATER_LIGHT);
		this.tickDelay = 1;
	}
	
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("sea_lantern");
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.SEA_LANTERN.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int add = lootingLevel > 0 ? 1 : 0;
		this.addDrop(dropList, Blocks.SEA_LANTERN, 0, 1, 2 + add, 100);
		this.addDrop(dropList, Items.PRISMARINE_SHARD, 0, 1, 3, 4 + lootingLevel * 10);
		this.addDrop(dropList, Items.PRISMARINE_CRYSTALS, 0, 1, 3, 4 + lootingLevel * 10);
	}
 
	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_GLASS_STEP;
	}
}
