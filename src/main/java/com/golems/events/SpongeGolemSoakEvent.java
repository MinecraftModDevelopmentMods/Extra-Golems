package com.golems.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
 * To modify which blocks count as 'water' you must call 
 * {@link #setWaterPredicate(Predicate)} and {@link #initAffectedBlockList(int)},
 * in that order. You can 'add' your liquid to the current predicate by passing
 * {@code SpongeGolemSoakEvent#getWaterPredicate().and(yourPredicate)} to 
 * {@link #setWaterPredicate(Predicate)}
 */
@Event.HasResult
@Cancelable
public class SpongeGolemSoakEvent extends Event
{
	protected List<BlockPos> affectedBlocks;
	protected Predicate<IBlockState> waterPredicate;
	
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
		this.setReplacementState(Blocks.AIR.getDefaultState());
		this.setWaterPredicate(new Predicate<IBlockState>()
		{
			@Override
			public boolean test(IBlockState state) 
			{
				return state.getMaterial() == Material.WATER || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER;
			}	
		});
		
		initAffectedBlockList(RADIUS);
	}
	
	public void initAffectedBlockList(final int RANGE)
	{
		this.affectedBlocks = new ArrayList(RANGE * RANGE * RANGE * 4);
		final int MAX_DIS = RANGE * RANGE;
		// check sphere around golem to absorb water
		for(int i = -RANGE; i <= RANGE; i++)
		{
			for(int j = -RANGE; j <= RANGE; j++)
			{
				for(int k = -RANGE; k <= RANGE; k++)
				{
					final BlockPos CURRENT = this.spongeGolemPos.add(i, j, k);
					if(spongeGolemPos.distanceSq(CURRENT) <= MAX_DIS)
					{
						final IBlockState STATE = this.spongeGolem.worldObj.getBlockState(CURRENT);
						if(this.waterPredicate.test(STATE))
						{
							this.affectedBlocks.add(CURRENT);
						}
					}
				}
			}	
		}
	}
	
	public List<BlockPos> getPositionList()
	{
		return this.affectedBlocks;
	}
	
	public Predicate<IBlockState> getWaterPredicate()
	{
		return this.waterPredicate;
	}
	
	public void setWaterPredicate(Predicate<IBlockState> waterPred)
	{
		this.waterPredicate = waterPred;
	}
	
	/** Sets the IBlockState that will replace water when this event is finalized **/
	public void setReplacementState(IBlockState toReplaceWater)
	{
		this.replacesWater = toReplaceWater;
	}
	
	public IBlockState getReplacementState()
	{
		return this.replacesWater;
	}
	
	public boolean removeBlockPos(BlockPos toRemove)
	{
		return this.affectedBlocks.remove(toRemove);
	}
}
