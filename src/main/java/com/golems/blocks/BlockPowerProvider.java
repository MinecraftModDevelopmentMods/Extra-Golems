package com.golems.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPowerProvider extends BlockUtility implements ITileEntityProvider {

	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

	public BlockPowerProvider() {
		super();
		setDefaultState(blockState.getBaseState().withProperty(POWER, Integer.valueOf(0)));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(POWER, meta % 16);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POWER).intValue();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { POWER });
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its
	 * state.
	 */
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return this.getMetaFromState(blockState);
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return this.getMetaFromState(blockState);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMovingPowerSource();
	}
}