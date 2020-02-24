package com.mcmoddev.golems.entity;

import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.SpongeGolemSoakEvent;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public final class SpongeGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Absorb Water";
  public static final String INTERVAL = "Water Soaking Frequency";
  public static final String RANGE = "Water Soaking Range";

  public SpongeGolem(final EntityType<? extends GolemBase> entityType, final World world) {
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
  public void livingTick() {
    super.livingTick();
    if (Math.abs(this.getMotion().getX()) < 0.03D && Math.abs(this.getMotion().getZ()) < 0.03D && world.isRemote) {
      final BasicParticleType particle = this.isBurning() ? ParticleTypes.SMOKE : ParticleTypes.SPLASH;
      final double x = this.rand.nextDouble() - 0.5D * (double) this.getWidth() * 0.6D;
      final double y = this.rand.nextDouble() * (this.getHeight() - 0.75D);
      final double z = this.rand.nextDouble() - 0.5D * (double) this.getWidth();
      this.world.addParticle(particle, this.posX + x, this.posY + y, this.posZ + z, (this.rand.nextDouble() - 0.5D) * 0.5D,
          this.rand.nextDouble() - 0.5D, (this.rand.nextDouble() - 0.5D) * 0.5D);
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
    public boolean shouldExecute() {
      return golem.ticksExisted % interval == 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }

    @Override
    public void startExecuting() {
      final int x = MathHelper.floor(golem.posX);
      final int y = MathHelper.floor(golem.posY - 0.20000000298023224D) + 2;
      final int z = MathHelper.floor(golem.posZ);
      final BlockPos center = new BlockPos(x, y, z);
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
        flag &= golem.getEntityWorld().setBlockState(p, replaceWater.apply(golem.getEntityWorld().getBlockState(p)), updateFlag);
      }
      return flag;
    }
  }

}
