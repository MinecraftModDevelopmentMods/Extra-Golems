package com.mcmoddev.golems.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import javax.annotation.Nullable;

public abstract class BlockUtility extends Block implements IBucketPickupHandler, ILiquidContainer {

	private final int tickRate;

	public BlockUtility(final Properties prop, final int tickrate) {
		super(prop.hardnessAndResistance(-1F).doesNotBlockMovement());
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.WATERLOGGED, false));
		this.tickRate = tickrate;
	}

	protected boolean remove(final World worldIn, final BlockState state, final BlockPos pos, final int flag) {
		// remove this block and replace with air or water
		final BlockState replaceWith = state.get(BlockStateProperties.WATERLOGGED)
			? Fluids.WATER.getStillFluid().getDefaultState().getBlockState()
			: Blocks.AIR.getDefaultState();
		// replace with air OR water depending on waterlogged state
		return worldIn.setBlockState(pos, replaceWith, flag);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		if (state.get(BlockStateProperties.WATERLOGGED)) {
			worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public IFluidState getFluidState(BlockState state) {
		return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false)
			: super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
		return !state.get(BlockStateProperties.WATERLOGGED) && fluidIn == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
		if (!state.get(BlockStateProperties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
			if (!worldIn.isRemote()) {
				worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
				worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(),
					fluidStateIn.getFluid().getTickRate(worldIn));
			}
			return true;
		} else {
			return false;
		}
	}

//	@Override
//	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState) {
//		if(this.getTickRandomly(state)) {
//			worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
//			worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
//			worldIn.notifyNeighbors(pos, this);
//		}
//	}

	@Override
	public void randomTick(final BlockState state, final World worldIn, final BlockPos pos, final Random rand) {
		this.tick(state, worldIn, pos, rand);
	}

	@Override
	public int tickRate(IWorldReader worldIn) {
		return this.ticksRandomly ? tickRate : super.tickRate(worldIn);
	}

//	@Override
//	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
//		return VoxelShapes.empty();
//	}

//	@Override
//	public boolean isCollidable(BlockState state) {
//		return false;
//	}
//
//	@Override
//	public boolean isFullCube(BlockState state) {
//		return false;
//	}

	/**
	 * @deprecated
	 */
//	@Deprecated
//	@Override
//	public boolean isTopSolid(final BlockState state) {
//		return false;
//	}
//
//	@Override
//	public boolean isBlockNormalCube(BlockState state) {
//		return false;
//	}

//
//	@Override
//	public IItemProvider getItemDropped(BlockState state, World worldIn, BlockPos pos, int fortune) {
//		// don't drop anything
//		return Items.AIR;
//	}
	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		return true;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState();
	}

	@Override
	public BlockState getStateForPlacement(BlockState state, Direction facing, BlockState state2, IWorld world,
			BlockPos pos1, BlockPos pos2, Hand hand) {
		return getDefaultState();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
	}

	@Override
	public void onLanded(IBlockReader worldIn, Entity entityIn) {
		// do nothing
	}

	@Override
	public boolean canSpawnInBlock() {
		return true;
	}

//	@Override
//	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, EnumFacing face) {
//		return BlockFaceShape.UNDEFINED;
//	}
}
