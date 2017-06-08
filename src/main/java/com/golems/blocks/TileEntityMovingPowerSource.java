package com.golems.blocks;

import java.util.List;

import com.golems.entity.EntityRedstoneGolem;

public class TileEntityMovingPowerSource extends TileEntityMovingLightSource
{    	
	public TileEntityMovingPowerSource() {}

	@Override
	public void update()
	{
		List<EntityRedstoneGolem> entityList = worldObj.getEntitiesWithinAABB(EntityRedstoneGolem.class, this.getAABBToCheck(this.worldObj, this.getPos()));

		// if no golem was found, delete this tile entity and block
		if(entityList.isEmpty())
		{
			if(worldObj.getBlockState(getPos()).getBlock() instanceof BlockPowerProvider)
			{
				selfDestruct();
			}
		}
	}
	
}
