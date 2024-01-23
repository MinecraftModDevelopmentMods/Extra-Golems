package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

import java.util.Optional;

public class LookAtWhenActiveGoal extends LookAtPlayerGoal {

	protected final IExtraGolem entity;
	protected final Mob mob;
	protected final MinMaxBounds.Ints variants;

	public LookAtWhenActiveGoal(IExtraGolem entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, MinMaxBounds.Ints variants) {
		super(entityIn.asMob(), watchTargetClass, maxDistance);
		this.entity = entityIn;
		this.mob = entityIn.asMob();
		this.variants = variants;
	}

	@Override
	public boolean canUse() {
		final Optional<UseFuelBehaviorData> oData = this.entity.getBehaviorData(UseFuelBehaviorData.class);
		return oData.isPresent() && oData.get().hasFuel() && variants.matches(entity.getVariant()) && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		final Optional<UseFuelBehaviorData> oData = this.entity.getBehaviorData(UseFuelBehaviorData.class);
		return oData.isPresent() && oData.get().hasFuel() && variants.matches(entity.getVariant()) && super.canContinueToUse();
	}
}
