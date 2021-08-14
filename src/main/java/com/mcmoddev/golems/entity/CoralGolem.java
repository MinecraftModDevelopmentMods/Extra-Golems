package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public final class CoralGolem extends GolemMultiTextured {

  // whether or not this golem is "dry"
  private static final EntityDataAccessor<Boolean> DRY = SynchedEntityData.defineId(CoralGolem.class, EntityDataSerializers.BOOLEAN);

  // the amount of time since this golem started changing between "dry" and "wet"
  private static final EntityDataAccessor<Integer> CHANGE_TIME = SynchedEntityData.defineId(CoralGolem.class, EntityDataSerializers.INT);

  private static final String KEY_DRY = "isDry";
  private static final String KEY_CHANGE = "changeTime";

  public static final String ALLOW_HEALING = "Allow Special: Healing";
  public static final String DRY_TIMER = "Max Wet Time";
  public static final String[] TEXTURE_NAMES = { "tube_coral_block", "brain_coral_block", "bubble_coral_block", "fire_coral_block", "horn_coral_block" };
  public static final String[] LOOT_TABLES = { "tube", "brain", "bubble", "fire", "horn" };
  public final ResourceLocation[] texturesDry;
  public final ResourceLocation[] lootTablesDry;
  private final boolean allowHealing;

  // the minimum amount of time before golem will change between "dry" and "wet"
  private final int maxChangingTime;

  public CoralGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLES);
    this.texturesDry = new ResourceLocation[TEXTURE_NAMES.length];
    this.lootTablesDry = new ResourceLocation[LOOT_TABLES.length];
    for (int n = 0, len = TEXTURE_NAMES.length; n < len; n++) {
      // initialize "dead" textures
      this.texturesDry[n] = new ResourceLocation("minecraft", "textures/block/dead_" + TEXTURE_NAMES[n] + ".png");
      this.lootTablesDry[n] = new ResourceLocation(ExtraGolems.MODID, "entities/" + this.getGolemContainer().getName() + "/dead_" + LOOT_TABLES[n]);
    }
    allowHealing = this.getConfigBool(ALLOW_HEALING);
    maxChangingTime = this.getConfigInt(DRY_TIMER);
  }

  /** @return whether this golem is dried out or wet **/
  public boolean isDry() {
    return this.getEntityData().get(DRY).booleanValue();
  }

  /** Updates the "Dry" flag **/
  public void setDry(boolean isDry) {
    if (isDry() != isDry) {
      this.getEntityData().set(DRY, Boolean.valueOf(isDry));
    }
  }

  /** @return the amount of time this golem has been "changing" **/
  public int getChangingTime() {
    return this.getEntityData().get(CHANGE_TIME).intValue();
  }

  /** Adds or removes time to the change timer **/
  public void addChangingTime(final int toAdd) {
    if (toAdd != 0) {
      this.getEntityData().set(CHANGE_TIME, getChangingTime() + toAdd);
    }
  }

  public void setChangingTime(final int toSet) {
    this.getEntityData().set(CHANGE_TIME, toSet);
  }

  @Override
  protected void actuallyHurt(DamageSource source, float amount) {
    if (this.isDry()) {
      // damage resistant when dried out
      amount *= 0.7F;
    }
    super.actuallyHurt(source, amount);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(DRY, Boolean.valueOf(false));
    this.getEntityData().define(CHANGE_TIME, Integer.valueOf(0));
  }

  @Override
  public void aiStep() {
    super.aiStep();
    if (this.isEffectiveAi() && !this.level.isClientSide) {
      // the golem is "changing" whenever it is either in water AND dry, or out of
      // water AND wet
      final boolean isChanging = (this.isInWater() == this.isDry());
      if (isChanging) {
        // update change timer
        addChangingTime(1);
        // update "dry" data if the golem has been "changing" for long enough
        if (getChangingTime() > maxChangingTime) {
          setDry(!this.isInWater());
          setChangingTime(0);
        }
      } else {
        setChangingTime(0);
      }
      // only do some behavior when wet (not dried out)
      if (!this.isDry()) {
        // randomly reduce timer if golem is wet (but not submerged)
        // extends "wet" lifetime by roughly 30%
        if (isChanging && this.isInWaterOrRain() && getChangingTime() > 0 && random.nextInt(3) == 0) {
          addChangingTime(-1);
        }
        // heals randomly when wet
        if (this.allowHealing && random.nextInt(650) == 0) {
          this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 1));
        }
      }
    }
  }

  @Override
  public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
    super.onSyncedDataUpdated(key);
    if (DRY.equals(key)) {
      this.setDry(this.getEntityData().get(DRY).booleanValue());
      if (this.isDry()) {
        // adjust values when the golem dries out: less health, less speed, more attack
        // note how we use mult and div to truncate to a specific number of decimal
        // places
        double dryHealth = Math.floor(getGolemContainer().getHealth() * 0.7D * 10D) / 10D;
        double dryAttack = Math.floor(getGolemContainer().getAttack() * 1.45D * 10D) / 10D;
        double drySpeed = Math.floor(getGolemContainer().getSpeed() * 0.7D * 100D) / 100D;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(dryHealth);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(dryAttack);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(drySpeed);
        // particle effects to show that the golem is "drying out"
        final Vec3 pos = this.position().add(0, 0.2D, 0);
        ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y, pos.z, 0.09D, ParticleTypes.SMOKE, 80);
      } else {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(getGolemContainer().getHealth());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getGolemContainer().getAttack());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(getGolemContainer().getSpeed());
      }
    }
  }

  @Override
  public void addAdditionalSaveData(final CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
    nbt.putBoolean(KEY_DRY, this.isDry());
    nbt.putByte(KEY_CHANGE, (byte) this.getChangingTime());
  }

  @Override
  public void readAdditionalSaveData(final CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    this.setDry(nbt.getBoolean(KEY_DRY));
    this.setChangingTime(nbt.getByte(KEY_CHANGE));
  }

  @Override
  public ResourceLocation[] getTextureArray() {
    return this.isDry() ? this.lootTablesDry : super.getTextureArray();
  }

  @Override
  public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
    this.setDry(!(body.getBlock() instanceof CoralBlock));
    super.onBuilt(body, legs, arm1, arm2);
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
    return new ItemStack(
        GolemTextureBytes.getByByte(this.isDry() ? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL, (byte) this.getTextureNum()));
  }

  @Override
  public boolean shouldMoveToWater(final Vec3 target) {
    // allowed to leave water if NOT dry and NOT too far away
    if (this.isDry()) {
      return true;
    } else {
      double dis = this.position().distanceTo(target);
      return dis > 8.0D && getTimeUntilChange() < 60;
    }
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return this.isDry() ? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL;
  }
  
  @Override
  public ResourceLocation[] getLootTableArray() {
    return this.isDry() ? this.lootTablesDry : super.getLootTableArray();
  }

  public int getTimeUntilChange() {
    return maxChangingTime - getChangingTime();
  }
}
