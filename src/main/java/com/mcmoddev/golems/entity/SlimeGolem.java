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
  public static final String ALLOW_SPLITTING = "Allow Special: Split";
  public static final String KNOCKBACK = "Knockback Factor";

  public SlimeGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
  }

  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      // knocks back the target entity (if it's adult and not attacking a slime)
      if (this.getConfigBool(ALLOW_SPECIAL) && !(entity instanceof SlimeEntity) && !this.isChild()) {
        knockbackTarget(entity, this.getConfigDouble(KNOCKBACK));
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
      if (!this.isChild() && source.getImmediateSource() != null && this.getConfigBool(ALLOW_SPECIAL)) {
        knockbackTarget(source.getImmediateSource(), this.getConfigDouble(KNOCKBACK));
      }
    }
  }

  /**
   * Adds extra velocity to the golem's knockback attack.
   **/
  private void knockbackTarget(final Entity entity, final double knockbackFactor) {
    final Vec3d myPos = this.getPositionVec();
    final Vec3d ePos = entity.getPositionVec();
    final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
    final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
    entity.addVelocity(dX, knockbackFactor / 4, dZ);
    entity.velocityChanged = true;
  }

  @Override
  public void onDeath(final DamageSource source) {
    if (this.getConfigBool(ALLOW_SPLITTING)) {
      trySpawnChildren(2);
    }
    super.onDeath(source);
  }
}
