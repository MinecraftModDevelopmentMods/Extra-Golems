package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class WoolGolem extends GolemMultiTextured {

  public static final String[] coloredWoolTypes = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };

  public WoolGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, coloredWoolTypes);
  }

  @Override
  public void setTextureNum(byte toSet) {
    // note: skips texture for 'white'
    toSet %= (byte) (coloredWoolTypes.length - 1);
    super.setTextureNum(toSet);
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.WOOL, (byte) this.getTextureNum()));
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.WOOL;
  }
}
