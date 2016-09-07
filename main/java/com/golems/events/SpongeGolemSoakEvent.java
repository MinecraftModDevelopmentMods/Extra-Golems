package com.golems.events;

import java.util.ArrayList;
import java.util.List;

import com.golems.entity.GolemBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event exists for other mods or addons to handle and modify
 * the Sponge Golem's behavior. It is not handled in Extra Golems.
 */
@Event.HasResult
@Cancelable
public class SpongeGolemSoakEvent extends Event
{
	public List<BlockPos> affectedBlocks;
	
	public final GolemBase spongeGolem;
	public final BlockPos spongeGolemPos;
	public final int range;
	
	protected IBlockState replacesWater;
	/** This will be passed in World#setBlockState **/
	public int updateFlag = 3;
	
	public SpongeGolemSoakEvent(GolemBase golem, BlockPos center, final int RADIUS)
	{
		this.setResult(Result.ALLOW);
		this.spongeGolem = golem;
		this.spongeGolemPos = center;
		this.range = RADIUS;
		this.replacesWater = Blocks.AIR.getDefaultState();
		initAffectedBlockList();
	}
	
	protected void initAffectedBlockList()
	{
		this.affectedBlocks = new ArrayList(this.range * this.range * this.range * 4);
		// check sphere around golem to absorb water
		int x = spongeGolemPos.getX();
		int y = spongeGolemPos.getY();
		int z = spongeGolemPos.getZ();
		for(int i = -range; i <= range; i++)
		{
			for(int j = -range; j <= range; j++)
			{
				for(int k = -range; k <= range; k++)
				{
					if(spongeGolemPos.distanceSq(x + i, y + j, z + k) <= range * range)
					{
						BlockPos pos = new BlockPos(x + i, y + j, z + k);
						IBlockState state = this.spongeGolem.worldObj.getBlockState(pos);

						if(state.getMaterial() == Material.WATER || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER)
						{
							this.affectedBlocks.add(pos);
						}
					}
				}
			}	
		}
	}
	
	/** Sets the IBlockState that will replace water when this event is finalized **/
	public void setReplacementState(IBlockState toReplaceWater)
	{
		this.replacesWater = toReplaceWater;
	}
	
	/** Final action of this event -- replaces all blocks in the list with the passed IBlockState **/
	public boolean replaceWater()
	{
		boolean flag = true;
		for(BlockPos p : this.affectedBlocks)
		{
			flag &= this.spongeGolem.worldObj.setBlockState(p, this.replacesWater, this.updateFlag);
		}
		return flag;
	}
	
	public boolean removeBlockPos(BlockPos toRemove)
	{
		return this.affectedBlocks.remove(toRemove);
	}
}
