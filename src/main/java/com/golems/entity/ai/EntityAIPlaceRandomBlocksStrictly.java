package com.golems.entity.ai;

import java.util.function.Predicate;

import com.golems.entity.GolemBase;
import com.google.common.base.Predicates;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIPlaceRandomBlocksStrictly extends EntityAIPlaceRandomBlocks
{	
	public EntityAIPlaceRandomBlocksStrictly(GolemBase golemBase, int ticksBetweenPlanting, IBlockState[] plants, Block[] soils, boolean configAllows)
	{
		super(golemBase, ticksBetweenPlanting, plants, soils, getPredicate(configAllows));
	}
	
	public EntityAIPlaceRandomBlocksStrictly(GolemBase golemBase, int ticksBetweenPlanting, IBlockState[] plants, boolean configAllows)
	{
		this(golemBase, ticksBetweenPlanting, plants, null, configAllows);
	}
	
	@Override
	public boolean shouldExecute() 
	{
		return canExecute.test(this) && golem.worldObj.rand.nextInt(tickDelay) == 0;
	}
	
	public static Predicate<EntityAIPlaceRandomBlocks> getPredicate(final boolean ret)
	{
		return new Predicate<EntityAIPlaceRandomBlocks>()
		{
			@Override
			public boolean test(EntityAIPlaceRandomBlocks t) 
			{
				return ret;
			}
		};
	}
	
	public static Predicate<EntityAIPlaceRandomBlocks> getGriefingPredicate()
	{
		return new Predicate<EntityAIPlaceRandomBlocks>()
		{
			@Override
			public boolean test(EntityAIPlaceRandomBlocks t) 
			{
				return t.golem.worldObj.getGameRules().getBoolean("mobGriefing");
			}
		};
	}
}