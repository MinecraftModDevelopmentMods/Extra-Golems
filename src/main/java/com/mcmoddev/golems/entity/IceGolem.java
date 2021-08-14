package com.mcmoddev.golems.entity;

import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.IceGolemFreezeEvent;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public final class IceGolem extends GolemBase {

  public static final String AOE = "Area of Effect";
  public static final String FROST = "Use Frosted Ice";

  public IceGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(2, new FreezeBlocksGoal(this, this.getConfigInt(AOE), this.getConfigBool(FROST)));
  }

  /**
   * Called frequently so the entity can update its state every tick as required.
   * For example, zombies and skeletons use this to react to sunlight and start to
   * burn.
   */
  @Override
  public void aiStep() {
    super.aiStep();
    final BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement().above(2);
    if (this.level.getBiome(pos).getTemperature(pos) > 1.0F) {
      this.hurt(DamageSource.ON_FIRE, 1.0F);
    }
  }

  @Override
  public boolean doHurtTarget(final Entity entity) {
    if (super.doHurtTarget(entity)) {
      if (entity.isOnFire()) {
        this.hurt(DamageSource.GENERIC, 0.5F);
      }
      return true;
    }
    return false;
  }

  public static class FreezeBlocksGoal extends Goal {

    protected final GolemBase golem;
    protected final int range;
    protected final boolean frosted;

    public FreezeBlocksGoal(final GolemBase golemIn, final int rangeIn, final boolean useFrost) {
      golem = golemIn;
      range = rangeIn;
      frosted = useFrost;
    }

    @Override
    public boolean canUse() {
      return golem.tickCount % 2 == 0;
    }

    @Override
    public boolean canContinueToUse() {
      return false;
    }

    @Override
    public void start() {
      final BlockPos below = this.golem.getBlockBelow();

      if (range > 0) {
        final IceGolemFreezeEvent event = new IceGolemFreezeEvent(golem, below, range, frosted);
        if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY) {
          this.freezeBlocks(event.getAffectedPositions(), event.getFunction(), event.updateFlag);
        }
      }
    }

    /**
     * Usually called after creating and firing a {@link IceGolemFreezeEvent}.
     * Iterates through the list of positions and calls
     * {@code apply(BlockState input)} on the passed
     * {@code Function<BlockState, BlockState>} .
     *
     * @return whether all setBlockState calls were successful.
     **/
    public boolean freezeBlocks(final List<BlockPos> positions, final Function<BlockState, BlockState> function, final int updateFlag) {
      boolean flag = true;
      for (BlockPos pos : positions) {
        flag &= golem.getCommandSenderWorld().setBlock(pos, function.apply(golem.getCommandSenderWorld().getBlockState(pos)), updateFlag);
      }
      return flag;
    }

  }
}
