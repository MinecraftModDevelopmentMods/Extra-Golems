package com.golems.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.golems.entity.GolemBase;
import com.google.common.base.Function;

import net.minecraft.block.Block;
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
public class IceGolemFreezeEvent extends Event
{
	protected List<BlockPos> affectedBlocks;
	protected Function<IBlockState, IBlockState> freezeFunction;

	public final GolemBase iceGolem;
	public final BlockPos iceGolemPos;
	
	/** This percentage of Packed Ice placed will become regular ice instead **/
	public final int ICE_CHANCE = 52;
	/** This percentage of Obsidian placed will become cobblestone instead **/
	public final int COBBLE_CHANCE = 29;
	
	/** This should be passed in World#setBlockState when using this event **/
	public int updateFlag;
	
	public IceGolemFreezeEvent(GolemBase golem, BlockPos center, final int RADIUS)
	{
		this.setResult(Result.ALLOW);
		this.iceGolem = golem;
		this.iceGolemPos = center;
		this.updateFlag = 3;
		this.initAffectedBlockList(RADIUS);
		this.setFunction(new DefaultFreezeFunction(golem.getRNG(), ICE_CHANCE, COBBLE_CHANCE));
	}

	public void initAffectedBlockList(final int RANGE)
	{
		this.affectedBlocks = new ArrayList<BlockPos>(RANGE * RANGE * 2 * 4);
		final int MAX_DIS = RANGE * RANGE;
		// check 3-layer circle around this golem (disc, not sphere) to add positions to the map
		for(int i = -RANGE; i <= RANGE; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				for(int k = -RANGE; k <= RANGE; k++)
				{
					final BlockPos CURRENT_POS = this.iceGolemPos.add(i, j, k);
					if(iceGolemPos.distanceSq(CURRENT_POS) <= MAX_DIS)
					{
						this.affectedBlocks.add(CURRENT_POS);
					}
				}
			}	
		}
	}
	
	public Function<IBlockState, IBlockState> getFunction()
	{
		return this.freezeFunction;
	}
	
	public void setFunction(Function<IBlockState, IBlockState> toSet)
	{
		this.freezeFunction = toSet;
	}
	
	public List<BlockPos> getAffectedPositions()
	{
		return this.affectedBlocks;
	}
	
	public boolean add(BlockPos pos)
	{
		return this.affectedBlocks.add(pos);
	}
	
	public boolean remove(BlockPos toRemove)
	{
		return this.affectedBlocks.remove(toRemove);
	}
	
	public static class DefaultFreezeFunction implements Function<IBlockState, IBlockState>
	{
		/** Random instance **/
		public final Random RANDOM;
		/** This percentage of Packed Ice placed will become regular ice instead **/
		public final int ICE_CHANCE;
		/** This percentage of Obsidian placed will become cobblestone instead **/
		public final int COBBLE_CHANCE;
		
		public DefaultFreezeFunction(Random random, int iceChanceIn, int cobbleChanceIn)
		{
			super();
			this.RANDOM = random;
			this.ICE_CHANCE = iceChanceIn;
			this.COBBLE_CHANCE = cobbleChanceIn;
		}
		
		@Override
		public IBlockState apply(IBlockState input) 
		{
			final IBlockState COBBLE_STATE = Blocks.COBBLESTONE.getDefaultState();
			final IBlockState ICE_STATE = Blocks.ICE.getDefaultState();
			final Material MATERIAL = input.getMaterial();
			if(MATERIAL.isLiquid())
			{
				final Block BLOCK = input.getBlock();

				if(BLOCK == Blocks.WATER)
				{
					boolean isNotPacked = this.RANDOM.nextInt(100) < this.ICE_CHANCE;
					return isNotPacked ? ICE_STATE : Blocks.PACKED_ICE.getDefaultState();
				}		
				else if(BLOCK == Blocks.LAVA)
				{
					boolean isNotObsidian = this.RANDOM.nextInt(100) < this.COBBLE_CHANCE;
					return isNotObsidian ? COBBLE_STATE : Blocks.OBSIDIAN.getDefaultState();    		
				}
				else if(BLOCK == Blocks.FLOWING_WATER) 
				{
					return ICE_STATE;
				}
				else if(BLOCK == Blocks.FLOWING_LAVA) 
				{
					return COBBLE_STATE;  	
				}
			}
			
			return input;
		}
	}
}
