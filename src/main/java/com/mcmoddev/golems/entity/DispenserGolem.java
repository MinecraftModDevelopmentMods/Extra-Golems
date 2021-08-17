//package com.mcmoddev.golems.entity;
//
//import java.util.EnumSet;
//import java.util.List;
//import java.util.function.Predicate;
//
//import com.mcmoddev.golems.entity.base.GolemBase;
//import com.mcmoddev.golems.menu.ContainerDispenserGolem;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.network.syncher.EntityDataAccessor;
//import net.minecraft.network.syncher.EntityDataSerializers;
//import net.minecraft.network.syncher.SynchedEntityData;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.util.Mth;
//import net.minecraft.world.Container;
//import net.minecraft.world.ContainerListener;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.damagesource.DamageSource;
//import net.minecraft.world.damagesource.IndirectEntityDamageSource;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.ai.goal.Goal;
//import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
//import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
//import net.minecraft.world.entity.item.ItemEntity;
//import net.minecraft.world.entity.monster.RangedAttackMob;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.entity.projectile.AbstractArrow;
//import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
//import net.minecraft.world.entity.projectile.ProjectileUtil;
//import net.minecraft.world.item.ArrowItem;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.GameRules;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.Vec3;
//import net.minecraftforge.fmllegacy.network.NetworkHooks;
//
//public final class DispenserGolem extends GolemBase implements RangedAttackMob, ContainerListener {
//  private static final EntityDataAccessor<Integer> ARROWS = SynchedEntityData.defineId(DispenserGolem.class, EntityDataSerializers.INT);
//  
//  public static final String ALLOW_SPECIAL = "Allow Special: Shoot Arrows";
//  public static final String ARROW_DAMAGE = "Arrow Damage";
//  public static final String ARROW_SPEED = "Arrow Speed";
//
//  private static final String KEY_INVENTORY = "Items";
//  private static final String KEY_SLOT = "Slot";
//  private static final int INVENTORY_SIZE = 9;
//
//  private boolean allowArrows;
//  private double arrowDamage;
//  private int arrowSpeed;
//  private SimpleContainer inventory;
//
//  private final RangedAttackGoal aiArrowAttack;
//  private final MeleeAttackGoal aiMeleeAttack;
//    
//  protected final Predicate<ItemStack> pickUpItemstackPredicate = stack -> {
//    // make sure the item is an arrow
//    if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
//      // make sure the entity can pick up this stack
//      for (int i = 0, l = this.inventory.getContainerSize(); i < l; i++) {
//        final ItemStack invStack = this.inventory.getItem(i);
//        if (invStack.isEmpty() || (invStack.getItem() == stack.getItem()
//            && ItemStack.tagMatches(invStack, stack) 
//            && invStack.getCount() + stack.getCount() <= invStack.getMaxStackSize())) {
//          return true;
//        }
//      }
//    }
//    return false;
//  };
//
//  public DispenserGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
//    super(entityType, world);
//    // set config values
//    this.allowArrows = this.getConfigBool(ALLOW_SPECIAL);
//    this.arrowDamage = Math.max(0D, this.getConfigDouble(ARROW_DAMAGE));
//    this.arrowSpeed = this.getConfigInt(ARROW_SPEED);
//    // init combat AI
//    aiArrowAttack = new RangedAttackGoal(this, 1.0D, arrowSpeed, 32.0F);
//    aiMeleeAttack = new MeleeAttackGoal(this, 1.0D, true);
//    this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32.0F);
//    // init inventory
//    this.initInventory();
//  }
//  
//  @Override
//  protected void defineSynchedData() {
//    super.defineSynchedData();
//    this.getEntityData().define(ARROWS, Integer.valueOf(0));
//  }
//
//  @Override
//  protected void registerGoals() {
//    super.registerGoals();
//    this.goalSelector.addGoal(4, new MoveToArrowsGoal(this, 10.0D, 1.0D));
//    // TODO make Goal so entity stays in place while player looks at inventory
//  }
//
//  @Override
//  protected void doPush(Entity entityIn) {
//    super.doPush(entityIn);
//    this.updateCombatTask(entityIn != null && this.getLastHurtByMob() != null && entityIn == this.getLastHurtByMob());
//  }
//
//  @Override
//  public void aiStep() {
//    super.aiStep();
//    // update combat style every few seconds
//    if (this.tickCount % 50 == 0) {
//      final boolean forceMelee = !allowArrows || (this.getLastHurtByMob() != null && this.getLastHurtByMob().distanceToSqr(this) < 4.5D);
//      this.updateCombatTask(forceMelee);
//    }
//    // pick up nearby arrows
//    final List<ItemEntity> droppedArrows = this.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, 
//        this.getBoundingBox().inflate(1.0D), e -> !e.hasPickUpDelay() && pickUpItemstackPredicate.test(e.getItem()));
//   
//    // check a whole load of conditions to make sure we can pick up nearby arrows
//    if (!droppedArrows.isEmpty() && !this.level.isClientSide() && this.isAlive() && !this.dead 
//        && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
//        && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
//      // actually pick up the arrows
//      this.level.getProfiler().push("dispenserGolemLooting");
//      for (final ItemEntity i : droppedArrows) {
//        final int arrowCountBefore = this.getArrowsInInventory();
//        final ItemStack item = i.getItem().copy();
//        i.setItem(this.inventory.addItem(item));
//        final int arrowCountAfter = this.updateArrowsInInventory();
//        this.take(i, arrowCountAfter - arrowCountBefore);
//      }
//      this.level.getProfiler().pop();
//    }
//  }
//
//  @Override
//  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
//    if (!player.isCrouching() && player instanceof ServerPlayer) {
//      // open dispenser GUI by sending request to server
//      NetworkHooks.openGui((ServerPlayer) player, new ContainerDispenserGolem.Provider(inventory));
//      player.swing(hand);
//      return InteractionResult.SUCCESS;
//    }
//    return super.mobInteract(player, hand);
//  }
//
//  @Override
//  public boolean hurt(final DamageSource src, final float amnt) {
//    if (super.hurt(src, amnt)) {
//      // if it's an arrow or something, set the attacker as revenge target
//      if (src instanceof IndirectEntityDamageSource && src.getEntity() instanceof LivingEntity) {
//        this.setLastHurtByMob((LivingEntity) src.getEntity());
//      }
//      return true;
//    }
//    return false;
//  }
//
//  @Override
//  public void dropEquipment() {
//    // drop all items in inventory
//    for (int i = 0, l = this.inventory.getContainerSize(); i < l; i++) {
//      final ItemStack stack = this.inventory.getItem(i);
//      if (!stack.isEmpty()) {
//        this.spawnAtLocation(stack.copy());
//        this.inventory.setItem(i, ItemStack.EMPTY);
//      }
//    }
//    this.containerChanged(inventory);
//  }
//
//  @Override
//  public void readAdditionalSaveData(final CompoundTag tag) {
//    super.readAdditionalSaveData(tag);
//    final ListTag list = tag.getList(KEY_INVENTORY, 10);
//    initInventory();
//    // read inventory slots from NBT
//    for (int i = 0; i < list.size(); i++) {
//      CompoundTag slotNBT = list.getCompound(i);
//      int slotNum = slotNBT.getByte(KEY_SLOT) & 0xFF;
//      if (slotNum >= 0 && slotNum < this.inventory.getContainerSize()) {
//        this.inventory.setItem(slotNum, ItemStack.of(slotNBT));
//      }
//    }
//    containerChanged(this.inventory);
//  }
//
//  @Override
//  public void addAdditionalSaveData(final CompoundTag tag) {
//    super.addAdditionalSaveData(tag);
//    ListTag listNBT = new ListTag();
//    // write inventory slots to NBT
//    for (int i = 0; i < this.inventory.getContainerSize(); i++) {
//      ItemStack stack = this.inventory.getItem(i);
//      if (!stack.isEmpty()) {
//        CompoundTag slotNBT = new CompoundTag();
//        slotNBT.putByte(KEY_SLOT, (byte) i);
//        stack.save(slotNBT);
//        listNBT.add(slotNBT);
//      }
//    }
//    tag.put(KEY_INVENTORY, listNBT);
//  }
//
//  private void initInventory() {
//    SimpleContainer inv = this.inventory;
//    this.inventory = new SimpleContainer(INVENTORY_SIZE);
//    if (inv != null) {
//      inv.removeListener(this);
//      int i = Math.min(inv.getContainerSize(), this.inventory.getContainerSize());
//      for (int j = 0; j < i; j++) {
//        ItemStack itemstack = inv.getItem(j);
//        if (!itemstack.isEmpty()) {
//          this.inventory.setItem(j, itemstack.copy());
//        }
//      }
//    }
//    this.inventory.addListener(this);
//    containerChanged(this.inventory);
//  }
//
//  @Override
//  public void containerChanged(final Container inv) {
//    if (this.isEffectiveAi()) {
//      this.updateArrowsInInventory();
//      this.updateCombatTask();
//    }
//  }
//
//  private static ItemStack findArrowsInInventory(final Container inv) {
//    // search inventory to find suitable arrow itemstack
//    for (int i = 0, l = inv.getContainerSize(); i < l; i++) {
//      final ItemStack stack = inv.getItem(i);
//      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
//        return stack;
//      }
//    }
//    return ItemStack.EMPTY;
//  }
//  
//  private int updateArrowsInInventory() {
//    int arrowCount = 0;
//    // add up the size of each itemstack in inventory
//    for (int i = 0, l = this.inventory.getContainerSize(); i < l; i++) {
//      final ItemStack stack = this.inventory.getItem(i);
//      if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
//        arrowCount += stack.getCount();
//      }
//    }
//    // update data manager if necessary
//    if(arrowCount != getArrowsInInventory()) {
//      this.getEntityData().set(ARROWS, arrowCount);
//    }
//    // return arrow count
//    return arrowCount;
//  }
//  
//  public int getArrowsInInventory() {
//    return this.getEntityData().get(ARROWS).intValue();
//  }
//
//  @Override
//  public void performRangedAttack(final LivingEntity target, final float distanceFactor) {
//    ItemStack itemstack = findArrowsInInventory(this.inventory);
//    if (!itemstack.isEmpty()) {
//      // make an arrow out of the inventory
//      AbstractArrow arrow = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor);
//      // set the arrow position and velocity
//      final Vec3 myPos = this.position();
//      arrow.setPos(myPos.x, myPos.y + this.getBbHeight() * 0.66666F, myPos.z);
//      double d0 = target.getX() - this.getX();
//      double d1 = target.getY(1.0D / 3.0D) - arrow.getY();
//      double d2 = target.getZ() - this.getZ();
//      double d3 = (double) Mth.sqrt((float) (d0 * d0 + d2 * d2));
//      arrow.setOwner(this);
//      arrow.setBaseDamage(arrowDamage + random.nextDouble() * 0.5D);
//      arrow.pickup = Pickup.ALLOWED;
//      arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, 1.2F);
//      // play sound and add arrow to world
//      this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 0.9F + random.nextFloat() * 0.2F);
//      this.level.addFreshEntity(arrow);
//      // update itemstack and inventory
//      itemstack.shrink(1);
//      this.containerChanged(this.inventory);
//    }
//  }
//
//  public void updateCombatTask() {
//    updateCombatTask(!allowArrows);
//  }
//
//  public void updateCombatTask(final boolean forceMelee) {
//    if (!this.level.isClientSide()) {
//      // remove both goals (clean slate)
//      this.goalSelector.removeGoal(this.aiMeleeAttack);
//      this.goalSelector.removeGoal(this.aiArrowAttack);
//      // check if target is close enough to attack
//      if (forceMelee || this.getArrowsInInventory() == 0) {
//        this.goalSelector.addGoal(0, this.aiMeleeAttack);
//      } else {
//        this.goalSelector.addGoal(0, aiArrowAttack);
//      }
//    }
//  }
//  
//  public class MoveToArrowsGoal extends Goal {
//    protected final DispenserGolem golem;
//    protected final double range;
//    protected final double speed;
//    
//    public MoveToArrowsGoal(final DispenserGolem golemIn, final double rangeIn, final double speedIn) {
//      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
//      golem = golemIn;
//      range = rangeIn;
//      speed = speedIn;
//    }
//
//    @Override
//    public boolean canUse() {
//      return golem.allowArrows;
//      }
//    
//    @Override
//    public boolean canContinueToUse() {
//      return false;
//    }
//    
//    @Override
//    public void tick() {
//      // make a list of arrow itemstacks in nearby area
//      final List<ItemEntity> droppedArrows = golem.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, 
//          golem.getBoundingBox().inflate(range), e -> !e.hasPickUpDelay() && pickUpItemstackPredicate.test(e.getItem()));
//     
//      if (!droppedArrows.isEmpty()) {
//        // path toward the nearest arrow itemstack
//        droppedArrows.sort((e1, e2) -> (int)(golem.distanceToSqr(e1) - golem.distanceToSqr(e2)));
//        golem.getNavigation().moveTo(droppedArrows.get(0), speed);
//      }
//    }
//
//    @Override
//    public void start() {
//       tick();
//    }
//  }
//}
