package com.golems.events;

import java.util.HashMap;
import java.util.Map;

import com.golems.entity.GolemBase;

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
public class IceGolemFreezeEvent extends Event
{
	public Map<BlockPos, IBlockState> affectedBlocks;

	public final GolemBase iceGolem;
	public final BlockPos iceGolemPos;
	public final int range;
	
	/** This percentage of Packed Ice placed will become regular ice instead **/
	public int iceChance = 62;
	/** This percentage of Obsidian placed will become cobblestone instead **/
	public int cobbleChance = 39;

	public IceGolemFreezeEvent(GolemBase golem, BlockPos center, final int RADIUS)
	{
		this.setResult(Result.ALLOW);
		this.iceGolem = golem;
		this.iceGolemPos = center;
		this.range = RADIUS;
		initAffectedBlockList();
	}

	protected void initAffectedBlockList()
	{
		this.affectedBlocks = new HashMap(this.range * this.range * 2 * 4);
		int x = iceGolemPos.getX();
		int y = iceGolemPos.getY();
		int z = iceGolemPos.getZ();

		// check 3-layer circle around this golem (disc, not sphere) to add positions to the map
		for(int i = -range; i <= range; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				for(int k = -range; k <= range; k++)
				{
					if(iceGolemPos.distanceSq(x + i, y, z + k) <= range * range)
					{
						BlockPos pos = new BlockPos(x + i, y + j, z + k);
						IBlockState state = this.iceGolem.worldObj.getBlockState(pos);
						if(state.getMaterial().isLiquid())
						{
							IBlockState toBecome = null;

							if(state.getBlock() == Blocks.WATER)
							{
								boolean isNotPacked = this.iceGolem.getRNG().nextInt(100) < this.iceChance;
								toBecome = isNotPacked ? Blocks.ICE.getDefaultState() : Blocks.PACKED_ICE.getDefaultState();
							}
							else if(state.getBlock() == Blocks.FLOWING_WATER)
							{
								toBecome = Blocks.ICE.getDefaultState();	    		
							}
							else if(state.getBlock() == Blocks.FLOWING_LAVA)
							{
								toBecome = Blocks.COBBLESTONE.getDefaultState();  		
							}
							else if(state.getBlock() == Blocks.LAVA)
							{
								boolean isNotObsidian = this.iceGolem.getRNG().nextInt(100) < this.cobbleChance;
								toBecome = isNotObsidian ? Blocks.COBBLESTONE.getDefaultState() : Blocks.OBSIDIAN.getDefaultState();    		
							}
							
							if(toBecome != null)
							{
								this.add(pos, toBecome);
							}	
						}
					}
				}
			}	
		}
	}

	/** Final action of this event -- replaces all blocks in the map with their frozen counterpart **/
	public boolean freezeBlocks()
	{
		boolean flag = false;
		BlockPos[] positions = makeBlockPosArray();
		IBlockState[] states = makeStateArray(positions);
		for(int i = 0, len = this.affectedBlocks.size(); i < len; i++)
		{
			flag &= this.iceGolem.worldObj.setBlockState(positions[i], states[i]);
		}
		return flag;
	}
	
	public IBlockState add(BlockPos pos, IBlockState state)
	{
		return this.affectedBlocks.put(pos, state);
	}
	
	public boolean removeBlockPos(BlockPos toRemove)
	{
		return this.affectedBlocks.remove(toRemove) != null;
	}
	
	public void clearMap()
	{
		this.affectedBlocks.clear();
	}
	
	public BlockPos[] makeBlockPosArray()
	{
		return this.affectedBlocks.keySet().toArray(new BlockPos[this.affectedBlocks.size()]);
	}
	
	public IBlockState[] makeStateArray(BlockPos[] keys)
	{
		IBlockState[] states = new IBlockState[keys.length];
		for(int i = 0, len = keys.length; i < len; i++)
		{
			states[i] = this.affectedBlocks.get(keys[i]);
		}
		return states;
	}
}
