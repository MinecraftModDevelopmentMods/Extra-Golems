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

public class EntityGlowstoneGolem extends GolemLightProvider
{			
	public EntityGlowstoneGolem(World world) 
	{
		super(world, Config.GLOWSTONE.getBaseAttack(), Blocks.GLOWSTONE, EnumLightLevel.FULL);
		this.setCanTakeFallDamage(true);
		this.isImmuneToFire = true;
	}
	
	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("glowstone");
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.GLOWSTONE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 6 + this.rand.nextInt(8 + lootingLevel * 2);
		this.addDrop(dropList, new ItemStack(Items.GLOWSTONE_DUST, size), 100);
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
