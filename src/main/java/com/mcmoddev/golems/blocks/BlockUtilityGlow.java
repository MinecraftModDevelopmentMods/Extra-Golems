package com.mcmoddev.golems.blocks;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockUtilityGlow extends BlockUtility {
	
	public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light", 0, 15);
	/* Default value for TICK_RATE. Not necessary to define through config. */
	public static final int UPDATE_TICKS = 6;
	
	public BlockUtilityGlow(Material m, final float defaultLight, final int tickRate) {
		super(Properties.create(m).needsRandomTick().lightValue((int)(defaultLight * 15.0F)), tickRate);
		int light = (int) (defaultLight * 15.0F);
		this.setDefaultState(this.getDefaultState().with(LIGHT_LEVEL, light));
	}

	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
		// make a slightly expanded AABB to check for the golem
		final AxisAlignedBB toCheck = new AxisAlignedBB(pos).grow(0.5D);
		// we'll probably only ever get one golem, but it doesn't hurt to be safe and check them all
		final List<GolemBase> list = worldIn.getEntitiesWithinAABB(GolemBase.class, toCheck);
		boolean hasLightGolem = list != null && !list.isEmpty() && hasLightGolem(list);

		if (hasLightGolem) {
			// light golem is nearby, schedule another update
			worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
		} else {
			this.remove(worldIn, state, pos, 3);
		}	
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(LIGHT_LEVEL);
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.get(LIGHT_LEVEL);
	}

	/**
	 * @return if the given list contains any golems for whom 
	 * {@link GolemBase#doesProvideLight()} returns true
	 **/
	protected boolean hasLightGolem(final List<GolemBase> golems) {
		for(GolemBase g : golems) {
			if(g.doesProvideLight()) {
				return true;
			}
		}
		return false;
	}
}
