package com.golems.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockUtility extends BlockEmptyDrops {

	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);

	public BlockUtility(Material m) {
		super(m);
		setDefaultState(blockState.getBaseState());
		setTickRandomly(false);
		blockHardness = -1F;
		this.translucent = true;
	}

	/**
	 * @deprecated call via
	 *             {@link IBlockState#getBoundingBox(IBlockAccess, BlockPos)}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
		return SINGULAR_AABB;
	}

	/**
	 * @deprecated call via
	 *             {@link IBlockState#getCollisionBoundingBox(IBlockAccess, BlockPos)}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	@Nullable
	@Override
	@Deprecated
	public AxisAlignedBB getCollisionBoundingBox(final IBlockState blockState, final World worldIn,
			final BlockPos pos) {
		return NULL_AABB;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for
	 * render
	 *
	 * @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible.
	 *             Implementing/overriding is fine.
	 */
	@Override
	@Deprecated
	public boolean isOpaqueCube(final IBlockState state) {
		return false;
	}

	/**
	 * @deprecated call via {@link IBlockState#isFullCube()} whenever possible.
	 *             Implementing/overriding is fine.
	 */
	@Override
	@Deprecated
	public boolean isFullCube(final IBlockState state) {
		return false;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public AxisAlignedBB getSelectedBoundingBox(final IBlockState blockState, final World world, final BlockPos pos) {
		return SINGULAR_AABB;
	}

	@Override
	public boolean isBlockSolid(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
		return false;
	}

	@Override
	public boolean isSideSolid(final IBlockState baseState, final IBlockAccess worldIn, final BlockPos pos,
			final EnumFacing side) {
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
	 * Whether this Block can be replaced directly by other blocks (true for e.g.
	 * tall grass)
	 */
	@Override
	public boolean isReplaceable(final IBlockAccess worldIn, final BlockPos pos) {
		return true;
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
			final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer,
			final ItemStack stack) {
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

//	@Override
//	@SideOnly(Side.CLIENT)
//	public BlockRenderLayer getRenderLayer() {
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return false;
	}

	@Override
	public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
	}

	@Override
	public void onLanded(final World worldIn, final Entity entityIn) {
	}
}
