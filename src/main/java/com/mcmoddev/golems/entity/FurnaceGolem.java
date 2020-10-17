package com.mcmoddev.golems.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public final class FurnaceGolem extends GolemBase {

  private static final DataParameter<Integer> FUEL = EntityDataManager.createKey(FurnaceGolem.class, DataSerializers.VARINT);
  private static final String KEY_FUEL = "FuelRemaining";

  private static final ResourceLocation LIT = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.FURNACE_GOLEM + "/lit.png");
  private static final ResourceLocation UNLIT = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.FURNACE_GOLEM + "/unlit.png");

  public static final String FUEL_FACTOR = "Burn Time";
  public static final int MAX_FUEL = 102400;
  public final int fuelBurnFactor;

  public FurnaceGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    fuelBurnFactor = Math.max(1, getConfigInt(FUEL_FACTOR));
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(FUEL, Integer.valueOf(0));
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    removeGoal(LookAtGoal.class);
    removeGoal(LookRandomlyGoal.class);
    this.goalSelector.addGoal(0, new InertGoal());
    this.goalSelector.addGoal(1, new UseFuelGoal());
    this.goalSelector.addGoal(1, new TemptGoal(this, 0.7D, Ingredient.fromTag(ItemTags.COALS), false));
    this.goalSelector.addGoal(7, new LookAtWhenActiveGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal(this));
  }
  
  /**
   * Removes all instances of the given goal
   * @param goalToRemove the goal class (must match exactly)
   * @return true if any goals were removed
   **/
  private boolean removeGoal(final Class<? extends Goal> goalToRemove) {
    final List<Goal> goalsToRemove = new ArrayList<>();
    this.goalSelector.goals.forEach(g -> {
      if(g.getGoal().getClass() == goalToRemove) {
        goalsToRemove.add(g.getGoal());
      }
    });
    // remove the erroring goals
    goalsToRemove.forEach(g -> this.goalSelector.removeGoal(g) );    
    return !goalsToRemove.isEmpty();
  }

  @Override
  public void livingTick() {
    super.livingTick();
    if (this.world.isRemote && rand.nextInt(20) == 0) {
      // particle effects
      final double pMotion = 0.03D;
      final Vec3d pos = this.getPositionVec();
      world.addParticle(this.hasFuel() ? ParticleTypes.FLAME : ParticleTypes.SMOKE,
          pos.x + world.rand.nextDouble() * 0.4D - 0.2D + this.getMotion().getX() * 8,
          pos.y + world.rand.nextDouble() * 0.5D + this.getHeight() / 2.0D,
          pos.z + world.rand.nextDouble() * 0.4D - 0.2D + this.getMotion().getZ() * 8, world.rand.nextDouble() * pMotion - pMotion * 0.5D,
          world.rand.nextDouble() * pMotion * 0.75D, world.rand.nextDouble() * pMotion - pMotion * 0.5D);
    }
  }

  @Override
  public void readAdditional(final CompoundNBT tag) {
    super.readAdditional(tag);
    this.setFuel(tag.getInt(KEY_FUEL));
  }

  @Override
  public void writeAdditional(final CompoundNBT tag) {
    super.writeAdditional(tag);
    tag.putInt(KEY_FUEL, getFuel());
  }

  @Override
  protected boolean processInteract(final PlayerEntity player, final Hand hand) {
    // allow player to add fuel to the golem by clicking on them with a fuel item
    final Vec3d pos = this.getPositionVec();
    ItemStack stack = player.getHeldItem(hand);
    int burnTime = ForgeHooks.getBurnTime(stack) * (player.isCrouching() ? stack.getCount() : 1);
    if (burnTime > 0 && (getFuel() + burnTime) <= MAX_FUEL) {
      if (player.isCrouching()) {
        // take entire ItemStack
        this.addFuel(burnTime * stack.getCount());
        stack = stack.getContainerItem();
      } else {
        // take one item from ItemStack
        this.addFuel(burnTime);
        if (stack.getCount() > 1) {
          stack.shrink(1);
        } else {
          stack = stack.getContainerItem();
        }
      }
      // update the player's held item
      player.setHeldItem(hand, stack);
      // add particles
      ItemBedrockGolem.spawnParticles(this.world, pos.x, pos.y + this.getHeight() / 2.0D, pos.z, 0.03D, ParticleTypes.FLAME, 10);
      return true;
    }

    // allow player to remove burn time by using a water bucket
    if (stack.getItem() == Items.WATER_BUCKET) {
      this.setFuel(0);
      player.setHeldItem(hand, stack.getContainerItem());
      ItemBedrockGolem.spawnParticles(this.world, pos.x, pos.y + this.getHeight() / 2.0D, pos.z, 0.1D, ParticleTypes.LARGE_SMOKE, 15);
      return true;
    }

    return super.processInteract(player, hand);
  }

  public ResourceLocation getTexture() {
    return hasFuel() ? LIT : UNLIT;
  }

  public boolean hasFuel() {
    return getFuel() > 0;
  }

  /** @return the current fuel level as a number **/
  public int getFuel() {
    return this.getDataManager().get(FUEL).intValue();
  }

  /** @return a number between 0.0 and 1.0 to indicate fuel level **/
  public float getFuelPercentage() {
    return (float) getFuel() / (float) MAX_FUEL;
  }

  public void setFuel(final int fuel) {
    if (getFuel() != fuel) {
      this.getDataManager().set(FUEL, Integer.valueOf(fuel));
    }
  }

  public void addFuel(final int toAdd) {
    if (toAdd != 0) {
      this.getDataManager().set(FUEL, getFuel() + toAdd);
    }
  }

  class UseFuelGoal extends Goal {

    protected UseFuelGoal() {
      super();
    }

    @Override
    public boolean shouldExecute() {
      // only uses fuel every X ticks
      return FurnaceGolem.this.isServerWorld() && FurnaceGolem.this.getFuel() > 0 
          && FurnaceGolem.this.ticksExisted % FurnaceGolem.this.fuelBurnFactor == 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }

    @Override
    public void startExecuting() {
      FurnaceGolem.this.addFuel(-1);
    }
  }
  
  class LookAtWhenActiveGoal extends LookAtGoal {
    public LookAtWhenActiveGoal(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
      super(entityIn, watchTargetClass, maxDistance);
    }
    
    @Override
    public boolean shouldExecute() {
      return FurnaceGolem.this.hasFuel() && super.shouldExecute();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return FurnaceGolem.this.hasFuel() && super.shouldContinueExecuting();
    }
  }
  
  class LookRandomlyWhenActiveGoal extends LookRandomlyGoal {
    public LookRandomlyWhenActiveGoal(MobEntity entitylivingIn) {
      super(entitylivingIn);
    }

    @Override
    public boolean shouldExecute() {
      return FurnaceGolem.this.hasFuel() && super.shouldExecute();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return FurnaceGolem.this.hasFuel() && super.shouldContinueExecuting();
    }
  }

  class InertGoal extends Goal {

    protected InertGoal() {
      super();
      this.setMutexFlags(EnumSet.allOf(Goal.Flag.class));
    }

    @Override
    public boolean shouldExecute() {
      return !FurnaceGolem.this.hasFuel();
    }

    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }

    @Override
    public void startExecuting() {
      tick();
    }

    @Override
    public void tick() {
      // freeze the FurnaceGolem.this and ai tasks
      final Vec3d pos = FurnaceGolem.this.getPositionVec();
      FurnaceGolem.this.setMotion(FurnaceGolem.this.getMotion().mul(0, 1.0D, 0));
      FurnaceGolem.this.setMoveForward(0F);
      FurnaceGolem.this.setMoveStrafing(0F);
      FurnaceGolem.this.moveController.setMoveTo(pos.x, pos.y, pos.z, 0.1D);
      FurnaceGolem.this.setJumping(false);
      FurnaceGolem.this.setAttackTarget(null);
      FurnaceGolem.this.setRevengeTarget(null);
      FurnaceGolem.this.getNavigator().clearPath();
      FurnaceGolem.this.prevRotationPitch = -15F;
      FurnaceGolem.this.setRotation(prevRotationYaw, prevRotationPitch);
      // set looking down
      final double lookX = FurnaceGolem.this.getLookVec().getX();
      final double lookY = Math.toRadians(-15D);
      final double lookZ = FurnaceGolem.this.getLookVec().getZ();
      FurnaceGolem.this.getLookController().setLookPosition(lookX, lookY, lookZ, FurnaceGolem.this.getHorizontalFaceSpeed(), FurnaceGolem.this.getVerticalFaceSpeed());
    }
  }
}
