package com.golems.blocks;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUtilityGlowWater extends BlockUtilityGlow {
	public BlockUtilityGlowWater(final Material m, final float defaultLight, final int tickRate, final IBlockState replaceWith) {
		super(m, defaultLight, tickRate, replaceWith);
		int light = (int) (defaultLight * 15.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockLiquid.LEVEL, 0).withProperty(BlockUtilityGlow.LIGHT_LEVEL, light));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockUtilityGlow.LIGHT_LEVEL, BlockLiquid.LEVEL);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 **/
	@Override
	public IBlockState getStateFromMeta(final int metaIn) {
		int meta = metaIn;
		if (meta < 0)
			meta = 0;
		if (meta > 15)
			meta = 15;
		return this.getDefaultState().withProperty(BlockUtilityGlow.LIGHT_LEVEL, meta).withProperty(BlockLiquid.LEVEL, 0);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 **/
	@Override
	public int getMetaFromState(final IBlockState state) {
		return state.getValue(BlockUtilityGlow.LIGHT_LEVEL).intValue();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public EnumBlockRenderType getRenderType(final IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

//	@Override
//	@SideOnly(Side.CLIENT)
//	public BlockRenderLayer getRenderLayer() {
//		return BlockRenderLayer.TRANSLUCENT;
//	}

	/**
	 * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess, BlockPos, EnumFacing)} whenever
	 * possible. Implementing/overriding is fine.
	 */
	@Deprecated
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState offset = blockAccess.getBlockState(pos.offset(side, 1));
		return !offset.isSideSolid(blockAccess, pos.offset(side, 1), side.getOpposite()) && offset.getMaterial() != Material.WATER;
	}
}
