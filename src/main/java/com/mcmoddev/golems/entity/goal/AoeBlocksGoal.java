package com.mcmoddev.golems.entity.goal;

import java.util.Random;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.events.AoeFunction;
import com.mcmoddev.golems.events.GolemModifyBlocksEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
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
  public boolean canUse() {
    return range > 0 && entity.tickCount % interval == 0;
  }
  
  @Override
  public void start() {
    final BlockPos below = entity.getBlockBelow();
    final GolemModifyBlocksEvent event = new GolemModifyBlocksEvent(entity, below, range, sphere, modifyFunction);
    if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY) {
      event.getAffectedPositions().forEach(pos -> entity.level.setBlock(pos, event.getFunction().map(entity, pos, entity.level.getBlockState(pos)), event.updateFlag));
    }
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }
  
  public static class DryFunction implements AoeFunction {
    
    public DryFunction() { }

    @Override
    public BlockState map(final LivingEntity entity, final BlockPos pos, final BlockState input) {
      if(input.hasProperty(BlockStateProperties.WATERLOGGED)) {
        return input.setValue(BlockStateProperties.WATERLOGGED, false);
      }
      if(input.getMaterial() == Material.WATER || input.getMaterial() == Material.REPLACEABLE_WATER_PLANT) {
        return Blocks.AIR.defaultBlockState();
      }
      return input;
    }
  }
  
  public static class FreezeFunction implements AoeFunction {
    
    /** Random instance. **/
    public final Random random;
    /** This percentage of Packed Ice placed will become regular ice instead. **/
    public final int iceChance = 52;
    /** This percentage of Obsidian placed will become cobblestone instead. **/
    public final int cobbleChance = 29;
    /** When true, all water will turn to Frosted Ice **/
    public final boolean frostedIce;

    public FreezeFunction(final Random randomIn, final boolean useFrost) {
      this.random = randomIn;
      this.frostedIce = useFrost;
    }

    @Override
    public BlockState map(final LivingEntity entity, final BlockPos pos, final BlockState input) {
      final BlockState cobbleState = Blocks.COBBLESTONE.defaultBlockState();
      final BlockState iceState = this.frostedIce ? Blocks.FROSTED_ICE.defaultBlockState() : Blocks.ICE.defaultBlockState();
      final Material material = input.getMaterial();
      if (material.isLiquid()) {
        final Block block = input.getBlock();

        if (block == Blocks.WATER) {
          final boolean isNotPacked = this.frostedIce || this.random.nextInt(100) < this.iceChance;
          return isNotPacked ? iceState : Blocks.PACKED_ICE.defaultBlockState();
        } else if (block == Blocks.LAVA) {
          final boolean isNotObsidian = this.random.nextInt(100) < this.cobbleChance;
          return isNotObsidian ? cobbleState : Blocks.OBSIDIAN.defaultBlockState();
        }
      }

      return input;
    }
  }
}
