package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SlimeGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
  public static final String SPLITTING_CHILDREN = "Splitting Factor";
  public static final String KNOCKBACK = "Knockback Factor";
  
  private boolean allowKnockback;
  private double knockbackAmount;

  public SlimeGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    allowKnockback = this.getConfigBool(ALLOW_SPECIAL);
    knockbackAmount = this.getConfigDouble(KNOCKBACK);
  }

  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      // knocks back the target entity (if it's adult and not attacking a slime)
      if (!this.isChild() && allowKnockback && !(entity instanceof SlimeEntity)) {
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

  @Override
  public void setChild(final boolean isChild) {
    super.setChild(isChild);
    if(isChild) {
      allowKnockback = false;
    }
  }
 
  @Override
  public void onDeath(final DamageSource source) {
    int children = this.getConfigInt(SPLITTING_CHILDREN);
    if (children > 0) {
      trySpawnChildren(children);
    }
    super.onDeath(source);
  }
  
  /**
   * Adds extra velocity to the golem's knockback attack.
   **/
  private void applyKnockback(final Entity entity, final double knockbackFactor) {
    final Vec3d myPos = this.getPositionVec();
    final Vec3d ePos = entity.getPositionVec();
    final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
    final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
    entity.addVelocity(dX, knockbackFactor / 2, dZ);
    entity.velocityChanged = true;
  }
}
