package com.golems.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUtility extends Block {

	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D,
			0.5D, 0.5D);

	public BlockUtility() {
		super(Material.AIR);
		setDefaultState(blockState.getBaseState());
		setTickRandomly(false);
		blockHardness = -1F;
		this.translucent = true;
	}

	/**
	 * @deprecated
	 */
	@Nullable
	@Deprecated
	@Override
	public AxisAlignedBB getCollisionBoundingBox(final IBlockState blockState, final IBlockAccess access,
			final BlockPos pos) {
		return NULL_AABB;
	}

	/**
	 * @deprecated
	 */
	@Nullable
	@Deprecated
	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState blockState, final IBlockAccess access, final BlockPos pos) {
		return NULL_AABB;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public AxisAlignedBB getSelectedBoundingBox(final IBlockState blockState, final World world, final BlockPos pos) {
		return SINGULAR_AABB;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean isTopSolid(final IBlockState state) {
		return false;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean isFullBlock(final IBlockState state) {
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render.
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean isOpaqueCube(final IBlockState state) {
		return false;
	}

	@Override
	public boolean canCollideCheck(final IBlockState state, final boolean hitIfLiquid) {
		return false;
	}

	/**
	 * Spawns this Block's drops into the World as EntityItems.
	 */
	@Override
	public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state,
			final float chance, final int fortune) {
		// Because we don't want to drop anything.
	}

	/**
	 * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
	 */
	@Override
	public boolean isReplaceable(final IBlockAccess worldIn, final BlockPos pos) {
		return true;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean isFullCube(final IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
		return true;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
			final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
		return getDefaultState();
	}

	@Override
	public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
		// Do nothing.
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public IBlockState getStateFromMeta(final int meta) {
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(final IBlockState state) {
		return 0;
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
		return;
	}

	@Override
	public void onLanded(final World worldIn, final Entity entityIn) {
		return;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, (IProperty[]) new IProperty[0]);
	}
}
