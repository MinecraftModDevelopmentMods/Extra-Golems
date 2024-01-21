package com.mcmoddev.golems.block;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;

public final class GolemHeadBlock extends HorizontalDirectionalBlock {

	public GolemHeadBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.NORMAL;
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
	public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState oldState, boolean isMoving) {
		super.onPlace(blockState, level, blockPos, oldState, isMoving);
		trySpawnGolem(null, level, blockPos);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		trySpawnGolem(placer, level, pos);
	}

	public static void registerDispenserBehavior() {
		// load carved pumpkin behavior
		final DispenseItemBehavior carvedPumpkinBehavior = DispenserBlock.DISPENSER_REGISTRY.getOrDefault(Items.CARVED_PUMPKIN, new DefaultDispenseItemBehavior());
		final DispenseItemBehavior wrappedBehavior = new OptionalDispenseItemBehavior() {
			protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
				final Level level = blockSource.getLevel();
				final Direction facing = blockSource.getBlockState().getValue(DispenserBlock.FACING);
				final BlockPos blockpos = blockSource.getPos().relative(facing);
				// check if the block can be placed
				if(level.isEmptyBlock(blockpos) && GolemHeadBlock.canSpawnGolem(level, blockpos)) {
					if (!level.isClientSide) {
						// place the block
						level.setBlock(blockpos, EGRegistry.BlockReg.GOLEM_HEAD.get().defaultBlockState().setValue(FACING, facing), Block.UPDATE_ALL);
						level.gameEvent(null, GameEvent.BLOCK_PLACE, blockpos);
					}
					// shrink item stack
					itemStack.shrink(1);
					this.setSuccess(true);
				}
				if(itemStack.is(Items.CARVED_PUMPKIN) || ExtraGolems.CONFIG.pumpkinBuildsGolems()) {
					return carvedPumpkinBehavior.dispense(blockSource, itemStack);
				}
				return itemStack;
			}
		};

		// register dispenser behaviors
		DispenserBlock.registerBehavior(EGRegistry.ItemReg.GOLEM_HEAD.get(), wrappedBehavior);
		DispenserBlock.registerBehavior(Items.CARVED_PUMPKIN, wrappedBehavior);
	}

	/**
	 * Checks if a GolemBase can be built the given position.
	 *
	 * @param level   current world
	 * @param headPos the position of the entity head block
	 * @return if the entity was built and spawned
	 */
	public static boolean canSpawnGolem(final Level level, final BlockPos headPos) {
		// get all the block values that we will be using in the following
		final Block blockBelow1 = level.getBlockState(headPos.below(1)).getBlock();
		final Block blockBelow2 = level.getBlockState(headPos.below(2)).getBlock();
		final Block blockArmNorth = level.getBlockState(headPos.below(1).north(1)).getBlock();
		final Block blockArmSouth = level.getBlockState(headPos.below(1).south(1)).getBlock();
		final Block blockArmEast = level.getBlockState(headPos.below(1).east(1)).getBlock();
		final Block blockArmWest = level.getBlockState(headPos.below(1).west(1)).getBlock();
		// true if the entity is completely Iron Blocks
		boolean isIron;

		////// Hard-coded support for Snow Golem //////
		if (doBlocksMatch(Blocks.SNOW_BLOCK, blockBelow1, blockBelow2)) {
			return true;
		}

		////// Hard-coded support for Iron Golem //////
		isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		if (!isIron) {
			// try to find an Iron Golem east-west aligned
			isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
		}

		if (isIron) {
			return true;
		}

		if (isInvalidBlock(blockBelow1) || isInvalidBlock(blockBelow2)) {
			return false;
		}

		////// Attempt to locate a Golem from this mod //////
		ResourceLocation golemId = ExtraGolems.getGolemId(level, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		// if no entity found for North-South, try to find one for East-West pattern
		if (golemId == null) {
			golemId = ExtraGolems.getGolemId(level, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
		}

		return golemId != null;
	}

	/**
	 * Attempts to build a GolemBase with the given head position. Checks if a entity can
	 * be built there and, if so, removes the blocks and spawns the corresponding
	 * entity.
	 *
	 * @param placer  the living entity that triggered the golem spawning
	 * @param level   current world
	 * @param headPos the position of the entity head block
	 * @return if the entity was built and spawned
	 */
	public static boolean trySpawnGolem(@Nullable final Entity placer, final Level level, final BlockPos headPos) {
		// get all the block and state values that we will be using in the following
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
		boolean isEastWest;
		// true if the entity is completely Iron Blocks
		boolean isIron;

		////// Hard-coded support for Snow Golem //////
		if (doBlocksMatch(Blocks.SNOW_BLOCK, blockBelow1, blockBelow2)) {
			if(!level.isClientSide()) {
				removeGolemBody(level, headPos);
				final SnowGolem entitysnowman = EntityType.SNOW_GOLEM.create(level);
				entitysnowman.moveTo(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
				level.addFreshEntity(entitysnowman);
			}
			return true;
		}

		////// Hard-coded support for Iron Golem //////
		isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		isEastWest = false;
		if (!isIron) {
			// try to find an Iron Golem east-west aligned
			isIron = doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
			isEastWest = true;
		}

		if (isIron) {
			if(!level.isClientSide()) {
				removeAllGolemBlocks(level, headPos, isEastWest);
				// build Iron Golem
				final IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);
				ironGolem.setPlayerCreated(true);
				ironGolem.moveTo(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
				level.addFreshEntity(ironGolem);
			}
			return true;
		}

		if (isInvalidBlock(blockBelow1) || isInvalidBlock(blockBelow2)) {
			return false;
		}

		////// Attempt to spawn a Golem from this mod //////
		ResourceLocation golemId = ExtraGolems.getGolemId(level, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
		isEastWest = false;
		// if no entity found for North-South, try to find one for East-West pattern
		if (golemId == null) {
			golemId = ExtraGolems.getGolemId(level, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
			isEastWest = true;
		}

		if (golemId != null) {
			if(!level.isClientSide()) {
				final GolemBase golem = GolemBase.create(level, golemId);
				// spawn the entity!
				removeAllGolemBlocks(level, headPos, isEastWest);
				golem.setPlayerCreated(true);
				golem.moveTo(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
				level.addFreshEntity(golem);
				if (level instanceof ServerLevel) {
					golem.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(headPos), MobSpawnType.MOB_SUMMONED, null, null);
				}
				if(isEastWest) {
					golem.onBuilt(stateBelow1, stateBelow2, stateArmEast, stateArmWest, placer);
				} else {
					golem.onBuilt(stateBelow1, stateBelow2, stateArmNorth, stateArmSouth, placer);
				}
			}
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
		return b == null || b == Blocks.AIR || b == Blocks.WATER || b == Blocks.LAVA;
	}
}
