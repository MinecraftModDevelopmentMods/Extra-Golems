package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class WarpedStemGolem extends GolemBase {
  
  public static final String ALLOW_SPECIAL = "Allow Special: Plant Fungus";
  public static final String FREQUENCY = "Fungus Frequency";

  public WarpedStemGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
    int freq = allowed ? this.getConfigInt(FREQUENCY) : -100;
    freq += this.random.nextInt(Math.max(10, freq / 2));
    final BlockState[] mushrooms = { Blocks.WARPED_FUNGUS.defaultBlockState() };
    final Block[] soils = { Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL };
    this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, mushrooms, soils, allowed));
  }
}
