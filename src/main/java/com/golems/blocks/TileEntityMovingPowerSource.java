package com.golems.blocks;

import java.util.List;

import com.golems.entity.EntityRedstoneGolem;

public class TileEntityMovingPowerSource extends TileEntityMovingLightSource {

	public TileEntityMovingPowerSource() {
		//
	}

	@Override
	public void update() {
		final List<EntityRedstoneGolem> entityList = world.getEntitiesWithinAABB(
				EntityRedstoneGolem.class, this.getAABBToCheck(this.world, this.getPos()));

		// if no golem was found, delete this tile entity and block
		if (entityList.isEmpty()) {
			if (world.getBlockState(getPos()).getBlock() instanceof BlockPowerProvider) {
				selfDestruct();
			}
		}
	}

}
