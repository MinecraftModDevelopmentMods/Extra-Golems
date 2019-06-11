package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntityLapisGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

	private static final Effect[] badEffects = {MobEffects.BLINDNESS, MobEffects.SLOWNESS, MobEffects.POISON,
		MobEffects.INSTANT_DAMAGE, MobEffects.WEAKNESS, MobEffects.WITHER, MobEffects.LEVITATION, MobEffects.GLOWING};

	public EntityLapisGolem(final World world) {
		super(GolemNames.LAPIS_GOLEM, world);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.4D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.LAPIS_GOLEM);
	}

	/**
	 * Attack by adding potion effect as well.
	 */
	@Override
	public boolean attackEntityAsMob(final Entity entityIn) {
		if (super.attackEntityAsMob(entityIn) && entityIn instanceof LivingEntity) {
			final LivingEntity entity = (LivingEntity) entityIn;
			if (this.getConfigBool(ALLOW_SPECIAL)) {
				final Effect potionID = entity.isEntityUndead() ? Effects.field_76432_h // INSTANT HEALTH
					: badEffects[rand.nextInt(badEffects.length)];
				final int len = potionID.isInstant() ? 1 : 20 * (5 + rand.nextInt(9));
				final int amp = potionID.isInstant() ? rand.nextInt(2) : rand.nextInt(3);
				entity.addPotionEffect(new EffectInstance(potionID, len, amp));
			}
			return true;
		}
		return false;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
