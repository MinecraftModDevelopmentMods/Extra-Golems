package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CoralBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class CoralGolem extends GolemMultiTextured {

  private static final DataParameter<Boolean> DRY = EntityDataManager.createKey(CoralGolem.class, DataSerializers.BOOLEAN);
  private static final String KEY_DRY = "isDry";

  public static final String ALLOW_HEALING = "Allow Special: Healing";
  public static final String DRY_TIMER = "Max Wet Time";

  public static final String[] VARIANTS = { "tube", "brain", "bubble", "fire", "horn" };
  public final ResourceLocation[] texturesDry;

  private final boolean allowHealing;
  // the minimum amount of time before golem will change between "dry" and "wet"
  private final int maxChangingTime;
  // the amount of time since this golem started changing between "dry" and "wet"
  private int timeChanging = 0;

  public CoralGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, VARIANTS);
    this.texturesDry = new ResourceLocation[VARIANTS.length];
    for (int n = 0, len = VARIANTS.length; n < len; n++) {
      // initialize "dead" textures
      this.texturesDry[n] = makeTexture(ExtraGolems.MODID, this.getGolemContainer().getName() + "/" + VARIANTS[n] + "_dead");
    }
    allowHealing = this.getConfigBool(ALLOW_HEALING);
    maxChangingTime = this.getConfigInt(DRY_TIMER);
  }

  public boolean isDry() {
    return this.getDataManager().get(DRY).booleanValue();
  }

  public void setDry(boolean isDry) {
    if (this.getDataManager().get(DRY).booleanValue() != isDry) {
      this.getDataManager().set(DRY, Boolean.valueOf(isDry));
    }
  }

  @Override
  protected void damageEntity(DamageSource source, float amount) {
    if (this.isDry()) {
      // damage resistant when dried out
      amount *= 0.7F;
    }
    super.damageEntity(source, amount);
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DRY, Boolean.valueOf(false));
  }

  @Override
  public void livingTick() {
    super.livingTick();
    // update "dry" data if the golem has been "changing" state for long enough
    final boolean isChanging = this.isInWaterOrBubbleColumn() == this.isDry();
    if (isChanging) {
      if (!this.world.isRemote && ++timeChanging > maxChangingTime) {
        this.setDry(!this.isInWaterOrBubbleColumn());
        this.timeChanging = 0;
      }
    } else {
      timeChanging = 0;
    }
    // only do some behavior when not dried out
    if (!this.isDry()) {
      // randomly reduce timer if golem is wet (but not submerged)
      // extends "wet" lifetime by roughly 30%
      if (this.isWet() && timeChanging > 0 && rand.nextInt(3) == 0) {
        timeChanging--;
      }
      // heals randomly when wet
      if (this.allowHealing && rand.nextInt(650) == 0) {
        this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 50, 1));
      }
    }
  }

  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if (DRY.equals(key)) {
      this.setDry(this.getDataManager().get(DRY).booleanValue());
      if (this.isDry()) {
        // adjust values when the golem dries out: less health, less speed, more attack
        // note how we use mult and div to truncate to a specific number of decimal
        // places
        double dryHealth = Math.floor(getGolemContainer().getHealth() * 0.7D * 10D) / 10D;
        double dryAttack = Math.floor(getGolemContainer().getAttack() * 1.45D * 10D) / 10D;
        double drySpeed = Math.floor(getGolemContainer().getSpeed() * 0.7D * 100D) / 100D;
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(dryHealth);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(dryAttack);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(drySpeed);
        // particle effects to show that the golem is "drying out"
        ItemBedrockGolem.spawnParticles(this.world, this.posX, this.posY + 0.1D, this.posZ, 0.09D, ParticleTypes.SMOKE, 80);
      } else {
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getGolemContainer().getHealth());
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getGolemContainer().getAttack());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getGolemContainer().getSpeed());
      }
    }
  }

  @Override
  public void writeAdditional(final CompoundNBT nbt) {
    super.writeAdditional(nbt);
    nbt.putBoolean(KEY_DRY, this.isDry());
  }

  @Override
  public void readAdditional(final CompoundNBT nbt) {
    super.readAdditional(nbt);
    this.setDry(nbt.getBoolean(KEY_DRY));
  }

  @Override
  public ResourceLocation[] getTextureArray() {
    return this.isDry() ? this.texturesDry : this.textures;
  }

  @Override
  public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
    this.setDry(!(body.getBlock() instanceof CoralBlock));
    super.onBuilt(body, legs, arm1, arm2);
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(
        GolemTextureBytes.getByByte(this.isDry() ? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL, (byte) this.getTextureNum()));
  }

  @Override
  public boolean shouldMoveToWater(final Vec3d target) {
    // allowed to leave water if NOT dry and NOT too far away
    if (!this.isDry()) {
      double dis = this.getPositionVec().distanceTo(target);
      if (dis < 8.0D && getTimeUntilChange() > 60) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return this.isDry() ? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL;
  }

  public int getTimeUntilChange() {
    return maxChangingTime - timeChanging;
  }
}
