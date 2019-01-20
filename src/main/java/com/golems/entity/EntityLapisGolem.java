package com.golems.entity;

import java.util.List;

import com.golems.util.GolemConfigSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityLapisGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

	private static final Potion[] badEffects = { MobEffects.BLINDNESS, MobEffects.SLOWNESS, MobEffects.POISON,
			MobEffects.INSTANT_DAMAGE, MobEffects.WEAKNESS, MobEffects.WITHER, MobEffects.LEVITATION, MobEffects.GLOWING };

	public EntityLapisGolem(final World world) {
		super(world);
		this.setLootTableLoc("golem_lapis");
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("lapis");
	}

	/** Attack by adding potion effect as well. */
	@Override
	public boolean attackEntityAsMob(final Entity entityIn) {
		if (super.attackEntityAsMob(entityIn) && entityIn instanceof EntityLivingBase) {
			final EntityLivingBase entity = (EntityLivingBase) entityIn;
			final GolemConfigSet cfg = getConfig(this);
			if (cfg.getBoolean(ALLOW_SPECIAL)) {
				final Potion potionID = entity.isEntityUndead() ? MobEffects.INSTANT_HEALTH
						: badEffects[rand.nextInt(badEffects.length)];
				final int len = potionID.isInstant() ? 1 : 20 * (5 + rand.nextInt(9));
				final int amp = potionID.isInstant() ? rand.nextInt(2) : rand.nextInt(3);
				entity.addPotionEffect(new PotionEffect(potionID, len, amp));
			}
			return true;
		}
		return false;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(getConfig(this).getBoolean(EntityLapisGolem.ALLOW_SPECIAL))
			list.add( TextFormatting.LIGHT_PURPLE + trans("entitytip.attacks_use_potion_effects"));
		return list;
	}
}
