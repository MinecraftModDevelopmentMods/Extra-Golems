package com.golems.blocks;

import com.golems.entity.GolemBase;
import com.golems.items.ItemBedrockGolem;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public final class BlockGolemHead extends BlockHorizontal {

    public BlockGolemHead() {
        super(Material.GROUND);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(CreativeTabs.MISC);
        this.setSoundType(SoundType.WOOD);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
                                            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING,
                placer.getHorizontalFacing().getOpposite());
    }

    /**
     * Convert the given metadata into a BlockState for this Block.
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value.
     */
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty[]) new IProperty[]{FACING});
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(final World world, final BlockPos pos, final IBlockState state) {
        super.onBlockAdded(world, pos, state);
        final IBlockState stateBelow1 = world.getBlockState(pos.down(1));
        final IBlockState stateBelow2 = world.getBlockState(pos.down(2));
        final Block blockBelow1 = stateBelow1.getBlock();
        final Block blockBelow2 = stateBelow2.getBlock();
        final double x = pos.getX() + 0.5D;
        final double y = pos.getY() - 1.95D;
        final double z = pos.getZ() + 0.5D;

        if (blockBelow1 == blockBelow2) {
            final boolean flagX = isGolemXAligned(world, pos);
            final boolean flagZ = isGolemZAligned(world, pos);

            // hard-coded support for Snow Golem
            if (blockBelow1 == Blocks.SNOW) {
                if (!world.isRemote) {
                    removeGolemBody(world, pos);
                    final EntitySnowman entitysnowman = new EntitySnowman(world);
                    ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Snow Golem\n");
                    entitysnowman.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
                    world.spawnEntity(entitysnowman);
                }

                ItemBedrockGolem.spawnParticles(world, x, y + 0.5D, z, 0.2D);
                return;
            }

            if (!world.isRemote && (flagX || flagZ)) {
                // determine each arm of the golem
                EnumFacing face = flagX ? EnumFacing.EAST : EnumFacing.NORTH;
                IBlockState arm1 = world.getBlockState(pos.down(1).offset(face, 1));
                IBlockState arm2 = world.getBlockState(pos.down(1).offset(face.getOpposite(), 1));

                // hard-coded support for Iron Golem
                if (blockBelow1 == Blocks.IRON_BLOCK) {
                    removeAllGolemBlocks(world, pos, flagX);
                    // build Iron Golem
                    final EntityIronGolem golem = new EntityIronGolem(world);
                    ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Iron Golem\n");
                    golem.setPlayerCreated(true);
                    golem.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
                    world.spawnEntity(golem);
                    return;
                }

                // query the GolemLookup to see if there is a golem that can be built with this
                // if there is, double-check its spawn permissions, then build!
                if (GolemLookup.isBuildingBlock(blockBelow1)) {
                    // get the golem
                    final GolemBase golem = GolemLookup.getGolem(world, blockBelow1);
                    ExtraGolems.LOGGER.log(Level.DEBUG, "golem = " + (golem != null ? golem.toString() : "null"));
                    ExtraGolems.LOGGER.log(Level.DEBUG, "block = " + blockBelow1.toString());
                    if (golem == null) return;

                    // get the spawn permissions (assume it's allowed if none found)
                    final GolemConfigSet cfg = GolemLookup.getConfig(golem.getClass());
                    boolean allowed = cfg == null || cfg.canSpawn();
                    ExtraGolems.LOGGER.log(Level.DEBUG, "CFG = " + (cfg != null ? cfg.toString() : "null"));
                    if (!allowed) return;

                    // clear the area where the golem blocks were
                    removeAllGolemBlocks(world, pos, flagX);

                    // spawn the golem
                    ExtraGolems.LOGGER.info("[Extra Golems]: Building golem " + golem.toString() + "\n");
                    golem.setPlayerCreated(true);
                    golem.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
                    world.spawnEntity(golem);
                    golem.onBuilt(stateBelow1, stateBelow2, arm1, arm2);

                }
            }
        }
    }

    /**
     * @return {@code true} if the blocks at x-1 and x+1 match the block at x.
     **/
    public static boolean isGolemXAligned(final World world, final BlockPos headPos) {
        final BlockPos[] armsX = {headPos.down(1).west(1), headPos.down(1).east(1)};
        final Block below = world.getBlockState(headPos.down(1)).getBlock();
        return world.getBlockState(armsX[0]).getBlock() == below
                && world.getBlockState(armsX[1]).getBlock() == below;
    }

    /**
     * @return {@code true} if the blocks at z-1 and z+1 match the block at z.
     **/
    public static boolean isGolemZAligned(final World world, final BlockPos headPos) {
        final BlockPos[] armsZ = {headPos.down(1).north(1), headPos.down(1).south(1)};
        final Block below = world.getBlockState(headPos.down(1)).getBlock();
        return world.getBlockState(armsZ[0]).getBlock() == below
                && world.getBlockState(armsZ[1]).getBlock() == below;
    }

    /**
     * Replaces this block and the four construction blocks with air.
     **/
    public static void removeAllGolemBlocks(final World world, final BlockPos pos, final boolean isXAligned) {
        removeGolemBody(world, pos);
        removeGolemArms(world, pos, isXAligned);
    }

    /**
     * Replaces this block and the two below it with air.
     **/
    public static void removeGolemBody(final World world, final BlockPos head) {
        world.setBlockToAir(head);
        world.setBlockToAir(head.down(1));
        world.setBlockToAir(head.down(2));
    }

    /**
     * Replaces blocks at arm positions with air.
     **/
    public static void removeGolemArms(final World world, final BlockPos pos, final boolean isXAligned) {
        if (isXAligned) {
            world.setBlockToAir(pos.down(1).west(1));
            world.setBlockToAir(pos.down(1).east(1));
        } else {
            world.setBlockToAir(pos.down(1).north(1));
            world.setBlockToAir(pos.down(1).south(1));
        }
    }
}
