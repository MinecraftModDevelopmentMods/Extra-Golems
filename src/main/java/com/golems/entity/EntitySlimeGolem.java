package com.golems.entity;

import com.golems.util.GolemConfigSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public final class EntitySlimeGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
	public static final String KNOCKBACK = "Knockback Factor";

	public EntitySlimeGolem(final World world) {
		super(world);
		this.setCanSwim(true);
		this.setLootTableLoc("golem_slime");
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("slime");
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			GolemConfigSet cfg = getConfig(this);
			if (cfg.getBoolean(ALLOW_SPECIAL)) {
				knockbackTarget(entity, cfg.getFloat(KNOCKBACK));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void damageEntity(final DamageSource source, final float amount) {
		if (!this.isEntityInvulnerable(source)) {
			super.damageEntity(source, amount);
			GolemConfigSet cfg = getConfig(this);
			if (source.getImmediateSource() != null && cfg.getBoolean(ALLOW_SPECIAL)) {
				knockbackTarget(source.getImmediateSource(),
					cfg.getFloat(KNOCKBACK) * 0.325F);
			}
		}
	}

	protected void knockbackTarget(final Entity entity, final double knockbackFactor) {
		final double dX = Math.signum(entity.posX - this.posX) * knockbackFactor;
		final double dZ = Math.signum(entity.posZ - this.posZ) * knockbackFactor;
		entity.addVelocity(dX, knockbackFactor / 4, dZ);
		entity.velocityChanged = true;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_SLIME_STEP;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (getConfig(this).getBoolean(EntitySlimeGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.GREEN + trans("entitytip.has_knockback"));
		return list;
	}
}
