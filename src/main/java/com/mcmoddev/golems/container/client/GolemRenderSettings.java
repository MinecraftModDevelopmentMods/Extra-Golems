package com.mcmoddev.golems.container.client;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ibm.icu.impl.Pair;
import com.mcmoddev.golems.ExtraGolems;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public class GolemRenderSettings {
  
  public static final ResourceLocation FALLBACK_BLOCK = new ResourceLocation("minecraft", "textures/block/clay.png");
  public static final ResourceLocation BASE_TEMPLATE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/template.png");

  public static final GolemRenderSettings EMPTY = new GolemRenderSettings(
      new ResourceLocation(ExtraGolems.MODID, "empty"), FALLBACK_BLOCK.toString(), BASE_TEMPLATE, Optional.empty(), false, 
      Optional.empty(), false, Lists.newArrayList(), Optional.empty());
  
  public static final Codec<GolemRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("material").forGetter(GolemRenderSettings::getMaterial),
      Codec.STRING.optionalFieldOf("base", FALLBACK_BLOCK.toString()).forGetter(GolemRenderSettings::getBaseRaw),
      ResourceLocation.CODEC.optionalFieldOf("base_template", BASE_TEMPLATE).forGetter(GolemRenderSettings::getBaseTemplate),
      Codec.INT.optionalFieldOf("base_color").forGetter(GolemRenderSettings::getBaseColor),
      Codec.BOOL.optionalFieldOf("use_biome_color", false).forGetter(GolemRenderSettings::useBiomeColor),
      Codec.BOOL.optionalFieldOf("base_light").forGetter(GolemRenderSettings::getBaseLight),
      Codec.BOOL.optionalFieldOf("translucent", false).forGetter(GolemRenderSettings::isTranslucent),
      LayerRenderSettings.CODEC.listOf()
        .optionalFieldOf("layers", Lists.newArrayList(LayerRenderSettings.EYES, LayerRenderSettings.VINES))
        .forGetter(GolemRenderSettings::getLayers),
      MultitextureRenderSettings.CODEC.optionalFieldOf("multitexture").forGetter(GolemRenderSettings::getMultitexture)
    ).apply(instance, GolemRenderSettings::new));
  
  
  private final ResourceLocation material;
  private final String baseRaw;
  private final Pair<ResourceLocation, Boolean> base;
  private final ResourceLocation baseTemplate;
  private final Optional<Integer> baseColor;
  private final boolean useBiomeColor;
  private final Optional<Boolean> baseLight;
  private final boolean translucent;
  private final List<LayerRenderSettings> layers;
  private final Optional<MultitextureRenderSettings> multitexture;
  
  private GolemRenderSettings(ResourceLocation material, String baseRaw, ResourceLocation baseTemplate, 
      Optional<Integer> baseColor, boolean useBiomeColor, Optional<Boolean> baseLight, boolean transparent,
      List<LayerRenderSettings> layers, Optional<MultitextureRenderSettings> multitexture) {
    super();
    this.material = material;
    this.baseRaw = baseRaw;
    // determine if base is block or prefab texture
    if(baseRaw.length() > 0 && baseRaw.charAt(0) == '#') {
      ResourceLocation r = new ResourceLocation(baseRaw.substring(1));
      this.base = Pair.of(new ResourceLocation(r.getNamespace(), "textures/entity/" + r.getPath()), false);
    } else {
      ResourceLocation r = new ResourceLocation(baseRaw);
      this.base = Pair.of(new ResourceLocation(r.getNamespace(), r.getPath()), true);
    }
    this.baseColor = baseColor;
    this.baseTemplate = baseTemplate;
    this.baseLight = baseLight;
    this.useBiomeColor = useBiomeColor;
    this.translucent = transparent;
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
  public Optional<Integer> getBaseColor() { return baseColor; }
  
  /** @return a prefab template texture to use **/
  public ResourceLocation getBaseTemplate() { return baseTemplate; }
  
  /**
   * @return an Optional containing the light for the layer.
   * If empty, use the Golem light
   */
  public Optional<Boolean> getBaseLight() { return baseLight; }
  
  /** @return true to use the biome color instead of {@link #getBaseColor()} **/
  public boolean useBiomeColor() { return useBiomeColor; }
  
  /** @return whether the texture should be rendered translucent **/
  public boolean isTranslucent() { return translucent; }
  
  /** @return a List of LayerRenderSettings, may be empty **/
  public List<LayerRenderSettings> getLayers() { return layers; }
  
  /** @return the GolemMultiTextureRenderSettings, if present **/
  public Optional<MultitextureRenderSettings> getMultitexture() {
    return multitexture;
  }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("GolemRenderSettings: ");
    b.append("material[").append(material).append("] ");
    b.append("base[").append(base).append("] ");
    b.append("template[").append(baseTemplate).append("] ");
    b.append("color[").append(baseColor).append("] ");
    b.append("biome_color[").append(useBiomeColor).append("] ");
    b.append("light[").append(baseLight).append("] ");
    b.append("translucent[").append(translucent).append("] ");
    b.append("layers[").append(layers).append("] ");
    return b.toString();
  }
  
  private static final Vector3f ONE = new Vector3f(1.0F, 1.0F, 1.0F);

  /**
   * Separates a hex color into RGB components.
   * If the color int is less than 0, returns
   * a Vector3f with all 1.0s
   * @param color a packed int RGB color
   * @return the red, green, and blue components as a Vector3f
   **/
  public static Vector3f unpackColor(final int color) {
    if(color < 0) {
      return ONE;
    }
    long tmpColor = color;
    if ((tmpColor & -67108864) == 0) {
      tmpColor |= -16777216;
    }
    float colorRed = (float) (tmpColor >> 16 & 255) / 255.0F;
    float colorGreen = (float) (tmpColor >> 8 & 255) / 255.0F;
    float colorBlue = (float) (tmpColor & 255) / 255.0F;
    return new Vector3f(colorRed, colorGreen, colorBlue);
  }
}
