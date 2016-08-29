package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityBookshelfGolem extends GolemBase 
{		
	public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";
	private Potion[] goodEffects = 
		{MobEffects.FIRE_RESISTANCE,MobEffects.REGENERATION,MobEffects.STRENGTH,MobEffects.ABSORPTION,MobEffects.LUCK,
		 MobEffects.INSTANT_HEALTH,MobEffects.RESISTANCE,MobEffects.INVISIBILITY,MobEffects.SPEED,MobEffects.JUMP_BOOST};
	
	public EntityBookshelfGolem(World world) 
	{
		super(world, Config.BOOKSHELF.getBaseAttack(), Blocks.BOOKSHELF);
	}
	
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("books");
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
	    super.onLivingUpdate();

	    // Potion effects:  for this golem only
	    if(Config.BOOKSHELF.getBoolean(ALLOW_SPECIAL) && this.getActivePotionEffects().isEmpty() && rand.nextInt(40) == 0)
	    {
	    	this.addPotionEffect(new PotionEffect(goodEffects[rand.nextInt(goodEffects.length)], 200 + 100 * (1 + rand.nextInt(5)), 1));
	    }
	}
		
	@Override
	protected void applyAttributes() 
	{
	 	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.BOOKSHELF.getMaxHealth());
	  	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
	}
	
	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 4 + this.rand.nextInt(6 + lootingLevel);
		this.addDrop(dropList, new ItemStack(Items.BOOK, size), 100);
		this.addDrop(dropList, Blocks.PLANKS, 0, 3, 12, 75);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
