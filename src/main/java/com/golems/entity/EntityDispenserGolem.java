package com.golems.entity;

import java.util.List;

import com.golems.container.ContainerDispenserGolem;
import com.golems.gui.GuiLoader;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityDispenserGolem extends GolemBase implements IRangedAttackMob, IInventoryChangedListener {
  public static final String ALLOW_SPECIAL = "Allow Special: Shoot Arrows";
  public static final String ARROW_DAMAGE = "Arrow Damage";
  public static final String ARROW_SPEED = "Arrow Speed";

  private static final String KEY_INVENTORY = "Items";
  private static final String KEY_SLOT = "Slot";
  private static final int INVENTORY_SIZE = 9;
  private static final Predicate ARROW_PREDICATE = new Predicate<EntityItem>() {
    @Override
    public boolean apply(final EntityItem i) {
      return i != null && i.getItem() != null && !i.cannotPickup() && i.getItem().getItem() instanceof ItemArrow;
    }
  };

  private boolean allowArrows;
  private double arrowDamage;
  private int arrowSpeed;
  private InventoryBasic inventory;

  private final EntityAIAttackRanged aiArrowAttack;
  private final EntityAIAttackMelee aiMeleeAttack;

  public EntityDispenserGolem(final World world) {
    super(world);
    this.setLootTableLoc(GolemNames.DISPENSER_GOLEM);
    this.addHealItem(new ItemStack(Blocks.COBBLESTONE), 0.25D);
    // set config values
    final GolemConfigSet cfg = getConfig(this);
    this.allowArrows = cfg.getBoolean(ALLOW_SPECIAL);
    this.arrowDamage = Math.max(0D, cfg.getFloat(ARROW_DAMAGE));
    this.arrowSpeed = cfg.getInt(ARROW_SPEED);
    // init combat AI
    aiArrowAttack = new EntityAIAttackRanged(this, 1.0D, arrowSpeed, 32.0F);
    aiMeleeAttack = new EntityAIAttackMelee(this, 1.0D, true);
    // init inventory
    this.initInventory();
  }

  @Override
  protected void initEntityAI() {
    super.initEntityAI();
    // TODO make Goal so golem stays in place while player looks at inventory
  }

  @Override
  protected void collideWithEntity(final Entity entityIn) {
    super.collideWithEntity(entityIn);
    this.updateCombatTask(entityIn != null && this.getAttackTarget() != null && entityIn == this.getAttackTarget());
  }

  @Override
  public boolean attackEntityFrom(final DamageSource source, final float amount) {
    if (super.attackEntityFrom(source, amount)) {
      // target the actual source of damage, eg, the skeleton that shot the arrow
      if (source.getTrueSource() instanceof EntityLivingBase) {
        this.setRevengeTarget((EntityLivingBase) source.getTrueSource());
      }
      return true;
    }
    return false;
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    // update combat style every few seconds
    if (this.ticksExisted % 50 == 0) {
      final boolean forceMelee = !allowArrows
          || (this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < 3.5D);
      this.updateCombatTask(forceMelee);
    }
    // pick up any arrow items that are nearby
    // note: does not pick up arrow entities, only itemstacks for now
    final int frequency = 24;
    final double range = 0.8D;
    final boolean gameRule = this.world.getGameRules().getBoolean("mobGriefing");
    if (this.isServerWorld() && gameRule && rand.nextInt(frequency) == 0) {
      // get a list of nearby entity items that contain arrows
      final List<EntityItem> itemList = this.world.getEntitiesWithinAABB(EntityItem.class,
          this.getEntityBoundingBox().expand(range, 0, range), ARROW_PREDICATE);
      // if any are found, try to add them to the inventory
      for (final EntityItem i : itemList) {
        final ItemStack item = i.getItem().copy();
        final ItemStack remainder = this.inventory.addItem(item);
//				if(remainder != null && remainder.stackSize > 0 && remainder.getItem() == i.getItem().getItem()) {
        i.setItem(remainder);
//				} else {
//					i.setEntityItemStack(new ItemStack(Blocks.AIR, 0));
//				}
      }
    }
  }

  @Override
  protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
    if (!player.isSneaking()) {
      if (player instanceof EntityPlayerMP) {
        // open dispenser GUI by sending request to server
        ((EntityPlayerMP) player).displayGui(new ContainerDispenserGolem.Provider(inventory));
        player.swingArm(hand);
      } else {
        // open dispenser GUI by sending request to server
        GuiLoader.loadDispenserGolemGui(player, this.inventory);
        player.swingArm(hand);
      }
      return true;
    }
    return super.processInteract(player, hand);
  }

  @Override
  public void dropEquipment(final boolean recentlyHit, final int lootingModifier) {
    super.dropEquipment(recentlyHit, lootingModifier);
    // drop all items in inventory
    for (int i = 0, l = this.inventory.getSizeInventory(); i < l; i++) {
      final ItemStack stack = this.inventory.getStackInSlot(i);
      if (!stack.isEmpty()) {
        this.entityDropItem(stack.copy(), 0.5F);
        this.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
      }
    }
  }

  @Override
  public void readEntityFromNBT(final NBTTagCompound tag) {
    super.readEntityFromNBT(tag);
    final NBTTagList list = tag.getTagList(KEY_INVENTORY, 10);
    initInventory();

    for (int i = 0; i < list.tagCount(); i++) {
      NBTTagCompound slotNBT = list.getCompoundTagAt(i);
      int slotNum = slotNBT.getByte(KEY_SLOT) & 0xFF;

      if (slotNum >= 0 && slotNum < this.inventory.getSizeInventory()) {
        this.inventory.setInventorySlotContents(slotNum, new ItemStack(slotNBT));
      }
    }
    onInventoryChanged(this.inventory);
  }

  @Override
  public void writeEntityToNBT(final NBTTagCompound tag) {
    super.writeEntityToNBT(tag);
    NBTTagList listNBT = new NBTTagList();
    for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
      ItemStack stack = this.inventory.getStackInSlot(i);

      if (!stack.isEmpty()) {
        NBTTagCompound slotNBT = new NBTTagCompound();
        slotNBT.setByte(KEY_SLOT, (byte) i);
        stack.writeToNBT(slotNBT);
        listNBT.appendTag(slotNBT);
      }
    }
    tag.setTag(KEY_INVENTORY, listNBT);
  }

  private void initInventory() {
    InventoryBasic inv = this.inventory;
    this.inventory = new InventoryBasic(this.getDisplayName().getFormattedText(), true, INVENTORY_SIZE);
    if (inv != null) {
      inv.removeInventoryChangeListener(this);
      int i = Math.min(inv.getSizeInventory(), this.inventory.getSizeInventory());
      for (int j = 0; j < i; j++) {
        ItemStack itemstack = inv.getStackInSlot(j);
        if (!itemstack.isEmpty()) {
          this.inventory.setInventorySlotContents(j, itemstack.copy());
        }
      }
    }
    this.inventory.addInventoryChangeListener(this);
    onInventoryChanged(this.inventory);
  }

  @Override
  public void onInventoryChanged(final IInventory inv) {
    if (this.isServerWorld()) {
      this.updateCombatTask();
    }
  }

  /**
   * Searches an inventory for the first slot that contains arrows
   * 
   * @param inv the inventory to search
   * @return the slot index, or -1 if no arrows are found
   **/
  private static int findArrowIndex(final IInventory inv) {
    // search inventory to find suitable arrow itemstack
    for (int i = 0, l = inv.getSizeInventory(); i < l; i++) {
      final ItemStack stack = inv.getStackInSlot(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ItemArrow) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Searches the inventory for an ItemStack that contains arrows
   * 
   * @param inv the inventory to search
   * @return the arrow ItemStack, or null if none are found
   **/
  private static ItemStack findArrows(final IInventory inv) {
    final int slot = findArrowIndex(inv);
    if (slot < 0 || slot > inv.getSizeInventory()) {
      return ItemStack.EMPTY;
    }
    return inv.getStackInSlot(slot);
  }

  /**
   * @param inv the inventory to search
   * @return the total number of arrows in the inventory. May be zero.
   **/
  private static int countArrows(final IInventory inv) {
    // search inventory to find suitable arrow itemstack
    if (inv == null) {
      return 0;
    }
    int arrowCount = 0;
    for (int i = 0, l = inv.getSizeInventory(); i < l; i++) {
      final ItemStack stack = inv.getStackInSlot(i);
      if (!stack.isEmpty() && stack.getItem() instanceof ItemArrow) {
        arrowCount += stack.getCount();
      }
    }
    return arrowCount;
  }

  @Override
  public void attackEntityWithRangedAttack(final EntityLivingBase target, final float distanceFactor) {
    final int slot = findArrowIndex(this.inventory);
    ItemStack itemstack = slot < 0 ? ItemStack.EMPTY : this.inventory.getStackInSlot(slot);
    if (!itemstack.isEmpty() && target != null && target.isEntityAlive()) {
      final boolean spectral = itemstack.getItem() == Items.SPECTRAL_ARROW;
      EntityArrow arrow;
      if (spectral) {
        arrow = new EntitySpectralArrow(this.world, this);
      } else {
        arrow = new EntityTippedArrow(this.world, this);
        ((EntityTippedArrow) arrow).setPotionEffect(itemstack);
      }
      arrow.posY = this.posY + this.height * 0.6F;
      double d0 = target.posX - this.posX;
      double d1 = target.getEntityBoundingBox().minY + (target.height / 3.0D) - arrow.posY;
      double d2 = target.posZ - this.posZ;
      double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
      arrow.setDamage(arrowDamage + rand.nextDouble() * 0.5D);
      arrow.pickupStatus = PickupStatus.ALLOWED;
      arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, 1.2F);
      // play sound and add arrow to world
      this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.9F + rand.nextFloat() * 0.2F);
      this.world.spawnEntity(arrow);
      // update itemstack and inventory
      itemstack.shrink(1);
      this.inventory.setInventorySlotContents(slot, itemstack);
      this.onInventoryChanged(this.inventory);
    }
  }

  @Override
  public void setSwingingArms(boolean swingingArms) {
  }

  public void updateCombatTask() {
    updateCombatTask(!allowArrows);
  }

  public void updateCombatTask(final boolean forceMelee) {
    if (this.world != null && !this.world.isRemote) {
      // remove both goals (clean slate)
      this.tasks.removeTask(this.aiMeleeAttack);
      this.tasks.removeTask(this.aiArrowAttack);
      // check if target is close enough to attack
      final ItemStack ammo = findArrows(this.inventory);
      if (forceMelee || ammo.isEmpty()) {
        this.tasks.addTask(0, this.aiMeleeAttack);
      } else {
        this.tasks.addTask(0, aiArrowAttack);
      }
    }
  }

  @Override
  protected ResourceLocation applyTexture() {
    return makeTexture(ExtraGolems.MODID, GolemNames.DISPENSER_GOLEM);
  }

  @Override
  public SoundEvent getGolemSound() {
    return SoundEvents.BLOCK_STONE_STEP;
  }

  @Override
  public List<String> addSpecialDesc(final List<String> list) {
    if (getConfig(this).getBoolean(ALLOW_SPECIAL)) {
      list.add(TextFormatting.LIGHT_PURPLE + trans("entitytip.shoots_arrows"));
      // final int arrowCount = countArrows(this.inventory);
      // if(arrowCount > 0) {
      // TODO this number only updates client-side after opening inventory
      // list.add(TextFormatting.GRAY + trans("entitytip.arrows") + ": " +
      // TextFormatting.WHITE + arrowCount);
      // } else {
      list.add(TextFormatting.GRAY + trans("entitytip.click_refill"));
      // }
    }
    return list;
  }
}
