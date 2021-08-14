package com.mcmoddev.golems.entity;

import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.SpongeGolemSoakEvent;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public final class SpongeGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Absorb Water";
  public static final String INTERVAL = "Water Soaking Frequency";
  public static final String RANGE = "Water Soaking Range";

  public SpongeGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    if (this.getConfigBool(ALLOW_SPECIAL)) {
      this.goalSelector.addGoal(2, new SoakWaterGoal(this, this.getConfigInt(INTERVAL), this.getConfigInt(RANGE)));
    }
  }

  /**
   * Called frequently so the entity can update its state every tick as required.
   * For example, zombies and skeletons use this to react to sunlight and start to
   * burn.
   */
  @Override
  public void aiStep() {
    super.aiStep();
    if (Math.abs(this.getDeltaMovement().x()) < 0.03D && Math.abs(this.getDeltaMovement().z()) < 0.03D && level.isClientSide) {
      final SimpleParticleType particle = this.isOnFire() ? ParticleTypes.SMOKE : ParticleTypes.SPLASH;
      final double x = this.random.nextDouble() - 0.5D * (double) this.getBbWidth() * 0.6D;
      final double y = this.random.nextDouble() * (this.getBbHeight() - 0.75D);
      final double z = this.random.nextDouble() - 0.5D * (double) this.getBbWidth();
      final Vec3 pos = this.position();
      this.level.addParticle(particle, pos.x + x, pos.y + y, pos.z + z, (this.random.nextDouble() - 0.5D) * 0.5D, this.random.nextDouble() - 0.5D,
          (this.random.nextDouble() - 0.5D) * 0.5D);
    }
  }

  public static class SoakWaterGoal extends Goal {

    protected final GolemBase golem;
    protected final int interval;
    protected final int range;

    public SoakWaterGoal(final GolemBase golemIn, final int intervalIn, final int rangeIn) {
      golem = golemIn;
      interval = Math.min(1, intervalIn);
      range = rangeIn;
    }

    @Override
    public boolean canUse() {
      return golem.tickCount % interval == 0;
    }

    @Override
    public boolean canContinueToUse() {
      return false;
    }

    @Override
    public void start() {
      final BlockPos center = this.golem.getBlockBelow();
      final SpongeGolemSoakEvent event = new SpongeGolemSoakEvent(golem, center, range);
      if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY) {
        this.replaceWater(event.getPositionList(), event.getAbsorbFunction(), event.updateFlag);
      }
    }

    /**
     * Usually called after creating and firing a {@link SpongeGolemSoakEvent}.
     * Iterates through the list of positions and replaces each one with the passed
     * BlockState.
     *
     * @return whether all setBlockState calls were successful.
     **/
    public boolean replaceWater(final List<BlockPos> positions, final Function<BlockState, BlockState> replaceWater, final int updateFlag) {
      boolean flag = true;
      for (final BlockPos p : positions) {
        flag &= golem.getCommandSenderWorld().setBlock(p, replaceWater.apply(golem.getCommandSenderWorld().getBlockState(p)), updateFlag);
      }
      return flag;
    }
  }

}
