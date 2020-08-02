package com.mcmoddev.golems_quark.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemTextureBytes;
import com.mcmoddev.golems_quark.QuarkGolemsEntities;
import com.mcmoddev.golems_quark.util.QuarkGolemNames;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.module.QuiltedWoolModule;

public class QuiltedWoolGolem extends GolemMultiColorized {
  
  private static final Map<Block, Byte> textureBytes = new HashMap<>();

  private static final ResourceLocation TEXTURE_BASE = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM);
  private static final ResourceLocation TEXTURE_OVERLAY = GolemBase.makeTexture(QuarkGolemsEntities.MODID, QuarkGolemNames.QUILTEDWOOL_GOLEM + "_white");

  public QuiltedWoolGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, QuarkGolemsEntities.MODID, TEXTURE_BASE, TEXTURE_OVERLAY, DYE_COLORS);
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(textureBytes, (byte) this.getTextureNum()));
  }
  
  @Override
  public Map<Block, Byte> getTextureBytes() {
    // we have to do this late because not all blocks are loaded initially
    if(textureBytes.isEmpty()) {
      fillTextureBytes();
    }
    return textureBytes;
  }
  
  private static void fillTextureBytes() {
    // fills a map with Block-Byte references to correctly build the golem
    if(ModuleLoader.INSTANCE.isModuleEnabled(QuiltedWoolModule.class)) {
      for(final DyeColor d : DyeColor.values()) {
        final Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark:" + d.getTranslationKey() + "_quilted_wool"));
        textureBytes.put(b, (byte) d.getId());
      }
    }
  }
}
