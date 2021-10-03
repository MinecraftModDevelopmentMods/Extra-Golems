package com.mcmoddev.golems.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.golem_stats.GolemContainer;
import com.mcmoddev.golems.golem_stats.GolemContainer.SwimMode;
import com.mcmoddev.golems.golem_stats.behavior.ExplodeBehavior;
import com.mcmoddev.golems.golem_stats.behavior.GolemBehaviors;
import com.mcmoddev.golems.golem_stats.behavior.ShootArrowsBehavior;
import com.mcmoddev.golems.golem_stats.behavior.UseFuelBehavior;
import com.mcmoddev.golems.entity.goal.GoToWaterGoal;
import com.mcmoddev.golems.entity.goal.PlaceUtilityBlocksGoal;
import com.mcmoddev.golems.entity.goal.SwimUpGoal;
import com.mcmoddev.golems.item.SpawnGolemItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;


/**
 * Base class for all golems in this mod.
 **/
public class GolemBase extends IronGolemEntity implements IMultitextured, IFuelConsumer, IRandomTeleporter, IRandomExploder, IArrowShooter, IEntityAdditionalSpawnData {
  
  protected static final DataParameter<String> MATERIAL = EntityDataManager.createKey(GolemBase.class, DataSerializers.STRING);
  protected static final DataParameter<Boolean> CHILD = EntityDataManager.createKey(GolemBase.class, DataSerializers.BOOLEAN);
  protected static final DataParameter<Byte> TEXTURE = EntityDataManager.createKey(GolemBase.class, DataSerializers.BYTE);
  protected static final DataParameter<Integer> FUEL = EntityDataManager.createKey(GolemBase.class, DataSerializers.VARINT);
  protected static final DataParameter<Boolean> FUSE_LIT = EntityDataManager.createKey(GolemBase.class, DataSerializers.BOOLEAN);
  protected static final DataParameter<Integer> ARROWS = EntityDataManager.createKey(GolemBase.class, DataSerializers.VARINT);
  
  public static final String KEY_MATERIAL = "Material";
  public static final String KEY_CHILD = "IsChild";

  private ResourceLocation material = new ResourceLocation(ExtraGolems.MODID, "empty");
  private GolemContainer container = GolemContainer.EMPTY;
  
  protected ITextComponent description;

  // swimming helpers
  protected final SwimmerPathNavigator waterNavigator;
  protected final GroundPathNavigator groundNavigator;
  protected boolean swimmingUp;
  
  // explode behavior
  protected int fuse;
  
  // shoot arrows behavior
  protected final RangedAttackGoal aiArrowAttack;
  protected final MeleeAttackGoal aiMeleeAttack;
  private Inventory inventory;
  
  // color
  protected int biomeColor = 8626266;
  
  public GolemBase(EntityType<? extends GolemBase> type, World world) {
    super(type, world);
    // the following will be unused if swimming is not enabled
    this.waterNavigator = new SwimmerPathNavigator(this, world);
    this.groundNavigator = new GroundPathNavigator(this, world);
    // the following will only be used if ShootArrowsBehavior is added
    aiArrowAttack = new RangedAttackGoal(this, 1.0D, 28, 32.0F);
    aiMeleeAttack = new MeleeAttackGoal(this, 1.0D, true);
    initArrowInventory();
  }
  
  public static GolemBase create(final World world, final ResourceLocation material) {
    GolemBase golem = new GolemBase(EGRegistry.GOLEM, world);
    golem.setMaterial(material);
    return golem;
  }
  
  public void setMaterial(final ResourceLocation materialIn) {
    if(materialIn.equals(material)) {
      return;
    }
    // update material and container
    this.getDataManager().set(MATERIAL, materialIn.toString());
    this.material = materialIn;
    this.container = ExtraGolems.GOLEM_CONTAINERS.get(materialIn).orElse(GolemContainer.EMPTY);
    this.attributes = new AttributeModifierManager(container.getAttributeSupplier().get().create());
    // bedrock golem invulnerability
	if(SpawnGolemItem.BEDROCK_GOLEM.equals(materialIn)) {
	  setInvulnerable(true);
	}

    if (!world.isRemote()) {
      // define behavior for the given swimming ability
      switch (container.getSwimAbility()) {
      case FLOAT:
        // basic swimming AI
        goalSelector.addGoal(0, new SwimGoal(this));
        break;
      case SWIM:
        // advanced swimming AI
        stepHeight = 1.0F;
        moveController = new SwimmingMovementController(this);
        setPathPriority(PathNodeType.WATER, 0.0F);
        goalSelector.addGoal(1, new GoToWaterGoal(this, 14, 1.0D));
        goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8F, 200));
        goalSelector.addGoal(5, new SwimUpGoal(this, 1.0D, world.getSeaLevel() + 1));
        break;
      case SINK:
      default:
        // no swimming AI
        break;
      }
      // define pathfinding priority
      if (container.getAttributes().isHurtByWater()) {
        this.setPathPriority(PathNodeType.WATER, -1.0F);
      }

