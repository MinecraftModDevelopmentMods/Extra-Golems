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

public final class EntitySlimeGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
	public static final String KNOCKBACK = "Knockback Factor";

	public EntitySlimeGolem(final World world) {
		super(world, Config.SLIME.getBaseAttack(), Blocks.SLIME_BLOCK);
		this.setCanSwim(true);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("slime");
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			if (Config.SLIME.getBoolean(ALLOW_SPECIAL)) {
				knockbackTarget(entity, Config.SLIME.getFloat(KNOCKBACK));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void damageEntity(final DamageSource source, final float amount) {
		if (!this.isEntityInvulnerable(source)) {
			super.damageEntity(source, amount);
			if (source.getImmediateSource() != null && Config.SLIME.getBoolean(ALLOW_SPECIAL)) {
				knockbackTarget(source.getImmediateSource(),
						Config.SLIME.getFloat(KNOCKBACK) * 0.325F);
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
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.SLIME.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
	}

	@Override
	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
		final int size = 11 + this.rand.nextInt(16 + lootingLevel * 4);
		this.addDrop(dropList, new ItemStack(Items.SLIME_BALL, size), 100);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_SLIME_STEP;
	}
}
