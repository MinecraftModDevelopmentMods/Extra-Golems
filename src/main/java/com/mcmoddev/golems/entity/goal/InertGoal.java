package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IFuelConsumer;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;


public class InertGoal<T extends MobEntity & IFuelConsumer> extends Goal {
  
  protected T entity;

  public InertGoal(final T entity) {
    super();
    this.entity = entity;
    this.setMutexFlags(EnumSet.allOf(Goal.Flag.class));
  }

  @Override
  public boolean shouldExecute() {
    return !entity.hasFuel();
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }

  @Override
  public void startExecuting() {
    tick();
  }

  @Override
  public void tick() {
	// freeze the entity and ai tasks
	final Vector3d pos = entity.getPositionVec();
	entity.setMotion(entity.getMotion().mul(0, 1.0D, 0));
	entity.setMoveForward(0F);
	entity.setMoveStrafing(0F);
	entity.getMoveHelper().setMoveTo(pos.x, pos.y, pos.z, 0.1D);
	entity.setJumping(false);
	entity.setAttackTarget(null);
	entity.setRevengeTarget(null);
	entity.getNavigator().clearPath();
	entity.prevRotationPitch = -15F;
	entity.rotationYaw = entity.prevRotationYaw;
	entity.rotationPitch = entity.prevRotationPitch;
	// set looking down
	final double lookX = entity.getLookVec().getX();
	final double lookY = Math.toRadians(-15D);
	final double lookZ = entity.getLookVec().getZ();
	entity.getLookController().setLookPosition(lookX, lookY, lookZ, entity.getHorizontalFaceSpeed(), entity.getVerticalFaceSpeed());
  }
}
