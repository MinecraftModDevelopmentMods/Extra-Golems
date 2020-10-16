package com.mcmoddev.golems.util;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class GolemRenderSettings {
  
  public static final ResourceLocation FALLBACK_BLOCK = new ResourceLocation("minecraft", "textures/block/clay.png");
  public static final ResourceLocation FALLBACK_PREFAB = new ResourceLocation("minecraft", "textures/entity/iron_golem/iron_golem.png");
  public static final ResourceLocation FALLBACK_VINES = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/vines.png");
  public static final ResourceLocation FALLBACK_EYES = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/eyes.png");

  public static final int VINES_COLOR = 0x83a05a;
  
  /** When false, none of this class is used **/
  private final boolean hasCustomRender;
  
  // These are used when a texture is auto-generated
  private final boolean hasTransparency;
  private final ILightingProvider textureGlow;
  private final ITextureProvider blockTexture;
  private final boolean hasVines;
  private final ILightingProvider vinesGlow;
  private final ITextureProvider vinesTexture;
  private final ILightingProvider eyesGlow;
  private final ITextureProvider eyesTexture;
  
  // This is used when a texture is already made
  private final boolean hasPrefabTexture;
  private final ITextureProvider prefabTexture;

  private final boolean hasColor;
  private final IColorProvider textureColorProvider;
  private final IColorProvider vinesColorProvider;
  
  public GolemRenderSettings(boolean lHasCustomRender, boolean lHasTransparency,
      ILightingProvider lTextureGlow, ITextureProvider lBlockTextureProvider, 
      boolean lHasVines, boolean lVinesGlow, ITextureProvider lVinesTextureProvider, 
      boolean lEyesGlow, ITextureProvider lEyesTextureProvider,
      boolean lHasPrefabTexture, ITextureProvider lPrefabTextureProvider, 
      boolean lHasColor, IColorProvider lTextureColorProvider, 
      IColorProvider lVinesColorProvider) {
    this.hasCustomRender = lHasCustomRender;
    this.hasTransparency = lHasTransparency;
    this.textureGlow = lTextureGlow;
    this.blockTexture = lBlockTextureProvider;
    this.hasVines = lHasVines;
    this.vinesGlow = g -> lVinesGlow;
    this.vinesTexture = lVinesTextureProvider;
    this.eyesGlow = g -> lEyesGlow;
    this.eyesTexture = lEyesTextureProvider;
    this.hasPrefabTexture = lHasPrefabTexture;
    this.prefabTexture = lPrefabTextureProvider;
    this.hasColor = lHasColor;
    this.textureColorProvider = lTextureColorProvider;
    this.vinesColorProvider = lVinesColorProvider;
  }
  
  /** @return whether to skip these render settings when registering renders **/
  public boolean hasCustomRender() { return hasCustomRender; }
  /** @return whether the texture should be rendered transparent **/
  public boolean hasTransparency() { return hasTransparency; }
  /** @return whether the eyes should be rendered with constant light **/
  public ILightingProvider getTextureLighting() { return textureGlow; }
  /** @return the block texture provider **/
  public ITextureProvider getBlockTexture() { return blockTexture; }
  /** @return whether to render vines **/
  public boolean hasVines() { return hasVines; }
  /** @return whether the eyes should be rendered with constant light **/
  public ILightingProvider getVinesLighting() { return vinesGlow; }
  /** @return the vines texture provider **/
  public ITextureProvider getVinesTexture() { return vinesTexture; }
  /** @return whether the eyes should be rendered with constant light **/
  public ILightingProvider getEyesLighting() { return eyesGlow; }
  /** @return the vines texture provider **/
  public ITextureProvider getEyesTexture() { return eyesTexture; }
  /** @return whether a prefabricated texture should be used **/
  public boolean hasPrefabTexture() { return hasPrefabTexture; }
  /** @return the prefab texture provider **/
  public ITextureProvider getPrefabTexture() { return prefabTexture; }
  /** @return whether the texture should be colored **/
  public boolean hasColor() { return hasColor; }
  /** @return the texture color provider **/
  public IColorProvider getBlockColorProvider() { return textureColorProvider; }
  /** @return the vines color provider **/
  public IColorProvider getVinesColorProvider() { return vinesColorProvider; }
  
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
  
  @FunctionalInterface
  public static interface ITextureProvider {
    ResourceLocation getTexture(final GolemBase entity);
  }
  
  @FunctionalInterface
  public static interface IColorProvider {
    int getColor(final GolemBase entity);
  }
  
  @FunctionalInterface
  public static interface ILightingProvider {
    boolean disableLighting(final GolemBase entity);
  }

}
