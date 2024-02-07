package com.mcmoddev.golems.data.behavior.data;

import com.mcmoddev.golems.data.behavior.AbstractShootBehavior;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

/**
 * Used by subclasses of {@link AbstractShootBehavior} to manage goals
 */
public class ShootBehaviorData implements IBehaviorData {

	protected final IExtraGolem entity;
	private final RangedAttackGoal rangedAttackGoal;
	private final MeleeAttackGoal meleeAttackGoal;

	public ShootBehaviorData(IExtraGolem entity, RangedAttackGoal rangedAttackGoal, MeleeAttackGoal meleeAttackGoal) {
		this.entity = entity;
		this.rangedAttackGoal = rangedAttackGoal;
		this.meleeAttackGoal = meleeAttackGoal;
	}

	//// GETTERS ////

	public RangedAttackGoal getRangedGoal() {
		return rangedAttackGoal;
	}

	public MeleeAttackGoal getMeleeGoal() {
		return meleeAttackGoal;
	}

	/** @param ammo the ammo count **/
	public void setAmmo(final int ammo) {
		this.entity.setAmmo(ammo);
	}

	/** @return the ammo count **/
	public int getAmmo() {
		return this.entity.getAmmo();
	}

	//// NBT ////

	@Override
	public CompoundTag serializeNBT() {
		return new CompoundTag();
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		// do nothing
	}
}
