package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.EndGolemTeleportEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class EndstoneGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Teleporting";
  public static final String ALLOW_WATER_HURT = "Can Take Water Damage";

  /**
   * Max distance for one teleport; range is 32.0 for endstone golem, 64 for
   * enderman.
   **/
  protected double range = 32.0D;
  protected boolean allowTeleport = true;
  protected boolean isHurtByWater = true;
  protected boolean hasAmbientParticles = true;

  protected int ticksBetweenIdleTeleports = 200;
  /**
   * Percent chance to teleport away when hurt by non-projectile.
   **/
  protected int chanceToTeleportWhenHurt = 25;

  /** Default constructor. **/
  public EndstoneGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    this(entityType, world, 32.0D, true);
    this.isHurtByWater = this.getConfigBool(ALLOW_WATER_HURT);
    this.allowTeleport = this.getConfigBool(ALLOW_SPECIAL);
    this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
    if (isHurtByWater) {
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }
  }

  /**
   * Flexible constructor to allow child classes to customize.
   *
   * @param entityType       the EntityType
   * @param world            the worldObj
   * @param teleportRange    64.0 for enderman, 32.0 for endstone golem
   * @param ambientParticles whether to always display "portal" particles
   **/
  public EndstoneGolem(final EntityType<? extends GolemBase> entityType, final Level world, final double teleportRange,
      final boolean ambientParticles) {
    super(entityType, world);
    this.range = teleportRange;
    this.hasAmbientParticles = ambientParticles;
  }

  @Override
  public void customServerAiStep() {
    super.customServerAiStep();
    // take damage from water
    if (this.isHurtByWater && this.isInWaterRainOrBubble()) {
      this.hurt(DamageSource.DROWN, 1.0F);
    }
    // try to teleport toward target entity
    if (this.getLastHurtByMob() != null) {
      this.lookAt(this.getLastHurtByMob(), 100.0F, 100.0F);
      if (this.getLastHurtByMob().distanceToSqr(this) > 15.0D && (random.nextInt(12) == 0 || this.getLastHurtByMob().getLastHurtByMob() == this)) {
        this.teleportToEntity(this.getLastHurtByMob());
      }
    } else if (random.nextInt(this.ticksBetweenIdleTeleports) == 0) {
      // or just teleport randomly
      this.teleportRandomly();
    }
  }

  @Override
  public void aiStep() {
    // spawn particles around the golem
    if (this.level.isClientSide && this.hasAmbientParticles) {
      final Vec3 pos = this.position();
      for (int i = 0; i < 2; ++i) {
        this.level.addParticle(ParticleTypes.PORTAL, pos.x + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
            pos.y + this.random.nextDouble() * (double) this.getBbHeight() - 0.25D, pos.z + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
            (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
      }
    }
    // turn off jumping flag
    this.jumping = false;
    super.aiStep();
  }

  @Override
  public boolean hurt(final DamageSource src, final float amnt) {
    if (this.isInvulnerableTo(src)) {
      return false;
    }

    // if it's an arrow or something...
    if (src instanceof IndirectEntityDamageSource) {
      // try to teleport to the attacker
      if (src.getEntity() instanceof LivingEntity) {
        this.setLastHurtByMob((LivingEntity) src.getEntity());
        this.teleportToEntity(src.getEntity());
        return false;
      }
      // if teleporting to the attacker didn't work, golem teleports AWAY
      for (int i = 0; i < 32; ++i) {
        if (this.teleportRandomly()) {
          return false;
        }
      }
    } else {
      // if it's something else, golem MIGHT teleport away
      // if it passes a random chance OR has no attack target
      if (random.nextInt(100) < this.chanceToTeleportWhenHurt || (this.getLastHurtByMob() == null && random.nextBoolean())
          || (this.isHurtByWater && src == DamageSource.DROWN)) {
        // attempt teleport
        for (int i = 0; i < 16; ++i) {
          if (this.teleportRandomly()) {
            break;
          }
        }
      }
    }
    return super.hurt(src, amnt);
  }

  @Override
  public boolean isSensitiveToWater() {
    return this.isHurtByWater;
  }

  protected boolean teleportRandomly() {
    final Vec3 pos = this.position();
    final double d0 = pos.x + (this.random.nextDouble() - 0.5D) * range;
    final double d1 = pos.y + (this.random.nextDouble() - 0.5D) * range * 0.5D;
    final double d2 = pos.z + (this.random.nextDouble() - 0.5D) * range;
    return this.attemptTeleportTo(d0, d1, d2);
  }

  /**
   * Teleport the golem to another entity.
   **/
  protected boolean teleportToEntity(final Entity entity) {
    Vec3 vec3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
    vec3d = vec3d.normalize();
    double d = this.range * 0.25D;
    double d0 = this.range * 0.5D;
    double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * d - vec3d.x * d0;
    double d2 = this.getY() + (this.random.nextDouble() - 0.5D) * d - vec3d.y * d0;
    double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * d - vec3d.z * d0;
    return this.attemptTeleportTo(d1, d2, d3);
  }

  /**
   * Teleport the golem.
   **/
  protected boolean attemptTeleportTo(final double x, final double y, final double z) {
    final EndGolemTeleportEvent event = new EndGolemTeleportEvent(this, x, y, z, 0);
    if (!this.allowTeleport || MinecraftForge.EVENT_BUS.post(event)) {
      return false;
    }
    final boolean flag = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

    if (flag) {
      this.level.playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT,
          this.getSoundSource(), 1.0F, 1.0F);
      this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    return flag;
  }
}
