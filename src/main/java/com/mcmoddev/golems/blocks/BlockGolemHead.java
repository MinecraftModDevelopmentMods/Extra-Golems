package com.mcmoddev.golems.blocks;

import com.mcmoddev.golems.entity.base.GolemBase;
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

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
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
	 * @param headPos the position of the golem head block
	 * @return if the golem was built and spawned
	 */
	public static boolean trySpawnGolem(final World world, final BlockPos headPos) {
		if(world.isRemote) return false;
		
		// get all the block and state values that we will be using in the following code
		final IBlockState stateBelow1 = world.getBlockState(headPos.down(1));
		final IBlockState stateBelow2 = world.getBlockState(headPos.down(2));
		final IBlockState stateArmNorth = world.getBlockState(headPos.down(1).north(1));
		final IBlockState stateArmSouth = world.getBlockState(headPos.down(1).south(1));
		final IBlockState stateArmEast = world.getBlockState(headPos.down(1).east(1));
		final IBlockState stateArmWest = world.getBlockState(headPos.down(1).west(1));
		final Block blockBelow1 = stateBelow1.getBlock();
		final Block blockBelow2 = stateBelow2.getBlock();
		final Block blockArmNorth = stateArmNorth.getBlock();
		final Block blockArmSouth = stateArmSouth.getBlock();
		final Block blockArmEast = stateArmEast.getBlock();
		final Block blockArmWest = stateArmWest.getBlock();
		// this is where the golem will spawn at the end
		final double spawnX = headPos.getX() + 0.5D;
		final double spawnY = headPos.getY() - 1.95D;
		final double spawnZ = headPos.getZ() + 0.5D;
		// true if the golem is East-West aligned
		boolean flagX;
		// true if the golem is completely Iron Blocks
		boolean isIron;
		
		////// Hard-coded support for Snow Golem //////
		if (doBlocksMatch(Blocks.SNOW_BLOCK, blockBelow1, blockBelow2)) {
			removeGolemBody(world, headPos);
			final EntitySnowman entitysnowman = new EntitySnowman(world);
			ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Snow Golem");
			entitysnowman.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
			world.spawnEntity(entitysnowman);
			return true;
		}
		
		////// Hard-coded support for Iron Golem //////
		isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		flagX = false;
		if(!isIron) {
			// try to find an Iron Golem east-west aligned
			isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
			flagX = true;
		}
		
		if (isIron) {
			removeAllGolemBlocks(world, headPos, flagX);
			// build Iron Golem
			final EntityIronGolem ironGolem = new EntityIronGolem(world);
			ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Iron Golem");
			ironGolem.setPlayerCreated(true);
			ironGolem.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
			world.spawnEntity(ironGolem);
			return true;
		}
		
		////// Attempt to spawn a Golem from this mod //////
		GolemBase golem = GolemRegistrar.getGolem(world, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		flagX = false;
		// if no golem found for North-South, try to find one for East-West pattern
		if(golem == null) {
			golem = GolemRegistrar.getGolem(world, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
			flagX = true;
		}

		if(golem != null && golem.getGolemContainer().isEnabled()) {
			// spawn the golem!
			removeAllGolemBlocks(world, headPos, flagX);
			golem.setPlayerCreated(true);
			golem.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
			ExtraGolems.LOGGER.info("[Extra Golems]: Building golem " + golem.toString());
			world.spawnEntity(golem);
			golem.onBuilt(stateBelow1, stateBelow2, flagX ? stateArmEast : stateArmWest, flagX ? stateArmNorth : stateArmSouth);
			if(!golem.updateHomeVillage()) {
				golem.setHomePosAndDistance(golem.getPosition(), GolemBase.WANDER_DISTANCE);
			}
			return true;
		}
		// No Golems of any kind were spawned :(
		return false;
	}

	/**
	 * 
	 * @param master the Block to check against
	 * @param toCheck other Block values that you want to ensure are equal
	 * @return true if [every Block in {@code toCheck}] == master
	 **/
	public static boolean doBlocksMatch(Block master, Block... toCheck) {
		boolean success = toCheck != null && toCheck.length > 0;
		for(Block b : toCheck) {
			success &= b == master;
		}
		return success;
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
