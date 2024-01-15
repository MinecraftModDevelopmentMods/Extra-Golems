package com.mcmoddev.golems.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public abstract class UtilityBlock extends Block implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected final int tickRate;

	public UtilityBlock(final Properties prop, final int tickrate) {
		super(prop.strength(-1F).noCollission().randomTicks());
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
		this.tickRate = tickrate;
	}

	protected boolean remove(final Level level, final BlockState state, final BlockPos pos, final int flag) {
		// remove this block and replace with air or water
		final BlockState replaceWith = state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState().createLegacyBlock()
				: Blocks.AIR.defaultBlockState();
		// replace with air OR water depending on waterlogged state
		return level.setBlock(pos, replaceWith, flag);
	}

	@Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(final BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
		if (this.isRandomlyTicking(state)) {
			level.scheduleTick(pos, this, tickRate);
			if (state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
			}
			level.updateNeighborsAt(pos, this);
		}
	}

	@Override
	public void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource rand) {
		super.tick(state, level, pos, rand);
		if (this.isRandomlyTicking(state)) {
			level.scheduleTick(pos, this, tickRate);
			if (state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
			}
		}
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}

		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext cxt) {
		return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState().getShape(level, pos, cxt) : Shapes.empty();
	}

	@Override
	public ItemStack getCloneItemStack(final BlockGetter level, final BlockPos pos, final BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canBeReplaced(final BlockState state, final BlockPlaceContext useContext) {
		return true;
	}

	@Override
	public RenderShape getRenderShape(final BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entityIn, final float fallDistance) {
		// do nothing
	}

	@Override
	public void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity) {
		// do nothing
	}

	@Override
	public void updateEntityAfterFallOn(final BlockGetter level, final Entity entityIn) {
		// do nothing
	}

	@Override
	public boolean isPossibleToRespawnInThis(BlockState p_279289_) {
		return true;
	}
}
