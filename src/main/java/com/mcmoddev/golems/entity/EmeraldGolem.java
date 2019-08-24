package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public final class EmeraldGolem extends GolemBase {

	public EmeraldGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
	}

//	//Broken in 9.0.0
//	/**
//	 * Updates this golem's home position IF there is a nearby village.
//	 *
//	 * @return if the golem found a village home
//	 * @see #updateHomeVillageInRange(BlockPos, int)
//	 **/
//	@Override
//	public boolean updateHomeVillage() {
//		// EMERALD golem checks a much larger radius than usual
//		final int radius = WANDER_DISTANCE * 6;
//		return updateHomeVillageInRange(new BlockPos(this), radius);
//	}
}
