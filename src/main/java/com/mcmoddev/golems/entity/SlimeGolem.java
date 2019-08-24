package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public final class SlimeGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
	public static final String ALLOW_SPLITTING = "Allow Special: Split";
	public static final String KNOCKBACK = "Knockback Factor";
	
	public SlimeGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			// knocks back the entity it's attacking (if it's adult and not attacking a slime)
			if (this.getConfigBool(ALLOW_SPECIAL) && !(entity instanceof SlimeEntity) && !this.isChild()) {
				knockbackTarget(entity, this.getConfigDouble(KNOCKBACK));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void damageEntity(final DamageSource source, final float amount) {
		if (!this.isInvulnerableTo(source)) {
			super.damageEntity(source, amount);
			// knocks back the entity that is attacking it
			if (!this.isChild() && source.getImmediateSource() != null && this.getConfigBool(ALLOW_SPECIAL)) {
				knockbackTarget(source.getImmediateSource(), this.getConfigDouble(KNOCKBACK));
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
	public void onDeath(final DamageSource source) {
		if(!this.world.isRemote && !this.isChild() && this.getConfigBool(ALLOW_SPLITTING)) {
			GolemBase slime1 = this.container.getEntityType().create(this.world);
			GolemBase slime2 = this.container.getEntityType().create(this.world);
			slime1.setChild(true);
			slime2.setChild(true);
			// copy attack target info
			if (this.getAttackTarget() != null) {
				slime1.setAttackTarget(this.getAttackTarget());
				slime2.setAttackTarget(this.getAttackTarget());
			}
			// set location
			slime1.setLocationAndAngles(this.posX + rand.nextDouble() - 0.5D, this.posY,
					this.posZ + rand.nextDouble() - 0.5D, this.rotationYaw + rand.nextInt(20) - 10, 0);
			slime2.setLocationAndAngles(this.posX + rand.nextDouble() - 0.5D, this.posY,
					this.posZ + rand.nextDouble() - 0.5D, this.rotationYaw + rand.nextInt(20) - 10, 0);
			// spawn the entities
			this.getEntityWorld().addEntity(slime1);
			this.getEntityWorld().addEntity(slime2);
		}

		super.onDeath(source);
	}

	@Override
	public void notifyDataManagerChange(final DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (CHILD.equals(key)) {
			if (this.isChild()) {
				// truncate these values to one decimal place after reducing them from base values
				double childHealth = (Math.floor(container.getHealth() * 0.3D * 10D)) / 10D;
				double childAttack = (Math.floor(container.getAttack() * 0.6D * 10D)) / 10D;
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(childHealth);
				this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(childAttack);
				this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
			} else {
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(container.getHealth());
				this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(container.getAttack());
				this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(container.getKnockbackResist());
			}
			// recalculate size
			this.recalculateSize();
		}
	}
}
