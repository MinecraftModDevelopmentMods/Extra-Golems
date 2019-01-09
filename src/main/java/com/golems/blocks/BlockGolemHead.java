package com.golems.blocks;

import com.golems.entity.GolemBase;
import com.golems.events.GolemBuildEvent;
import com.golems.items.ItemBedrockGolem;
import com.golems.main.ExtraGolems;

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
import net.minecraftforge.common.MinecraftForge;

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
		return new BlockStateContainer(this, (IProperty[]) new IProperty[] { FACING });
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
		final Block blockBelow1 = stateBelow1.getBlock();
		final Block blockBelow2 = world.getBlockState(pos.down(2)).getBlock();
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();

		if (blockBelow1 == blockBelow2) {
			final boolean flagX = isGolemXAligned(world, pos);
			final boolean flagZ = isGolemZAligned(world, pos);
			// final IBlockState meta = world.getBlockState(pos.down(1));

			// hard-coded support for Snow Golem
			if (blockBelow1 == Blocks.SNOW) {
				if (!world.isRemote) {
					removeGolemBody(world, pos);
					final EntitySnowman entitysnowman = new EntitySnowman(world);
					ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Snow Golem\n");
					entitysnowman.setLocationAndAngles((double) x + 0.5D, (double) y - 1.95D,
							(double) z + 0.5D, 0.0F, 0.0F);
					world.spawnEntity(entitysnowman);
				}

				ItemBedrockGolem.spawnParticles(world, x + 0.5D, y - 1.5D, z + 0.5D, 0.2D);
				return;
			}

			if (flagX || flagZ) {
				if (!world.isRemote) {
					// hard-coded support for Iron Golem
					if (blockBelow1 == Blocks.IRON_BLOCK) {
						removeAllGolemBlocks(world, pos, flagX);
						// spawn the golem
						final EntityIronGolem golem = new EntityIronGolem(world);
						ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Iron Golem\n");
						golem.setPlayerCreated(true);
						golem.setLocationAndAngles((double) x + 0.5D, (double) y - 1.95D,
								(double) z + 0.5D, 0.0F, 0.0F);
						world.spawnEntity(golem);
						return;
					}

					// post an event that, when handled, will initialize the golem to spawn
					final boolean sameMeta = getAreGolemBlocksSameMeta(world, pos, stateBelow1, flagX);
					final GolemBuildEvent event = new GolemBuildEvent(world, stateBelow1, sameMeta, flagX);
					MinecraftForge.EVENT_BUS.post(event);
					if (event.isGolemNull() || event.isGolemBanned()) {
						return;
					}

					// clear the area where the golem blocks were
					removeAllGolemBlocks(world, pos, flagX);

					// spawn the golem
					final GolemBase golem = event.getGolem();
					ExtraGolems.LOGGER.info("[Extra Golems]: Building golem " + golem.toString() + "\n");
					golem.setPlayerCreated(true);
					golem.setLocationAndAngles((double) x + 0.5D, (double) y - 1.95D,
							(double) z + 0.5D, 0.0F, 0.0F);
					world.spawnEntity(golem);
				}
			}
		}
	}

	/**
	 * @return {@code true} if the blocks at x-1 and x+1 match the block at x.
	 **/
	public static boolean isGolemXAligned(final World world, final BlockPos headPos) {
		final BlockPos[] armsX = { headPos.down(1).west(1), headPos.down(1).east(1) };
		final Block below = world.getBlockState(headPos.down(1)).getBlock();
		return world.getBlockState(armsX[0]).getBlock() == below
				&& world.getBlockState(armsX[1]).getBlock() == below;
	}

	/**
	 * @return {@code true} if the blocks at z-1 and z+1 match the block at z.
	 **/
	public static boolean isGolemZAligned(final World world, final BlockPos headPos) {
		final BlockPos[] armsZ = { headPos.down(1).north(1), headPos.down(1).south(1) };
		final Block below = world.getBlockState(headPos.down(1)).getBlock();
		return world.getBlockState(armsZ[0]).getBlock() == below
				&& world.getBlockState(armsZ[1]).getBlock() == below;
	}
	
	/**
	 * @return true if all 4 construction blocks have the same metadata
	 **/
	public static boolean getAreGolemBlocksSameMeta(final World worldObj, final BlockPos headPos, IBlockState blockState, boolean isGolemXAligned) {
		// SOUTH=z++; WEST=x--; NORTH=z--; EAST=x++
		final Block blockBelow = blockState.getBlock();
		final BlockPos[] armsX = { headPos.down(1).west(1), headPos.down(1).east(1) };
		final BlockPos[] armsZ = { headPos.down(1).north(1), headPos.down(1).south(1) };
		final int metaBelow1 = blockBelow.getMetaFromState(blockState);
		IBlockState state;
		state = worldObj.getBlockState(headPos.down(2));
		final int metaBelow2 = blockBelow.getMetaFromState(state);
		state = isGolemXAligned ? worldObj.getBlockState(armsX[0])
				: worldObj.getBlockState(armsZ[0]);
		final int metaArm1 = blockBelow.getMetaFromState(state);
		state = isGolemXAligned ? worldObj.getBlockState(armsX[1])
				: worldObj.getBlockState(armsZ[1]);
		final int metaArm2 = blockBelow.getMetaFromState(state);

		return metaBelow1 == metaBelow2 && metaBelow2 == metaArm1 && metaArm1 == metaArm2;
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

	/** Replaces blocks at arm positions with air. **/
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
