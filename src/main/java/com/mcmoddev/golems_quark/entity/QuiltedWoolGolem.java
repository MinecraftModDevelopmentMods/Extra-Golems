package com.mcmoddev.golems_quark.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.util.GolemTextureBytes;
import com.mcmoddev.golems_quark.QuarkGolemsEntities;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.building.module.QuiltedWoolModule;

public class QuiltedWoolGolem extends GolemMultiTextured {
  
  private static final Map<Block, Byte> textureBytes = new HashMap<>();
  
  public static final String[] TEXTURE_NAMES = { "black_quilted_wool", "orange_quilted_wool", "magenta_quilted_wool", "light_blue_quilted_wool", 
      "yellow_quilted_wool", "lime_quilted_wool", "pink_quilted_wool", "gray_quilted_wool", "light_gray_quilted_wool", "cyan_quilted_wool", "purple_quilted_wool", 
      "blue_quilted_wool", "brown_quilted_wool", "green_quilted_wool", "red_quilted_wool", "white_quilted_wool" };
  
  public static final String[] LOOT_TABLE_NAMES = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };


  public QuiltedWoolGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, QuarkGolemsEntities.QUARK, TEXTURE_NAMES, QuarkGolemsEntities.MODID, LOOT_TABLE_NAMES);
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
        final Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark:" + d.getString() + "_quilted_wool"));
        textureBytes.put(b, (byte) d.getId());
      }
    }
  }
}
