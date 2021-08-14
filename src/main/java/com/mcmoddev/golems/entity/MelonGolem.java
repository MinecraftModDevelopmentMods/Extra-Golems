package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public final class MelonGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Plant Flowers";
  public static final String FREQUENCY = "Flower Frequency";
  public static final String ALLOW_HEALING = "Allow Special: Random Healing";

  public MelonGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  /* Create an EntityAIPlaceRandomBlocks */
  @Override
  protected void registerGoals() {
    super.registerGoals();
    final Block[] soils = { Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL };
    // init list and AI for planting flowers
    final BlockState[] flowers = { Blocks.POPPY.defaultBlockState(), Blocks.DANDELION.defaultBlockState(), Blocks.BLUE_ORCHID.defaultBlockState(),
        Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(),
        Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(),
        Blocks.OXEYE_DAISY.defaultBlockState() };
    // get other parameters for the AI
    final int freq = this.getConfigInt(FREQUENCY);
    final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
    this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, flowers, soils, allowed));
    // healing goal
    if (this.getConfigBool(ALLOW_HEALING)) {
      this.goalSelector.addGoal(4,
          new PassiveEffectsGoal(this, MobEffects.REGENERATION, 50, 60, 1, 1, g -> g.getCommandSenderWorld().getRandom().nextInt(450) == 0));
    }
  }
}
