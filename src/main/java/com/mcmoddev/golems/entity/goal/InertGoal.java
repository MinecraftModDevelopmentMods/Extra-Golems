package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IFuelConsumer;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class InertGoal<T extends Mob & IFuelConsumer> extends Goal {
  
  protected T entity;

  public InertGoal(final T entity) {
    super();
    this.entity = entity;
    this.setFlags(EnumSet.allOf(Goal.Flag.class));
  }

  @Override
  public boolean canUse() {
    return !entity.hasFuel();
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }

  @Override
  public void start() {
    tick();
  }

  @Override
  public void tick() {
    // freeze the entity and ai tasks
    final Vec3 pos = entity.position();
    final Vec3 forward = entity.getForward().scale(0.1D);
    entity.setDeltaMovement(entity.getDeltaMovement().multiply(0, 1.0D, 0));
    entity.setZza(0F);
    entity.setXxa(0F);
    entity.getMoveControl().setWantedPosition(pos.x, pos.y, pos.z, 0.1D);
    entity.setJumping(false);
    entity.setTarget(null);
    entity.setLastHurtByMob(null);
    entity.getNavigation().stop();
    entity.xRotO = -1.5F;
    // set looking down
    entity.getLookControl().setLookAt(forward.x, forward.y, forward.z, 100.0F, 100.0F);
  }
}
