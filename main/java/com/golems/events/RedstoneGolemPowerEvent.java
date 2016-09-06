package com.golems.events;

import com.golems.blocks.BlockPowerProvider;
import com.golems.entity.GolemBase;
import com.golems.main.GolemItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when an EntityRedstoneGolem is about to place a BlockPowerProvider.
 * This event exists for other mods or addons to handle and modify
 * the Redstone Golem's behavior. It is not handled in Extra Golems.
 */
@Event.HasResult
@Cancelable
public class RedstoneGolemPowerEvent extends Event 
{
	public final GolemBase golem;
	public final BlockPos posToAffect;
	
	protected int powerLevel;
	protected boolean canPlace;
	public int updateFlag = 3;
	
	public RedstoneGolemPowerEvent(GolemBase golemBase, BlockPos toAffect, int defPower)
	{
		this.setResult(Result.ALLOW);
		this.golem = golemBase;
		this.posToAffect = toAffect;
		this.powerLevel = defPower;
	}
	
	public void setCanPlace(boolean toSet)
	{
		this.canPlace = toSet;
	}
	
	public void setPowerLevel(int toSet)
	{
		this.powerLevel = toSet > 15 || toSet < 0 ? 15 : toSet;
	}
	
	/**
	 * Final action of this event: places a BlockPowerProvider at the location of this event.
	 * Only fires when called externally (Redstone Golem checks that the Result is not Result.DENY first)
	 **/
	public boolean placePower()
	{
		IBlockState powerState = GolemItems.blockPowerSource.getDefaultState().withProperty(BlockPowerProvider.POWER, this.powerLevel);
		return this.golem.worldObj.setBlockState(this.posToAffect, powerState, updateFlag);
	}
}
