package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class StainedGlassGolem extends GolemMultiColorized {

  public static final String PREFIX = "stained_glass";

  private static final ResourceLocation TEXTURE_BASE = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM);
  private static final ResourceLocation TEXTURE_OVERLAY = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM + "_grayscale");

  public StainedGlassGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, TEXTURE_BASE, TEXTURE_OVERLAY, DYE_COLORS);
  }

  @Override
  public boolean hasTransparency() {
    return true;
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.GLASS, (byte) this.getTextureNum()));
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.GLASS;
  }
}
