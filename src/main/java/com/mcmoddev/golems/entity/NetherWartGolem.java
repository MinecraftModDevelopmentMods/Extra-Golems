package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.Effects;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public final class NetherWartGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
  public static final String FREQUENCY = "Netherwart Frequency";
  public static final String ALLOW_HEALING = "Allow Special: Random Healing";
  private boolean allowHealing;

  public NetherWartGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    this.allowHealing = this.getConfigBool(ALLOW_HEALING);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    final BlockState[] flowers = { Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 0),
        Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 1), Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 2) };
    final Block[] soils = { Blocks.SOUL_SAND };
    final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
    final int freq = this.getConfigInt(FREQUENCY);
    this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, flowers, soils, allow));
    if (allowHealing) {
      this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, Effects.REGENERATION, 50, 60, 1, 1,
          g -> (g.getEntityWorld().func_234922_V_() == DimensionType.THE_NETHER 
            || !g.getEntityWorld().isDaytime()) && g.getEntityWorld().getRandom().nextInt(450) == 0));
    }
  }
}
