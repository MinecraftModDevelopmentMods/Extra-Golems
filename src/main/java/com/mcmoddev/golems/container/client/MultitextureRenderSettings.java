package com.mcmoddev.golems.container.client;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MultitextureRenderSettings {
  
  public static final MultitextureRenderSettings EMPTY = new MultitextureRenderSettings(Maps.newHashMap());
  
  public static final Codec<MultitextureRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, i -> Integer.toString(i)), ResourcePair.CODEC).fieldOf("base_map").forGetter(MultitextureRenderSettings::getBaseMap)
    ).apply(instance, MultitextureRenderSettings::new));
  
  private final ImmutableMap<Integer, ResourcePair> baseMap;
  
  private MultitextureRenderSettings(Map<Integer, ResourcePair> baseMapRaw) {
    ImmutableMap.Builder<Integer, ResourcePair> builder = ImmutableMap.builder();
    baseMapRaw.forEach((num, pair) -> {
      builder.put(num, GolemRenderSettings.buildPreferredTexture(ImmutableList.of(pair)));
    });
    this.baseMap = builder.build(); 
  }
  
  /** 
   * @return a map of texture IDs and ResourceLocation/Boolean pairs.
   * The ResourceLocation is a block texture if the Boolean is true.
   * The ResourceLocation is a prefab if the Boolean is false.
   **/
  public Map<Integer, ResourcePair> getBaseMap() { return baseMap; }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("MultitextureRenderSettings: ");
    b.append("base_map[").append(baseMap).append("] ");
    return b.toString();
  }
}
