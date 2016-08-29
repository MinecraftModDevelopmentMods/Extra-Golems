package com.golems.events;

import com.golems.blocks.BlockPowerProvider;
import com.golems.entity.GolemBase;
import com.golems.main.GolemItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event exists for other mods or addons to handle and modify
 * the Sponge Golem's behavior. It is not handled in Extra Golems.
 */
@Event.HasResult
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
	
	public boolean placePower()
	{
		IBlockState powerState = GolemItems.blockPowerSource.getDefaultState().withProperty(BlockPowerProvider.POWER, this.powerLevel);
		return this.golem.worldObj.setBlockState(this.posToAffect, powerState, updateFlag);
	}
}
