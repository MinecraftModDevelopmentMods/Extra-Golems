package com.mcmoddev.golems.container.behavior.parameter;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundNBT;

public class ChangeTexturesBehaviorParameter extends BehaviorParameter {
  
  private final double chance;
  private final Map<String, Integer> textureMap;
  
  public ChangeTexturesBehaviorParameter(final CompoundNBT tag) {
    super();
    this.chance = tag.getDouble("chance");
    this.textureMap = ImmutableMap.copyOf(readStringIntMap(tag.getCompound("textures")));
  }
  
  /** @return the chance for this conditional texture to apply **/
  public double getChance() { return chance; }
  
  /** @return the String to Texture ID map **/
  public Map<String, Integer> getTextures() { return textureMap; }
  
  
  /**
   * Accepts an object
   * @param input the string key
   * @param fallback the value to return if the string key is absent
   * @return the texture ID contained in the map for the given input
   */
  public int getTextureId(final String input, final int fallback) {
    return textureMap.getOrDefault(input.toString(), fallback);
  }
  
  @Override
  public String toString() {
    return "ConditionalTextures: chance[" + chance + "] textureMap[" + textureMap.toString() + "]";
  }
}
