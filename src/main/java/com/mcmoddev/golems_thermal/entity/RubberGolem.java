package com.mcmoddev.golems_thermal.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class RubberGolem extends GolemBase {
  
  public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
  public static final String KNOCKBACK = "Knockback Factor";
  
  private boolean allowKnockback;
  private double knockbackAmount;

  public RubberGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    allowKnockback = this.getConfigBool(ALLOW_SPECIAL);
    knockbackAmount = this.getConfigDouble(KNOCKBACK);
  }

  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      // knocks back the target entity (if it's adult and not attacking a slime)
      if (allowKnockback) {
        applyKnockback(entity, knockbackAmount);
      }
      return true;
    }
    return false;
  }

  @Override
  protected void damageEntity(final DamageSource source, final float amount) {
    if (!this.isInvulnerableTo(source)) {
      super.damageEntity(source, amount);
      // knocks back the entity that is attacking it
      if (allowKnockback && !this.isChild() && source.getImmediateSource() != null) {
        applyKnockback(source.getImmediateSource(), this.getConfigDouble(KNOCKBACK));
      }
    }
  }
  
  /**
   * Adds extra velocity to the golem's knockback attack.
   **/
  private void applyKnockback(final Entity entity, final double knockbackFactor) {
    final Vector3d myPos = this.getPositionVec();
    final Vector3d ePos = entity.getPositionVec();
    final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
    final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
    entity.addVelocity(dX, knockbackFactor / 2, dZ);
    entity.velocityChanged = true;
  }
}
