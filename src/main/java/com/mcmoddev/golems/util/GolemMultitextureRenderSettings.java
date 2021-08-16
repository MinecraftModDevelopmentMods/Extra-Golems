package com.mcmoddev.golems.util;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javafx.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class GolemMultitextureRenderSettings {
  
  public static final Codec<GolemMultitextureRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.unboundedMap(Codec.INT, Codec.STRING).fieldOf("base_map").forGetter(GolemMultitextureRenderSettings::getBaseMapRaw)
    ).apply(instance, GolemMultitextureRenderSettings::new));
  
  private final Map<Integer, String> baseMapRaw;
  
  private final ImmutableMap<Integer, Pair<ResourceLocation, Boolean>> baseMap;
  
  private GolemMultitextureRenderSettings(Map<Integer, String> baseMapRaw) {
    this.baseMapRaw = baseMapRaw;
    // convert to resource locations
    Map<Integer, Pair<ResourceLocation, Boolean>> map = new HashMap<>();
    baseMapRaw.forEach((i, s) -> GolemContainer.parseIdOrTag(s, 
        id -> map.put(i, new Pair<ResourceLocation, Boolean>(id, true)), 
        id -> map.put(i, new Pair<ResourceLocation, Boolean>(id, false))));
    baseMap = ImmutableMap.copyOf(map);
  }

  /** @return a map of texture IDs and String representations of texture path **/
  public Map<Integer, String> getBaseMapRaw() { return baseMapRaw; }
  
  /** 
   * @return a map of texture IDs and ResourceLocations/Boolean pair.
   * The ResourceLocation is a block texture if the Boolean is true.
   * The ResourceLocation is a prefab if the Boolean is false.
   **/
  public Map<Integer, Pair<ResourceLocation, Boolean>> getBaseMap() { return baseMap; }
}
