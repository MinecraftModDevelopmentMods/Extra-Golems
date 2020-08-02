package com.mcmoddev.golems.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class BlockUtility extends Block implements IBucketPickupHandler, ILiquidContainer {

  private final int tickRate;

  public BlockUtility(final Properties prop, final int tickrate) {
    super(prop.hardnessAndResistance(-1F).doesNotBlockMovement());
    this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.WATERLOGGED, false));
    this.tickRate = tickrate;
  }

  protected boolean remove(final World worldIn, final BlockState state, final BlockPos pos, final int flag) {
    // remove this block and replace with air or water
    final BlockState replaceWith = state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluid().getDefaultState().getBlockState()
        : Blocks.AIR.getDefaultState();
    // replace with air OR water depending on waterlogged state
    return worldIn.setBlockState(pos, replaceWith, flag);
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.WATERLOGGED);
  }

  @Override
  public Fluid pickupFluid(final IWorld worldIn, final BlockPos pos, final BlockState state) {
    if (state.get(BlockStateProperties.WATERLOGGED)) {
      worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
      return Fluids.WATER;
    } else {
      return Fluids.EMPTY;
    }
  }

  @Override
  public FluidState getFluidState(final BlockState state) {
    return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
  }

  @Override
  public boolean canContainFluid(final IBlockReader worldIn, final BlockPos pos, final BlockState state, final Fluid fluidIn) {
    return !state.get(BlockStateProperties.WATERLOGGED) && fluidIn == Fluids.WATER;
  }

  @Override
  public boolean receiveFluid(final IWorld worldIn, final BlockPos pos, final BlockState state, final FluidState fluidStateIn) {
    if (!state.get(BlockStateProperties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
      if (!worldIn.isRemote()) {
        worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
        worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void onBlockAdded(final BlockState state, final World worldIn, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
    if (this.ticksRandomly(state)) {
      worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
      worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      worldIn.notifyNeighbors(pos, this);
    }
  }
//
//	@Override
//	public void randomTick(final BlockState state, final World worldIn, final BlockPos pos, final Random rand) {
//		this.tick(state, worldIn, pos, rand);
//	}

  @Override
  public int tickRate(final IWorldReader worldIn) {
    return this.ticksRandomly ? tickRate : super.tickRate(worldIn);
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return VoxelShapes.empty();
  }

  @Override
  public VoxelShape getCollisionShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return VoxelShapes.empty();
  }

  @Override
  public VoxelShape getRaytraceShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
    return VoxelShapes.empty();
  }

  @Override
  public VoxelShape getRenderShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
    return VoxelShapes.empty();
  }

//  @Override
//  public boolean isNormalCube(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
//    return false;
//  }
//	
//	@Override
//	public boolean isSolid(final BlockState state) {
//		return false;
//	}

  @Override
  public ItemStack getItem(final IBlockReader worldIn, final BlockPos pos, final BlockState state) {
    return ItemStack.EMPTY;
  }

//	@Override
//	public boolean isCollidable(BlockState state) {
//		return false;
//	}

  @Override
  public boolean isReplaceable(final BlockState state, final BlockItemUseContext useContext) {
    return true;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    return getDefaultState();
  }

  @Override
  public BlockRenderType getRenderType(final BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Override
  public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
    // do nothing
  }

  @Override
  public void onEntityCollision(final BlockState state, final World worldIn, final BlockPos pos, final Entity entity) {
    // do nothing
  }

  @Override
  public void onLanded(final IBlockReader worldIn, final Entity entityIn) {
    // do nothing
  }

  @Override
  public boolean canSpawnInBlock() {
    return true;
  }
}
