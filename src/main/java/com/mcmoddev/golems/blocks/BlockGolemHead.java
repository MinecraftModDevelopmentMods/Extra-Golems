package com.mcmoddev.golems.blocks;

import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BlockGolemHead extends HorizontalBlock {

  /*
   * This behavior is modified from that of CARVED_PUMPKIN, where the block is
   * placed if a Golem pattern is found. Here we immediately spawn the golem and
   * shrink the itemstack, without placing the block, meaning that if there is no
   * golem to spawn then the block will be 'tossed' instead of placed.
   *
   * public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new
   * BehaviorDefaultDispenseItem() {
   * 
   * @Override protected ItemStack dispenseStack(final IBlockSource source, final
   * ItemStack stack) { final World world = source.getWorld(); final EnumFacing
   * facing = source.getBlockState().get(BlockDispenser.FACING); final BlockPos
   * blockpos = source.getBlockPos().offset(facing); if
   * (world.isAirBlock(blockpos)) { System.out.println(blockpos.toString() +
   * " IS AIR BLOCK"); if(!world.isRemote) { world.setBlockState(blockpos,
   * GolemItems.GOLEM_HEAD.getDefaultState().with(HORIZONTAL_FACING, facing), 3);
   * } stack.shrink(1); } else { return super.dispenseStack(source, stack); }
   * 
   * return stack; } };
   */
  public BlockGolemHead() {
    super(Block.Properties.from(Blocks.CARVED_PUMPKIN));
    this.setDefaultState(this.getStateContainer().getBaseState().with(HORIZONTAL_FACING, Direction.NORTH));
    // dispenser behavior TODO: NOT WORKING
    // BlockDispenser.registerDispenseBehavior(this.asItem(),
    // BlockGolemHead.DISPENSER_BEHAVIOR);
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
    trySpawnGolem(worldIn, pos);
  }

//	@Override
//	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState) {
//		super.onBlockAdded(state, worldIn, pos, state);
//		trySpawnGolem(worldIn, pos);
//	}

  /**
   * Attempts to build a golem with the given head position. Checks if a golem can
   * be built there and, if so, removes the blocks and spawns the corresponding
   * golem.
   *
   * @param world   current world
   * @param headPos the position of the golem head block
   * @return if the golem was built and spawned
   */
  public static boolean trySpawnGolem(final World world, final BlockPos headPos) {
    if (world.isRemote) {
      return false;
    }

    // get all the block and state values that we will be using in the following
    // code
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
      final SnowGolemEntity entitysnowman = EntityType.SNOW_GOLEM.create(world);
      ExtraGolems.LOGGER.debug("[Extra Golems]: Building regular boring Snow Golem");
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
      ExtraGolems.LOGGER.debug("[Extra Golems]: Building regular boring Iron Golem");
      ironGolem.setPlayerCreated(true);
      ironGolem.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
      world.addEntity(ironGolem);
      return true;
    }

    if (isInvalidBlock(blockBelow1) || isInvalidBlock(blockBelow2)) {
      return false;
    }

    ////// Attempt to spawn a Golem from this mod //////
    GolemBase golem = GolemRegistrar.getGolem(world, blockBelow1, blockBelow2, blockArmNorth, blockArmSouth);
    flagX = false;
    // if no golem found for North-South, try to find one for East-West pattern
    if (golem == null) {
      golem = GolemRegistrar.getGolem(world, blockBelow1, blockBelow2, blockArmEast, blockArmWest);
      flagX = true;
    }

    if (golem != null && golem.getGolemContainer().isEnabled()) {
      // spawn the golem!
      removeAllGolemBlocks(world, headPos, flagX);
      golem.setPlayerCreated(true);
      golem.setLocationAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
      ExtraGolems.LOGGER.debug("[Extra Golems]: Building golem " + golem.toString());
      world.addEntity(golem);
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
   * @return true if the block should not be considered a golem building block
   **/
  private static boolean isInvalidBlock(final Block b) {
    return b == null || b == Blocks.AIR || b == Blocks.WATER;
  }
}
