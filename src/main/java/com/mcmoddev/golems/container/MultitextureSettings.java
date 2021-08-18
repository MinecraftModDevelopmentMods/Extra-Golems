package com.mcmoddev.golems.container;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MultitextureSettings {
  
  public static final MultitextureSettings EMPTY = new MultitextureSettings(0, false, Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap());
  
  public static final Codec<MultitextureSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.fieldOf("texture_count").forGetter(MultitextureSettings::getTextureCount),
      Codec.BOOL.optionalFieldOf("cycle", false).forGetter(MultitextureSettings::canCycle),
      Codec.unboundedMap(ResourcePair.CODEC, Codec.INT).fieldOf("block_texture_map").forGetter(MultitextureSettings::getBlockTextureMap),
      Codec.unboundedMap(Codec.INT, ResourceLocation.CODEC).fieldOf("loot_table_map").forGetter(MultitextureSettings::getLootTableMap),
      Codec.unboundedMap(Codec.INT, Codec.INT).fieldOf("texture_glow_map").forGetter(MultitextureSettings::getTextureGlowMap)
    ).apply(instance, MultitextureSettings::new));
  
  private final int textureCount;
  private final boolean cycle;
  private final ImmutableMap<ResourcePair, Integer> blockTextureMap;
  private final ImmutableMap<Integer, Integer> textureGlowMap;
  private final ImmutableMap<Integer, ResourceLocation> lootTableMap;
  
  private MultitextureSettings(int textureCount, boolean cycle, 
      Map<ResourcePair, Integer> blockTextureMap,
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

  /** @return a map of Block IDs or Tags and Texture IDs **/
  public Map<ResourcePair, Integer> getBlockTextureMap() { return blockTextureMap; }

  /** @return a map of Block IDs and light values **/
  public Map<Integer, Integer> getTextureGlowMap() { return textureGlowMap; }
  
  /** @return a map of Block IDs and loot table locations **/
  public Map<Integer, ResourceLocation> getLootTableMap() { return lootTableMap; }

  /**
   * Searches the block-to-textureID map for a match.
   * First checks against the block name, then checks
   * each of the block's tags.
   * @param block the block
   * @return the texture ID (defaults to 0)
   */
  public int getTextureFromBlock(final Block block) {
    ResourcePair key = new ResourcePair(block.getRegistryName(), true);
    // if the block is defined literally, use that to look up texture id
    if(blockTextureMap.containsKey(key)) {
      return blockTextureMap.get(key);
    }
    // otherwise, use each of the block's tags to look up texture id
    for(final ResourceLocation tag : block.getTags()) {
      key = new ResourcePair(tag, false);
      if(blockTextureMap.containsKey(key)) {
        return blockTextureMap.get(key);
      }
    }
    // default is 0
    return 0;
  }
}
