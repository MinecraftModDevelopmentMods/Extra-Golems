package com.mcmoddev.golems.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import com.mcmoddev.golems.container.ContainerDispenserGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public final class DispenserGolem extends GolemBase implements IRangedAttackMob, IInventoryChangedListener {
  private static final DataParameter<Integer> ARROWS = EntityDataManager.createKey(DispenserGolem.class, DataSerializers.VARINT);
  
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
  
  protected final Predicate<? super ItemEntity> pickUpItemstackPredicate = e -> {
    // make sure the item is an arrow
    if(!e.cannotPickup() && e.getItem().getItem() instanceof ArrowItem) {
      // make sure the golem can pick up this stack
      for (int i = 0, l = this.inventory.getSizeInventory(); i < l; i++) {
        final ItemStack stack = this.inventory.getStackInSlot(i);
        if (stack.isEmpty() || (stack.getItem() == e.getItem().getItem() && stack.getCount() + e.getItem().getCount() <= stack.getMaxStackSize())) {
          return true;
        }
      }
    }
    
    return false;
  };

  public DispenserGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    // set config values
    this.allowArrows = this.getConfigBool(ALLOW_SPECIAL);
    this.arrowDamage = Math.max(0D, this.getConfigDouble(ARROW_DAMAGE));
    this.arrowSpeed = this.getConfigInt(ARROW_SPEED);
    // init combat AI
    aiArrowAttack = new RangedAttackGoal(this, 1.0D, arrowSpeed, 32.0F);
    aiMeleeAttack = new MeleeAttackGoal(this, 1.0D, true);
    this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32.0F);
    // init inventory
    this.initInventory();
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(ARROWS, Integer.valueOf(0));
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new FindArrowsGoal(10.0D));
    // TODO make Goal so golem stays in place while player looks at inventory
  }

  @Override
  protected void collideWithEntity(Entity entityIn) {
    super.collideWithEntity(entityIn);
    this.updateCombatTask(entityIn != null && this.getRevengeTarget() != null && entityIn == this.getRevengeTarget());
  }

  @Override
  public void livingTick() {
    super.livingTick();
    // update combat style every few seconds
    if (this.ticksExisted % 50 == 0) {
      final boolean forceMelee = !allowArrows || (this.getRevengeTarget() != null && this.getRevengeTarget().getDistanceSq(this) < 4.5D);
      this.updateCombatTask(forceMelee);
    }
    // pick up any arrow items that are nearby
    // note: does not pick up arrow entities, only itemstacks for now
    this.world.getProfiler().startSection("dispenserGolemLooting");
    if (!this.world.isRemote && this.isAlive() && !this.dead 
        && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)
        && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
      // make a list of nearby itemstacks containing arrows
      final List<ItemEntity> droppedArrows = DispenserGolem.this.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, 
          DispenserGolem.this.getBoundingBox().grow(1.0, 0, 1.0), 
          e -> !e.cannotPickup() && e.getItem().getItem() instanceof ArrowItem);
      // if any dropped arrow itemstacks are found, try to add them to the inventory
      for (final ItemEntity i : droppedArrows) {
        final int arrowCountBefore = this.getArrowsInInventory();
        final ItemStack item = i.getItem().copy();
        i.setItem(this.inventory.addItem(item));
        final int arrowCountAfter = this.updateArrowsInInventory();
        this.onItemPickup(i, arrowCountAfter - arrowCountBefore);
      }
    }
    this.world.getProfiler().endSection();
  }

  @Override
  protected ActionResultType func_230254_b_(final PlayerEntity player, final Hand hand) { // processInteract
    if (!player.isCrouching() && player instanceof ServerPlayerEntity) {
      // open dispenser GUI by sending request to server
      NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerDispenserGolem.Provider(inventory));
      player.swingArm(hand);
      return ActionResultType.SUCCESS;
    }
    return super.func_230254_b_(player, hand);
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
    this.onInventoryChanged(inventory);
  }

  @Override
  public void readAdditional(final CompoundNBT tag) {
    super.readAdditional(tag);
    final ListNBT list = tag.getList(KEY_INVENTORY, 10);
    initInventory();
    // read inventory slots from NBT
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
    // write inventory slots to NBT
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
      this.updateArrowsInInventory();
      this.updateCombatTask();
    }
  }

  private static ItemStack findArrowsInInventory(final IInventory inv) {
    // search inventory to find suitable arrow itemstack
    for (int i = 0, l = inv.getSizeInventory(); i < l; i++) {
      final ItemStack stack = inv.getStackInSlot(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
        return stack;
      }
    }
    return ItemStack.EMPTY;
  }
  
  private int updateArrowsInInventory() {
    int arrowCount = 0;
    // add up the size of each itemstack in inventory
    for (int i = 0, l = this.inventory.getSizeInventory(); i < l; i++) {
      final ItemStack stack = this.inventory.getStackInSlot(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
        arrowCount += stack.getCount();
      }
    }
    // update data manager if necessary
    if(arrowCount != getArrowsInInventory()) {
      this.getDataManager().set(ARROWS, arrowCount);
    }
    // return arrow count
    return arrowCount;
  }
  
  public int getArrowsInInventory() {
    return this.getDataManager().get(ARROWS).intValue();
  }
  
  public int getMaxArrowsInInventory() {
    return INVENTORY_SIZE * 64;
  }

  @Override
  public void attackEntityWithRangedAttack(final LivingEntity target, final float distanceFactor) {
    ItemStack itemstack = findArrowsInInventory(this.inventory);
    if (!itemstack.isEmpty()) {
      // make an arrow out of the inventory
      AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, itemstack, distanceFactor);
      // set the arrow position and velocity
      final Vector3d myPos = this.getPositionVec();
      arrow.setPosition(myPos.x, myPos.y + this.getHeight() * 0.66666F, myPos.z);
      double d0 = target.getPosX() - this.getPosX();
      double d1 = target.getPosYHeight(1.0D / 3.0D) - arrow.getPosY();
      double d2 = target.getPosZ() - this.getPosZ();
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
      if (forceMelee || this.getArrowsInInventory() == 0) {
        this.goalSelector.addGoal(0, this.aiMeleeAttack);
      } else {
        this.goalSelector.addGoal(0, aiArrowAttack);
      }
    }
  }
  
  public class FindArrowsGoal extends Goal {
    protected final double range;
    
    public FindArrowsGoal(final double rangeIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      range = rangeIn;
    }

    @Override
    public boolean shouldExecute() {
      return DispenserGolem.this.allowArrows 
          && DispenserGolem.this.getArrowsInInventory() < DispenserGolem.this.getMaxArrowsInInventory();
      }
    
    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }
    
    @Override
    public void tick() {
      final List<ItemEntity> droppedArrows = DispenserGolem.this.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, 
          DispenserGolem.this.getBoundingBox().grow(range), pickUpItemstackPredicate);
      // path toward the nearest dropped arrow item
      if (!droppedArrows.isEmpty()) {
        droppedArrows.sort((e1, e2) -> (int)(DispenserGolem.this.getDistanceSq(e1) - DispenserGolem.this.getDistanceSq(e2)));
        DispenserGolem.this.getNavigator().tryMoveToEntityLiving(droppedArrows.get(0), 1.2D);
      }
    }

    @Override
    public void startExecuting() {
       tick();
    }
  }
}
