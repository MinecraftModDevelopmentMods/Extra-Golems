package com.mcmoddev.golems.entity.ai;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SwimmingMovementController extends MovementController {
	private final GolemBase golem;

	public SwimmingMovementController(GolemBase golem) {
		super(golem);
		this.golem = golem;
	}

	@Override
	public void tick() {
		LivingEntity target = this.golem.getAttackTarget();
		final Vec3d gPos = this.golem.getPositionVec();
		final Vec3d tPos = target != null ? target.getPositionVec() : null;
		if (GolemBase.isSwimmingUp(this.golem) && this.golem.isInWater()) {
			if (target != null && tPos != null && (tPos.y > gPos.y || golem.isSwimmingUp())) {
				this.golem.setMotion(this.golem.getMotion().add(0.0D, 0.002D, 0.0D));
			}

			if (this.action != MovementController.Action.MOVE_TO || this.golem.getNavigator().noPath()) {
				this.golem.setAIMoveSpeed(0.0F);

				return;
			}
			double x1 = this.posX - gPos.x;
			double y1 = this.posY - gPos.y;
			double z1 = this.posZ - gPos.z;
			double dis = MathHelper.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
			y1 /= dis;

			float f1 = (float) (MathHelper.atan2(z1, x1) * 57.2957763671875D) - 90.0F;
			this.golem.rotationYaw = limitAngle(this.golem.rotationYaw, f1, 90.0F);
			this.golem.renderYawOffset = this.golem.rotationYaw;

			float moveSpeed2 = MathHelper.lerp(0.125F, this.golem.getAIMoveSpeed(), (float) this.speed);
			this.golem.setAIMoveSpeed(moveSpeed2);
			this.golem.setMotion(this.golem.getMotion().add(moveSpeed2 * x1 * 0.005D,
					moveSpeed2 * y1 * 0.1D, moveSpeed2 * z1 * 0.005D));

		} else {
			if (!this.golem.onGround) {
				this.golem.setMotion(this.golem.getMotion().add(0.0D, -0.008D, 0.0D));
			}
			super.tick();
		}
	}
}
