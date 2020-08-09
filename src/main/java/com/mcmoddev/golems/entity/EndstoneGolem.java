package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.EndGolemTeleportEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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
  public EndstoneGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    this(entityType, world, 32.0D, true);
    this.isHurtByWater = this.getConfigBool(ALLOW_WATER_HURT);
    this.allowTeleport = this.getConfigBool(ALLOW_SPECIAL);
    this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
    if (isHurtByWater) {
      this.setPathPriority(PathNodeType.WATER, -1.0F);
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
  public EndstoneGolem(final EntityType<? extends GolemBase> entityType, final World world, final double teleportRange,
      final boolean ambientParticles) {
    super(entityType, world);
    this.range = teleportRange;
    this.hasAmbientParticles = ambientParticles;
  }

  @Override
  public void updateAITasks() {
    super.updateAITasks();
    // take damage from water
    if (this.isHurtByWater && this.isInWaterRainOrBubbleColumn()) {
      this.attackEntityFrom(DamageSource.DROWN, 1.0F);
    }
    // try to teleport toward target entity
    if (this.getRevengeTarget() != null) {
      this.faceEntity(this.getRevengeTarget(), 100.0F, 100.0F);
      if (this.getRevengeTarget().getDistanceSq(this) > 15.0D && (rand.nextInt(12) == 0 || this.getRevengeTarget().getRevengeTarget() == this)) {
        this.teleportToEntity(this.getRevengeTarget());
      }
    } else if (rand.nextInt(this.ticksBetweenIdleTeleports) == 0) {
      // or just teleport randomly
      this.teleportRandomly();
    }
  }

  @Override
  public void livingTick() {
    // spawn particles around the golem
    if (this.world.isRemote && this.hasAmbientParticles) {
      final Vector3d pos = this.getPositionVec();
      for (int i = 0; i < 2; ++i) {
        this.world.addParticle(ParticleTypes.PORTAL, pos.x + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth(),
            pos.y + this.rand.nextDouble() * (double) this.getHeight() - 0.25D, pos.z + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth(),
            (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
      }
    }
    // turn off jumping flag
    this.isJumping = false;
    super.livingTick();
  }

  @Override
  public boolean attackEntityFrom(final DamageSource src, final float amnt) {
    if (this.isInvulnerableTo(src)) {
      return false;
    }

    // if it's an arrow or something...
    if (src instanceof IndirectEntityDamageSource) {
      // try to teleport to the attacker
      if (src.getTrueSource() instanceof LivingEntity) {
        this.setRevengeTarget((LivingEntity) src.getTrueSource());
        this.teleportToEntity(src.getTrueSource());
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
      if (rand.nextInt(100) < this.chanceToTeleportWhenHurt || (this.getRevengeTarget() == null && rand.nextBoolean())
          || (this.isHurtByWater && src == DamageSource.DROWN)) {
        // attempt teleport
        for (int i = 0; i < 16; ++i) {
          if (this.teleportRandomly()) {
            break;
          }
        }
      }
    }
    return super.attackEntityFrom(src, amnt);
  }

  @Override
  public boolean isWaterSensitive() {
    return this.isHurtByWater;
  }

  protected boolean teleportRandomly() {
    final Vector3d pos = this.getPositionVec();
    final double d0 = pos.x + (this.rand.nextDouble() - 0.5D) * range;
    final double d1 = pos.y + (this.rand.nextDouble() - 0.5D) * range * 0.5D;
    final double d2 = pos.z + (this.rand.nextDouble() - 0.5D) * range;
    return this.teleportTo(d0, d1, d2);
  }

  /**
   * Teleport the golem to another entity.
   **/
  protected boolean teleportToEntity(final Entity entity) {
    Vector3d vec3d = new Vector3d(this.getPosX() - entity.getPosX(), this.getPosYHeight(0.5D) - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
    vec3d = vec3d.normalize();
    double d = this.range * 0.25D;
    double d0 = this.range * 0.5D;
    double d1 = this.getPosX() + (this.rand.nextDouble() - 0.5D) * d - vec3d.x * d0;
    double d2 = this.getPosY() + (this.rand.nextDouble() - 0.5D) * d - vec3d.y * d0;
    double d3 = this.getPosZ() + (this.rand.nextDouble() - 0.5D) * d - vec3d.z * d0;
    return this.teleportTo(d1, d2, d3);
  }

  /**
   * Teleport the golem.
   **/
  protected boolean teleportTo(final double x, final double y, final double z) {
    final EndGolemTeleportEvent event = new EndGolemTeleportEvent(this, x, y, z, 0);
    if (!this.allowTeleport || MinecraftForge.EVENT_BUS.post(event)) {
      return false;
    }
    final boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

    if (flag) {
      this.world.playSound((PlayerEntity) null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
          this.getSoundCategory(), 1.0F, 1.0F);
      this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    return flag;
  }
}
