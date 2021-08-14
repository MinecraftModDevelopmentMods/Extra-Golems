package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public final class StainedGlassGolem extends GolemMultiTextured {
  
  public static final String[] TEXTURE_NAMES = { "black_stained_glass", "orange_stained_glass", "magenta_stained_glass", "light_blue_stained_glass", 
      "yellow_stained_glass", "lime_stained_glass", "pink_stained_glass", "gray_stained_glass", "light_gray_stained_glass", "cyan_stained_glass", "purple_stained_glass", 
      "blue_stained_glass", "brown_stained_glass", "green_stained_glass", "red_stained_glass", "white_stained_glass" };
  
  public static final String[] LOOT_TABLE_NAMES = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };

  public StainedGlassGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.GLASS, (byte) this.getTextureNum()));
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.GLASS;
  }
}
