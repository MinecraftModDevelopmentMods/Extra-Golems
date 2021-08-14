package com.mcmoddev.golems.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

public final class FurnaceGolem extends GolemBase {

  private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(FurnaceGolem.class, EntityDataSerializers.INT);
  private static final String KEY_FUEL = "FuelRemaining";

  private static final ResourceLocation LIT = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.FURNACE_GOLEM + "/lit.png");
  private static final ResourceLocation UNLIT = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.FURNACE_GOLEM + "/unlit.png");

  public static final String FUEL_FACTOR = "Burn Time";
  public static final int MAX_FUEL = 102400;
  public final int fuelBurnFactor;

  public FurnaceGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    fuelBurnFactor = Math.max(1, getConfigInt(FUEL_FACTOR));
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(FUEL, Integer.valueOf(0));
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    removeGoal(LookAtPlayerGoal.class);
    removeGoal(RandomLookAroundGoal.class);
    this.goalSelector.addGoal(0, new InertGoal());
    this.goalSelector.addGoal(1, new UseFuelGoal());
    this.goalSelector.addGoal(1, new TemptGoal(this, 0.7D, Ingredient.of(ItemTags.COALS), false));
    this.goalSelector.addGoal(7, new LookAtWhenActiveGoal(this, Player.class, 6.0F));
    this.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal(this));
  }
  
  /**
   * Removes all instances of the given goal
   * @param goalToRemove the goal class (must match exactly)
   * @return true if any goals were removed
   **/
  private boolean removeGoal(final Class<? extends Goal> goalToRemove) {
    final List<Goal> goalsToRemove = new ArrayList<>();
    this.goalSelector.availableGoals.forEach(g -> {
      if(g.getGoal().getClass() == goalToRemove) {
        goalsToRemove.add(g.getGoal());
      }
    });
    // remove the erroring goals
    goalsToRemove.forEach(g -> this.goalSelector.removeGoal(g) );    
    return !goalsToRemove.isEmpty();
  }

  @Override
  public void aiStep() {
    super.aiStep();
    if (this.level.isClientSide && random.nextInt(20) == 0) {
      // particle effects
      final double pMotion = 0.03D;
      final Vec3 pos = this.position();
      level.addParticle(this.hasFuel() ? ParticleTypes.FLAME : ParticleTypes.SMOKE,
          pos.x + level.random.nextDouble() * 0.4D - 0.2D + this.getDeltaMovement().x() * 8,
          pos.y + level.random.nextDouble() * 0.5D + this.getBbHeight() / 2.0D,
          pos.z + level.random.nextDouble() * 0.4D - 0.2D + this.getDeltaMovement().z() * 8, level.random.nextDouble() * pMotion - pMotion * 0.5D,
          level.random.nextDouble() * pMotion * 0.75D, level.random.nextDouble() * pMotion - pMotion * 0.5D);
    }
  }

  @Override
  public void readAdditionalSaveData(final CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    this.setFuel(tag.getInt(KEY_FUEL));
  }

  @Override
  public void addAdditionalSaveData(final CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putInt(KEY_FUEL, getFuel());
  }

  @Override
  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    // allow player to add fuel to the golem by clicking on them with a fuel item
    final Vec3 pos = this.position();
    ItemStack stack = player.getItemInHand(hand);
    int burnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * (player.isCrouching() ? stack.getCount() : 1);
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
      player.setItemInHand(hand, stack);
      // add particles
      ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.03D, ParticleTypes.FLAME, 10);
      return InteractionResult.CONSUME;
    }

    // allow player to remove burn time by using a water bucket
    if (stack.getItem() == Items.WATER_BUCKET) {
      this.setFuel(0);
      player.setItemInHand(hand, stack.getContainerItem());
      ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.1D, ParticleTypes.LARGE_SMOKE, 15);
      return InteractionResult.CONSUME;
    }

    return super.mobInteract(player, hand);
  }

  public ResourceLocation getTexture() {
    return hasFuel() ? LIT : UNLIT;
  }

  public boolean hasFuel() {
    return getFuel() > 0;
  }

  /** @return the current fuel level as a number **/
  public int getFuel() {
    return this.getEntityData().get(FUEL).intValue();
  }

  /** @return a number between 0.0 and 1.0 to indicate fuel level **/
  public float getFuelPercentage() {
    return (float) getFuel() / (float) MAX_FUEL;
  }

  public void setFuel(final int fuel) {
    if (getFuel() != fuel) {
      this.getEntityData().set(FUEL, Integer.valueOf(fuel));
    }
  }

  public void addFuel(final int toAdd) {
    if (toAdd != 0) {
      this.getEntityData().set(FUEL, getFuel() + toAdd);
    }
  }

  class UseFuelGoal extends Goal {

    protected UseFuelGoal() {
      super();
    }

    @Override
    public boolean canUse() {
      // only uses fuel every X ticks
      return FurnaceGolem.this.isEffectiveAi() && FurnaceGolem.this.getFuel() > 0 
          && FurnaceGolem.this.tickCount % FurnaceGolem.this.fuelBurnFactor == 0;
    }

    @Override
    public boolean canContinueToUse() {
      return false;
    }

    @Override
    public void start() {
      FurnaceGolem.this.addFuel(-1);
    }
  }
  
  class LookAtWhenActiveGoal extends LookAtPlayerGoal {
    public LookAtWhenActiveGoal(Mob entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
      super(entityIn, watchTargetClass, maxDistance);
    }
    
    @Override
    public boolean canUse() {
      return FurnaceGolem.this.hasFuel() && super.canUse();
    }
    
    @Override
    public boolean canContinueToUse() {
      return FurnaceGolem.this.hasFuel() && super.canContinueToUse();
    }
  }
  
  class LookRandomlyWhenActiveGoal extends RandomLookAroundGoal {
    public LookRandomlyWhenActiveGoal(Mob entitylivingIn) {
      super(entitylivingIn);
    }

    @Override
    public boolean canUse() {
      return FurnaceGolem.this.hasFuel() && super.canUse();
    }
    
    @Override
    public boolean canContinueToUse() {
      return FurnaceGolem.this.hasFuel() && super.canContinueToUse();
    }
  }

  class InertGoal extends Goal {

    protected InertGoal() {
      super();
      this.setFlags(EnumSet.allOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
      return !FurnaceGolem.this.hasFuel();
    }

    @Override
    public boolean canContinueToUse() {
      return false;
    }

    @Override
    public void start() {
      tick();
    }

    @Override
    public void tick() {
      // freeze the FurnaceGolem.this and ai tasks
      final Vec3 pos = FurnaceGolem.this.position();
      FurnaceGolem.this.setDeltaMovement(FurnaceGolem.this.getDeltaMovement().multiply(0, 1.0D, 0));
      FurnaceGolem.this.setZza(0F);
      FurnaceGolem.this.setXxa(0F);
      FurnaceGolem.this.moveControl.setWantedPosition(pos.x, pos.y, pos.z, 0.1D);
      FurnaceGolem.this.setJumping(false);
      FurnaceGolem.this.setTarget(null);
      FurnaceGolem.this.setLastHurtByMob(null);
      FurnaceGolem.this.getNavigation().stop();
      FurnaceGolem.this.xRotO = -15F;
      FurnaceGolem.this.setRot(yRotO, xRotO);
      // set looking down
      final double lookX = FurnaceGolem.this.getLookAngle().x();
      final double lookY = Math.toRadians(-15D);
      final double lookZ = FurnaceGolem.this.getLookAngle().z();
      FurnaceGolem.this.getLookControl().setLookAt(lookX, lookY, lookZ, FurnaceGolem.this.getMaxHeadYRot(), FurnaceGolem.this.getMaxHeadXRot());
    }
  }
}
