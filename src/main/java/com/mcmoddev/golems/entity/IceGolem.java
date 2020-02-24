package com.mcmoddev.golems.entity;

import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.IceGolemFreezeEvent;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public final class IceGolem extends GolemBase {

  public static final String AOE = "Area of Effect";
  public static final String FROST = "Use Frosted Ice";

  public IceGolem(final EntityType<? extends GolemBase> entityType, final World world) {
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
  public void livingTick() {
    super.livingTick();
    final BlockPos pos = this.getPosition();
    if (this.world.getBiome(pos).getTemperature(pos) > 1.0F) {
      this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
    }
  }

  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      if (entity.isBurning()) {
        this.attackEntityFrom(DamageSource.GENERIC, 0.5F);
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
    public boolean shouldExecute() {
      return golem.ticksExisted % 2 == 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }

    @Override
    public void startExecuting() {
      final int x = MathHelper.floor(golem.posX);
      final int y = MathHelper.floor(golem.posY - 0.20000000298023224D);
      final int z = MathHelper.floor(golem.posZ);
      final BlockPos below = new BlockPos(x, y, z);

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
        flag &= golem.getEntityWorld().setBlockState(pos, function.apply(golem.getEntityWorld().getBlockState(pos)), updateFlag);
      }
      return flag;
    }

  }
}
