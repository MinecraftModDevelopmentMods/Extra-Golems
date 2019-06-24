package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntityCoalGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Blindness";

	public EntityCoalGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.COAL_GOLEM);
	}

	/**
	 * Attack by adding potion effect as well.
	 */
	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			final int BLIND_CHANCE = 2;
			if (entity instanceof LivingEntity && this.getConfigBool(ALLOW_SPECIAL) && this.rand.nextInt(BLIND_CHANCE) == 0) {
				((LivingEntity) entity).addPotionEffect(
					new EffectInstance(Effects.BLINDNESS, 20 * (3 + rand.nextInt(5)), 0));
			}
			return true;
		}
		return false;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// if burning, the fire never goes out on its own
		if (this.isBurning()) {
			this.setFire(2);
		}
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
