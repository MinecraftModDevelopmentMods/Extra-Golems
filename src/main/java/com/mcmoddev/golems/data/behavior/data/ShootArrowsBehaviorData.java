package com.mcmoddev.golems.data.behavior.data;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.ShootArrowsBehavior;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Used by {@link ShootArrowsBehavior} to manage arrows and inventory
 */
public class ShootArrowsBehaviorData implements IBehaviorData {

	private final IExtraGolem entity;
	private final EntityDataAccessor<Integer> arrows;
	private RangedAttackGoal rangedAttackGoal;
	private MeleeAttackGoal meleeAttackGoal;

	public ShootArrowsBehaviorData(IExtraGolem entity, EntityDataAccessor<Integer> arrows, RangedAttackGoal rangedAttackGoal, MeleeAttackGoal meleeAttackGoal) {
		this.entity = entity;
		this.arrows = arrows;
		this.rangedAttackGoal = rangedAttackGoal;
		this.meleeAttackGoal = meleeAttackGoal;
	}

	//// METHODS ////

	public void setArrowsInInventory(int count) {
		entity.asMob().getEntityData().set(arrows, count);
	}

	//// GETTERS ////

	public int getArrowsInInventory() {
		return entity.asMob().getEntityData().get(arrows);
	}

	public RangedAttackGoal getRangedGoal() {
		return rangedAttackGoal;
	}

	public MeleeAttackGoal getMeleeGoal() {
		return meleeAttackGoal;
	}

	//// NBT ////

	private static final String KEY_ARROW_COUNT = "ArrowCount";

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag tag = new CompoundTag();
		tag.putInt(KEY_ARROW_COUNT, getArrowsInInventory());
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.setArrowsInInventory(tag.getInt(KEY_ARROW_COUNT));
	}
}
