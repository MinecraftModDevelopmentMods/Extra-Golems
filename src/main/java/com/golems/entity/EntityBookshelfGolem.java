package com.golems.entity;

import java.util.List;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityBookshelfGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";
	private static final Potion[] goodEffects = { MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION,
			MobEffects.STRENGTH, MobEffects.ABSORPTION, MobEffects.LUCK, MobEffects.INSTANT_HEALTH,
			MobEffects.RESISTANCE, MobEffects.INVISIBILITY, MobEffects.SPEED,
			MobEffects.JUMP_BOOST };

	public EntityBookshelfGolem(final World world) {
		super(world, Config.BOOKSHELF.getBaseAttack(), Blocks.BOOKSHELF);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("books");
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (Config.BOOKSHELF.getBoolean(ALLOW_SPECIAL) && this.getActivePotionEffects().isEmpty()
				&& rand.nextInt(40) == 0) {
			final Potion potion = goodEffects[rand.nextInt(goodEffects.length)];
			final int len = potion.isInstant() ? 1 : 200 + 100 * (1 + rand.nextInt(5));
			this.addPotionEffect(new PotionEffect(potion, len, rand.nextInt(2)));
		}
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.BOOKSHELF.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		this.addDrop(dropList, Items.BOOK, 0, 4, 8 + lootingLevel, 100);
//		this.addDrop(dropList, Blocks.PLANKS, 0, 3, 12, 75);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(Config.BOOKSHELF.getBoolean(EntityBookshelfGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.LIGHT_PURPLE + trans("entitytip.grants_self_potion_effects"));
		return list;
	}
}
