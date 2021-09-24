package com.mcmoddev.golems.container.render;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;

import java.util.Optional;

public class LayerRenderSettings {

  public static final Codec<LayerRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourcePair.CODEC.fieldOf("texture").forGetter(LayerRenderSettings::getTextureRaw),
      ResourceLocation.CODEC.optionalFieldOf("template", GolemRenderSettings.BASE_TEMPLATE).forGetter(LayerRenderSettings::getTemplateRaw),
      Codec.INT.optionalFieldOf("color").forGetter(LayerRenderSettings::getColor),
      Codec.BOOL.optionalFieldOf("light").forGetter(LayerRenderSettings::getLight),
      Codec.BOOL.optionalFieldOf("translucent", false).forGetter(LayerRenderSettings::isTranslucent)
    ).apply(instance, LayerRenderSettings::new));

  private final ResourcePair textureRaw;
  private ResourcePair texture;
  private final ResourceLocation templateRaw;
  private ResourceLocation template;
  private final Optional<Integer> color;
  private final Optional<Boolean> light;
  private final boolean translucent;
  
  private LayerRenderSettings(ResourcePair textureRaw, ResourceLocation templateRaw, Optional<Integer> color,
      Optional<Boolean> light, boolean translucent) {
    super();
	this.textureRaw = textureRaw;
	this.templateRaw = templateRaw;
    this.color = color;
    this.light = light;
    this.translucent = translucent;
  }

  public boolean load() {
	return DistExecutor.runForDist(() -> () -> {
	  this.texture = GolemRenderSettings.buildPreferredTexture(ImmutableList.of(textureRaw));
	  this.template = new ResourceLocation(templateRaw.getNamespace(), "textures/entity/" + templateRaw.getPath() + ".png");
	  return true;
	}, () -> () -> false);
  }

  public ResourcePair getTextureRaw() { return textureRaw; }
  
  /** @return the ResourceLocation of the prefab texture **/
  public ResourcePair getTexture() { return texture; }

  public ResourceLocation getTemplateRaw() { return templateRaw; }
  
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
