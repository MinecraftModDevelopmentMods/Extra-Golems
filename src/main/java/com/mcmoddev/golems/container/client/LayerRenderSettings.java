package com.mcmoddev.golems.container.client;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public class LayerRenderSettings {

  public static final Codec<LayerRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourcePair.CODEC.fieldOf("texture").forGetter(LayerRenderSettings::getTexture),
      ResourceLocation.CODEC.optionalFieldOf("template", GolemRenderSettings.BASE_TEMPLATE).forGetter(LayerRenderSettings::getTemplate),
      Codec.INT.optionalFieldOf("color").forGetter(LayerRenderSettings::getColor),
      Codec.BOOL.optionalFieldOf("light").forGetter(LayerRenderSettings::getLight),
      Codec.BOOL.optionalFieldOf("translucent", false).forGetter(LayerRenderSettings::isTranslucent)
    ).apply(instance, LayerRenderSettings::new));

  private final ResourcePair texture;
  private final ResourceLocation template;
  private final Optional<Integer> color;
  private final Optional<Boolean> light;
  private final boolean translucent;
  
  private LayerRenderSettings(ResourcePair texture, ResourceLocation template, Optional<Integer> color, 
      Optional<Boolean> light, boolean translucent) {
    super();
    this.texture = GolemRenderSettings.buildPreferredTexture(ImmutableList.of(texture));
    this.template = new ResourceLocation(template.getNamespace(), "textures/entity/" + template.getPath() + ".png");
    this.color = color;
    this.light = light;
    this.translucent = translucent;
  }
  
  /** @return the ResourceLocation of the prefab texture **/
  public ResourcePair getTexture() { return texture; }
  
  /** @return the ResourceLocation of the template **/
  public ResourceLocation getTemplate() { return template; }
  
  /**
   * @return an Optional containing a color for the layer.
   * If empty, use the Base color
   */
  public Optional<Integer> getColor() { return color; }
  
  /**
   * @return an Optional containing the light for the layer.
   * If empty, use the Base light
   */
  public Optional<Boolean> getLight() { return light; }
  
  /** @return true if the layer is translucent **/
  public boolean isTranslucent() { return translucent; }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("LayerRenderSettings: ");
    b.append("texture[").append(texture).append("] ");
    b.append("color[").append(color).append("] ");
    b.append("light[").append(light).append("] ");
    b.append("translucent[").append(translucent).append("] ");
    return b.toString();
  }
}
