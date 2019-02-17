package com.golems.blocks;

import com.golems.entity.GolemBase;
import com.golems.items.ItemBedrockGolem;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public final class BlockGolemHead extends BlockHorizontal {

	public BlockGolemHead() {
		super(Properties.from(Blocks.PUMPKIN));
		this.setDefaultState(this.getStateContainer().getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH));
	}

	@Nullable
	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}


	@Override
	public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
		super.onBlockAdded(state, worldIn, pos, state);
		trySpawnGolem(worldIn, pos);
	}
	
	/**
	 * Attempts to build a golem with the given head position.
	 * Checks if a golem can be built there and, if so, removes
	 * the blocks and spawns the corresponding golem.
	 * @param world current world
	 * @param pos the position of the golem head block
	 * @return if the golem was built and spawned
	 */
	public static boolean trySpawnGolem(final World world, final BlockPos pos) {
		final IBlockState stateBelow1 = world.getBlockState(pos.down(1));
		final IBlockState stateBelow2 = world.getBlockState(pos.down(2));
		final Block blockBelow1 = stateBelow1.getBlock();
		final Block blockBelow2 = stateBelow2.getBlock();
		final double x = pos.getX() + 0.5D;
		final double y = pos.getY() - 1.95D;
		final double z = pos.getZ() + 0.5D;

		if (blockBelow1 == blockBelow2 && blockBelow1 != Blocks.AIR) {
			// hard-coded support for Snow Golem
			if (blockBelow1 == Blocks.SNOW) {
				if (!world.isRemote) {
					removeGolemBody(world, pos);
					final EntitySnowman entitysnowman = new EntitySnowman(world);
					ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Snow Golem");
					entitysnowman.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
					world.spawnEntity(entitysnowman);
				}

				ItemBedrockGolem.spawnParticles(world, x, y + 0.5D, z, 0.2D);
				return true;
			}
			
			final boolean flagX = isGolemXAligned(world, pos);
			final boolean flagZ = isGolemZAligned(world, pos);
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
					ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Iron Golem");
					golem.setPlayerCreated(true);
					golem.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
					world.spawnEntity(golem);
					return true;
				}

				// query the GolemLookup to see if there is a golem that can be built with this
				// if there is, double-check its spawn permissions, then build!
				if (GolemLookup.isBuildingBlock(blockBelow1)) {
					// get the golem
					final GolemBase golem = GolemLookup.getGolem(world, blockBelow1);
					if (golem == null) return false;

					// get the spawn permissions (assume it's allowed if none found)
					final GolemConfigSet cfg = GolemLookup.getConfig(golem.getClass());
					boolean allowed = cfg != null && cfg.canSpawn();
					if (!allowed) return false;

					// clear the area where the golem blocks were
					removeAllGolemBlocks(world, pos, flagX);

					// spawn the golem
					ExtraGolems.LOGGER.info("[Extra Golems]: Building golem " + golem.toString());
					golem.setPlayerCreated(true);
					golem.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
					world.spawnEntity(golem);
					golem.onBuilt(stateBelow1, stateBelow2, arm1, arm2);
					if(!golem.updateHomeVillage()) {
						golem.setHomePosAndDistance(golem.getPosition(), GolemBase.WANDER_DISTANCE);
					}
					return true;
				}
			}
		}
		return false;
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
		world.removeBlock(head);
		world.removeBlock(head.down(1));
		world.removeBlock(head.down(2));
	}

	/**
	 * Replaces blocks at arm positions with air.
	 **/
	public static void removeGolemArms(final World world, final BlockPos pos, final boolean isXAligned) {
		if (isXAligned) {
			world.removeBlock(pos.down(1).west(1));
			world.removeBlock(pos.down(1).east(1));
		} else {
			world.removeBlock(pos.down(1).north(1));
			world.removeBlock(pos.down(1).south(1));
		}
	}
}
