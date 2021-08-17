package com.mcmoddev.golems.util;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public class MultitextureSettings {
  
  public static final MultitextureSettings EMPTY = new MultitextureSettings(0, false, Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap());
  
  public static final Codec<MultitextureSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.fieldOf("texture_count").forGetter(MultitextureSettings::getTextureCount),
      Codec.BOOL.optionalFieldOf("cycle", false).forGetter(MultitextureSettings::canCycle),
      Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("block_texture_map").forGetter(MultitextureSettings::getBlockTextureMap),
      Codec.unboundedMap(Codec.INT, ResourceLocation.CODEC).fieldOf("loot_table_map").forGetter(MultitextureSettings::getLootTableMap),
      Codec.unboundedMap(Codec.INT, Codec.INT).fieldOf("texture_glow_map").forGetter(MultitextureSettings::getTextureGlowMap)
    ).apply(instance, MultitextureSettings::new));
  
  private final int textureCount;
  private final boolean cycle;
  private final ImmutableMap<ResourceLocation, Integer> blockTextureMap;
  private final ImmutableMap<Integer, Integer> textureGlowMap;
  private final ImmutableMap<Integer, ResourceLocation> lootTableMap;
  
  private MultitextureSettings(int textureCount, boolean cycle, 
      Map<ResourceLocation, Integer> blockTextureMap,
      Map<Integer, ResourceLocation> lootTableMap,
      Map<Integer, Integer> textureGlowMap) {
    super();
    this.textureCount = textureCount;
    this.cycle = cycle;
    this.blockTextureMap = ImmutableMap.copyOf(blockTextureMap);
    this.lootTableMap = ImmutableMap.copyOf(lootTableMap);
    this.textureGlowMap = ImmutableMap.copyOf(textureGlowMap);
    
    // validate blockTextureMap
    blockTextureMap.forEach((block, num) -> {
      if(num >= textureCount) {
        throw new IllegalArgumentException("block_texture_map contains out of bounds texture ID (max is " + (textureCount - 1));
      }
    });
    // validate lootTableMap
    lootTableMap.forEach((num, loot) -> {
      if(num >= textureCount) {
        throw new IllegalArgumentException("loot_table_map contains out of bounds texture ID (max is " + (textureCount - 1));
      }
    });
    // validate textureGlowMap
    textureGlowMap.forEach((num, light) -> {
      if(num >= textureCount) {
        throw new IllegalArgumentException("texture_glow_map contains out of bounds texture ID (max is " + (textureCount - 1));
      }
    });
  }
  
  /** @return the number of textures **/
  public int getTextureCount() { return textureCount; }

  /** @return true to cycle textures upon entity interaction **/
  public boolean canCycle() { return cycle; }

  /** @return a map of Block IDs and Texture IDs **/
  public Map<ResourceLocation, Integer> getBlockTextureMap() { return blockTextureMap; }

  /** @return a map of Block IDs and light values **/
  public Map<Integer, Integer> getTextureGlowMap() { return textureGlowMap; }
  
  /** @return a map of Block IDs and loot table locations **/
  public Map<Integer, ResourceLocation> getLootTableMap() { return lootTableMap; }

  
}
