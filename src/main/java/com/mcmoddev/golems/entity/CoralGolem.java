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
import net.minecraft.entity.ai.attributes.Attributes;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class CoralGolem extends GolemMultiTextured {

  // whether or not this golem is "dry"
  private static final DataParameter<Boolean> DRY = EntityDataManager.createKey(CoralGolem.class, DataSerializers.BOOLEAN);

  // the amount of time since this golem started changing between "dry" and "wet"
  private static final DataParameter<Integer> CHANGE_TIME = EntityDataManager.createKey(CoralGolem.class, DataSerializers.VARINT);

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

  public CoralGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLES);
    this.texturesDry = new ResourceLocation[TEXTURE_NAMES.length];
    this.lootTablesDry = new ResourceLocation[LOOT_TABLES.length];
    for (int n = 0, len = TEXTURE_NAMES.length; n < len; n++) {
      // initialize "dead" textures
      this.lootTablesDry[n] = new ResourceLocation("minecraft", "textures/block/dead_" + TEXTURE_NAMES[n] + ".png");
      this.lootTablesDry[n] = new ResourceLocation(ExtraGolems.MODID, "entities/" + this.getGolemContainer().getName() + "/dead_" + LOOT_TABLES[n]);
    }
    allowHealing = this.getConfigBool(ALLOW_HEALING);
    maxChangingTime = this.getConfigInt(DRY_TIMER);
  }

  /** @return whether this golem is dried out or wet **/
  public boolean isDry() {
    return this.getDataManager().get(DRY).booleanValue();
  }

  /** Updates the "Dry" flag **/
  public void setDry(boolean isDry) {
    if (isDry() != isDry) {
      this.getDataManager().set(DRY, Boolean.valueOf(isDry));
    }
  }

  /** @return the amount of time this golem has been "changing" **/
  public int getChangingTime() {
    return this.getDataManager().get(CHANGE_TIME).intValue();
  }

  /** Adds or removes time to the change timer **/
  public void addChangingTime(final int toAdd) {
    if (toAdd != 0) {
      this.getDataManager().set(CHANGE_TIME, getChangingTime() + toAdd);
    }
  }

  public void setChangingTime(final int toSet) {
    this.getDataManager().set(CHANGE_TIME, toSet);
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
    this.getDataManager().register(CHANGE_TIME, Integer.valueOf(0));
  }

  @Override
  public void livingTick() {
    super.livingTick();
    if (this.isServerWorld() && !this.world.isRemote) {
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
        if (isChanging && this.isWet() && getChangingTime() > 0 && rand.nextInt(3) == 0) {
          addChangingTime(-1);
        }
        // heals randomly when wet
        if (this.allowHealing && rand.nextInt(650) == 0) {
          this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 50, 1));
        }
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
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(dryHealth);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(dryAttack);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(drySpeed);
        // particle effects to show that the golem is "drying out"
        final Vector3d pos = this.getPositionVec().add(0, 0.2D, 0);
        ItemBedrockGolem.spawnParticles(this.world, pos.x, pos.y, pos.z, 0.09D, ParticleTypes.SMOKE, 80);
      } else {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(getGolemContainer().getHealth());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getGolemContainer().getAttack());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(getGolemContainer().getSpeed());
      }
    }
  }

  @Override
  public void writeAdditional(final CompoundNBT nbt) {
    super.writeAdditional(nbt);
    nbt.putBoolean(KEY_DRY, this.isDry());
    nbt.putByte(KEY_CHANGE, (byte) this.getChangingTime());
  }

  @Override
  public void readAdditional(final CompoundNBT nbt) {
    super.readAdditional(nbt);
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
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(
        GolemTextureBytes.getByByte(this.isDry() ? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL, (byte) this.getTextureNum()));
  }

  @Override
  public boolean shouldMoveToWater(final Vector3d target) {
    // allowed to leave water if NOT dry and NOT too far away
    if (this.isDry()) {
      return true;
    } else {
      double dis = this.getPositionVec().distanceTo(target);
      return dis > 8.0D && getTimeUntilChange() < 60;
    }
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return this.isDry() ? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL;
  }

  public int getTimeUntilChange() {
    return maxChangingTime - getChangingTime();
  }
}
