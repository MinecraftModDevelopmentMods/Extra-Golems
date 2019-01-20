package com.golems.blocks;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
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
        return new BlockStateContainer(this, new IProperty[]{BlockUtilityGlow.LIGHT_LEVEL, BlockLiquid.LEVEL});
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     **/
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

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

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

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess, BlockPos, EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
