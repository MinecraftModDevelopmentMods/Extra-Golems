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
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityLapisGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

	private Potion[] badEffects = { MobEffects.BLINDNESS, MobEffects.SLOWNESS, MobEffects.POISON,
			MobEffects.INSTANT_DAMAGE, MobEffects.WEAKNESS, MobEffects.WITHER, MobEffects.UNLUCK,
			MobEffects.GLOWING };

	public EntityLapisGolem(World world) {
		super(world, Config.LAPIS.getBaseAttack(), Blocks.LAPIS_BLOCK);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("lapis");
	}

	/** Attack by adding potion effect as well */
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (super.attackEntityAsMob(entityIn) && entityIn instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) entityIn;
			if (Config.LAPIS.getBoolean(ALLOW_SPECIAL)) {
				Potion potionID = entity.isEntityUndead() ? MobEffects.INSTANT_HEALTH
						: badEffects[rand.nextInt(badEffects.length)];
				int len = potionID.isInstant() ? 1 : 20 * (5 + rand.nextInt(9));
				int amp = potionID.isInstant() ? rand.nextInt(2) : rand.nextInt(3);
				entity.addPotionEffect(new PotionEffect(potionID, len, amp));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.LAPIS.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel) {
		int size = 8 + this.rand.nextInt(10) + lootingLevel * 4;
		this.addDrop(dropList, new ItemStack(Items.DYE, size, EnumDyeColor.BLUE.getDyeDamage()),
				100);
		this.addDrop(dropList, Items.GOLD_INGOT, 0, 1, 1 + lootingLevel, 8 + lootingLevel * 30);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}