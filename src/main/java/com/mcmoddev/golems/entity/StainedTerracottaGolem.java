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

public final class StainedTerracottaGolem extends GolemMultiColorized {

  public static final String PREFIX = "stained_clay";

  private static final ResourceLocation TEXTURE_BASE = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDTERRACOTTA_GOLEM);
  private static final ResourceLocation TEXTURE_OVERLAY = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDTERRACOTTA_GOLEM + "_grayscale");

  public StainedTerracottaGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, TEXTURE_BASE, TEXTURE_OVERLAY, DYE_COLORS);
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.TERRACOTTA, (byte) this.getTextureNum()));
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.TERRACOTTA;
  }
}
