package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntityLapisGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

	private static final Effect[] badEffects = { Effects.BLINDNESS, Effects.SLOWNESS, Effects.POISON,
			Effects.INSTANT_DAMAGE, Effects.WEAKNESS, Effects.WITHER, Effects.LEVITATION, Effects.GLOWING };

	public EntityLapisGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
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
				final Effect potionID = entity.isEntityUndead() ? Effects.INSTANT_HEALTH
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
