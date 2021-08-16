package com.mcmoddev.golems.util;

import java.util.Optional;

import com.mcmoddev.golems.ExtraGolems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public class GolemRenderLayerSettings {
  /** Default value to use when coloring the vines layer **/
  private static final int VINES_COLOR = 0x83a05a; // 8626266
  
  public static final GolemRenderLayerSettings VINES = new GolemRenderLayerSettings(new ResourceLocation(ExtraGolems.MODID, "layer/vines.png"), Optional.of(VINES_COLOR), Optional.empty());
  public static final GolemRenderLayerSettings EYES = new GolemRenderLayerSettings(new ResourceLocation(ExtraGolems.MODID, "layer/eyes/eyes.png"), Optional.empty(), Optional.empty());

  public static final Codec<GolemRenderLayerSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("texture").forGetter(GolemRenderLayerSettings::getTexture),
      Codec.INT.optionalFieldOf("color").forGetter(GolemRenderLayerSettings::getColor),
      Codec.BOOL.optionalFieldOf("light").forGetter(GolemRenderLayerSettings::getLight)
    ).apply(instance, GolemRenderLayerSettings::new));

  private final ResourceLocation texture;
  private final Optional<Integer> color;
  private final Optional<Boolean> light;
  
  private GolemRenderLayerSettings(ResourceLocation texture, Optional<Integer> color, Optional<Boolean> light) {
    super();
    this.texture = texture;
    this.color = color;
    this.light = light;
  }
  
  /** @return the ResourceLocation of the prefab texture **/
  public ResourceLocation getTexture() {
    return texture;
  }
  
  /**
   * @return an Optional containing a color for the layer.
   * If empty, use the Golem color
   */
  public Optional<Integer> getColor() {
    return color;
  }
  
  /**
   * @return an Optional containing the light for the layer.
   * If empty, use the Golem light
   */
  public Optional<Boolean> getLight() {
    return light;
  }
  
  
}
