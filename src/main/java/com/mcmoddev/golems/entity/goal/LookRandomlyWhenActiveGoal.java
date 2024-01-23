package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

import java.util.Optional;

public class LookRandomlyWhenActiveGoal extends RandomLookAroundGoal {

	protected final IExtraGolem entity;
	protected final Mob mob;
	private MinMaxBounds.Ints variants;

	public LookRandomlyWhenActiveGoal(IExtraGolem entity, MinMaxBounds.Ints variants) {
		super(entity.asMob());
		this.variants = variants;
		this.entity = entity;
		this.mob = entity.asMob();
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