      // register goals
      registerGlowGoal();
      registerPowerGoal();
      
      // allow behaviors to register goals
      container.getBehaviors().values().forEach(list -> list.forEach(b -> b.onRegisterGoals(this)));
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
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(MATERIAL, new ResourceLocation(ExtraGolems.MODID, "empty").toString());
    this.getDataManager().register(CHILD, Boolean.valueOf(false));
    this.getDataManager().register(TEXTURE, Byte.valueOf((byte) 0));
    this.getDataManager().register(FUEL, Integer.valueOf(0));
    this.getDataManager().register(FUSE_LIT, Boolean.valueOf(false));
    this.getDataManager().register(ARROWS, Integer.valueOf(0));
  }
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
	super.notifyDataManagerChange(key);
    if(MATERIAL.equals(key)) {
      setMaterial(new ResourceLocation(this.getDataManager().get(MATERIAL)));
    } if (CHILD.equals(key)) {
      if (this.isChild()) {
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
      this.recalculateSize();
    } else if (TEXTURE.equals(key)) {
      this.setTextureId((byte) this.getTextureId());
    }
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
										  @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    this.setHealth(this.getMaxHealth());
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
  
  protected void registerGlowGoal() {
    // register light World AI if enabled
    if(container.getMaxLightLevel() > 0) {
      int lightInt = container.getMaxLightLevel();
      final BlockState state = EGRegistry.UTILITY_LIGHT.getDefaultState().with(GlowBlock.LIGHT_LEVEL, lightInt);
      this.goalSelector.addGoal(9, new PlaceUtilityBlocksGoal(this, state, GlowBlock.UPDATE_TICKS, 
          true, (golem, pos) -> golem.isProvidingLight()));
    }
  }
  
  protected void registerPowerGoal() {
    // register power World AI if enabled
    if(getContainer().getMaxPowerLevel() > 0) {
      int powerInt = getContainer().getMaxPowerLevel();
      final BlockState state = EGRegistry.UTILITY_POWER.getDefaultState().with(PowerBlock.POWER_LEVEL, powerInt);
      final int freq = PowerBlock.UPDATE_TICKS;
      this.goalSelector.addGoal(9, new PlaceUtilityBlocksGoal(this, state, freq, false, (golem, pos) -> golem.isProvidingPower()));
    }
  }

  /////////////// GOLEM UTILITY METHODS //////////////////

  /**
   * Whether right-clicking on this entity triggers a texture change.
   *
   * @return True if this is a Multitextured golem AND texture cycle is enabled
   **/
  public boolean canInteractChangeTexture() {
    return getContainer().getMultitexture().isPresent()
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
      return getContainer().getMultitexture().get().getLight(this) > 0;
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
    if(this.isChild()) {
      amount *= 1.75F;
    }
    // max heal amount is 64, for no reason at all
    return Math.min(amount, 64.0F);
  }

  public BlockPos getBlockBelow() {
    return getPositionUnderneath();
  }
  
  public ItemStack getBanner() { return this.getItemStackFromSlot(EquipmentSlotType.CHEST); }
  
  public void setBanner(final ItemStack bannerItem) { 
    this.setItemStackToSlot(EquipmentSlotType.CHEST, bannerItem);
    if(bannerItem.getItem() instanceof BannerItem) {
      this.setDropChance(EquipmentSlotType.CHEST, 1.0F);
    }
  }

  /////////////// OVERRIDEN BEHAVIOR //////////////////
  
  @Override
  public void livingTick() {
    super.livingTick();
    // take damage from water
    if (getContainer().getAttributes().isHurtByWater() && this.isInWaterRainOrBubbleColumn()) {
      this.attackEntityFrom(DamageSource.DROWN, 1.0F);
    }
    // take damage from heat
    if(getContainer().getAttributes().isHurtByHeat()) {
      final BlockPos pos = this.getBlockBelow().up(2);
      if (this.world.getBiome(pos).getTemperature(pos) > 1.0F) {
        this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
      }
    }
    // update combat goal when arrows behavior is enabled
    if(getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS) && ticksExisted % 35 == 1) {
      final boolean forceMelee = (getAttackTarget() != null && getAttackTarget().getDistanceSq(this) < 8.0D);
      updateCombatTask(forceMelee);
    }
  }
  
  @Override
  public void tick() {
    super.tick();
    // client-side updates
    if(world.isRemote()) {
      // update biome color
      if(this.ticksExisted % 15 == 1) {
        final Biome biome = this.world.getBiome(this.getBlockBelow().up(2));
        biomeColor = biome.getFoliageColor();
      }
      // spawn fuse particles
      if(isFuseLit()) {
        world.addParticle(ParticleTypes.SMOKE, getPosX() + rand.nextDouble() - 0.5D, getPosY() + (rand.nextDouble() * getHeight()), getPosZ() + rand.nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);
      }
      // spawn particles based on container
      getContainer().getParticle().ifPresent(particle -> {
		if(particle.shouldParticleSpawn(rand)) {
		  world.addParticle(particle.getParticleOptions(), getPosX() + rand.nextDouble() - 0.5D, getPosY() + (rand.nextDouble() * getEyeHeight()), getPosZ() + rand.nextDouble() - 0.5D,
				  0.1D * (rand.nextDouble() - 0.5D), 0.1D * (rand.nextDouble() - 0.5D), 0.1D * (rand.nextDouble() - 0.5D));
		}
	  });
    }
  }


  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) {
    if (!getContainer().getAttributes().isHurtByFall()) {
      return false;
    }

    float[] ret = net.minecraftforge.common.ForgeHooks.onLivingFall(this, distance, damageMultiplier);
    if (ret == null) return false;
    distance = ret[0];
    damageMultiplier = ret[1];

    boolean flag = super.onLivingFall(distance, damageMultiplier);
    int i = this.calculateFallDamage(distance, damageMultiplier);
    if (i > 0) {
       this.playSound(this.getFallSound(i), 1.0F, 1.0F);
       this.playFallSound();
       this.attackEntityFrom(DamageSource.FALL, (float)i);
       return true;
    } else {
       return flag;
    }
  }
  
  @Override
  public boolean isImmuneToFire() {
    return getContainer().getAttributes().hasFireImmunity();
  }
  
  @Override
  public boolean isImmuneToExplosions() {
    return getContainer().getAttributes().hasExplosionImmunity();
  }

  @Override
  public boolean canAttack(final EntityType<?> type) {
    if (type == EntityType.PLAYER && this.isPlayerCreated()) {
      return EGConfig.enableFriendlyFire();
    }
    if (type == EntityType.VILLAGER || type == EGRegistry.GOLEM || type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM) {
      return false;
    }
    return super.canAttack(type);
  }
  
  @Override
  public boolean attackEntityAsMob(Entity target) {
    if(super.attackEntityAsMob(target)) {
      // use attack knockback stat
      final double knockback = getContainer().getAttributes().getAttackKnockback();
      if(knockback > 0 && !isChild()) {
        final Vector3d myPos = this.getPositionVec();
        final Vector3d ePos = target.getPositionVec();
        final double dX = Math.signum(ePos.x - myPos.x) * knockback;
        final double dZ = Math.signum(ePos.z - myPos.z) * knockback;
        target.setMotion(target.getMotion().add(dX, knockback / 2, dZ));
      }
      // allow behaviors to process doHurtTarget
      this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onHurtTarget(this, target)));
      return true;
    }
    return false;
  }

  @Override
  public boolean attackEntityFrom(DamageSource source, float amount) {
	final double knockback = getContainer().getAttributes().getAttackKnockback() * 0.25D;
	if(source.getImmediateSource() != null && knockback > 0 && !isChild()) {
	  final Vector3d myPos = this.getPositionVec();
	  final Vector3d ePos = source.getImmediateSource().getPositionVec();
	  final double dX = Math.signum(ePos.x - myPos.x) * knockback;
	  final double dZ = Math.signum(ePos.z - myPos.z) * knockback;
	  source.getImmediateSource().setMotion(source.getImmediateSource().getMotion().add(dX, knockback / 2, dZ));
	}
	return super.attackEntityFrom(source, amount);
  }
  
  @Override
  protected void damageEntity(DamageSource source, float amount) {
    super.damageEntity(source, amount);
    // allow behaviors to process actuallyHurt
    this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onActuallyHurt(this, source, amount)));
  }

  @Override
  public ItemStack getPickedResult(final RayTraceResult ray) {
    return container.hasBlocks() ? new ItemStack(container.getAllBlocks().toArray(new Block[0])[0]) : ItemStack.EMPTY;
  }

  @Override
  protected ActionResultType getEntityInteractionResult(final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    // DEBUG
    // ExtraGolems.LOGGER.info(getMaterial() + "=" + getContainer().toString());
	// ExtraGolems.GOLEM_RENDER_SETTINGS.get(getMaterial()).ifPresent(r -> ExtraGolems.LOGGER.info(r.toString()));
    // Attempt to remove banner from the entity
    if(!this.getBanner().isEmpty() && stack.getItem() instanceof ShearsItem) {
      this.entityDropItem(this.getBanner(), this.isChild() ? 0.9F : 1.4F);
      this.setBanner(ItemStack.EMPTY);
      return ActionResultType.CONSUME;
    }
    // Attempt to place a banner on the entity
    if(stack.getItem() instanceof BannerItem && processInteractBanner(player, hand, stack)) {
      return ActionResultType.CONSUME;
    }
    // Attempt to heal the entity
    final float healAmount = getHealAmount(stack);
    if (!stack.isEmpty() && healAmount > 0 && processInteractHeal(player, hand, stack, healAmount)) {
      return ActionResultType.CONSUME;
    }
    // Cycle texture when server-side player interacts with the entity.
    // This only runs for one hand, whether or not the hand is empty,
    // to avoid double-interaction that causes double texture cycles.
    if(hand == Hand.MAIN_HAND && !world.isRemote() && !player.isCrouching()
        && canInteractChangeTexture() && cycleTexture()) {
      player.swingArm(hand);
      return ActionResultType.CONSUME;
    }
    // Allow behaviors to process mobInteract
    this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onMobInteract(this, player, hand)));
    return super.getEntityInteractionResult(player, hand);
  }
  
  /**
   * Called when the player uses an item that might be a banner
   * @param player the player using the item
   * @param hand the player hand
   * @param stack the item being used
   * @return true if the item was consumed
   */
  protected boolean processInteractBanner(final PlayerEntity player, final Hand hand, final ItemStack stack) {
    if(!this.getBanner().isEmpty()) {
      this.entityDropItem(this.getBanner(), this.isChild() ? 0.9F : 1.4F);
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
  protected boolean processInteractHeal(final PlayerEntity player, final Hand hand, final ItemStack stack, final float healAmount) {
    if (EGConfig.enableHealGolems() && this.getHealth() < this.getMaxHealth()) {
      heal(healAmount);
      // update stack size/item
      if(!player.isCreative()) {
        if (stack.getCount() > 1) {
          stack.shrink(1);
        } else {
          // update the player's held item
          player.setHeldItem(hand, stack.getContainerItem());
        }
      }
      // if currently attacking this player, stop
      if (this.getAttackTarget() == player) {
        this.setRevengeTarget(null);
        this.setAttackTarget(null);
      }
      // spawn particles and play sound
      final Vector3d pos = this.getPositionVec();
      SpawnGolemItem.spawnParticles(this.world, pos.x, pos.y + this.getHeight() / 2.0D, pos.z, 0.15D, ParticleTypes.INSTANT_EFFECT, 30);
      this.playSound(SoundEvents.BLOCK_STONE_PLACE, 0.85F, 1.1F + rand.nextFloat() * 0.2F);
      return true;
    }
    return false;
  }
  
  @Override
  public boolean isWaterSensitive() {
    return this.getContainer().getAttributes().isHurtByWater();
  }
  
  @Override
  public float getBrightness() {
    return (this.isProvidingLight() || this.isProvidingPower()) ? 1.0F : super.getBrightness();
  }
  
  @Override
  protected ITextComponent getProfessionName() {
    if(description == null) {
      description = new TranslationTextComponent("entity." + material.getNamespace() + ".golem." + material.getPath());
    }
    return description;
  }
  
  @Override
  protected ResourceLocation getLootTable() {
    return getContainer().getLootTable(this);
  }

  @Override
  public boolean isChild() {
    return this.getDataManager().get(CHILD).booleanValue();
  }

  /** Update whether this entity is 'child' and recalculate size **/
  public void setBaby(final boolean isChild) {
    if (this.getDataManager().get(CHILD).booleanValue() != isChild) {
      this.getDataManager().set(CHILD, Boolean.valueOf(isChild));
      this.recalculateSize();
    }
  }
  
  @Override
  public void onDeath(final DamageSource source) {
    // allow behaviors to process die
    this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onDie(this, source)));
    super.onDeath(source);
  }
  
  ////////////////NBT /////////////////

  @Override
  public void readAdditional(final CompoundNBT tag) {
    super.readAdditional(tag);
    this.setMaterial(new ResourceLocation(tag.getString(KEY_MATERIAL)));
    this.setBaby(tag.getBoolean(KEY_CHILD));
    container.getMultitexture().ifPresent(m -> loadTextureId(tag));
    // allow behaviors to process readData
    initArrowInventory();
    this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onReadData(this, tag)));
  }

  @Override
  public void writeAdditional(final CompoundNBT tag) {
    super.writeAdditional(tag);
    tag.putString(KEY_MATERIAL, getMaterial().toString());
    tag.putBoolean(KEY_CHILD, this.isChild());
    container.getMultitexture().ifPresent(m -> saveTextureId(tag));
    // allow behaviors to process writeData
    this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onWriteData(this, tag)));
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer) {
    buffer.writeResourceLocation(material);
  }

  @Override
  public void readSpawnData(PacketBuffer buffer) {
    setMaterial(buffer.readResourceLocation());
    setHealth((float) getMaxHealth());
  }

  ///////////////////// SOUND OVERRIDES ////////////////////

  @Override
  protected SoundEvent getAmbientSound() {
    return getGolemSound();
  }

  @Override
  protected SoundEvent getHurtSound(final DamageSource ignored) {
    return getGolemSound() == SoundEvents.BLOCK_GLASS_STEP ? SoundEvents.BLOCK_GLASS_HIT : getGolemSound();
  }

  @Override
  protected SoundEvent getDeathSound() {
    return getGolemSound() == SoundEvents.BLOCK_GLASS_STEP ? SoundEvents.BLOCK_GLASS_BREAK : getGolemSound();
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
    if(toSet >= 0) {
      this.getDataManager().set(TEXTURE, toSet);
    }
  }

  @Override
  public int getTextureId() {
    return this.getDataManager().get(TEXTURE);
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
  
  /** @return the current biome color (only updated client-side) **/
  public int getBiomeColor() { return biomeColor; }

  ///////////////////// FUEL ////////////////////////
  
  @Override
  public void setFuel(int fuel) {
    getDataManager().set(FUEL, fuel);
  }

  @Override
  public int getFuel() { return getDataManager().get(FUEL); }

  @Override
  public int getMaxFuel() {
    List<UseFuelBehavior> b = container.getBehaviors(GolemBehaviors.USE_FUEL);
    return b.isEmpty() ? 0 : b.get(0).getMaxFuel();
  }
  
  
  ///////////////////// EXPLODE ////////////////////////
  
  @Override
  public GolemBase getGolemEntity() { return this; }
  
  @Override
  public int getFuseLen() { 
    List<ExplodeBehavior> b = container.getBehaviors(GolemBehaviors.EXPLODE);
    return b.isEmpty() ? 0 : b.get(0).getFuseLen();
  }

  @Override
  public int getFuse() { return fuse; }

  @Override
  public void setFuse(int fuseIn) { fuse = fuseIn; }  
  
  @Override
  public void setFuseLit(boolean litIn) { getDataManager().set(FUSE_LIT, litIn); }

  @Override
  public boolean isFuseLit() { return getDataManager().get(FUSE_LIT); }
  
  ///////////////////// SHOOT ARROWS ////////////////////////

  @Override
  public void initArrowInventory() {
    Inventory simplecontainer = this.inventory;
    this.inventory = new Inventory(INVENTORY_SIZE);
    if (simplecontainer != null) {
       simplecontainer.removeListener(this);
       int i = Math.min(simplecontainer.getSizeInventory(), this.inventory.getSizeInventory());

       for(int j = 0; j < i; ++j) {
          ItemStack itemstack = simplecontainer.getStackInSlot(j);
          if (!itemstack.isEmpty()) {
             this.inventory.setInventorySlotContents(j, itemstack.copy());
          }
       }
    }

    this.inventory.addListener(this);
    this.onInventoryChanged(this.inventory);
  }
  
  @Override
  public double getArrowDamage() {
    if(getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
      double multiplier = isChild() ? 0.5D : 1.0D;
      return multiplier * getContainer().<ShootArrowsBehavior>getBehaviors(GolemBehaviors.SHOOT_ARROWS).get(0).getDamage();
    }
    return 0;
  }

  @Override
  public boolean canPickUpItem(ItemStack stack) {
	if(canAddArrowToInventory(stack)) {
	  return true;
	}
	return super.canPickUpItem(stack);
  }

  @Override
  public boolean canPickUpLoot() {
    return getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS) || super.canPickUpLoot();
  }
  
  @Override
  public IInventory getArrowInventory() { return inventory; }
  
  @Override
  public RangedAttackGoal getRangedGoal() { return aiArrowAttack; }
  
  @Override
  public MeleeAttackGoal getMeleeGoal() { return aiMeleeAttack; }
  
  @Override
  public int getArrowsInInventory() { return getDataManager().get(ARROWS); }

  @Override
  public void setArrowsInInventory(int count) { getDataManager().set(ARROWS, count); }
  
  @Override
  protected void dropInventory() {
    super.dropInventory();
    if(getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
      dropArrowInventory();
    }
  }
  
  @Override
  public boolean func_233665_g_(ItemStack stack) {
    if(!stack.isEmpty() && stack.getItem() instanceof ArrowItem
        && getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)
        && canAddArrowToInventory(stack)) {
      // attempt to add the arrows to the inventory
      addArrowToInventory(stack);
      return true;
    } else {
      return super.func_233665_g_(stack);
    }
  }
  
  @Override
  public void onItemPickup(Entity itemEntity, int quantity) {
    super.onItemPickup(itemEntity, quantity);
    onInventoryChanged(getArrowInventory());
  }

  ///////////////////// SWIMMING BEHAVIOR ////////////////////////

  @Override
  public void travel(final Vector3d vec) {
    if (isServerWorld() && isInWater() && isSwimmingUp()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getMotion());
      setMotion(getMotion().scale(0.9D));
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
    if (!this.world.isRemote) {
      if (isServerWorld() && isInWater() && isSwimmingUp()) {
        this.navigator = this.waterNavigator;
        setSwimming(true);
      } else {
        this.navigator = this.groundNavigator;
        setSwimming(false);
      }
    }
  }

  @Override
  protected float getWaterSlowDown() {
    return container.getSwimAbility() == SwimMode.SWIM ? 0.88F : super.getWaterSlowDown();
  }

  @Override
  public boolean isPushedByWater() {
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
    LivingEntity e = getAttackTarget();
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
  public boolean shouldMoveToWater(final Vector3d target) {
    return container.getSwimAbility() == SwimMode.SWIM;
  }

  static class SwimmingMovementController extends MovementController {
	private final GolemBase golem;

	public SwimmingMovementController(GolemBase golem) {
	  super(golem);
	  this.golem = golem;
	}

	@Override
	public void tick() {
	  // All of this is copied from DrownedEntity#MoveHelperController
	  LivingEntity target = this.golem.getAttackTarget();
	  if (this.golem.isSwimmingUp() && this.golem.isInWater()) {
		if ((target != null && target.getPosY() > this.golem.getPosY()) || this.golem.swimmingUp) {
		  this.golem.setMotion(this.golem.getMotion().add(0.0D, 0.002D, 0.0D));
		}

		if (this.action != MovementController.Action.MOVE_TO || this.golem.getNavigator().noPath()) {
		  this.golem.setAIMoveSpeed(0.0F);
		  return;
		}
		double dX = this.posX - this.golem.getPosX();
		double dY = this.posY - this.golem.getPosY();
		double dZ = this.posZ - this.golem.getPosZ();
		double dTotal = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
		dY /= dTotal;

		float rot = (float) (MathHelper.atan2(dZ, dX) * 57.2957763671875D) - 90.0F;
		this.golem.rotationYaw = limitAngle(this.golem.rotationYaw, rot, 90.0F);
		this.golem.renderYawOffset = this.golem.rotationYaw;

		float moveSpeed = (float) (this.speed * this.golem.getAttributeValue(Attributes.MOVEMENT_SPEED));
		float moveSpeedAdjusted = MathHelper.lerp(0.125F, this.golem.getAIMoveSpeed(), moveSpeed);
		this.golem.setAIMoveSpeed(moveSpeedAdjusted);
		this.golem.setMotion(this.golem.getMotion().add(moveSpeedAdjusted * dX * 0.005D, moveSpeedAdjusted * dY * 0.1D,
				moveSpeedAdjusted * dZ * 0.005D));
	  } else {
		if (!this.golem.onGround) {
		  this.golem.setMotion(this.golem.getMotion().add(0.0D, -0.008D, 0.0D));
		}
		super.tick();
	  }
	}
  }
}
