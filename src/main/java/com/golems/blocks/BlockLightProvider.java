package com.golems.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLightProvider extends BlockUtility implements ITileEntityProvider {

	public static final PropertyInteger LIGHT = PropertyInteger.create("light", 0, 15);

	public BlockLightProvider() {
		super();
		setDefaultState(blockState.getBaseState().withProperty(LIGHT, Integer.valueOf(0)));
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public IBlockState getStateFromMeta(final int meta) {
		return getDefaultState().withProperty(LIGHT, meta % 16);
	}

	@Override
	public int getMetaFromState(final IBlockState state) {
		return state.getValue(LIGHT).intValue();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, (IProperty[]) new IProperty[] { LIGHT });
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public int getLightValue(final IBlockState state) {
		return state.getValue(LIGHT).intValue();
	}

	@Override
	public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return state.getValue(LIGHT).intValue();
	}

	@Override
	public TileEntity createNewTileEntity(final World worldIn, final int meta) {
		return new TileEntityMovingLightSource();
	}
}