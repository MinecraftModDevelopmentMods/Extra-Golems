package com.mcmoddev.golems.entity.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mcmoddev.golems.entity.ai.GoToWaterGoal;
import com.mcmoddev.golems.entity.ai.SwimUpGoal;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.ExtraGolemsEntities;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemContainer.SwimMode;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends IronGolemEntity {

  protected static final DataParameter<Boolean> CHILD = EntityDataManager.createKey(GolemBase.class, DataSerializers.BOOLEAN);
  protected static final String KEY_CHILD = "isChild";

  private final GolemContainer container;

  // swimming helpers
  protected final SwimmerPathNavigator waterNavigator;
  protected final GroundPathNavigator groundNavigator;
  protected boolean swimmingUp;

  public GolemBase(EntityType<? extends GolemBase> type, World world) {
    super(type, world);
    this.container = GolemRegistrar.getContainer(type);
    // the following will be unused if swimming is not enabled
    this.waterNavigator = new SwimmerPathNavigator(this, world);
    this.groundNavigator = new GroundPathNavigator(this, world);
    // define behavior for the given swimming ability
    switch (container.getSwimMode()) {
    case FLOAT:
      // basic swimming AI
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.navigator.setCanSwim(true);
      break;
    case SWIM:
      // advanced swimming AI
      this.stepHeight = 1.0F;
      this.moveController = new SwimmingMovementController(this);
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.goalSelector.addGoal(1, new GoToWaterGoal(this, 14, 1.0D));
      this.goalSelector.addGoal(2, new SwimUpGoal(this, 1.0D, this.world.getSeaLevel()));
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
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(CHILD, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    
// TODO IronGolemEntity now uses PatrolThroughVillageGoal and ReturnToVillageGoal
//    // find and list erroring goals that will be removed
//    final List<Goal> erroringGoals = new ArrayList<>();
//    this.goalSelector.goals.forEach(g -> {
//      if(g.getGoal().getClass() == MoveThroughVillageGoal.class) {
//        erroringGoals.add(g.getGoal());
//      }
//    });
//    // remove the erroring goals
//    erroringGoals.forEach(g -> {
//      this.goalSelector.removeGoal(g);
//      ExtraGolems.LOGGER.debug("Removed erroring goal inherited from IronGolemEntity");
//    });    
//    // add in custom implementation of erroring goal
//    this.goalSelector.addGoal(3, new MoveThroughVillageGoalFixed(this, 0.6D, false, 4, () -> {
//      return false;
//   }));
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
    return false;
  }

  /**
   * Whether this golem provides power (by placing power source blocks). Does not
   * change any behavior, but is used in the Power Block code to determine if it
   * can stay.
   *
   * @see com.mcmoddev.golems.blocks.BlockUtilityPower
   **/
  public boolean isProvidingPower() {
    return false;
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
    if(this.isChild()) {
      amount *= 1.75F;
    }
    // max heal amount is 32, for no reason at all
    return Math.min(amount, 32.0F);
  }

  public BlockPos getBlockBelow() {
//    int i = MathHelper.floor(this.getPosX());
//    int j = MathHelper.floor(this.getPosY() - 0.2D);
//    int k = MathHelper.floor(this.getPosZ());
//    return new BlockPos(i, j, k);
    return getPositionUnderneath();
  }

  /////////////// CONFIG HELPERS //////////////////

  public ForgeConfigSpec.ConfigValue getConfigValue(final String name) {
    final GolemSpecialContainer cont = this.getGolemContainer().getSpecialContainer(name);
    if(null == cont) {
      ExtraGolems.LOGGER.error("Tried to access config value '" + name + "' in golem '" 
          + this.getGolemContainer().getName() + "' but the config name wasn't registered!");
    } else {
      return (ExtraGolemsConfig.GOLEM_CONFIG.specials.get(cont)).value;
    }
    return null;
  }

  public boolean getConfigBool(final String name) {
    return (Boolean) getConfigValue(name).get();
  }

  public int getConfigInt(final String name) {
    return (Integer) getConfigValue(name).get();
  }

  public double getConfigDouble(final String name) {
    return (Double) getConfigValue(name).get();
  }

  /////////////// OVERRIDEN BEHAVIOR //////////////////

  // fall(float, float)
  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) {
    if (!container.takesFallDamage()) {
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
  public boolean isImmuneToExplosions() {
    return this.getGolemContainer().isImmuneToExplosions();
  }

  @Override
  public boolean canAttack(final EntityType<?> type) {
    if (type == EntityType.PLAYER && this.isPlayerCreated()) {
      return ExtraGolemsConfig.enableFriendlyFire();
    }
    if (type == EntityType.VILLAGER || type.getRegistryName().toString().contains("golem")) {
      return false;
    }
    return super.canAttack(type);
  }

  @Override
  public ItemStack getPickedResult(final RayTraceResult ray) {
    final Block block = container.getPrimaryBuildingBlock();
    return block != null ? new ItemStack(block) : ItemStack.EMPTY;
  }

  @Override
  protected ActionResultType func_230254_b_(final PlayerEntity player, final Hand hand) { // processInteract
    ItemStack stack = player.getHeldItem(hand);
    float healAmount = getHealAmount(stack);
    if (ExtraGolemsConfig.enableHealGolems() && this.getHealth() < this.getMaxHealth() && healAmount > 0) {
      heal(healAmount);
      // update stack size/item
      if(!player.isCreative()) {
        if (stack.getCount() > 1) {
          stack.shrink(1);
        } else {
          stack = stack.getContainerItem();
        }
        // update the player's held item
        player.setHeldItem(hand, stack);
      }
      // if currently attacking this player, stop
      if (this.getAttackTarget() == player) {
        this.setRevengeTarget(null);
        this.setAttackTarget(null);
      }
      // spawn particles and play sound
      final Vector3d pos = this.getPositionVec();
      ItemBedrockGolem.spawnParticles(this.world, pos.x, pos.y + this.getHeight() / 2.0D, pos.z, 0.15D, ParticleTypes.INSTANT_EFFECT, 30);
      this.playSound(SoundEvents.BLOCK_STONE_PLACE, 0.85F, 1.1F + rand.nextFloat() * 0.2F);
      return ActionResultType.CONSUME;
    }
    return super.func_230254_b_(player, hand); // processInteract
  }
  
  ///////////////// CHILD LOGIC ///////////////////

  @Override
  public boolean isChild() {
    return this.getDataManager().get(CHILD).booleanValue();
  }

  /** Update whether this entity is 'child' and recalculate size **/
  public void setChild(final boolean isChild) {
    if (this.getDataManager().get(CHILD).booleanValue() != isChild) {
      this.getDataManager().set(CHILD, Boolean.valueOf(isChild));
      this.recalculateSize();
    }
  }
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if (CHILD.equals(key)) {
      if (this.isChild()) {
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
      this.recalculateSize();
    }
  }
  
  /**
   * Attempts to spawn the given number of "mini" golems
   * @param count the number of children to spawn
   * @return a collection containing the entities that were spawned
   **/
  protected Collection<GolemBase> trySpawnChildren(final int count) {
    final List<GolemBase> children = new ArrayList<>();
    if(!this.world.isRemote && !this.isChild()) {
      for(int i = 0; i < count; i++) {
        GolemBase child = this.getGolemContainer().getEntityType().create(this.world);
        child.setChild(true);
        if (this.getAttackTarget() != null) {
          child.setAttackTarget(this.getAttackTarget());
        }
        // set location
        child.copyLocationAndAnglesFrom(this);
        // spawn the entity
        this.getEntityWorld().addEntity(child);
        // add to the list
        children.add(child);
      }
    }
    return children;
  }
  
  //////////////// NBT /////////////////

  @Override
  public void readAdditional(final CompoundNBT tag) {
    super.readAdditional(tag);
    this.setChild(tag.getBoolean(KEY_CHILD));
  }

  @Override
  public void writeAdditional(final CompoundNBT tag) {
    super.writeAdditional(tag);
    tag.putBoolean(KEY_CHILD, this.isChild());
  }

  /////////////// TEXTURE HELPERS //////////////////

  /**
   * This method is called from the golem Render code and should return the
   * current texture (skin) of the golem. Defaults to querying the container for a
   * texture.
   * 
   * @return a ResourceLocation to use for rendering
   **/
  public ResourceLocation getTexture() {
    return this.getGolemContainer().getTexture();
  }

  /**
   * Calls {@link #makeTexture(String, String)} on the assumption that MODID is
   * 'golems'. Texture should be at 'assets/golems/textures/entity/[TEXTURE].png'
   * <br>
   * For most golems, set the texture when building the GolemContainer using
   * {@link GolemContainer.Builder#setTexture(ResourceLocation)} or
   * {@link GolemContainer.Builder#basicTexture()}
   * 
   * @see ExtraGolemsEntities#makeTexture(String)
   **/
  protected static ResourceLocation makeTexture(final String TEXTURE) {
    return ExtraGolemsEntities.makeTexture(TEXTURE);
  }

  /**
   * Makes a ResourceLocation using the passed mod id and the texture name.
   * Texture should be at 'assets/[MODID]/textures/entity/[TEXTURE].png' <br>
   * For most golems, set the texture when building the GolemContainer using
   * {@link GolemContainer.Builder#setTexture(ResourceLocation)} or
   * {@link GolemContainer.Builder#basicTexture()}
   * 
   * @see #makeTexture(String)
   * @see ExtraGolemsEntities#makeTexture(String, String)
   **/
  protected static ResourceLocation makeTexture(final String MODID, final String TEXTURE) {
    return ExtraGolemsEntities.makeTexture(MODID, TEXTURE);
  }

  /** @return Whether the texture should be rendered as translucent **/
  public boolean hasTransparency() {
    return false;
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
   * @return A SoundEvent to play when the golem is attacking, walking, hurt, and
   *         on death
   **/
  public final SoundEvent getGolemSound() {
    return container.getSound();
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
    if (container.getSwimMode() != SwimMode.SWIM) {
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
    return container.getSwimMode() == SwimMode.SWIM ? 0.88F : super.getWaterSlowDown();
  }

  @Override
  public boolean isPushedByWater() {
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
   * @return true if the golem should move towards the water
   **/
  public boolean shouldMoveToWater(final Vector3d target) {
    return container.getSwimMode() == SwimMode.SWIM;
  }
  
  static class SwimmingMovementController extends MovementController {
    private final GolemBase golem;

    public SwimmingMovementController(GolemBase golem) {
      super(golem);
      this.golem = golem;
    }

    @Override
    public void tick() {
      LivingEntity target = this.golem.getAttackTarget();
      final Vector3d gPos = this.golem.getPositionVec();
      final Vector3d tPos = target != null ? target.getPositionVec() : null;
      if (GolemBase.isSwimmingUp(this.golem) && this.golem.isInWater()) {
        if (target != null && tPos != null && (tPos.y > gPos.y || golem.isSwimmingUp())) {
          this.golem.setMotion(this.golem.getMotion().add(0.0D, 0.002D, 0.0D));
        }

        if (this.action != MovementController.Action.MOVE_TO || this.golem.getNavigator().noPath()) {
          this.golem.setAIMoveSpeed(0.0F);

          return;
        }
        double x1 = this.posX - gPos.x;
        double y1 = this.posY - gPos.y;
        double z1 = this.posZ - gPos.z;
        double dis = MathHelper.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
        y1 /= dis;

        float f1 = (float) (MathHelper.atan2(z1, x1) * 57.2957763671875D) - 90.0F;
        this.golem.rotationYaw = limitAngle(this.golem.rotationYaw, f1, 90.0F);
        this.golem.renderYawOffset = this.golem.rotationYaw;

        float moveSpeed2 = MathHelper.lerp(0.125F, this.golem.getAIMoveSpeed(), (float) this.speed);
        this.golem.setAIMoveSpeed(moveSpeed2);
        this.golem.setMotion(this.golem.getMotion().add(moveSpeed2 * x1 * 0.005D, moveSpeed2 * y1 * 0.1D, moveSpeed2 * z1 * 0.005D));

      } else {
        if (!this.golem.onGround) {
          this.golem.setMotion(this.golem.getMotion().add(0.0D, -0.008D, 0.0D));
        }
        super.tick();
      }
    }
  }
}
