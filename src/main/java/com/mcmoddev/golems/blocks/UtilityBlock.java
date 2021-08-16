package com.mcmoddev.golems.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class UtilityBlock extends Block implements SimpleWaterloggedBlock {
  
  protected final int tickRate;

  public UtilityBlock(final Properties prop, final int tickrate) {
    super(prop.strength(-1F).noCollission().randomTicks());
    this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
    this.tickRate = tickrate;
  }

  protected boolean remove(final Level worldIn, final BlockState state, final BlockPos pos, final int flag) {
    // remove this block and replace with air or water
    final BlockState replaceWith = state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState().createLegacyBlock()
        : Blocks.AIR.defaultBlockState();
    // replace with air OR water depending on waterlogged state
    return worldIn.setBlock(pos, replaceWith, flag);
  }

  @Override
  protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.WATERLOGGED);
  }

//  @Override
//  public ItemStack pickupBlock(final LevelAccessor worldIn, final BlockPos pos, final BlockState state) {
//    worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
//    if (state.getValue(BlockStateProperties.WATERLOGGED)) {
//      return new ItemStack(Items.WATER_BUCKET);
//    } else {
//      return ItemStack.EMPTY;
//    }
//  }

  @Override
  public FluidState getFluidState(final BlockState state) {
    return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }
//
//  @Override
//  public boolean canPlaceLiquid(final BlockGetter worldIn, final BlockPos pos, final BlockState state, final Fluid fluidIn) {
//    return !state.getValue(BlockStateProperties.WATERLOGGED) && fluidIn == Fluids.WATER;
//  }
//
//  @Override
//  public boolean placeLiquid(final LevelAccessor worldIn, final BlockPos pos, final BlockState state, final FluidState fluidStateIn) {
//    if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
//      if (!worldIn.isClientSide()) {
//        worldIn.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
//        worldIn.getLiquidTicks().scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
//      }
//      return true;
//    } else {
//      return false;
//    }
//  }

  @Override
  public void onPlace(final BlockState state, final Level worldIn, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
    if (this.isRandomlyTicking(state)) {
      worldIn.getBlockTicks().scheduleTick(pos, this, tickRate);
      if(state.getValue(BlockStateProperties.WATERLOGGED)) {
        worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
      }
      worldIn.updateNeighborsAt(pos, this);
    }
  }

  @Override
  public void tick(final BlockState state, final ServerLevel worldIn, final BlockPos pos, final Random rand) {
    super.tick(state, worldIn, pos, rand);
    if (this.isRandomlyTicking(state)) {
      worldIn.getBlockTicks().scheduleTick(pos, this, tickRate);
      if(state.getValue(BlockStateProperties.WATERLOGGED)) {
        worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
      }
    }
  }

  @Override
  public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext cxt) {
    return Shapes.empty();
  }

  @Override
  public VoxelShape getCollisionShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext cxt) {
    return Shapes.empty();
  }

  @Override
  public VoxelShape getInteractionShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos) {
    return Shapes.empty();
  }

  @Override
  public VoxelShape getOcclusionShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos) {
    return Shapes.empty();
  }

  @Override
  public ItemStack getCloneItemStack(final BlockGetter worldIn, final BlockPos pos, final BlockState state) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canBeReplaced(final BlockState state, final BlockPlaceContext useContext) {
    return true;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockPlaceContext context) {
    return defaultBlockState();
  }

  @Override
  public RenderShape getRenderShape(final BlockState state) {
    return RenderShape.INVISIBLE;
  }

  @Override
  public void fallOn(final Level worldIn, final BlockState state, final BlockPos pos, final Entity entityIn, final float fallDistance) {
    // do nothing
  }

  @Override
  public void entityInside(final BlockState state, final Level worldIn, final BlockPos pos, final Entity entity) {
    // do nothing
  }

  @Override
  public void updateEntityAfterFallOn(final BlockGetter worldIn, final Entity entityIn) {
    // do nothing
  }

  @Override
  public boolean isPossibleToRespawnInThis() {
    return true;
  }
}
