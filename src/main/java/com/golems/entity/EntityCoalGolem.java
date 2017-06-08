package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityCoalGolem extends GolemBase
{
	public static final String ALLOW_SPECIAL = "Allow Special: Blindness";
	
	public EntityCoalGolem(World world) 
	{
		super(world, Config.COAL.getBaseAttack(), Blocks.COAL_BLOCK);
	}
	
	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("coal");
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.COAL.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}
	
	/** Attack by adding potion effect as well */
	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if(super.attackEntityAsMob(entity))
		{
			final int BLIND_CHANCE = 4;
			if(Config.COAL.getBoolean(ALLOW_SPECIAL) && entity instanceof EntityLivingBase && this.rand.nextInt(BLIND_CHANCE) == 0)
			{
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20 * (3 + rand.nextInt(5)), 0));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if(this.isBurning())
		{
			this.setFire(2);
		}
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 8 + this.rand.nextInt(8 + lootingLevel * 2);
		this.addDrop(dropList, new ItemStack(Items.COAL, size), 100);
		this.addDrop(dropList, Items.COAL, 1, 1, size / 4, 40);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
