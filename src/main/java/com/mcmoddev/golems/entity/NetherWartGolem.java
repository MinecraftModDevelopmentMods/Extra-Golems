package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public final class NetherWartGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
  public static final String FREQUENCY = "Netherwart Frequency";
  public static final String ALLOW_HEALING = "Allow Special: Random Healing";
  private boolean allowHealing;

  public NetherWartGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    this.allowHealing = this.getConfigBool(ALLOW_HEALING);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    final BlockState[] flowers = { Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 0),
        Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 1), Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 2) };
    final Block[] soils = { Blocks.SOUL_SAND };
    final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
    final int freq = this.getConfigInt(FREQUENCY);
    this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, flowers, soils, allow));
    if (allowHealing) {
      this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, MobEffects.REGENERATION, 50, 60, 1, 1,
          g -> !g.getCommandSenderWorld().isDay() && g.getCommandSenderWorld().getRandom().nextInt(450) == 0));
    }
  }
}
