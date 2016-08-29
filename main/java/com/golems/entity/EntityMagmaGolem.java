package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityMagmaGolem extends GolemLightProvider
{		
	public static final String ALLOW_FIRE_SPECIAL = "Allow Special: Burn Enemies";
	public static Block MAGMA = Blocks.field_189877_df;

	public EntityMagmaGolem(World world) 
	{
		super(world, Config.MAGMA.getBaseAttack(), MAGMA, EnumLightLevel.HALF);
		this.isImmuneToFire = true;
	}
	
	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("magma");
	}

	/** Attack by lighting on fire as well */
	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if(super.attackEntityAsMob(entity))
		{
			if(Config.MAGMA.getBoolean(ALLOW_FIRE_SPECIAL))
			{
				entity.setFire(2 + rand.nextInt(5));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.MAGMA.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = lootingLevel + this.rand.nextInt(4);
		this.addDrop(dropList, new ItemStack(MAGMA, size > 4 ? 4 : size), 90 + lootingLevel * 2);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
