package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public final class CrimsonStemGolem extends GolemBase {
  
  public static final String ALLOW_SPECIAL = "Allow Special: Plant Fungus";
  public static final String FREQUENCY = "Fungus Frequency";

  public CrimsonStemGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
    int freq = allowed ? this.getConfigInt(FREQUENCY) : -100;
    freq += this.rand.nextInt(Math.max(10, freq / 2));
    final BlockState[] mushrooms = { Blocks.CRIMSON_FUNGUS.getDefaultState() };
    final Block[] soils = { Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL };
    this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, mushrooms, soils, allowed));
  }
}
