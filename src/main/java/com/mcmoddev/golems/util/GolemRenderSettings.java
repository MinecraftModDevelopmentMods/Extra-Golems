package com.mcmoddev.golems.util;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.util.ResourceLocation;

public class GolemRenderSettings {
  
  public static final ResourceLocation FALLBACK_BLOCK = new ResourceLocation("minecraft", "textures/block/clay.png");
  public static final ResourceLocation FALLBACK_PREFAB = new ResourceLocation("minecraft", "textures/entity/iron_golem/iron_golem.png");
  public static final ResourceLocation FALLBACK_VINES = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/vines.png");
  
  /** When false, none of this class is used **/
  private final boolean hasCustomRender;
  
  // These are used when a texture is auto-generated
  private final boolean hasTransparency;
  private final ITextureProvider<? extends GolemBase> blockTexture;
  private final boolean hasVines;
  private final ITextureProvider<? extends GolemBase> vinesTexture;
  
  // This is used when a texture is already made
  private final boolean hasPrefabTexture;
  private final ITextureProvider<? extends GolemBase> prefabTexture;

  private final boolean hasBlockColor;
  private final IColorProvider<? extends GolemBase> blockColorProvider;
  private final boolean hasVinesColor;
  private final IColorProvider<? extends GolemBase> vinesColorProvider;
  
  public GolemRenderSettings(boolean lHasCustomRender, boolean lHasTransparency,
      ITextureProvider<? extends GolemBase> blockTextureProvider, 
      boolean lHasVines, ITextureProvider<? extends GolemBase> vinesTextureProvider, 
      boolean lHasPrefabTexture, ITextureProvider<? extends GolemBase> prefabTextureProvider, 
      boolean lHasBlockColor, IColorProvider<? extends GolemBase> lBlockColorProvider, 
      boolean lHasVinesColor, IColorProvider<? extends GolemBase> lVinesColorProvider) {
    this.hasCustomRender = lHasCustomRender;
    this.hasTransparency = lHasTransparency;
    this.blockTexture = blockTextureProvider;
    this.hasVines = lHasVines;
    this.vinesTexture = vinesTextureProvider;
    this.hasPrefabTexture = lHasPrefabTexture;
    this.prefabTexture = prefabTextureProvider;
    this.hasBlockColor = lHasBlockColor;
    this.blockColorProvider = lBlockColorProvider;
    this.hasVinesColor = lHasVinesColor;
    this.vinesColorProvider = lVinesColorProvider;
  }
  
  /** @return whether to skip these render settings when registering renders **/
  public boolean hasCustomRender() { return hasCustomRender; }
  /** @return whether the texture should be rendered transparent **/
  public boolean hasTransparency() { return hasTransparency; }
  /** @return the block texture provider **/
  public ITextureProvider<? extends GolemBase> getBlockTexture() { return blockTexture; }
  /** @return whether to render vines **/
  public boolean hasVines() { return hasVines; }
  /** @return the vines texture provider **/
  public ITextureProvider<? extends GolemBase> getVinesTexture() { return vinesTexture; }
  /** @return whether a prefabricated texture should be used **/
  public boolean hasPrefabTexture() { return hasPrefabTexture; }
  /** @return the prefab texture provider **/
  public ITextureProvider<? extends GolemBase> getPrefabTexture() { return prefabTexture; }
  /** @return whether the block texture should be colored **/
  public boolean hasBlockColor() { return hasBlockColor; }
  /** @return the block color provider **/
  public IColorProvider<? extends GolemBase> getBlockColorProvider() { return blockColorProvider; }
  /** @return whether the vines texture should be colored **/
  public boolean hasVinesColor() { return hasVinesColor; }
  /** @return the vines color provider **/
  public IColorProvider<? extends GolemBase> getVinesColorProvider() { return vinesColorProvider; }
  
  // Functional interfaces
  
  @FunctionalInterface
  public static interface ITextureProvider<T extends GolemBase> {
    ResourceLocation getTexture(final T entity);
  }
  
  @FunctionalInterface
  public static interface IColorProvider<T extends GolemBase> {
    int getColor(final T entity);
  }

}
