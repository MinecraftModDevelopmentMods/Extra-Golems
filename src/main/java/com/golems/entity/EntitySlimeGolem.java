package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public final class EntitySlimeGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
	public static final String ALLOW_SPLITTING = "Allow Special: Split";
	public static final String KNOCKBACK = "Knockback Factor";
	
	private float knockbackPower;

	public EntitySlimeGolem(final World world) {
		this(world, false);
	}
	
	public EntitySlimeGolem(final World world, final boolean isBaby) {
		super(GolemEntityTypes.SLIMEworld);
		this.setChild(isBaby);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.SLIME_GOLEM);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.SLIME_GOLEM);
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			GolemConfigSet cfg = getConfig(this);
			// knocks back the entity it's attacking (if it's adult and not attacking a slime)
			if (cfg.getBoolean(ALLOW_SPECIAL) && !(entity instanceof EntitySlime) && !this.isChild()) {
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
			// knocks back the entity that is attacking it
			if (!this.isChild() && source.getImmediateSource() != null && getConfig(this).getBoolean(ALLOW_SPECIAL)) {
				knockbackTarget(source.getImmediateSource(), this.knockbackPower);
			}
		}
	}

	/**
	 * Adds extra velocity to the golem's knockback attack. 
	 **/
	protected void knockbackTarget(final Entity entity, final double knockbackFactor) {
		final double dX = Math.signum(entity.posX - this.posX) * knockbackFactor;
		final double dZ = Math.signum(entity.posZ - this.posZ) * knockbackFactor;
		entity.addVelocity(dX, knockbackFactor / 4, dZ);
		entity.velocityChanged = true;
	}
	
	@Override
	public void setDead() {
		// spawn baby golems here if possible 
		if(!this.world.isRemote && !this.isChild() && getConfig(this).getBoolean(ALLOW_SPLITTING)) {
			GolemBase slime1 = new EntitySlimeGolem(this.world, true);
			GolemBase slime2 = new EntitySlimeGolem(this.world, true);
			// copy attack target info
			if(this.getAttackTarget() != null) {
				slime1.setAttackTarget(this.getAttackTarget());
				slime2.setAttackTarget(this.getAttackTarget());
			}
			// set location
			slime1.setLocationAndAngles(this.posX + rand.nextDouble() - 0.5D, this.posY, 
					 this.posZ + rand.nextDouble() - 0.5D, this.rotationYaw + rand.nextInt(20) - 10, 0);
			slime2.setLocationAndAngles(this.posX + rand.nextDouble() - 0.5D, this.posY, 
					 this.posZ + rand.nextDouble() - 0.5D, this.rotationYaw + rand.nextInt(20) - 10, 0);
			// spawn the entities
			this.getEntityWorld().spawnEntity(slime1);
			this.getEntityWorld().spawnEntity(slime2);
		}
		
		super.setDead();
	}
	
	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		// change stats if this is a child vs. an adult golem
		if(this.isChild()) {
			this.knockbackPower = 0.0F;
			this.setSize(0.7F, 1.45F);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getConfig(this).getMaxHealth() / 3);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getConfig(this).getBaseAttack() * 0.6F);
			this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
		} else {
			this.knockbackPower = getConfig(this).getFloat(KNOCKBACK) * 0.325F;
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getConfig(this).getMaxHealth());
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getConfig(this).getBaseAttack());
			this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.35D);
		}
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_SLIME_STEP;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		final GolemConfigSet cfg = getConfig(this);
		if (cfg.getBoolean(EntitySlimeGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.GREEN + trans("entitytip.has_knockback"));
		}
		if(!this.isChild() && cfg.getBoolean(ALLOW_SPLITTING)) {
			list.add(TextFormatting.GREEN + trans("entitytip.splits_upon_death"));
		}
		return list;
	}
}
