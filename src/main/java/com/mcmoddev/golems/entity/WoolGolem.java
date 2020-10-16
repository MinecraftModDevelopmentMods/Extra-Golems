package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class WoolGolem extends GolemMultiTextured {
  
  public static final String[] WOOL_COLORS = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };

  public WoolGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, WOOL_COLORS, WOOL_COLORS);
    for (int n = 0, len = WOOL_COLORS.length; n < len; n++) {
      // initialize textures
      this.textures[n] = new ResourceLocation(ExtraGolems.MODID, this.getGolemContainer().getName() + "/" + WOOL_COLORS[n] + ".png");
    }
  }

  @Override
  public void setTextureNum(byte toSet) {
    // note: skips texture for 'white'
    toSet %= (byte) (WOOL_COLORS.length - 1);
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
