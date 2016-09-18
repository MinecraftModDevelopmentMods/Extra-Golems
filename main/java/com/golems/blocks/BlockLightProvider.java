package com.golems.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLightProvider extends BlockUtility implements ITileEntityProvider
{
	public static final PropertyInteger LIGHT = PropertyInteger.create("light", 0, 15);

	public BlockLightProvider()
	{
		super();
		setDefaultState(blockState.getBaseState().withProperty(LIGHT, Integer.valueOf(0)));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(LIGHT, meta % 16);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(LIGHT).intValue();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { LIGHT });
	}
	
	@Override
    public int getLightValue(IBlockState state)
    {
        return state.getValue(LIGHT).intValue();
    }
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIGHT).intValue();
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityMovingLightSource();
	}
}