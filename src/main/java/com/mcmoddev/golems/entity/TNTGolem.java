package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class TNTGolem extends GolemBase {

  protected static final EntityDataAccessor<Boolean> DATA_IGNITED = SynchedEntityData.<Boolean>defineId(TNTGolem.class, EntityDataSerializers.BOOLEAN);
  public static final String ALLOW_SPECIAL = "Allow Special: Explode";

  protected final int minExplosionRad;
  protected final int maxExplosionRad;
  protected final int fuseLen;
  /**
   * Percent chance to explode while attacking a mob.
   **/
  protected final int chanceToExplodeWhenAttacking;
  protected boolean allowedToExplode = false;

  protected boolean willExplode;
  protected int fuseTimer;

  /** Default constructor for TNT golem. **/
  public TNTGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    this(entityType, world, 6, 10, 50, 10);
    this.allowedToExplode = this.getConfigBool(ALLOW_SPECIAL);
  }

  /**
   * Flexible constructor to allow child classes to customize.
   *
   * @param entityType
   * @param world
   * @param minExplosionRange
   * @param maxExplosionRange
   * @param minFuseLength
   * @param randomExplosionChance
   */
  public TNTGolem(final EntityType<? extends GolemBase> entityType, final Level world, final int minExplosionRange, final int maxExplosionRange,
      final int minFuseLength, final int randomExplosionChance) {
    super(entityType, world);
    this.minExplosionRad = minExplosionRange;
    this.maxExplosionRad = maxExplosionRange;
    this.fuseLen = minFuseLength;
    this.chanceToExplodeWhenAttacking = randomExplosionChance;
    this.resetIgnite();
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_IGNITED, false);
  }

  /**
   * Called frequently so the entity can update its state every tick as required.
   * For example, zombies and skeletons use this to react to sunlight and start to
   * burn.
   */
  @Override
  public void aiStep() {
    super.aiStep();

    if (this.isOnFire()) {
      this.ignite();
    }

    if (this.isInWaterOrRain()
        || (this.getTarget() != null && this.distanceToSqr(this.getTarget()) > this.minExplosionRad * this.maxExplosionRad)) {
      this.resetIgnite();
    }

    if (this.isIgnited()) {
      this.setDeltaMovement(0.0D, this.getDeltaMovement().y(), 0.0D);
      this.fuseTimer--;
      final Vec3 pos = this.position();
      ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y + 1.0D, pos.z, 0.21D, ParticleTypes.SMOKE, 6);
      if (this.fuseTimer <= 0) {
        this.willExplode = true;
      }
    }

    if (this.willExplode) {
      this.explode();
    }
  }

  @Override
  public void die(final DamageSource source) {
    super.die(source);
    this.explode();
  }

  @Override
  public boolean doHurtTarget(final Entity entity) {
    boolean flag = super.doHurtTarget(entity);

    if (flag && entity.isAlive() && random.nextInt(100) < this.chanceToExplodeWhenAttacking
        && this.distanceToSqr(entity) <= this.minExplosionRad * this.minExplosionRad) {
      this.ignite();
    }

    return flag;
  }

  @Override
  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    final ItemStack itemstack = player.getItemInHand(hand);
    if (!itemstack.isEmpty() && itemstack.getItem() == Items.FLINT_AND_STEEL) {
      final Vec3 pos = this.position();
      this.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F,
          this.random.nextFloat() * 0.4F + 0.8F);
      player.swing(hand);

      if (!this.level.isClientSide) {
        this.setSecondsOnFire(Math.floorDiv(this.fuseLen, 20));
        this.ignite();
        itemstack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
      }
      return InteractionResult.SUCCESS;
    }

    return super.mobInteract(player, hand);
  }

  protected void resetFuse() {
    this.fuseTimer = this.fuseLen + random.nextInt(Math.floorDiv(fuseLen, 2) + 1);
  }

  protected void setIgnited(final boolean toSet) {
    this.getEntityData().set(DATA_IGNITED, Boolean.valueOf(toSet));
  }

  protected boolean isIgnited() {
    return this.getEntityData().get(DATA_IGNITED).booleanValue();
  }

  protected void ignite() {
    if (!this.isIgnited()) {
      // update info
      this.setIgnited(true);
      this.resetFuse();
      // play sounds
      if (!this.isInWaterOrRain()) {
        this.playSound(SoundEvents.CREEPER_PRIMED, 0.9F, random.nextFloat());
      }
    }
  }

  protected void resetIgnite() {
    this.setIgnited(false);
    this.resetFuse();
    this.willExplode = false;
  }

  protected void explode() {
    if (this.allowedToExplode) {
      if (!this.level.isClientSide) {
        final boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        final float range = this.maxExplosionRad > this.minExplosionRad ? (minExplosionRad + random.nextInt(maxExplosionRad - minExplosionRad))
            : this.minExplosionRad;
        final Vec3 pos = this.position();
        this.level.explode(this, pos.x, pos.y, pos.z, range, flag ? BlockInteraction.BREAK : BlockInteraction.NONE);
        this.remove();
      }
    } else {
      resetIgnite();
    }
  }
}
