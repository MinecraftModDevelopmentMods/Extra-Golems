package com.mcmoddev.golems.entity;

import java.util.function.BiPredicate;

import com.mcmoddev.golems.ExtraGolems;

import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import javafx.scene.chart.Axis;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public interface IArrowShooter extends IRangedAttackMob, IInventoryChangedListener {
  
  static final String KEY_INVENTORY = "Items";
  static final String KEY_SLOT = "Slot";
  static final int INVENTORY_SIZE = 9;
  static final BiPredicate<IInventory, ItemStack> PICK_UP_ARROW_PRED = (inventory, stack) -> {
    // make sure the item is an arrow
    if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
      // make sure the entity can pick up this stack
      for (int i = 0, l = inventory.getSizeInventory(); i < l; i++) {
        final ItemStack invStack = inventory.getStackInSlot(i);
        if (invStack.isEmpty() || (invStack.getItem() == stack.getItem() && ItemStack.areItemStackTagsEqual(invStack, stack)
            && invStack.getCount() + stack.getCount() <= invStack.getMaxStackSize())) {
          return true;
        }
      }
    }
    return false;
  };

  /** @return the base damage per arrow **/
  double getArrowDamage();
  /** @return the number of arrows in the inventory **/
  int getArrowsInInventory();
  /** @param count the updated number of arrows in the inventory **/
  void setArrowsInInventory(int count);
  /** Initialize the arrow inventory **/
  void initArrowInventory();
  /** @return the arrow inventory container **/
  IInventory getArrowInventory();
  /** @return the RangedAttackGoal **/
  RangedAttackGoal getRangedGoal();
  /** @return the MeleeAttackGoal **/
  MeleeAttackGoal getMeleeGoal();
  /** @return the Golem **/
  GolemBase getGolemEntity();
  
  @Override
  default void onInventoryChanged(IInventory container) {
    GolemBase entity = getGolemEntity();
    if (!entity.world.isRemote) {
      entity.setArrowsInInventory(countArrowsInInventory());
      entity.updateCombatTask(false);
    }
  }

  @Override
  default void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
    final GolemBase entity = getGolemEntity();
    ItemStack itemstack = findArrowsInInventory(getArrowInventory());
    if (!itemstack.isEmpty()) {
      // first, raytrace to ensure no other creatures are in the way
      Vector3d pos = entity.getPositionVec().add(0, entity.getHeight() * 0.55F, 0);
      Vector3d to = target.getPositionVec().add(0, entity.getHeight() * 0.5F, 0);
      Vector3d scaled;
      // step along the vector and check for entities at each point
      for(double i = 0, l = pos.squareDistanceTo(to), stepSize = 0.25F; (i * i) < l; i += stepSize) {
        // scale the vector to the step progress
        scaled = (to.subtract(pos)).normalize().scale(i);
        final double x = pos.x + scaled.x;
        final double y = pos.y + scaled.y;
        final double z = pos.z + scaled.z;
        final AxisAlignedBB aabb = new AxisAlignedBB(x - 0.2D, y - 0.2D, z - 0.2D, x + 0.2D, y + 0.2D, z + 0.2D);
        // if any entity at this location cannot be attacked, exit the function
        for(final Entity e : entity.world.getEntitiesWithinAABBExcludingEntity(entity, aabb)) {
          if(!entity.canAttack(e.getType())) {
            return;
          }
        }
      }
	  // make an arrow out of the inventory
	  AbstractArrowEntity arrow = ProjectileHelper.fireArrow(entity, itemstack, distanceFactor);
	  // set the arrow position and velocity
	  final Vector3d entityPos = entity.getPositionVec();
	  arrow.setPosition(entityPos.x, entityPos.y + entity.getHeight() * 0.66666F, entityPos.z);
	  double d0 = target.getPosX() - entity.getPosX();
	  double d1 = target.getPosYHeight(1.0D / 3.0D) - arrow.getPosY();
	  double d2 = target.getPosZ() - entity.getPosZ();
	  double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
	  arrow.setShooter(entity);
	  arrow.setDamage(getArrowDamage() + entity.world.getRandom().nextDouble() * 0.5D);
	  arrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
	  arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, 1.2F);
	  // play sound and add arrow to world
	  entity.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.9F + entity.world.getRandom().nextFloat() * 0.2F);
	  entity.world.addEntity(arrow);
	  // update itemstack and inventory
	  itemstack.shrink(1);
	  this.onInventoryChanged(getArrowInventory());
    }
  }
  
  default void saveArrowInventory(final CompoundNBT tag) {
    ListNBT listNBT = new ListNBT();
    // write inventory slots to NBT
    for (int i = 0; i < getArrowInventory().getSizeInventory(); i++) {
      ItemStack stack = getArrowInventory().getStackInSlot(i);
      if (!stack.isEmpty()) {
        CompoundNBT slotNBT = new CompoundNBT();
        slotNBT.putByte(KEY_SLOT, (byte) i);
        stack.write(slotNBT);
        listNBT.add(slotNBT);
      }
    }
    tag.put(KEY_INVENTORY, listNBT);
  }
  
  default void loadArrowInventory(final CompoundNBT tag) {
    final ListNBT list = tag.getList(KEY_INVENTORY, 10);
    // read inventory slots from NBT
    for (int i = 0; i < list.size(); i++) {
      CompoundNBT slotNBT = list.getCompound(i);
      int slotNum = slotNBT.getByte(KEY_SLOT) & 0xFF;
      if (slotNum >= 0 && slotNum < getArrowInventory().getSizeInventory()) {
        getArrowInventory().setInventorySlotContents(slotNum, ItemStack.read(slotNBT));
      }
    }
    onInventoryChanged(getArrowInventory());
  }
  
  default void updateCombatTask(final boolean forceMelee) {
    final GolemBase entity = getGolemEntity();
    if (!entity.world.isRemote()) {
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
  
  static ItemStack findArrowsInInventory(final IInventory inv) {
    // search inventory to find suitable arrow itemstack
    for (int i = 0, l = inv.getSizeInventory(); i < l; i++) {
      final ItemStack stack = inv.getStackInSlot(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
        return stack;
      }
    }
    return ItemStack.EMPTY;
  }
  
  default void dropArrowInventory() {
    if (this.getArrowInventory() != null) {
      for (int i = 0; i < this.getArrowInventory().getSizeInventory(); ++i) {
        ItemStack itemstack = this.getArrowInventory().getStackInSlot(i);
        if (!itemstack.isEmpty()) {
          this.getGolemEntity().entityDropItem(itemstack);
        }
      }
    }
  }
  
  default int countArrowsInInventory() {
    int arrowCount = 0;
    // add up the size of each itemstack in inventory
    for (int i = 0, l = getArrowInventory().getSizeInventory(); i < l; i++) {
      final ItemStack stack = getArrowInventory().getStackInSlot(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
        arrowCount += stack.getCount();
      }
    }
    // return arrow count
    return arrowCount;
  }

  default boolean canAddArrowToInventory(final ItemStack stack) {
	if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ArrowItem
			&& getGolemEntity().getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
	  // make sure the entity can pick up this stack
	  for (int i = 0, l = getArrowInventory().getSizeInventory(); i < l; i++) {
		final ItemStack invStack = getArrowInventory().getStackInSlot(i);
		if (invStack.isEmpty() || (invStack.getItem() == stack.getItem() && ItemStack.areItemStackTagsEqual(invStack, stack)
				&& invStack.getCount() + stack.getCount() <= invStack.getMaxStackSize())) {
		  return true;
		}
	  }
	}
	return false;
  }

  default void addArrowToInventory(final ItemStack stack) {
	// make sure the entity can pick up this stack
	for (int i = 0, l = getArrowInventory().getSizeInventory(); i < l; i++) {
	  final ItemStack invStack = getArrowInventory().getStackInSlot(i);
	  if (invStack.isEmpty() || (invStack.getItem() == stack.getItem() && ItemStack.areItemStackTagsEqual(invStack, stack)
			  && invStack.getCount() + stack.getCount() <= invStack.getMaxStackSize())) {
		invStack.setCount(stack.getCount());
		stack.setCount(0);
		getArrowInventory().setInventorySlotContents(i, invStack);
		return;
	  }
	}
  }
}
