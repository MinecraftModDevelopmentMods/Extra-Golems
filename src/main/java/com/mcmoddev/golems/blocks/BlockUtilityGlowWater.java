package com.mcmoddev.golems.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockUtilityGlowWater extends BlockUtilityGlow {
	public BlockUtilityGlowWater(final Material m, final float defaultLight, final int tickRate, final IBlockState replaceWith) {
		super(m, defaultLight, tickRate, replaceWith);
		int light = (int) (defaultLight * 15.0F);
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockFlowingFluid.LEVEL, 0).with(BlockUtilityGlow.LIGHT_LEVEL, light));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(BlockUtilityGlow.LIGHT_LEVEL, BlockFlowingFluid.LEVEL);
	}


	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public EnumBlockRenderType getRenderType(final IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	/**
	 * Check if the face of a block should block rendering.
	 *
	 * Faces which are fully opaque should return true, faces with transparency
	 * or faces which do not span the full size of the block should return false.
	 *
	 * @param state The current block state
	 * @param world The current world
	 * @param pos Block position in world
	 * @param face The side to check
	 * @return True if the block is opaque on the specified side.
	 */
	@Override
	public boolean doesSideBlockRendering(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing face) {
		IBlockState offset = world.getBlockState(pos.offset(face, 1));
		//TODO: Make sure this works
		return !(offset.isSideInvisible(offset, face) && offset.getMaterial() != Material.WATER);
	}

//	/**
//	 * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess, BlockPos, EnumFacing)} whenever
//	 * possible. Implementing/overriding is fine.
//	 */
//	@Deprecated
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
//		IBlockState offset = blockAccess.getBlockState(pos.offset(side, 1));
//		return !offset.isSideSolid(blockAccess, pos.offset(side, 1), side.getOpposite()) && offset.getMaterial() != Material.WATER;
//	}


	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
}
