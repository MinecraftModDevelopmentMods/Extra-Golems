package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.GolemItems;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SeaLanternGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

  private static final float BRIGHTNESS = 1.0F;
  private static final int BRIGHTNESS_INT = (int) (BRIGHTNESS * 15.0F);

  public SeaLanternGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
    final int freq = BlockUtilityGlow.UPDATE_TICKS;
    this.goalSelector.addGoal(8,
        new PlaceUtilityBlockGoal(this, GolemItems.UTILITY_LIGHT.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, BRIGHTNESS_INT), freq, allow));
  }

  @Override
  protected float getWaterSlowDown() {
    return 0.95F;
  }

  @Override
  public float getBrightness() {
    return SeaLanternGolem.BRIGHTNESS;
  }

  @Override
  public boolean isProvidingLight() {
    return true;
  }

  @Override
  public boolean shouldMoveToWater(final Vec3d target) {
    // allowed to leave water at night to protect village, etc.
    return this.world.isDaytime();
  }
}
