package com.mcmoddev.golems.entity;

import java.util.Random;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public final class StrawGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Crop Boost";
  public static final String SPECIAL_FREQ = "Crop Boost Frequency";

  public StrawGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    if (this.getConfigBool(ALLOW_SPECIAL)) {
      this.goalSelector.addGoal(3, new BoostCropGoal(this, 4, this.getConfigInt(SPECIAL_FREQ)));
    }
  }

  public static class BoostCropGoal extends Goal {
    protected final GolemBase golem;
    protected final int range;
    protected final int frequency;

    public BoostCropGoal(final GolemBase golemIn, final int rangeIn, final int freq) {
      golem = golemIn;
      range = rangeIn;
      frequency = freq + golem.getCommandSenderWorld().getRandom().nextInt(Math.max(10, freq / 2));
    }

    @Override
    public boolean canUse() {
      return golem.getCommandSenderWorld().getRandom().nextInt(frequency) == 0;
    }

    @Override
    public void start() {
      tryBoostCrop();
    }

    /**
     * Checks random blocks in a radius until either a growable crop has been found
     * and boosted, or no crops were found in a limited number of attempts.
     *
     * @return always returns false...
     **/
    private boolean tryBoostCrop() {
      final Random rand = this.golem.getCommandSenderWorld().getRandom();
      final int maxAttempts = 25;
      final int variationY = 2;
      int attempts = 0;
      while (attempts++ <= maxAttempts) {
        // get random block in radius
        final int x1 = rand.nextInt(this.range * 2) - this.range;
        final int y1 = rand.nextInt(variationY * 2) - variationY;
        final int z1 = rand.nextInt(this.range * 2) - this.range;
        final BlockPos blockpos = this.golem.getBlockBelow().offset(x1, y1, z1);
        final BlockState state = golem.getCommandSenderWorld().getBlockState(blockpos);
        // if the block can be grown, grow it and return
        if (state.getBlock() instanceof BonemealableBlock) {
          BonemealableBlock crop = (BonemealableBlock) state.getBlock();
          if (golem.getCommandSenderWorld() instanceof ServerLevel
              && crop.isValidBonemealTarget(golem.getCommandSenderWorld(), blockpos, state, golem.getCommandSenderWorld().isClientSide)) {
            // grow the crop!
            crop.performBonemeal((ServerLevel) golem.getCommandSenderWorld(), rand, blockpos, state);
            // spawn particles
            if (golem.getCommandSenderWorld().isClientSide()) {
              BoneMealItem.addGrowthParticles(golem.getCommandSenderWorld(), blockpos, 0);
            }
            // cap the attempts here so we exit the while loop safely
            attempts = maxAttempts;
          }
        }
      }
      return false;
    }
  }
}
