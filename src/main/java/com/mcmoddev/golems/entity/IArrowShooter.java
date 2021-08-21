package com.mcmoddev.golems.entity;

import java.util.List;
import java.util.function.BiPredicate;

import com.mcmoddev.golems.ExtraGolems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public interface IArrowShooter extends RangedAttackMob, ContainerListener {
  
  static final String KEY_INVENTORY = "Items";
  static final String KEY_SLOT = "Slot";
  static final int INVENTORY_SIZE = 9;
  static final BiPredicate<Container, ItemStack> PICK_UP_ARROW_PRED = (inventory, stack) -> {
    // make sure the item is an arrow
    if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
      // make sure the entity can pick up this stack
      for (int i = 0, l = inventory.getContainerSize(); i < l; i++) {
        final ItemStack invStack = inventory.getItem(i);
        if (invStack.isEmpty() || (invStack.getItem() == stack.getItem() && ItemStack.tagMatches(invStack, stack)
            && invStack.getCount() + stack.getCount() <= invStack.getMaxStackSize())) {
          return true;
        }
      }
    }
    return false;
  };

  double getArrowDamage();
  int getArrowsInInventory();
  void setArrowsInInventory(int count);
  void initInventory();
  SimpleContainer getArrowInventory();
  RangedAttackGoal getRangedGoal();
  MeleeAttackGoal getMeleeGoal();
  GolemBase getGolemEntity();
  
  @Override
  default void containerChanged(Container container) {
    GolemBase entity = getGolemEntity();
    if (!entity.level.isClientSide()) {
      entity.setArrowsInInventory(countArrowsInInventory());
      entity.updateCombatTask(false);
    }
  }

  @Override
  default void performRangedAttack(LivingEntity target, float distanceFactor) {
    final GolemBase entity = getGolemEntity();
    ItemStack itemstack = findArrowsInInventory(getArrowInventory());
    if (!itemstack.isEmpty()) {
      // make an arrow out of the inventory
      AbstractArrow arrow = ProjectileUtil.getMobArrow(entity, itemstack, distanceFactor);
      arrow.setPos(entity.getX(), entity.getY() + entity.getBbHeight() * 0.55F, entity.getZ());
      double d0 = target.getX() - entity.getX();
      double d1 = target.getY(1.0D / 3.0D) - arrow.getY();
      double d2 = target.getZ() - entity.getZ();
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      // set location and attributes
      arrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - entity.level.getDifficulty().getId() * 4));
      arrow.pickup = Pickup.ALLOWED;
      arrow.setOwner(entity);
      arrow.setBaseDamage(getArrowDamage());
      // play sound and add arrow to world
      entity.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
      entity.level.addFreshEntity(arrow);
      // update itemstack and inventory
      itemstack.shrink(1);
      containerChanged(getArrowInventory());
    }
  }
  
  default void saveArrowInventory(final CompoundTag tag) {
    ListTag listNBT = new ListTag();
    // write inventory slots to NBT
    for (int i = 0; i < getArrowInventory().getContainerSize(); i++) {
      ItemStack stack = getArrowInventory().getItem(i);
      if (!stack.isEmpty()) {
        CompoundTag slotNBT = new CompoundTag();
        slotNBT.putByte(KEY_SLOT, (byte) i);
        stack.save(slotNBT);
        listNBT.add(slotNBT);
      }
    }
    tag.put(KEY_INVENTORY, listNBT);
  }
  
  default void loadArrowInventory(final CompoundTag tag) {
    final ListTag list = tag.getList(KEY_INVENTORY, 10);
    initInventory();
    // read inventory slots from NBT
    for (int i = 0; i < list.size(); i++) {
      CompoundTag slotNBT = list.getCompound(i);
      int slotNum = slotNBT.getByte(KEY_SLOT) & 0xFF;
      if (slotNum >= 0 && slotNum < getArrowInventory().getContainerSize()) {
        getArrowInventory().setItem(slotNum, ItemStack.of(slotNBT));
      }
    }
    containerChanged(getArrowInventory());
  }
  
  default void updateCombatTask(final boolean forceMelee) {
    final GolemBase entity = getGolemEntity();
    if (!entity.level.isClientSide()) {
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
    if (this.getArrowInventory() != null) {
      for (int i = 0; i < this.getArrowInventory().getContainerSize(); ++i) {
        ItemStack itemstack = this.getArrowInventory().getItem(i);
        if (!itemstack.isEmpty()) {
          this.getGolemEntity().spawnAtLocation(itemstack);
        }
      }
    }
  }
  
  default int countArrowsInInventory() {
    int arrowCount = 0;
    // add up the size of each itemstack in inventory
    for (int i = 0, l = getArrowInventory().getContainerSize(); i < l; i++) {
      final ItemStack stack = getArrowInventory().getItem(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
        arrowCount += stack.getCount();
      }
    }
    // return arrow count
    return arrowCount;
  }
  
  default void pickupArrows() {
    final GolemBase entity = getGolemEntity();
    // attempt to pick up nearby arrows
    final List<ItemEntity> droppedArrows = entity.level.getEntities(EntityType.ITEM, entity.getBoundingBox().inflate(1.0D),
        e -> !e.isRemoved() && !e.getItem().isEmpty() && !e.hasPickUpDelay() && entity.wantsToPickUp(e.getItem()));
    // DEBUG
    ExtraGolems.LOGGER.info("Picking up " + droppedArrows.size() + " arrow stacks"); // DEBUG
    // check a whole load of conditions to make sure we can pick up nearby arrows
    if (!droppedArrows.isEmpty() && entity.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
        && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(entity.level, entity)) {
      // actually pick up the arrows
      entity.level.getProfiler().push("GolemLooting");
      for (final ItemEntity i : droppedArrows) {
        // DEBUG
        ExtraGolems.LOGGER.info("Picking up arrows!" + i.getItem()); // DEBUG
        if(entity.getArrowInventory().canAddItem(i.getItem())) {
          i.setItem(entity.getArrowInventory().addItem(i.getItem()));
          entity.onItemPickup(i);
        }
      }
      entity.level.getProfiler().pop();
      // update inventory
      containerChanged(getArrowInventory());
    }
  }

}
