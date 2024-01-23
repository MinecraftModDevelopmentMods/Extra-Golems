package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

public class InertGoal extends Goal {

	protected IExtraGolem entity;
	protected Mob mob;
	private MinMaxBounds.Ints variants;

	public InertGoal(final IExtraGolem entity, final MinMaxBounds.Ints variants) {
		super();
		this.variants = variants;
		this.setFlags(EnumSet.allOf(Goal.Flag.class));
		this.entity = entity;
		this.mob = entity.asMob();
	}

	@Override
	public boolean canUse() {
		final Optional<UseFuelBehaviorData> oData = this.entity.getBehaviorData(UseFuelBehaviorData.class);
		return oData.isPresent() && !oData.get().hasFuel() && variants.matches(this.entity.getVariant());
	}

	@Override
	public void start() {
		tick();
	}

	@Override
	public void tick() {
		// clear anger target
		if(mob.getTarget() != null) {
			mob.setTarget(null);
		}
		// clear neutral mob target
		if(mob instanceof NeutralMob neutralMob) {
			neutralMob.setRemainingPersistentAngerTime(0);
			neutralMob.setPersistentAngerTarget(null);
		}
		// freeze the mob and ai tasks
		final Vec3 pos = mob.position();
		final Vec3 forward = mob.getForward().scale(0.1D);
		mob.setDeltaMovement(mob.getDeltaMovement().multiply(0, 1.0D, 0));
		mob.setZza(0F);
		mob.setXxa(0F);
		mob.getMoveControl().setWantedPosition(pos.x, pos.y, pos.z, 0.1D);
		mob.setJumping(false);
		mob.setLastHurtByMob(null);
		mob.getNavigation().stop();
		mob.xRotO = -1.5F;
		// set looking down
		mob.getLookControl().setLookAt(forward.x, forward.y, forward.z, 180.0F, 180.0F);
	}
}
