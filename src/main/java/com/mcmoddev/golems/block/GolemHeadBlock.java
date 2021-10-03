package com.mcmoddev.golems.block;

import javax.annotation.Nullable;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public final class GolemHeadBlock extends HorizontalBlock {

  public GolemHeadBlock() {
	super(Block.Properties.from(Blocks.CARVED_PUMPKIN));
	this.setDefaultState(this.getStateContainer().getBaseState().with(HORIZONTAL_FACING, Direction.NORTH));
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
	return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	builder.add(HORIZONTAL_FACING);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
	super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	trySpawnGolem(placer, worldIn, pos);
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
	if(worldIn instanceof World && trySpawnGolem(null, (World)worldIn, currentPos)) {
	  return Blocks.AIR.getDefaultState();
	}
	return stateIn;
  }

  public static boolean canDispenserPlace(IWorldReader world, BlockPos headPos) {
	// get all the block state values that we will be using for matching
	final Block blockBelow1 = world.getBlockState(headPos.down(1)).getBlock();
	final Block blockBelow2 = world.getBlockState(headPos.down(2)).getBlock();
	final Block blockArmNorth = world.getBlockState(headPos.down(1).north(1)).getBlock();
	final Block blockArmSouth = world.getBlockState(headPos.down(1).south(1)).getBlock();
	final Block blockArmEast = world.getBlockState(headPos.down(1).east(1)).getBlock();
	final Block blockArmWest = world.getBlockState(headPos.down(1).west(1)).getBlock();
	// snow golem
	if(doBlocksMatch(Blocks.SNOW_BLOCK, blockBelow1, blockBelow2)) {
	  return true;
	}
	// iron golem north-east
	if(doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth)) {
	  return true;
	}
	// iron golem east-west
	if(doBlocksMatch(Blocks.IRON_BLOCK, blockBelow1, blockBelow2, blockArmEast, blockArmWest)) {
	  return true;
	}
	// extra golem north-south
	if(ExtraGolems.getGolemId(blockBelow1, blockBelow2, blockArmNorth, blockArmSouth).isPresent()) {
	  return true;
	}
	// extra golem east-west
	if(ExtraGolems.getGolemId(blockBelow1, blockBelow2, blockArmEast, blockArmWest).isPresent()) {
	  return true;
	}
	// no golem pattern detected
	return false;
  }

  /**
   * Attempts to build a entity with the given head position. Checks if a entity can
   * be built there and, if so, removes the blocks and spawns the corresponding
   * entity.
   *
   * @param placer the living entity that triggered the golem spawning
   * @param world   current world
   * @param headPos the position of the entity head block
   * @return if the entity was built and spawned
   */
  public static boolean trySpawnGolem(@Nullable final Entity placer, final World world, final BlockPos headPos) {
    if (world.isRemote()) {
      return false;
    }

    // get all the block and state values that we will be using for matching
    final BlockState stateBelow1 = world.getBlockState(headPos.down(1));
    final BlockState stateBelow2 = world.getBlockState(headPos.down(2));
    final BlockState stateArmNorth = world.getBlockState(headPos.down(1).north(1));
    final BlockState stateArmSouth = world.getBlockState(headPos.down(1).south(1));
    final BlockState stateArmEast = world.getBlockState(headPos.down(1).east(1));
    final BlockState stateArmWest = world.getBlockState(headPos.down(1).west(1));
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
      removeGolemBody(world, headPos);
      final SnowGolemEntity entitysnowman = EntityType.SNOW_GOLEM.create(world);
      ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Snow Golem");
      entitysnowman.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
      world.addEntity(entitysnowman);
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
      removeAllGolemBlocks(world, headPos, flagX);
      // build Iron Golem
      final IronGolemEntity ironGolem = EntityType.IRON_GOLEM.create(world);
      ExtraGolems.LOGGER.info("[Extra Golems]: Building regular boring Iron Golem");
      ironGolem.setPlayerCreated(true);
      ironGolem.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
      world.addEntity(ironGolem);
      return true;
    }

    if (isInvalidBlock(blockBelow1) || isInvalidBlock(blockBelow2)) {
      return false;
    }

    ////// Attempt to spawn a Golem from this mod //////
    GolemBase golem = ExtraGolems.getGolem(world, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
    flagX = false;
    // if no entity found for North-South, try to find one for East-West pattern
    if (golem == null) {
      golem = ExtraGolems.getGolem(world, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
      flagX = true;
    }

    if (golem != null) {
      // spawn the entity!
      removeAllGolemBlocks(world, headPos, flagX);
      golem.setPlayerCreated(true);
      golem.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
      ExtraGolems.LOGGER.debug("[Extra Golems]: Building golem " + golem.toString());
      world.addEntity(golem);
      if(placer != null && placer.getEntityWorld() instanceof ServerWorld) {
        golem.onInitialSpawn((ServerWorld)placer.getEntityWorld(), world.getDifficultyForLocation(headPos), SpawnReason.MOB_SUMMONED, null, null);
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

  /**
   * @return true if the block should not be considered a entity building block
   **/
  private static boolean isInvalidBlock(final Block b) {
    return b == null || b == Blocks.AIR || b == Blocks.WATER;
  }
}
