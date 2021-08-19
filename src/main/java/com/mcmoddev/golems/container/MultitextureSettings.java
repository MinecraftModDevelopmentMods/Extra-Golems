package com.mcmoddev.golems.container;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

public class MultitextureSettings {
  
  public static final MultitextureSettings EMPTY = new MultitextureSettings(0, false, Maps.newHashMap());
  
  public static final Codec<MultitextureSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.fieldOf("texture_count").forGetter(MultitextureSettings::getTextureCount),
      Codec.BOOL.optionalFieldOf("cycle", false).forGetter(MultitextureSettings::canCycle),
      Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, i -> Integer.toString(i)), MultitextureSettings.TextureEntry.CODEC).fieldOf("textures").forGetter(MultitextureSettings::getTextureEntryMap)
    ).apply(instance, MultitextureSettings::new));
  
  private final int textureCount;
  private final boolean cycle;
  private final ImmutableMap<Integer, MultitextureSettings.TextureEntry> entryMap;
  private final ImmutableMap<ResourcePair, Integer> blockMap;
  
  private MultitextureSettings(int textureCount, boolean cycle, 
      Map<Integer, MultitextureSettings.TextureEntry> entryMap) {
    this.textureCount = textureCount;
    this.cycle = cycle;
    this.entryMap = ImmutableMap.copyOf(entryMap);
    // populate block-to-texture map and validate entry map
    final ImmutableMap.Builder<ResourcePair, Integer> builder = ImmutableMap.builder();
    this.entryMap.forEach((num, entry) -> {
      // validate num
      if(num < 0 || num >= this.textureCount) {
        throw new IllegalArgumentException("'textures' contains out of bounds texture ID '" + num + "' (max is " + (textureCount - 1));
      }
      // add blocks to texture map
      entry.getBlocks().forEach(resource -> builder.put(resource, num));
    });
    this.blockMap = builder.build();
  }
  
  /** @return the number of textures **/
  public int getTextureCount() { return textureCount; }

  /** @return true to cycle textures upon entity interaction **/
  public boolean canCycle() { return cycle; }
  
  /** @return the map of consolidated data about each texture **/
  public Map<Integer, MultitextureSettings.TextureEntry> getTextureEntryMap() { return entryMap; }

  /** @return a map of Block IDs or Tags and Texture IDs **/
  public Map<ResourcePair, Integer> getBlockMap() { return blockMap; }

  public int getLight(final GolemBase entity) {
    return entryMap.getOrDefault(entity.getTextureId(), MultitextureSettings.TextureEntry.EMPTY).getLight();
  }
  
  /**
   * @param entity the Golem
   * @return the Loot Table of the golem, taking into account texture ID
   */
  public ResourceLocation getLootTable(final GolemBase entity) {
    return entryMap.getOrDefault(entity.getTextureId(), MultitextureSettings.TextureEntry.EMPTY).getLootTable();
  }

  /**
   * Searches the block-to-textureID map for a match.
   * First checks against the block name, then checks
   * each of the block's tags.
   * @param block the block
   * @return the texture ID (defaults to 0)
   */
  public int getTextureFromBlock(final Block block) {
    ResourcePair key = new ResourcePair(block.getRegistryName(), false);
    // if the block is defined literally, use that to look up texture id
    if(getBlockMap().containsKey(key)) {
      return getBlockMap().get(key);
    }
    // otherwise, use each of the block's tags to look up texture id
    for(final ResourceLocation tag : block.getTags()) {
      key = new ResourcePair(tag, true);
      if(getBlockMap().containsKey(key)) {
        return getBlockMap().get(key);
      }
    }
    // default is 0
    return 0;
  }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("MultitextureSettings: ");
    b.append("texture_count[").append(textureCount).append("] ");
    b.append("cycle[").append(cycle).append("] ");
    b.append("textures[").append(entryMap).append("] ");
    return b.toString();
  }
  
  /**
   * This class stores information about a single texture
   * that will be used to populate the maps in MultitextureSettings
   */
  protected static class TextureEntry {
    
    public static final TextureEntry EMPTY = new TextureEntry(Lists.newArrayList(), LootTable.EMPTY.getLootTableId(), 0);
    
    public static final Codec<TextureEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.either(ResourcePair.CODEC, ResourcePair.CODEC.listOf())
        .xmap(either -> either.map(ImmutableList::of, Function.identity()), 
              list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list))
          .fieldOf("block").forGetter(TextureEntry::getBlocks),
        ResourceLocation.CODEC.fieldOf("loot_table").forGetter(TextureEntry::getLootTable),
        Codec.INT.optionalFieldOf("light", 0).forGetter(TextureEntry::getLight)
      ).apply(instance, TextureEntry::new));
    
    private final List<ResourcePair> blocks;
    private final ResourceLocation lootTable;
    private final int light;
    
    private TextureEntry(final List<ResourcePair> blocks, final ResourceLocation lootTable, final int light) {
      this.blocks = blocks;
      this.lootTable = lootTable;
      this.light = light;    
    }

    /** @return a List of blocks and block tags that apply to this entry **/
    public List<ResourcePair> getBlocks() { return blocks; }
    
    /** @return the loot table location for this entry **/
    public ResourceLocation getLootTable() { return lootTable; }
    
    /** @return the light value for this entry **/
    public int getLight() { return light; }
    
    @Override
    public String toString() {
      StringBuilder b = new StringBuilder("TextureEntry: ");
      b.append("block[").append(blocks).append("] ");
      b.append("loot_table[").append(lootTable).append("] ");
      b.append("light[").append(light).append("] ");
      return b.toString();
    }
  }
}
