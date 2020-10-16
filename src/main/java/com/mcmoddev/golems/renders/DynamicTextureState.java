package com.mcmoddev.golems.renders;

import java.io.IOException;

import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

/**
 * Adapted from AtelierAmber [Amber (was Ashe)#5285]. Used with permission.
 **/
public class DynamicTextureState {
  
  public static final int TILES = 8;
  public ResourceLocation location;
  public RenderState.TextureState state;
  public DynamicTexture texture;

  /**
   * @param name a ResourceLocation string of the image file to use
   **/
  public DynamicTextureState(String name) {
    location = new ResourceLocation(ExtraGolems.MODID, "dynamic/" + new ResourceLocation(name).getPath());
    
    // attempt to read a texture from the given location
    try (IResource res = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(name))) {
      NativeImage block = NativeImage.read(res.getInputStream());
      int blockWidth = block.getWidth();
      int width = TILES * blockWidth;
      int height = TILES * blockWidth;
      texture = new DynamicTexture(width, height, true);
      NativeImage img = texture.getTextureData();
      for (int i = 0; i < width; ++i) {
        for (int j = 0; j < height; ++j) {
          img.setPixelRGBA(i, j, block.getPixelRGBA(i % blockWidth, j % blockWidth));
        }
      }
    } catch (IOException e) {
      ExtraGolems.LOGGER.error("Error trying to make dynamic texture for " + name);
      texture = new DynamicTexture(16 * TILES, 16 * TILES, true);
      texture.getTextureData().fillAreaRGBA(0, 0, 16 * TILES, 16 * TILES, 0xffffffff);
      e.printStackTrace();
    }
    // update texture
    texture.updateDynamicTexture();
    TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    textureManager.loadTexture(location, texture);
    state = new RenderState.TextureState(location, false, false);
  }
}
