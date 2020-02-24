package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityHardenedClayGolem extends GolemBase {

  public EntityHardenedClayGolem(final World world) {
    super(world);
    this.setLootTableLoc(GolemNames.TERRACOTTA_GOLEM);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.18D);
  }

  protected ResourceLocation applyTexture() {
    return makeTexture(ExtraGolems.MODID, GolemNames.TERRACOTTA_GOLEM);
  }

  @Override
  public SoundEvent getGolemSound() {
    return SoundEvents.BLOCK_STONE_STEP;
  }
}
