package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntitySlimeGolem extends GolemBase 
{		
	public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
	public static final String KNOCKBACK = "Knockback Factor";
	
	public EntitySlimeGolem(World world) 
	{
		super(world, Config.SLIME.getBaseAttack(), Blocks.SLIME_BLOCK);
		this.setCanSwim(true);	
	}

	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("slime");
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if(super.attackEntityAsMob(entity))
		{
			if(Config.SLIME.getBoolean(ALLOW_SPECIAL))
			{
				knockbackTarget(entity, Config.SLIME.getFloat(KNOCKBACK));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void damageEntity(DamageSource source, float amount) 
	{
		if (!this.isEntityInvulnerable(source))
		{
			super.damageEntity(source, amount);
			if(source.getSourceOfDamage() != null && Config.SLIME.getBoolean(ALLOW_SPECIAL))
			{
				knockbackTarget(source.getSourceOfDamage(), Config.SLIME.getFloat(KNOCKBACK) * 0.325F);
			}
		}
	}
	
	protected void knockbackTarget(Entity entity, final double KNOCKBACK_FACTOR)
	{
		double dX = Math.signum(entity.posX - this.posX) * KNOCKBACK_FACTOR;
		double dZ = Math.signum(entity.posZ - this.posZ) * KNOCKBACK_FACTOR;
		entity.addVelocity(dX, KNOCKBACK_FACTOR / 4, dZ);
		entity.velocityChanged = true;
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.SLIME.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int size = 11 + this.rand.nextInt(16 + lootingLevel * 4);
		this.addDrop(dropList, new ItemStack(Items.SLIME_BALL, size), 100);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_SLIME_STEP;
	}
}
