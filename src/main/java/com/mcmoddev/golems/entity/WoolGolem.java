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

public final class WoolGolem extends GolemMultiTextured {
  
  public static final String[] TEXTURE_NAMES = { "black_wool", "orange_wool", "magenta_wool", "light_blue_wool", 
      "yellow_wool", "lime_wool", "pink_wool", "gray_wool", "light_gray_wool", "cyan_wool", "purple_wool", 
      "blue_wool", "brown_wool", "green_wool", "red_wool", "white_wool" };
  
  public static final String[] LOOT_TABLE_NAMES = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };

  public WoolGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
  }

  @Override
  public void setTextureNum(byte toSet) {
    // note: skips texture for 'white'
    toSet %= (byte) (LOOT_TABLE_NAMES.length - 1);
    super.setTextureNum(toSet);
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.WOOL, (byte) this.getTextureNum()));
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.WOOL;
  }
}
