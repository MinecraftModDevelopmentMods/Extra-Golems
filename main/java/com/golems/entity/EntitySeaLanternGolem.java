package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

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
	public EntitySeaLanternGolem(World world) 
	{
		super(world, Config.SEA_LANTERN.getBaseAttack(), Blocks.SEA_LANTERN, EnumLightLevel.WATER_FULL);
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
		int size = 1 + this.rand.nextInt(2 + lootingLevel);
		this.addDrop(dropList, new ItemStack(Blocks.SEA_LANTERN, size > 4 ? 4 : size), 100);
		this.addDrop(dropList, Items.PRISMARINE_SHARD, 0, 1, 3, 4 + lootingLevel * 5);
		this.addDrop(dropList, Items.PRISMARINE_CRYSTALS, 0, 1, 3, 4 + lootingLevel * 5);
	}
 
	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_GLASS_STEP;
	}
}
