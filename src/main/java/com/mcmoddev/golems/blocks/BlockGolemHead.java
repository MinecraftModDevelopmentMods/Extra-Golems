package com.mcmoddev.golems.blocks;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public final class BlockGolemHead extends BlockHorizontal {

	public BlockGolemHead() {
		super(Properties.from(Blocks.CARVED_PUMPKIN));
		this.setDefaultState(this.getStateContainer().getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH));
	}

	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public IBlockState getStateForPlacement(IBlockState state, EnumFacing facing, IBlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, EnumHand hand) {
		return this.getDefaultState().with(HORIZONTAL_FACING, facing.getOpposite());
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
		if(!(blockBelow1 == blockBelow2 && blockBelow1 != Blocks.AIR)) {
			return false;
		}
		// hard-coded support for Snow Golem
		if (blockBelow1 == Blocks.SNOW_BLOCK) {
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
			final GolemBase golem = GolemRegistrar.getGolem(world, blockBelow1);
			if (golem == null) return false;
			//get the spawn permissions
			if(!golem.getGolemContainer().enabled) return false;

			removeAllGolemBlocks(world, pos, flagX);
			golem.setPlayerCreated(true);
			golem.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
			ExtraGolems.LOGGER.info("[Extra Golems]: Building golem " + golem.toString());
			world.spawnEntity(golem);
			golem.onBuilt(stateBelow1, stateBelow2, arm1, arm2);
			if(!golem.updateHomeVillage()) {
				golem.setHomePosAndDistance(golem.getPosition(), GolemBase.WANDER_DISTANCE);
			}
			return true;
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
		world.destroyBlock(head, false);
		world.destroyBlock(head.down(1), false);
		world.destroyBlock(head.down(2), false);
	}

	/**
	 * Replaces blocks at arm positions with air.
	 **/
	public static void removeGolemArms(final World world, final BlockPos pos, final boolean isXAligned) {
		if (isXAligned) {
			world.destroyBlock(pos.down(1).west(1), false);
			world.destroyBlock(pos.down(1).east(1), false);
		} else {
			world.destroyBlock(pos.down(1).north(1), false);
			world.destroyBlock(pos.down(1).south(1), false);
		}
	}
}
