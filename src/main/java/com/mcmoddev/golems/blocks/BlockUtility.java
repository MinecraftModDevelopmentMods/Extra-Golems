package com.mcmoddev.golems.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public abstract class BlockUtility extends Block {

//	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D,
//		0.5D, 0.5D);

	public BlockUtility(Material m) {
		this(Properties.create(m).hardnessAndResistance(-1F).doesNotBlockMovement());
		setDefaultState(getStateContainer().getBaseState());
	}

	public BlockUtility(Properties prop) {
		super(prop);
	}

	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.empty();
	}

	public boolean isCollidable(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean isTopSolid(final IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem,
			int fortune) {
		// don't drop anything
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
		// don't drop anything
		return Items.AIR;
	}

	@Override
	public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
		return true;
	}

	@Nullable
	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState();
	}

	@Override
	public IBlockState getStateForPlacement(IBlockState state, EnumFacing facing, IBlockState state2, IWorld world,
			BlockPos pos1, BlockPos pos2, EnumHand hand) {
		return getDefaultState();
	}

	@Override
	public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
		// do nothing
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public EnumBlockRenderType getRenderType(final IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
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
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}
