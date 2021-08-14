package com.mcmoddev.golems.entity.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.GolemItems;
import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.blocks.BlockUtilityPower;
import com.mcmoddev.golems.entity.ai.GoToWaterGoal;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.ai.SwimUpGoal;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemContainer.SwimMode;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends IronGolem {

  protected static final EntityDataAccessor<Boolean> CHILD = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.BOOLEAN);
  protected static final String KEY_CHILD = "isChild";
  protected static final String KEY_BANNER = "Banner";
  
  public static final String ALLOW_LIGHT = "Allow Special: Light";
  public static final String ALLOW_POWER = "Allow Special: Power";

  private final GolemContainer container;

  // swimming helpers
  protected final WaterBoundPathNavigation waterNavigator;
  protected final GroundPathNavigation groundNavigator;
  protected boolean swimmingUp;
  
  public GolemBase(EntityType<? extends GolemBase> type, Level world) {
    super(type, world);
    this.container = GolemRegistrar.getContainer(type);
    // the following will be unused if swimming is not enabled
    this.waterNavigator = new WaterBoundPathNavigation(this, world);
    this.groundNavigator = new GroundPathNavigation(this, world);
    // define behavior for the given swimming ability
    switch (container.getSwimMode()) {
    case FLOAT:
      // basic swimming AI
      this.goalSelector.addGoal(0, new FloatGoal(this));
      break;
    case SWIM:
      // advanced swimming AI
      this.maxUpStep = 1.0F;
      this.moveControl = new SwimmingMovementController(this);
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.goalSelector.addGoal(1, new GoToWaterGoal(this, 14, 1.0D));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8F, 200));
      this.goalSelector.addGoal(5, new SwimUpGoal(this, 1.0D, this.level.getSeaLevel()));
      break;
    case SINK:
    default:
      // no swimming AI
      break;
    }
  }

  /**
   * Called after construction when a golem is built by a player
   * 
   * @param body
   * @param legs
   * @param arm1
   * @param arm2
   */
  public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
    // do nothing
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(CHILD, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    final GolemContainer cont = this.getGolemContainer();
    // register light level AI if enabled
    if(cont.getLightLevel() > 0 && getConfigBool(ALLOW_LIGHT)) {
      int lightInt = cont.getLightLevel();
      final BlockState state = GolemItems.UTILITY_LIGHT.defaultBlockState().setValue(BlockUtilityGlow.LIGHT_LEVEL, lightInt);
      this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, BlockUtilityGlow.UPDATE_TICKS, 
          true, null));
    }
    // register power level AI if enabled
    if(cont.getPowerLevel() > 0 && getConfigBool(ALLOW_POWER)) {
      int powerInt = cont.getPowerLevel();
      final BlockState state = GolemItems.UTILITY_POWER.defaultBlockState().setValue(BlockUtilityPower.POWER_LEVEL, powerInt);
      final int freq = BlockUtilityPower.UPDATE_TICKS;
      this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, freq));
    }
  }

  /////////////// GOLEM UTILITY METHODS //////////////////

  /**
   * Whether right-clicking on this entity triggers a texture change.
   *
   * @return True if this is a {@link IMultiTexturedGolem} AND the config option
   *         is enabled.
   **/
  public boolean canInteractChangeTexture() {
    return ExtraGolemsConfig.enableTextureInteract() && this instanceof IMultiTexturedGolem;
  }

  /**
   * Whether this golem provides light (by placing light source blocks). Does not
   * change any behavior, but is used in the Light Block code to determine if it
   * can stay (called AFTER light is placed).
   *
   * @see com.mcmoddev.golems.blocks.BlockUtilityGlow
   **/
  public boolean isProvidingLight() {
    return this.getGolemContainer().getLightLevel() > 0;
  }

  /**
   * Whether this golem provides power (by placing power source blocks). Does not
   * change any behavior, but is used in the Power Block code to determine if it
   * can stay.
   *
   * @see com.mcmoddev.golems.blocks.BlockUtilityPower
   **/
  public boolean isProvidingPower() {
    return this.getGolemContainer().getPowerLevel() > 0;
  }

  /** @return the Golem Container **/
  public GolemContainer getGolemContainer() {
    return container != null ? container : GolemRegistrar.getContainer(this.getType().getRegistryName());
  }

  /**
   * @param i the ItemStack being used to heal the golem
   * @return the amount by which this item should heal the golem, in half-hearts.
   *         Defaults to 25% of max health or 32.0, whichever is smaller
   **/
  public float getHealAmount(final ItemStack i) {
    float amount = (float) (this.getMaxHealth() * this.getGolemContainer().getHealAmount(i.getItem()));
    if(this.isBaby()) {
      amount *= 1.75F;
    }
    // max heal amount is 64, for no reason at all
    return Math.min(amount, 64.0F);
  }

  public BlockPos getBlockBelow() {
//    int i = MathHelper.floor(this.getPosX());
//    int j = MathHelper.floor(this.getPosY() - 0.2D);
//    int k = MathHelper.floor(this.getPosZ());
//    return new BlockPos(i, j, k);
    return getBlockPosBelowThatAffectsMyMovement();
  }
  
  public ItemStack getBanner() { return this.getItemBySlot(EquipmentSlot.CHEST); }
  
  public void setBanner(final ItemStack bannerItem) { 
    this.setItemSlot(EquipmentSlot.CHEST, bannerItem);
    if(bannerItem.getItem() instanceof BannerItem) {
      this.setDropChance(EquipmentSlot.CHEST, 1.0F);
    }
  }

  /////////////// CONFIG HELPERS //////////////////

  /**
   * @param name the name of the config value
   * @return the config value, or null if none is found
   **/
  public ForgeConfigSpec.ConfigValue getConfigValue(final String name) {
    final GolemContainer cont = this.getGolemContainer();
    final GolemSpecialContainer special = cont.getSpecialContainer(name);
    if(null == special) {
      ExtraGolems.LOGGER.error("Tried to access config value '" + name + "' in golem '" 
          + cont.getName() + "' but no config container was found!");
      return null;
    } else if(!ExtraGolemsConfig.GOLEM_CONFIG.specials.containsKey(special)) {
      ExtraGolems.LOGGER.error("Tried to access config value '" + name + "' in golem '"
          + cont.getName() + "' but the config value was not registered!");
      return null;
    }
    return (ExtraGolemsConfig.GOLEM_CONFIG.specials.get(special)).value;
  }
  
  /**
   * @param name the name of the config value
   * @return the config value, or false if none is found
   **/
  public boolean getConfigBool(final String name) {
    ForgeConfigSpec.ConfigValue v = getConfigValue(name);
    if(null == v) {
      return false;
    }
    return (Boolean) v.get();
  }

  /**
   * @param name the name of the config value
   * @return the config value, or 0 if none is found
   **/
  public int getConfigInt(final String name) {
    ForgeConfigSpec.ConfigValue v = getConfigValue(name);
    if(null == v) {
      return 0;
    }
    return (Integer) v.get();
  }

  /**
   * @param name the name of the config value
   * @return the config value, or 0 if none is found
   **/
  public double getConfigDouble(final String name) {
    ForgeConfigSpec.ConfigValue v = getConfigValue(name);
    if(null == v) {
      return 0.0D;
    }
    return (Double) v.get();
  }

  /////////////// OVERRIDEN BEHAVIOR //////////////////

  // fall(float, float)
  @Override
  public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
    if (!container.takesFallDamage()) {
      return false;
    }

    float[] ret = net.minecraftforge.common.ForgeHooks.onLivingFall(this, distance, damageMultiplier);
    if (ret == null) return false;
    distance = ret[0];
    damageMultiplier = ret[1];

    boolean flag = super.causeFallDamage(distance, damageMultiplier, source);
    int i = this.calculateFallDamage(distance, damageMultiplier);
    if (i > 0) {
       this.playSound(this.getFallDamageSound(i), 1.0F, 1.0F);
       this.playBlockFallSound();
       this.hurt(DamageSource.FALL, (float)i);
       return true;
    } else {
       return flag;
    }
  }
  
  @Override
  public boolean ignoreExplosion() {
    return this.getGolemContainer().isImmuneToExplosions();
  }

  @Override
  public boolean canAttackType(final EntityType<?> type) {
    if (type == EntityType.PLAYER && this.isPlayerCreated()) {
      return ExtraGolemsConfig.enableFriendlyFire();
    }
    if (type == EntityType.VILLAGER || type.getRegistryName().toString().contains("golem")) {
      return false;
    }
    return super.canAttackType(type);
  }

  @Override
  public ItemStack getPickedResult(final HitResult ray) {
    final Block block = container.getPrimaryBuildingBlock();
    return block != null ? new ItemStack(block) : ItemStack.EMPTY;
  }

  @Override
  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    // Attempt to remove banner from the golem
    if(!this.getBanner().isEmpty() && stack.getItem() instanceof ShearsItem) {
      this.spawnAtLocation(this.getBanner(), this.isBaby() ? 0.9F : 1.4F);
      this.setBanner(ItemStack.EMPTY);
    }
    // Attempt to place a banner on the golem
    if(stack.getItem() instanceof BannerItem && processInteractBanner(player, hand, stack)) {
      return InteractionResult.CONSUME;
    }
    // Attempt to heal the golem
    final float healAmount = getHealAmount(stack);
    if (healAmount > 0 && processInteractHeal(player, hand, stack, healAmount)) {
      return InteractionResult.CONSUME;
    }
    return super.mobInteract(player, hand);
  }
  
  /**
   * Called when the player uses an item that might be a banner
   * @param player the player using the item
   * @param hand the player hand
   * @param stack the item being used
   * @param healAmount the amount of health this item will restore
   * @return true if the item was consumed
   */
  protected boolean processInteractBanner(final Player player, final InteractionHand hand, final ItemStack stack) {
    if(!this.getBanner().isEmpty()) {
      this.spawnAtLocation(this.getBanner(), this.isBaby() ? 0.9F : 1.4F);
    }
    setBanner(stack.split(1));
    return true;
  }
  
  /**
   * Called when the player uses an item that can heal this golem
   * @param player the player using the item
   * @param hand the player hand
   * @param stack the item being used
   * @param healAmount the amount of health this item will restore
   * @return true if the item was consumed
   */
  protected boolean processInteractHeal(final Player player, final InteractionHand hand, final ItemStack stack, final float healAmount) {
    if (ExtraGolemsConfig.enableHealGolems() && this.getHealth() < this.getMaxHealth()) {
      heal(healAmount);
      // update stack size/item
      if(!player.isCreative()) {
        if (stack.getCount() > 1) {
          stack.shrink(1);
        } else {
          // update the player's held item
          player.setItemInHand(hand, stack.getContainerItem());
        }
      }
      // if currently attacking this player, stop
      if (this.getTarget() == player) {
        this.setLastHurtByMob(null);
        this.setTarget(null);
      }
      // spawn particles and play sound
      final Vec3 pos = this.position();
      ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.15D, ParticleTypes.INSTANT_EFFECT, 30);
      this.playSound(SoundEvents.STONE_PLACE, 0.85F, 1.1F + random.nextFloat() * 0.2F);
      return true;
    }
    return false;
  }
  
  @Override
  public float getBrightness() {
    return this.isProvidingLight() || this.isProvidingPower() ? 1.0F : super.getBrightness();
  }
  
  ///////////////// CHILD LOGIC ///////////////////

  @Override
  public boolean isBaby() {
    return this.getEntityData().get(CHILD).booleanValue();
  }

  /** Update whether this entity is 'child' and recalculate size **/
  public void setBaby(final boolean isChild) {
    if (this.getEntityData().get(CHILD).booleanValue() != isChild) {
      this.getEntityData().set(CHILD, Boolean.valueOf(isChild));
      this.refreshDimensions();
    }
  }
  
  @Override
  public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
    super.onSyncedDataUpdated(key);
    if (CHILD.equals(key)) {
      if (this.isBaby()) {
        // truncate these values to one decimal place after reducing them from base values
        double childHealth = (Math.floor(getGolemContainer().getHealth() * 0.3D * 10D)) / 10D;
        double childAttack = (Math.floor(getGolemContainer().getAttack() * 0.6D * 10D)) / 10D;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(childHealth);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(childAttack);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
      } else {
        // use full values for non-child golem
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(getGolemContainer().getHealth());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getGolemContainer().getAttack());
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(getGolemContainer().getKnockbackResist());
      }
      // recalculate size
      this.refreshDimensions();
    }
  }
  
  /**
   * Attempts to spawn the given number of "mini" golems
   * @param count the number of children to spawn
   * @return a collection containing the entities that were spawned
   **/
  protected Collection<GolemBase> trySpawnChildren(final int count) {
    final List<GolemBase> children = new ArrayList<>();
    if(!this.level.isClientSide && !this.isBaby()) {
      for(int i = 0; i < count; i++) {
        GolemBase child = this.getGolemContainer().getEntityType().create(this.level);
        child.setBaby(true);
        if (this.getTarget() != null) {
          child.setTarget(this.getTarget());
        }
        // set location
        child.copyPosition(this);
        // spawn the entity
        this.getCommandSenderWorld().addFreshEntity(child);
        // add to the list
        children.add(child);
      }
    }
    return children;
  }
  
  //////////////// NBT /////////////////

  @Override
  public void readAdditionalSaveData(final CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    this.setBaby(tag.getBoolean(KEY_CHILD));
  }

  @Override
  public void addAdditionalSaveData(final CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putBoolean(KEY_CHILD, this.isBaby());
  }

  ///////////////////// SOUND OVERRIDES ////////////////////

  @Override
  protected SoundEvent getAmbientSound() {
    return getGolemSound();
  }

  @Override
  protected SoundEvent getHurtSound(final DamageSource ignored) {
    return getGolemSound() == SoundEvents.GLASS_STEP ? SoundEvents.GLASS_HIT : getGolemSound();
  }

  @Override
  protected SoundEvent getDeathSound() {
    return getGolemSound() == SoundEvents.GLASS_STEP ? SoundEvents.GLASS_BREAK : getGolemSound();
  }

  /**
   * @return A SoundEvent to play when the golem is attacking, walking, hurt, and
   *         on death
   **/
  public final SoundEvent getGolemSound() {
    return container.getSound();
  }

  ///////////////////// SWIMMING BEHAVIOR ////////////////////////

  @Override
  public void travel(final Vec3 vec) {
    if (isEffectiveAi() && isInWater() && isSwimmingUp()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getDeltaMovement());
      setDeltaMovement(getDeltaMovement().scale(0.9D));
    } else {
      super.travel(vec);
    }
  }

  @Override
  public void updateSwimming() {
    if (container.getSwimMode() != SwimMode.SWIM) {
      super.updateSwimming();
      return;
    }
    if (!this.level.isClientSide) {
      if (isEffectiveAi() && isInWater() && isSwimmingUp()) {
        this.navigation = this.waterNavigator;
        setSwimming(true);
      } else {
        this.navigation = this.groundNavigator;
        setSwimming(false);
      }
    }
  }

  @Override
  protected float getWaterSlowDown() {
    return container.getSwimMode() == SwimMode.SWIM ? 0.88F : super.getWaterSlowDown();
  }

  @Override
  public boolean isPushedByFluid() {
    return !isSwimming();
  }

  public void setSwimmingUp(boolean isSwimmingUp) {
    this.swimmingUp = (isSwimmingUp && container.getSwimMode() == SwimMode.SWIM);
  }

  public boolean isSwimmingUp() {
    if (container.getSwimMode() != SwimMode.SWIM) {
      return false;
    }
    if (this.swimmingUp) {
      return true;
    }
    LivingEntity e = getTarget();
    return e != null && e.isInWater();
  }

  public static boolean isSwimmingUp(final GolemBase golem) {
    return golem.swimmingUp;
  }

  /**
   * Referenced from {@link GoToWaterGoal}.
   * 
   * @param target a location representing a water block
   * @return true if the golem should move towards the water
   **/
  public boolean shouldMoveToWater(final Vec3 target) {
    return container.getSwimMode() == SwimMode.SWIM;
  }

  static class SwimmingMovementController extends MoveControl {
    private final GolemBase golem;

    public SwimmingMovementController(GolemBase golemIn) {
      super(golemIn);
      this.golem = golemIn;
   }

    public void tick() {
      LivingEntity livingentity = this.golem.getTarget();
      if (this.golem.isSwimmingUp() && this.golem.isInWater()) {
        if (livingentity != null && livingentity.getY() > this.golem.getY() || this.golem.swimmingUp) {
          this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
        }

        if (this.operation != MoveControl.Operation.MOVE_TO || this.golem.getNavigation().isDone()) {
          this.golem.setSpeed(0.0F);
          return;
        }

        double d0 = this.wantedX - this.golem.getX();
        double d1 = this.wantedY - this.golem.getY();
        double d2 = this.wantedZ - this.golem.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d1 = d1 / d3;
        float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
        this.golem.setYRot(this.rotlerp(this.golem.getYRot(), f, 90.0F));
        this.golem.yBodyRot = this.golem.getYRot();
        float f1 = (float) (this.speedModifier * this.golem.getAttributeValue(Attributes.MOVEMENT_SPEED));
        float f2 = Mth.lerp(0.125F, this.golem.getSpeed(), f1);
        this.golem.setSpeed(f2);
        this.golem.setDeltaMovement(
            this.golem.getDeltaMovement().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
      } else {
        if (!this.golem.onGround) {
          this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
        }

        super.tick();
      }

    }
  }
}
