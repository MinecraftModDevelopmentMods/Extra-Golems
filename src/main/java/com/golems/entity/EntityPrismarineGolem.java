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

public class EntityPrismarineGolem extends GolemBase 
{			
	public EntityPrismarineGolem(World world) 
	{
		super(world, Config.PRISMARINE.getBaseAttack(), Blocks.PRISMARINE);
	}
	
	@Override
	protected ResourceLocation applyTexture()
	{
		return GolemBase.makeGolemTexture("prismarine");
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if(this.isInWater())
		{
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.50D);
		}
		else
		{
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
		}
	}
		
	@Override
	protected void applyAttributes() 
	{
	 	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.PRISMARINE.getMaxHealth());
	  	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
	}
	
	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int size = 6 + this.rand.nextInt(4 + lootingLevel * 2);
		this.addDrop(dropList, new ItemStack(Items.PRISMARINE_SHARD, size), 100);
		this.addDrop(dropList, Items.PRISMARINE_CRYSTALS, 0, 1, 3, 6 + lootingLevel * 5);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
