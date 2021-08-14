package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public final class SlimeGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
  public static final String SPLITTING_CHILDREN = "Splitting Factor";
  public static final String KNOCKBACK = "Knockback Factor";
  
  private boolean allowKnockback;
  private double knockbackAmount;

  public SlimeGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    allowKnockback = this.getConfigBool(ALLOW_SPECIAL);
    knockbackAmount = this.getConfigDouble(KNOCKBACK);
  }

  @Override
  public boolean doHurtTarget(final Entity entity) {
    if (super.doHurtTarget(entity)) {
      // knocks back the target entity (if it's adult and not attacking a slime)
      if (!this.isBaby() && allowKnockback && !(entity instanceof Slime)) {
        applyKnockback(entity, knockbackAmount);
      }
      return true;
    }
    return false;
  }

  @Override
  protected void actuallyHurt(final DamageSource source, final float amount) {
    if (!this.isInvulnerableTo(source)) {
      super.actuallyHurt(source, amount);
      // knocks back the entity that is attacking it
      if (allowKnockback && !this.isBaby() && source.getDirectEntity() != null) {
        applyKnockback(source.getDirectEntity(), this.getConfigDouble(KNOCKBACK));
      }
    }
  }

  @Override
  public void setBaby(final boolean isChild) {
    super.setBaby(isChild);
    if(isChild) {
      allowKnockback = false;
    }
  }
 
  @Override
  public void die(final DamageSource source) {
    int children = this.getConfigInt(SPLITTING_CHILDREN);
    if (children > 0) {
      trySpawnChildren(children);
    }
    super.die(source);
  }
  
  /**
   * Adds extra velocity to the golem's knockback attack.
   **/
  private void applyKnockback(final Entity entity, final double knockbackFactor) {
    final Vec3 myPos = this.position();
    final Vec3 ePos = entity.position();
    final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
    final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
    entity.push(dX, knockbackFactor / 2, dZ);
    entity.hurtMarked = true;
  }
}
