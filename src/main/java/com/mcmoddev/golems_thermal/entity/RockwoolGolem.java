package com.mcmoddev.golems_thermal.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.WoolGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.util.GolemTextureBytes;
import com.mcmoddev.golems_thermal.ThermalGolemsEntities;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class RockwoolGolem extends GolemMultiTextured {
  
  private static final Map<Block, Byte> textureBytes = new HashMap<>();

  public RockwoolGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, "minecraft", WoolGolem.TEXTURE_NAMES, ThermalGolemsEntities.MODID, WoolGolem.LOOT_TABLE_NAMES);
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
    for(final DyeColor d : DyeColor.values()) {
      final Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thermal:" + d.getString() + "_rockwool"));
      textureBytes.put(b, (byte) d.getId());
    }
  }
}
