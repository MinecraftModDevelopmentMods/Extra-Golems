package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public final class ConcreteGolem extends GolemMultiTextured {

  public static final String ALLOW_RESIST = "Allow Special: Resistance";
  
  public static final String[] TEXTURE_NAMES = { "black_concrete", "orange_concrete", "magenta_concrete", "light_blue_concrete", 
      "yellow_concrete", "lime_concrete", "pink_concrete", "gray_concrete", "light_gray_concrete", "cyan_concrete", "purple_concrete", 
      "blue_concrete", "brown_concrete", "green_concrete", "red_concrete", "white_concrete" };
  
  public static final String[] LOOT_TABLE_NAMES = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };

  private boolean resist;
  
  public ConcreteGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
    this.setPathfindingMalus(BlockPathTypes.WATER, -0.8F);
    resist = getConfigBool(ALLOW_RESIST);
  }

  @Override
  protected void actuallyHurt(DamageSource source, float amount) {
    if (resist && !source.isBypassMagic()) {
      amount *= 0.6F;
      if (source.isFire()) {
        // additional fire resistance
        amount *= 0.85F;
      }
    }
    super.actuallyHurt(source, amount);
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.CONCRETE;
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.CONCRETE, (byte) this.getTextureNum()));
  }
}
