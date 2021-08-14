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

public final class StainedTerracottaGolem extends GolemMultiTextured {
  
  public static final String[] TEXTURE_NAMES = { "black_terracotta", "orange_terracotta", "magenta_terracotta", "light_blue_terracotta", 
      "yellow_terracotta", "lime_terracotta", "pink_terracotta", "gray_terracotta", "light_gray_terracotta", "cyan_terracotta", "purple_terracotta", 
      "blue_terracotta", "brown_terracotta", "green_terracotta", "red_terracotta", "white_terracotta" };
  
  public static final String[] LOOT_TABLE_NAMES = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };

  public StainedTerracottaGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.TERRACOTTA, (byte) this.getTextureNum()));
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.TERRACOTTA;
  }
}
