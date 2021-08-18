package com.mcmoddev.golems.container.client;

import java.util.Optional;

import com.mcmoddev.golems.ExtraGolems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public class LayerRenderSettings {
  /** Default value to use when coloring the vines layer **/
  private static final int VINES_COLOR = 0x83a05a; // 8626266
  
  public static final LayerRenderSettings VINES = new LayerRenderSettings(new ResourceLocation(ExtraGolems.MODID, "layer/vines"), Optional.of(VINES_COLOR), Optional.empty(), false);
  public static final LayerRenderSettings EYES = new LayerRenderSettings(new ResourceLocation(ExtraGolems.MODID, "layer/eyes/eyes"), Optional.empty(), Optional.empty(), false);

  public static final Codec<LayerRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("texture").forGetter(LayerRenderSettings::getTexture),
      Codec.INT.optionalFieldOf("color").forGetter(LayerRenderSettings::getColor),
      Codec.BOOL.optionalFieldOf("light").forGetter(LayerRenderSettings::getLight),
      Codec.BOOL.optionalFieldOf("translucent", false).forGetter(LayerRenderSettings::isTranslucent)
    ).apply(instance, LayerRenderSettings::new));

  private final ResourceLocation texture;
  private final Optional<Integer> color;
  private final Optional<Boolean> light;
  private final boolean translucent;
  
  private LayerRenderSettings(ResourceLocation texture, Optional<Integer> color, 
      Optional<Boolean> light, boolean translucent) {
    super();
    this.texture = new ResourceLocation(texture.getNamespace(), "textures/entity/" +  texture.getPath() + ".png");
    this.color = color;
    this.light = light;
    this.translucent = translucent;
  }
  
  /** @return the ResourceLocation of the prefab texture **/
  public ResourceLocation getTexture() { return texture; }
  
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
