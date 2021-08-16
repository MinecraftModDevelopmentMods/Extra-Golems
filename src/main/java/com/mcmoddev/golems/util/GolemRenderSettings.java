package com.mcmoddev.golems.util;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javafx.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class GolemRenderSettings {
  
  public static final ResourceLocation FALLBACK_BLOCK = new ResourceLocation("minecraft", "textures/block/clay.png");
  
  public static final Codec<GolemRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("material").forGetter(GolemRenderSettings::getMaterial),
      Codec.STRING.optionalFieldOf("base", FALLBACK_BLOCK.toString()).forGetter(GolemRenderSettings::getBaseRaw),
      ResourceLocation.CODEC.optionalFieldOf("base_template").forGetter(GolemRenderSettings::getBaseTemplate),
      Codec.INT.optionalFieldOf("base_color", 0).forGetter(GolemRenderSettings::getBaseColor),
      Codec.BOOL.optionalFieldOf("use_biome_color", false).forGetter(GolemRenderSettings::useBiomeColor),
      Codec.INT.optionalFieldOf("base_light").forGetter(GolemRenderSettings::getBaseLight),
      Codec.BOOL.optionalFieldOf("transparent", false).forGetter(GolemRenderSettings::isTransparent),
      GolemRenderLayerSettings.CODEC.listOf()
        .optionalFieldOf("layers", Lists.newArrayList(GolemRenderLayerSettings.EYES, GolemRenderLayerSettings.VINES))
        .forGetter(GolemRenderSettings::getLayers),
      GolemMultitextureRenderSettings.CODEC.optionalFieldOf("multitexture").forGetter(GolemRenderSettings::getMultitexture)
    ).apply(instance, GolemRenderSettings::new));
  
  
  private final ResourceLocation material;
  private final String baseRaw;
  private final Pair<ResourceLocation, Boolean> base;
  private final Optional<ResourceLocation> baseTemplate;
  private final int baseColor;
  private final boolean useBiomeColor;
  private final Optional<Integer> baseLight;
  private final boolean transparent;
  private final List<GolemRenderLayerSettings> layers;
  private final Optional<GolemMultitextureRenderSettings> multitexture;
  
  
  private GolemRenderSettings(ResourceLocation material, String baseRaw, Optional<ResourceLocation> baseTemplate, int baseColor,
      boolean useBiomeColor, Optional<Integer> baseLight, boolean transparent,
      List<GolemRenderLayerSettings> layers, Optional<GolemMultitextureRenderSettings> multitexture) {
    super();
    this.material = material;
    this.baseRaw = baseRaw;
    // determine if base is block or prefab texture
    if(baseRaw.length() > 0 && baseRaw.charAt(0) == '#') {
      this.base = new Pair<>(new ResourceLocation(baseRaw.substring(1)), false);
    } else {
      this.base = new Pair<>(new ResourceLocation(baseRaw), true);
    }
    this.baseColor = baseColor;
    this.baseTemplate = baseTemplate;
    this.baseLight = baseLight;
    this.useBiomeColor = useBiomeColor;
    this.transparent = transparent;
    this.layers = ImmutableList.copyOf(layers);
    this.multitexture = multitexture;
  }

  /** @return the ID of the render settings. Must be unique. **/
  public ResourceLocation getMaterial() { return material; }




  
  /** @return a String representation of the base texture **/
  private String getBaseRaw() { return baseRaw; }
  
  /**
   * @return a ResourceLocation of the base texture.
   * The Boolean is true for a block texture and 
   * false for a prefab texture;
   */




  public Pair<ResourceLocation, Boolean> getBase() { return base; }

  /** @return a color to apply to the base texture **/
  public int getBaseColor() { return baseColor; }
  
  /** @return a prefab template texture to use, if any **/




  public Optional<ResourceLocation> getBaseTemplate() { return baseTemplate; }
  
  /** 
   * @return a light level to use for the base layer. 
   * If empty, uses Golem light level
   **/




  public Optional<Integer> getBaseLight() { return baseLight; }
  
  /** @return true to use the biome color instead of {@link #getBaseColor()} **/




  public boolean useBiomeColor() { return useBiomeColor; }
  
  /** @return whether the texture should be rendered transparent **/
  public boolean isTransparent() { return transparent; }
  
  /** @return a List of GolemRenderLayerSettings, may be empty **/




  public List<GolemRenderLayerSettings> getLayers() { return layers; }
  
  /** @return the GolemMultiTextureRenderSettings, if present **/




  public Optional<GolemMultitextureRenderSettings> getMultitexture() {
    return multitexture;
  }
  
  
  
  




  /**
   * @param color a packed int RGB color
   * @return the red, green, and blue components as a Vector3f
   **/
  public static Vector3f unpackColor(final int color) {
    long tmpColor = color;
    if ((tmpColor & -67108864) == 0) {
      tmpColor |= -16777216;
    }
    float colorRed = (float) (tmpColor >> 16 & 255) / 255.0F;
    float colorGreen = (float) (tmpColor >> 8 & 255) / 255.0F;
    float colorBlue = (float) (tmpColor & 255) / 255.0F;
    return new Vector3f(colorRed, colorGreen, colorBlue);
  }
  
  // Functional interfaces
  
  /**
   * Determines a texture to use on a render layer.
   * Depending on when and where this is used,
   * the texture may be a block (dynamic) or prefab
   * for layers like eyes and vines.
   * Intended for use in lambda expressions.
   **/
  @FunctionalInterface
  public static interface ITextureProvider {
    /**
     * Accepts an instance of a GolemBase and returns
     * the appropriate ResourceLocation texture. Since
     * this is called every frame, the ResourceLocation
     * can change during run-time and have immediate effects.
     * @param entity the golem
     * @return the texture to use
     */
    ResourceLocation getTexture(final GolemBase entity);
  }
  
  /**
   * Determines a color to apply to the layer.
   * Usually used for vines and simple layers.
   * Intended for use in lambda expressions.
   **/
  @FunctionalInterface
  public static interface IColorProvider {
    /**
     * Accepts an instance of a GolemBase and returns
     * a color value to apply to a texture. Used in conjunction
     * with {@link ITextureProvider}  
     * @param entity the golem
     * @return the color to use
     **/
    int getColor(final GolemBase entity);
  }
  
  /**
   * Determines the lighting to use for the layer.
   * Intended for use in lambda expressions.
   **/
  @FunctionalInterface
  public static interface ILightingProvider {
    /**
     * Accepts an instance of a GolemBase and returns
     * true when a texture should be rendered without
     * lighting, or returns false when the texture
     * should use world lighting. Used in conjunction
     * with {@link ITextureProvider}  
     * @param entity the golem
     * @return whether to disable lighting for this texture
     */
    boolean disableLighting(final GolemBase entity);
  }

}
