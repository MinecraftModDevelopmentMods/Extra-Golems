package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.container.ContainerDispenserGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public final class DispenserGolem extends GolemBase implements IRangedAttackMob, IInventoryChangedListener {
  public static final String ALLOW_SPECIAL = "Allow Special: Shoot Arrows";
  public static final String ARROW_DAMAGE = "Arrow Damage";
  public static final String ARROW_SPEED = "Arrow Speed";

  private static final String KEY_INVENTORY = "Items";
  private static final String KEY_SLOT = "Slot";
  private static final int INVENTORY_SIZE = 9;

  private boolean allowArrows;
  private double arrowDamage;
  private int arrowSpeed;
  private Inventory inventory;

  private final RangedAttackGoal aiArrowAttack;
  private final MeleeAttackGoal aiMeleeAttack;

  public DispenserGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    // set config values
    this.allowArrows = this.getConfigBool(ALLOW_SPECIAL);
    this.arrowDamage = Math.max(0D, this.getConfigDouble(ARROW_DAMAGE));
    this.arrowSpeed = this.getConfigInt(ARROW_SPEED);
    // init combat AI
    aiArrowAttack = new RangedAttackGoal(this, 1.0D, arrowSpeed, 32.0F);
    aiMeleeAttack = new MeleeAttackGoal(this, 1.0D, true);
    // init inventory
    this.initInventory();
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    // TODO make Goal so golem stays in place while player looks at inventory
  }

  @Override
  protected void collideWithEntity(Entity entityIn) {
    super.collideWithEntity(entityIn);
    this.updateCombatTask(entityIn != null && this.getAttackTarget() != null && entityIn == this.getAttackTarget());
  }

  @Override
  public void livingTick() {
    super.livingTick();
    // update combat style every few seconds
    if (this.ticksExisted % 50 == 0) {
      final boolean forceMelee = !allowArrows || (this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < 4.5D);
      this.updateCombatTask(forceMelee);
    }
    // pick up any arrow items that are nearby
    // note: does not pick up arrow entities, only itemstacks for now
    final int frequency = 24;
    final double range = 0.8D;
    final boolean gameRule = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
    if (this.isServerWorld() && gameRule && rand.nextInt(frequency) == 0) {
      // get a list of nearby entity items that contain arrows
      final List<ItemEntity> itemList = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(range, 0, range),
          i -> i != null && !i.getItem().isEmpty() && !i.cannotPickup() && i.getItem().getItem() instanceof ArrowItem);
      // if any are found, try to add them to the inventory
      for (final ItemEntity i : itemList) {
        final ItemStack item = i.getItem().copy();
        i.setItem(this.inventory.addItem(item));
      }
    }
  }

  @Override
  protected boolean processInteract(final PlayerEntity player, final Hand hand) {
    if (!player.isSneaking() && player instanceof ServerPlayerEntity) {
      // open dispenser GUI by sending request to server
      NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerDispenserGolem.Provider(inventory));
      player.swingArm(hand);
      return true;
    }
    return super.processInteract(player, hand);
  }

  @Override
  public boolean attackEntityFrom(final DamageSource src, final float amnt) {
    if (super.attackEntityFrom(src, amnt)) {
      // if it's an arrow or something, set the attacker as revenge target
      if (src instanceof IndirectEntityDamageSource && src.getTrueSource() instanceof LivingEntity) {
        this.setRevengeTarget((LivingEntity) src.getTrueSource());
      }
      return true;
    }
    return false;
  }

  @Override
  public void dropInventory() {
    // drop all items in inventory
    for (int i = 0, l = this.inventory.getSizeInventory(); i < l; i++) {
      final ItemStack stack = this.inventory.getStackInSlot(i);
      if (!stack.isEmpty()) {
        this.entityDropItem(stack.copy());
        this.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
      }
    }
  }

  @Override
  public void readAdditional(final CompoundNBT tag) {
    super.readAdditional(tag);
    final ListNBT list = tag.getList(KEY_INVENTORY, 10);
    initInventory();

    for (int i = 0; i < list.size(); i++) {
      CompoundNBT slotNBT = list.getCompound(i);
      int slotNum = slotNBT.getByte(KEY_SLOT) & 0xFF;

      if (slotNum >= 0 && slotNum < this.inventory.getSizeInventory()) {
        this.inventory.setInventorySlotContents(slotNum, ItemStack.read(slotNBT));
      }
    }
    onInventoryChanged(this.inventory);
  }

  @Override
  public void writeAdditional(final CompoundNBT tag) {
    super.writeAdditional(tag);
    ListNBT listNBT = new ListNBT();
    for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
      ItemStack stack = this.inventory.getStackInSlot(i);

      if (!stack.isEmpty()) {
        CompoundNBT slotNBT = new CompoundNBT();
        slotNBT.putByte(KEY_SLOT, (byte) i);
        stack.write(slotNBT);
        listNBT.add(slotNBT);
      }
    }
    tag.put(KEY_INVENTORY, listNBT);
  }

  private void initInventory() {
    Inventory inv = this.inventory;
    this.inventory = new Inventory(INVENTORY_SIZE);
    if (inv != null) {
      inv.removeListener(this);
      int i = Math.min(inv.getSizeInventory(), this.inventory.getSizeInventory());
      for (int j = 0; j < i; j++) {
        ItemStack itemstack = inv.getStackInSlot(j);
        if (!itemstack.isEmpty()) {
          this.inventory.setInventorySlotContents(j, itemstack.copy());
        }
      }
    }
    this.inventory.addListener(this);
    onInventoryChanged(this.inventory);
  }

  @Override
  public void onInventoryChanged(final IInventory inv) {
    if (this.isServerWorld()) {
      this.updateCombatTask();
    }
  }

  private static ItemStack findArrows(final IInventory inv) {
    // search inventory to find suitable arrow itemstack
    for (int i = 0, l = inv.getSizeInventory(); i < l; i++) {
      final ItemStack stack = inv.getStackInSlot(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
        return stack;
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void attackEntityWithRangedAttack(final LivingEntity target, final float distanceFactor) {
    ItemStack itemstack = findArrows(this.inventory);
    if (!itemstack.isEmpty()) {
      // make an arrow out of the inventory
      AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, itemstack, distanceFactor);
      // set the arrow position and velocity
      arrow.posY = this.posY + this.getHeight() * 0.6F;
      double d0 = target.posX - this.posX;
      double d1 = target.getBoundingBox().minY + (double) (target.getHeight() / 3.0F) - arrow.posY;
      double d2 = target.posZ - this.posZ;
      double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
      arrow.setShooter(this);
      arrow.setDamage(arrowDamage + rand.nextDouble() * 0.5D);
      arrow.pickupStatus = PickupStatus.ALLOWED;
      arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, 1.2F);
      // play sound and add arrow to world
      this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.9F + rand.nextFloat() * 0.2F);
      this.world.addEntity(arrow);
      // update itemstack and inventory
      itemstack.shrink(1);
      this.onInventoryChanged(this.inventory);
    }
  }

  public void updateCombatTask() {
    updateCombatTask(!allowArrows);
  }

  public void updateCombatTask(final boolean forceMelee) {
    if (this.world != null && !this.world.isRemote) {
      // remove both goals (clean slate)
      this.goalSelector.removeGoal(this.aiMeleeAttack);
      this.goalSelector.removeGoal(this.aiArrowAttack);
      // check if target is close enough to attack
      final ItemStack ammo = findArrows(this.inventory);
      if (forceMelee || ammo.isEmpty()) {
        this.goalSelector.addGoal(0, this.aiMeleeAttack);
      } else {
        this.goalSelector.addGoal(0, aiArrowAttack);
      }
    }
  }
}
