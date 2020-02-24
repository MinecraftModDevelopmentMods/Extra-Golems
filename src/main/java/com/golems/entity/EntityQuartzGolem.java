package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityQuartzGolem extends GolemBase {

  public EntityQuartzGolem(final World world) {
    super(world);
    this.setLootTableLoc(GolemNames.QUARTZ_GOLEM);
    this.addHealItem(new ItemStack(Items.QUARTZ), 0.25D);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
  }

  @Override
  protected ResourceLocation applyTexture() {
    return makeTexture(ExtraGolems.MODID, GolemNames.QUARTZ_GOLEM);
  }

  @Override
  public SoundEvent getGolemSound() {
    return SoundEvents.BLOCK_GLASS_STEP;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.BLOCK_GLASS_BREAK;
  }
}
