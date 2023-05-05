package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BurnInSunGoal extends Goal {

	protected final GolemBase entity;
	protected final float chance;

	public BurnInSunGoal(GolemBase entityIn, final float chanceIn) {
		setFlags(EnumSet.noneOf(Goal.Flag.class));
		this.entity = entityIn;
		this.chance = chanceIn;
	}

	@Override
	public boolean canUse() {
		return entity.isSunBurnTick() && entity.getRandom().nextFloat() < chance && entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void start() {
		entity.setSecondsOnFire(3);
	}
}
