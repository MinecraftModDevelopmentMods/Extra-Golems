package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.GolemContainer.SwimMode;
import com.mcmoddev.golems.container.behavior.GolemBehavior;
import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.container.behavior.UseFuelBehavior;
import com.mcmoddev.golems.entity.goal.GoToWaterGoal;
import com.mcmoddev.golems.entity.goal.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.goal.SwimUpGoal;
import com.mcmoddev.golems.item.SpawnGolemItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
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
import net.minecraftforge.fmllegacy.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

/**
 * Base class for all golems in this mod.
 **/
public class GolemBase extends IronGolem implements IMultitextured, IFuelConsumer, IRandomTeleporter, IRandomExploder, IEntityAdditionalSpawnData {
  
  protected static final EntityDataAccessor<Boolean> CHILD = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.BOOLEAN);
  protected static final EntityDataAccessor<Byte> TEXTURE = SynchedEntityData.<Byte>defineId(GolemBase.class, EntityDataSerializers.BYTE);
  protected static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.<Integer>defineId(GolemBase.class, EntityDataSerializers.INT);

  protected static final String KEY_MATERIAL = "Material";
  protected static final String KEY_CHILD = "IsChild";
  protected static final String KEY_BANNER = "Banner";

  private ResourceLocation material = GolemContainer.EMPTY.getMaterial();
  private GolemContainer container = GolemContainer.EMPTY;
  
  protected Component description;

  // swimming helpers
  protected final WaterBoundPathNavigation waterNavigator;
  protected final GroundPathNavigation groundNavigator;
  protected boolean swimmingUp;
  
  // explode goal
  protected int fuseLen;
  protected int fuse;
  protected boolean fuseLit;
  
  public GolemBase(EntityType<? extends GolemBase> type, Level world) {
    super(type, world);
    // the following will be unused if swimming is not enabled
    this.waterNavigator = new WaterBoundPathNavigation(this, world);
    this.groundNavigator = new GroundPathNavigation(this, world);
  }
  
  public static GolemBase create(final Level world, final ResourceLocation material) {
    GolemBase golem = new GolemBase(EGRegistry.GOLEM, world);
    golem.setMaterial(material);
    return golem;
  }
  
  public void setMaterial(final ResourceLocation materialIn) {
    this.material = materialIn;
    this.container = ExtraGolems.PROXY.GOLEM_CONTAINERS.get(materialIn).orElse(GolemContainer.EMPTY);
    attributes = new AttributeMap(container.getAttributeSupplier().get().build());
    setHealth((float) getMaxHealth());
    
    if (!level.isClientSide()) {
      // define behavior for the given swimming ability
      switch (container.getSwimAbility()) {
      case FLOAT:
        // basic swimming AI
        goalSelector.addGoal(0, new FloatGoal(this));
        break;
      case SWIM:
        // advanced swimming AI
        maxUpStep = 1.0F;
        moveControl = new SwimmingMovementController(this);
        setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        goalSelector.addGoal(1, new GoToWaterGoal(this, 14, 1.0D));
        goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8F, 200));
        goalSelector.addGoal(5, new SwimUpGoal(this, 1.0D, level.getSeaLevel()));
        break;
      case SINK:
      default:
        // no swimming AI
        break;
      }

      // register goals
      registerGlowGoal();
      registerPowerGoal();
      // allow behaviors to register goals
      container.getBehaviors().values().forEach(b -> b.onRegisterGoals(this));
    }
  }
  
  /** @return the Material that can be used to look up the GolemContainer **/
  public ResourceLocation getMaterial() {
    return this.material;
  }
  
  /** @return the GolemContainer that was looked up by Material **/
  public GolemContainer getContainer() {
    return this.container;
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(CHILD, Boolean.valueOf(false));
    this.getEntityData().define(TEXTURE, Byte.valueOf((byte) 0));
    this.getEntityData().define(FUEL, Integer.valueOf(0));
  }
  
  @Override
  public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
    super.onSyncedDataUpdated(key);
    if (CHILD.equals(key)) {
      if (this.isBaby()) {
        // truncate these values to one decimal place after reducing them from base values
        double childHealth = (Math.floor(getContainer().getAttributes().getHealth() * 0.3D * 10D)) / 10D;
        double childAttack = (Math.floor(getContainer().getAttributes().getAttack() * 0.6D * 10D)) / 10D;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(childHealth);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(childAttack);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
      } else {
        // use full values for non-child entity
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(getContainer().getAttributes().getHealth());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getContainer().getAttributes().getAttack());
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(getContainer().getAttributes().getKnockbackResist());
      }
      // recalculate size
      this.refreshDimensions();
    } else if (TEXTURE.equals(key)) {
      this.setTextureId((byte) this.getTextureId());
    }
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
  
  protected void registerGlowGoal() {
    // register light level AI if enabled
    if(container.getMaxLightLevel() > 0) {
      int lightInt = container.getMaxLightLevel();
      final BlockState state = EGRegistry.UTILITY_LIGHT.defaultBlockState().setValue(GlowBlock.LIGHT_LEVEL, lightInt);
      this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, GlowBlock.UPDATE_TICKS, 
          true, (golem, pos) -> golem.isProvidingLight()));
    }
  }
  
  protected void registerPowerGoal() {
    // register power level AI if enabled
    if(getContainer().getMaxPowerLevel() > 0) {
      int powerInt = getContainer().getMaxPowerLevel();
      final BlockState state = EGRegistry.UTILITY_POWER.defaultBlockState().setValue(PowerBlock.POWER_LEVEL, powerInt);
      final int freq = PowerBlock.UPDATE_TICKS;
      this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, freq, false, (golem, pos) -> golem.isProvidingPower()));
    }
  }

  /////////////// GOLEM UTILITY METHODS //////////////////

  /**
   * Whether right-clicking on this entity triggers a texture change.
   *
   * @return True if this is a {@link IMultitextured} AND the config option
   *         is enabled.
   **/
  public boolean canInteractChangeTexture() {
    return EGConfig.enableTextureInteract() && getContainer().getMultitexture().isPresent()
        && getContainer().getMultitexture().get().canCycle();
  }

  /**
   * Whether this entity provides light (by placing light source blocks). Does not
   * change any behavior, but is used in the Light Block code to determine if it
   * can stay (called AFTER light is placed).
   *
   * @see com.mcmoddev.golems.block.GlowBlock
   **/
  public boolean isProvidingLight() {
    if(getContainer().getMultitexture().isPresent()) {
      return getContainer().getMultitexture().get().getTextureGlowMap().get(getTextureId()) > 0;
    }
    return this.getContainer().getMaxLightLevel() > 0;
  }

  /**
   * Whether this entity provides power (by placing power source blocks). Does not
   * change any behavior, but is used in the Power Block code to determine if it
   * can stay.
   *
   * @see com.mcmoddev.golems.block.PowerBlock
   **/
  public boolean isProvidingPower() {
    return this.getContainer().getMaxPowerLevel() > 0;
  }

  /**
   * @param i the ItemStack being used to heal the entity
   * @return the amount by which this item should heal the entity, in half-hearts.
   *         Defaults to 25% of max health or 32.0, whichever is smaller
   **/
  public float getHealAmount(final ItemStack i) {
    float amount = (float) (this.getMaxHealth() * this.getContainer().getHealAmount(i.getItem()));
    if(this.isBaby()) {
      amount *= 1.75F;
    }
    // max heal amount is 64, for no reason at all
    return Math.min(amount, 64.0F);
  }

  public BlockPos getBlockBelow() {
    return getBlockPosBelowThatAffectsMyMovement();
  }
  
  public ItemStack getBanner() { return this.getItemBySlot(EquipmentSlot.CHEST); }
  
  public void setBanner(final ItemStack bannerItem) { 
    this.setItemSlot(EquipmentSlot.CHEST, bannerItem);
    if(bannerItem.getItem() instanceof BannerItem) {
      this.setDropChance(EquipmentSlot.CHEST, 1.0F);
    }
  }

  /////////////// OVERRIDEN BEHAVIOR //////////////////
  
  @Override
  public void customServerAiStep() {
    super.customServerAiStep();
    // take damage from water
    if (getContainer().getAttributes().isHurtByWater() && this.isInWaterRainOrBubble()) {
      this.hurt(DamageSource.DROWN, 1.0F);
    }
    // take damage from heat
    if(getContainer().getAttributes().isHurtByHeat()) {
      final BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement().above(2);
      if (this.level.getBiome(pos).getTemperature(pos) > 1.0F) {
        this.hurt(DamageSource.ON_FIRE, 1.0F);
      }
    }
    // modify fuse and fuse timer
    if(getContainer().hasBehavior(GolemBehaviors.EXPLODE)) {
      if (this.isInWaterRainOrBubble()) {
        this.resetFuseLit();
      }
    }
  }

  // fall(float, float)
  @Override
  public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
    if (!getContainer().getAttributes().isHurtByFall()) {
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
    return this.getContainer().getAttributes().hasExplosionImmunity();
  }

  @Override
  public boolean canAttackType(final EntityType<?> type) {
    if (type == EntityType.PLAYER && this.isPlayerCreated()) {
      return EGConfig.enableFriendlyFire();
    }
    if (type == EntityType.VILLAGER || type == EGRegistry.GOLEM || type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM) {
      return false;
    }
    return super.canAttackType(type);
  }
  
  @Override
  public boolean doHurtTarget(Entity target) {
    if(super.doHurtTarget(target)) {
      // allow behaviors to process doHurtTarget
      this.getContainer().getBehaviors().values().forEach(b -> b.onHurtTarget(this, target));
      return true;
    }
    return false;
  }
  
  @Override
  protected void actuallyHurt(DamageSource source, float amount) {
    super.actuallyHurt(source, amount);
    // allow behaviors to process actuallyHurt
    this.getContainer().getBehaviors().values().forEach(b -> b.onActuallyHurt(this, source, amount));
  }

  @Override
  public ItemStack getPickedResult(final HitResult ray) {
    return container.hasBlocks() ? new ItemStack(container.getAllBlocks().toArray(new Block[0])[0]) : ItemStack.EMPTY;
  }

  @Override
  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    // Attempt to remove banner from the entity
    if(!this.getBanner().isEmpty() && stack.getItem() instanceof ShearsItem) {
      this.spawnAtLocation(this.getBanner(), this.isBaby() ? 0.9F : 1.4F);
      this.setBanner(ItemStack.EMPTY);
    }
    // Attempt to place a banner on the entity
    if(stack.getItem() instanceof BannerItem && processInteractBanner(player, hand, stack)) {
      return InteractionResult.CONSUME;
    }
    // Attempt to heal the entity
    final float healAmount = getHealAmount(stack);
    if (healAmount > 0 && processInteractHeal(player, hand, stack, healAmount)) {
      return InteractionResult.CONSUME;
    }
    // Attempt to cycle texture
    if (!player.isCrouching() && stack.isEmpty() && this.canInteractChangeTexture()) {
      cycleTexture(player, hand);
    }
    // Attempt to consume fuel
    if(!player.isCrouching() && !stack.isEmpty() && container.hasBehavior(GolemBehaviors.USE_FUEL)) {
      consumeFuel(player, hand);
    }
    // allow behaviors to process mobInteract
    this.getContainer().getBehaviors().values().forEach(b -> b.onMobInteract(this, player, hand));
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
   * Called when the player uses an item that can heal this entity
   * @param player the player using the item
   * @param hand the player hand
   * @param stack the item being used
   * @param healAmount the amount of health this item will restore
   * @return true if the item was consumed
   */
  protected boolean processInteractHeal(final Player player, final InteractionHand hand, final ItemStack stack, final float healAmount) {
    if (EGConfig.enableHealGolems() && this.getHealth() < this.getMaxHealth()) {
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
      SpawnGolemItem.spawnParticles(this.level, pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.15D, ParticleTypes.INSTANT_EFFECT, 30);
      this.playSound(SoundEvents.STONE_PLACE, 0.85F, 1.1F + random.nextFloat() * 0.2F);
      return true;
    }
    return false;
  }
  
  @Override
  public boolean isSensitiveToWater() {
    return this.getContainer().getAttributes().isHurtByWater();
  }
  
  @Override
  public float getBrightness() {
    return this.isProvidingLight() || this.isProvidingPower() ? 1.0F : super.getBrightness();
  }
  
  @Override
  protected Component getTypeName() {
    if(description == null) {
      description = new TranslatableComponent(getType().getDescriptionId() + "." + material.getPath());
    }
    return description;
  }

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
  public void die(final DamageSource source) {
    // allow behaviors to process die
    this.getContainer().getBehaviors().values().forEach(b -> b.onDie(this, source));
    super.die(source);
  }
  
  ////////////////NBT /////////////////

  @Override
  public void readAdditionalSaveData(final CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    this.setMaterial(new ResourceLocation(tag.getString(KEY_MATERIAL)));
    this.setBaby(tag.getBoolean(KEY_CHILD));
    container.getMultitexture().ifPresent(m -> loadTextureId(tag));
    // allow behaviors to process readData
    this.getContainer().getBehaviors().values().forEach(b -> b.onReadData(this, tag));
  }

  @Override
  public void addAdditionalSaveData(final CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putString(KEY_MATERIAL, material.toString());
    tag.putBoolean(KEY_CHILD, this.isBaby());
    container.getMultitexture().ifPresent(m -> saveTextureId(tag));
    // allow behaviors to process writeData
    this.getContainer().getBehaviors().values().forEach(b -> b.onWriteData(this, tag));
  }

  @Override
  public Packet<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void writeSpawnData(FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(material);
  }

  @Override
  public void readSpawnData(FriendlyByteBuf buffer) {
    setMaterial(buffer.readResourceLocation());
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
   * @return A SoundEvent to play when the entity is attacking, walking, hurt, and
   *         on death
   **/
  public final SoundEvent getGolemSound() {
    return container.getSound();
  }
  
  ///////////////////// MULTITEXTURE ///////////////////////////

  @Override
  public int getTextureCount() {
    return container.getMultitexture().isPresent() ? container.getMultitexture().get().getTextureCount() : 0;
  }
  
  @Override
  public void setTextureId(byte toSet) {
    this.getEntityData().set(TEXTURE, toSet);
  }

  @Override
  public int getTextureId() {
    return this.getEntityData().get(TEXTURE);
  }

  /**
   * Called after construction when a entity is built by a player
   * 
   * @param body the body block
   * @param legs the legs block
   * @param arm1 the first arm block
   * @param arm2 the second arm block
   */
  public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
    container.getMultitexture().ifPresent(m -> this.setTextureId((byte) m.getTextureFromBlock(body.getBlock())));
  }

  ///////////////////// FUEL ////////////////////////
  
  @Override
  public void setFuel(int fuel) { getEntityData().set(FUEL, fuel); }

  @Override
  public int getFuel() { return getEntityData().get(FUEL); }

  @Override
  public int getMaxFuel() {
    GolemBehavior b = container.getBehaviors().get(GolemBehaviors.USE_FUEL);
    return b != null ? ((UseFuelBehavior)b).getMaxFuel() : 0;
  }
  
  
  ///////////////////// EXPLODE ////////////////////////
  
  @Override
  public int getFuseLen() { return fuseLen; }

  @Override
  public int getFuse() { return fuse; }

  @Override
  public void setFuse(int fuseIn) { fuse = fuseIn; }  
  
  @Override
  public void setFuseLit(boolean litIn) { fuseLit = true; }

  @Override
  public boolean isFuseLit() { return fuseLit; }
  
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
    if (container.getSwimAbility() != SwimMode.SWIM) {
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
    return container.getSwimAbility() == SwimMode.SWIM ? 0.88F : super.getWaterSlowDown();
  }

  @Override
  public boolean isPushedByFluid() {
    return !isSwimming();
  }

  public void setSwimmingUp(boolean isSwimmingUp) {
    this.swimmingUp = (isSwimmingUp && container.getSwimAbility() == SwimMode.SWIM);
  }

  public boolean isSwimmingUp() {
    if (container.getSwimAbility() != SwimMode.SWIM) {
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
   * @return true if the entity should move towards the water
   **/
  public boolean shouldMoveToWater(final Vec3 target) {
    return container.getSwimAbility() == SwimMode.SWIM;
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
