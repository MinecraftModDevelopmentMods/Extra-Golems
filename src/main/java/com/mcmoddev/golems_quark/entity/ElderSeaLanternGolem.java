package com.mcmoddev.golems_quark.entity;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.GolemItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public final class ElderSeaLanternGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

  /**
   * Float value between 0.0F and 1.0F that determines light level
   **/
  private final float brightness;

  public ElderSeaLanternGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    this.brightness = 1.0F;
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    int lightInt = 15;
    final BlockState state = GolemItems.UTILITY_LIGHT.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, lightInt);
    this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, BlockUtilityGlow.UPDATE_TICKS, 
        this.getConfigBool(ALLOW_SPECIAL), true, null));
  }

  @Override
  public boolean isProvidingLight() {
    return true;
  }

  @Override
  public float getBrightness() {
    return this.brightness;
  }
}
