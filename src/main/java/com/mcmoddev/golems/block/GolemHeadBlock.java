package com.mcmoddev.golems.block;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nullable;

public final class GolemHeadBlock extends HorizontalDirectionalBlock {

//    This behavior is modified from that of CARVED_PUMPKIN, where the block is
//    placed if a Golem pattern is found. Here we immediately spawn the entity and
//    shrink the itemstack, without placing the block, meaning that if there is no
//    entity to spawn then the block will be 'tossed' instead of placed.
//    public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new
//    BehaviorDefaultDispenseItem() {
//    
//    @Override protected ItemStack dispenseStack(final IBlockSource source, final
//    ItemStack stack) { final World world = source.getWorld(); final EnumFacing
//    facing = source.getBlockState().get(BlockDispenser.FACING); final BlockPos
//    blockpos = source.getBlockPos().offset(facing); if
//    (world.isAirBlock(blockpos)) { System.out.println(blockpos.toString() +
//    " IS AIR BLOCK"); if(!world.isRemote) { world.setBlockState(blockpos,
//    EGRegistry.GOLEM_HEAD.getDefaultState().with(HORIZONTAL_FACING, facing), 3);
//    } stack.shrink(1); } else { return super.dispenseStack(source, stack); }
//    
//    return stack; } };

	public GolemHeadBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
		// dispenser behavior TODO: NOT WORKING
		// BlockDispenser.registerDispenseBehavior(this.asItem(),
		// GolemHeadBlock.DISPENSER_BEHAVIOR);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		trySpawnGolem(placer, worldIn, pos);
	}

	/**
	 * Attempts to build a entity with the given head position. Checks if a entity can
	 * be built there and, if so, removes the blocks and spawns the corresponding
	 * entity.
	 *
	 * @param placer  the living entity that triggered the golem spawning
	 * @param level   current world
	 * @param headPos the position of the entity head block
	 * @return if the entity was built and spawned
	 */
	public static boolean trySpawnGolem(@Nullable final Entity placer, final Level level, final BlockPos headPos) {
		if (level.isClientSide()) {
			return false;
		}

		// get all the block and state values that we will be using in the following
		// code
		final BlockState stateBelow1 = level.getBlockState(headPos.below(1));
		final BlockState stateBelow2 = level.getBlockState(headPos.below(2));
		final BlockState stateArmNorth = level.getBlockState(headPos.below(1).north(1));
		final BlockState stateArmSouth = level.getBlockState(headPos.below(1).south(1));
		final BlockState stateArmEast = level.getBlockState(headPos.below(1).east(1));
		final BlockState stateArmWest = level.getBlockState(headPos.below(1).west(1));
		final Block blockBelow1 = stateBelow1.getBlock();
		final Block blockBelow2 = stateBelow2.getBlock();
		final Block blockArmNorth = stateArmNorth.getBlock();
		final Block blockArmSouth = stateArmSouth.getBlock();
		final Block blockArmEast = stateArmEast.getBlock();
		final Block blockArmWest = stateArmWest.getBlock();
		// this is where the entity will spawn at the end
		final double spawnX = headPos.getX() + 0.5D;
		final double spawnY = headPos.getY() - 1.95D;
		final double spawnZ = headPos.getZ() + 0.5D;
		// true if the entity is East-West aligned
		boolean flagX;
		// true if the entity is completely Iron Blocks
		boolean isIron;

		////// Hard-coded support for Snow Golem //////
		if (doBlocksMatch(Blocks.SNOW_BLOCK, blockBelow1, blockBelow2)) {
			removeGolemBody(level, headPos);
			final SnowGolem entitysnowman = EntityType.SNOW_GOLEM.create(level);
			ExtraGolems.LOGGER.debug("[Extra Golems]: Building regular boring Snow Golem");
			entitysnowman.moveTo(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
			level.addFreshEntity(entitysnowman);
			return true;
		}

		////// Hard-coded support for Iron Golem //////
		isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		flagX = false;
		if (!isIron) {
			// try to find an Iron Golem east-west aligned
			isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
			flagX = true;
		}

		if (isIron) {
			removeAllGolemBlocks(level, headPos, flagX);
			// build Iron Golem
			final IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);
			ExtraGolems.LOGGER.debug("[Extra Golems]: Building regular boring Iron Golem");
			ironGolem.setPlayerCreated(true);
			ironGolem.moveTo(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
			level.addFreshEntity(ironGolem);
			return true;
		}

		if (isInvalidBlock(blockBelow1) || isInvalidBlock(blockBelow2)) {
			return false;
		}

		////// Attempt to spawn a Golem from this mod //////
		GolemBase golem = ExtraGolems.getGolem(level, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		flagX = false;
		// if no entity found for North-South, try to find one for East-West pattern
		if (golem == null) {
			golem = ExtraGolems.getGolem(level, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
			flagX = true;
		}

		if (golem != null) {
			// spawn the entity!
			removeAllGolemBlocks(level, headPos, flagX);
			golem.setPlayerCreated(true);
			golem.moveTo(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
			ExtraGolems.LOGGER.debug("[Extra Golems]: Building golem " + golem);
			level.addFreshEntity(golem);
			if (placer != null && placer.getCommandSenderWorld() instanceof ServerLevel) {
				golem.finalizeSpawn((ServerLevel) placer.getCommandSenderWorld(), level.getCurrentDifficultyAt(headPos), MobSpawnType.MOB_SUMMONED, null, null);
			}
			golem.onBuilt(stateBelow1, stateBelow2, flagX ? stateArmEast : stateArmWest, flagX ? stateArmNorth : stateArmSouth);
			return true;
		}
		// No Golems of any kind were spawned :(
		return false;
	}

	/**
	 * @param master  the Block to check against
	 * @param toCheck other Block values that you want to ensure are equal
	 * @return true if [every Block in {@code toCheck}] == master
	 **/
	public static boolean doBlocksMatch(Block master, Block... toCheck) {
		boolean success = toCheck != null && toCheck.length > 0;
		for (Block b : toCheck) {
			success &= b == master;
		}
		return success;
	}

	/**
	 * Replaces this block and the four construction blocks with air.
	 **/
	public static void removeAllGolemBlocks(final Level world, final BlockPos pos, final boolean isXAligned) {
		removeGolemBody(world, pos);
		removeGolemArms(world, pos, isXAligned);
	}

	/**
	 * Replaces this block and the two below it with air.
	 **/
	public static void removeGolemBody(final Level world, final BlockPos head) {
		world.destroyBlock(head, false);
		world.destroyBlock(head.below(1), false);
		world.destroyBlock(head.below(2), false);
	}

	/**
	 * Replaces blocks at arm positions with air.
	 **/
	public static void removeGolemArms(final Level world, final BlockPos pos, final boolean isXAligned) {
		if (isXAligned) {
			world.destroyBlock(pos.below(1).west(1), false);
			world.destroyBlock(pos.below(1).east(1), false);
		} else {
			world.destroyBlock(pos.below(1).north(1), false);
			world.destroyBlock(pos.below(1).south(1), false);
		}
	}

	/**
	 * @return true if the block should not be considered a entity building block
	 **/
	private static boolean isInvalidBlock(final Block b) {
		return b == null || b == Blocks.AIR || b == Blocks.WATER;
	}
}
