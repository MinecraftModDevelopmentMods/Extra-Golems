package com.mcmoddev.golems.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiPredicate;

public interface IArrowShooter extends RangedAttackMob, ContainerListener, InventoryCarrier {

	String KEY_INVENTORY = "Items";
	String KEY_SLOT = "Slot";
	int INVENTORY_SIZE = 9;
	BiPredicate<Container, ItemStack> PICK_UP_ARROW_PRED = (inventory, stack) -> {
		// make sure the item is an arrow
		if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
			// make sure the entity can pick up this stack
			for (int i = 0, l = inventory.getContainerSize(); i < l; i++) {
				final ItemStack invStack = inventory.getItem(i);
				if (invStack.isEmpty() || (ItemStack.isSameItemSameTags(invStack, stack)
						&& invStack.getCount() + stack.getCount() <= invStack.getMaxStackSize())) {
					return true;
				}
			}
		}
		return false;
	};

	/**
	 * @return the base damage per arrow
	 **/
	double getArrowDamage();

	/**
	 * @return the number of arrows in the inventory
	 **/
	int getArrowsInInventory();

	/**
	 * @param count the updated number of arrows in the inventory
	 **/
	void setArrowsInInventory(int count);

	/**
	 * Initialize the arrow inventory
	 **/
	void initArrowInventory();

	/**
	 * @return the RangedAttackGoal
	 **/
	RangedAttackGoal getRangedGoal();

	/**
	 * @return the MeleeAttackGoal
	 **/
	MeleeAttackGoal getMeleeGoal();

	/**
	 * @return the Golem
	 **/
	GolemBase getGolemEntity();

	@Override
	default void containerChanged(Container container) {
		GolemBase entity = getGolemEntity();
		if (!entity.level().isClientSide()) {
			entity.setArrowsInInventory(countArrowsInInventory());
			entity.updateCombatTask(false);
		}
	}

	@Override
	default void performRangedAttack(LivingEntity target, float distanceFactor) {
		final GolemBase entity = getGolemEntity();
		ItemStack itemstack = findArrowsInInventory(getInventory());
		if (!itemstack.isEmpty()) {
			// first, raytrace to ensure no other creatures are in the way
			Vec3 pos = entity.position().add(0, entity.getBbHeight() * 0.55F, 0);
			Vec3 to = target.position().add(0, entity.getBbHeight() * 0.5F, 0);
			Vec3 scaled;
			// step along the vector and check for entities at each point
			for (double i = 0, l = pos.distanceToSqr(to), stepSize = 0.25F; (i * i) < l; i += stepSize) {
				// scale the vector to the step progress
				scaled = (to.subtract(pos)).normalize().scale(i);
				final double x = pos.x + scaled.x;
				final double y = pos.y + scaled.y;
				final double z = pos.z + scaled.z;
				final AABB aabb = new AABB(x - 0.2D, y - 0.2D, z - 0.2D, x + 0.2D, y + 0.2D, z + 0.2D);
				// if any entity at this location cannot be attacked, exit the function
				for (final Entity e : entity.level().getEntities(entity, aabb)) {
					if (!entity.canAttackType(e.getType())) {
						return;
					}
				}
			}
			// make an arrow out of the inventory
			AbstractArrow arrow = ProjectileUtil.getMobArrow(entity, itemstack, distanceFactor);
			arrow.setPos(entity.getX(), entity.getY() + entity.getBbHeight() * 0.55F, entity.getZ());
			double d0 = target.getX() - entity.getX();
			double d1 = target.getY(1.0D / 3.0D) - arrow.getY();
			double d2 = target.getZ() - entity.getZ();
			double d3 = Math.sqrt(d0 * d0 + d2 * d2);
			// set location and attributes
			arrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - entity.level().getDifficulty().getId() * 4));
			arrow.pickup = Pickup.ALLOWED;
			arrow.setOwner(entity);
			arrow.setBaseDamage(getArrowDamage());
			// play sound and add arrow to world
			entity.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
			entity.level().addFreshEntity(arrow);
			// update itemstack and inventory
			itemstack.shrink(1);
			containerChanged(getInventory());
		}
	}

	default void updateCombatTask(final boolean forceMelee) {
		final GolemBase entity = getGolemEntity();
		if (!entity.level().isClientSide()) {
			// remove both goals (clean slate)
			entity.goalSelector.removeGoal(getMeleeGoal());
			entity.goalSelector.removeGoal(getRangedGoal());
			// check if target is close enough to attack
			if (forceMelee || getArrowsInInventory() == 0) {
				entity.goalSelector.addGoal(0, getMeleeGoal());
			} else {
				entity.goalSelector.addGoal(0, getRangedGoal());
			}
		}
	}

	static ItemStack findArrowsInInventory(final Container inv) {
		// search inventory to find suitable arrow itemstack
		for (int i = 0, l = inv.getContainerSize(); i < l; i++) {
			final ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	default void dropArrowInventory() {
		for (int i = 0; i < this.getInventory().getContainerSize(); ++i) {
			ItemStack itemstack = this.getInventory().getItem(i);
			if (!itemstack.isEmpty()) {
				this.getGolemEntity().spawnAtLocation(itemstack);
			}
		}
	}

	default int countArrowsInInventory() {
		int arrowCount = 0;
		// add up the size of each itemstack in inventory
		for (int i = 0, l = getInventory().getContainerSize(); i < l; i++) {
			final ItemStack stack = getInventory().getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
				arrowCount += stack.getCount();
			}
		}
		// return arrow count
		return arrowCount;
	}
}
