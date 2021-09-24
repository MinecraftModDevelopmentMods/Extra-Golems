package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.event.AoeFunction;
import com.mcmoddev.golems.event.GolemModifyBlocksEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class AoeBlocksGoal extends Goal {
  protected final GolemBase entity;
  protected final int range;
  protected final int interval;
  protected final boolean sphere;
  protected final AoeFunction modifyFunction;
  
  protected int cooldown;

  public AoeBlocksGoal(final GolemBase golemIn, final int rangeIn, final int intervalIn,
      final boolean sphereIn, final AoeFunction modifyIn) {
    entity = golemIn;
    range = rangeIn;
    interval = Math.max(1, intervalIn);
    sphere = sphereIn;
    modifyFunction = modifyIn;
  }

  @Override
  public boolean shouldExecute() {
    return !entity.isChild() && range > 0 && entity.world.getRandom().nextInt(interval) == 0;
  }
  
  @Override
  public void startExecuting() {
    final BlockPos below = entity.getBlockBelow();
    final GolemModifyBlocksEvent event = new GolemModifyBlocksEvent(entity, below, range, sphere, modifyFunction);
    if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY) {
      event.getAffectedPositions().forEach(pos -> entity.world.setBlockState(pos, event.getFunction().map(entity, pos, entity.world.getBlockState(pos)), event.updateFlag));
    }
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }
  
  public static class DryFunction implements AoeFunction {
    
    public DryFunction() { }

    @Override
    public BlockState map(final LivingEntity entity, final BlockPos pos, final BlockState input) {
      if(input.hasProperty(BlockStateProperties.WATERLOGGED)) {
        return input.with(BlockStateProperties.WATERLOGGED, false);
      }
      if(input.getMaterial() == Material.WATER || input.getMaterial() == Material.OCEAN_PLANT) {
        return Blocks.AIR.getDefaultState();
      }
      return input;
    }
  }
  
  public static class FreezeFunction implements AoeFunction {
    
    /** This percentage of Packed Ice placed will become regular ice instead. **/
    public final int iceChance = 52;
    /** This percentage of Obsidian placed will become cobblestone instead. **/
    public final int cobbleChance = 29;
    /** When true, all water will turn to Frosted Ice **/
    public final boolean frostedIce;

    public FreezeFunction(final boolean useFrost) {
      this.frostedIce = useFrost;
    }

    @Override
    public BlockState map(final LivingEntity entity, final BlockPos pos, final BlockState input) {
      final BlockState cobbleState = Blocks.COBBLESTONE.getDefaultState();
      final BlockState iceState = this.frostedIce ? Blocks.FROSTED_ICE.getDefaultState() : Blocks.ICE.getDefaultState();
      final Material material = input.getMaterial();
      if (material.isLiquid()) {
        final Block block = input.getBlock();

        if (block == Blocks.WATER) {
          final boolean isNotPacked = this.frostedIce || entity.world.getRandom().nextInt(100) < this.iceChance;
          return isNotPacked ? iceState : Blocks.PACKED_ICE.getDefaultState();
        } else if (block == Blocks.LAVA) {
          final boolean isNotObsidian = entity.world.getRandom().nextInt(100) < this.cobbleChance;
          return isNotObsidian ? cobbleState : Blocks.OBSIDIAN.getDefaultState();
        }
      }

      return input;
    }
  }
  
  public static class GrowFunction implements AoeFunction {
    
    private final float growChance;
    
    public GrowFunction(final float growChanceIn) {
      this.growChance = growChanceIn;
    }

    @Override
    public BlockState map(LivingEntity entity, BlockPos pos, BlockState input) {
      // if the block can be grown, grow it and return
      if (input.getBlock() instanceof CropsBlock) {
        CropsBlock crop = (CropsBlock) input.getBlock();
        if(!crop.isMaxAge(input) && entity.world.getRandom().nextFloat() < growChance) {
          // determine the next grow stage for the crop
          int growAge = input.get(crop.getAgeProperty()) + MathHelper.nextInt(entity.world.getRandom(), 2, 5);
          int maxAge = crop.getMaxAge();
          if (growAge > maxAge) {
             growAge = maxAge;
          }
          // return the updated crop
          return crop.withAge(growAge);
        }
      }
      return input;
    }
    
  }
}
