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
 * Adapted from Amber (was Ashe)#5285 from Discord. Used with permission
 **/
public class DynamicTextureState {

  public String name;
  public ResourceLocation location;
  public RenderState.TextureState state;
  public DynamicTexture texture;

  /**
   * @param name a ResourceLocation string of the image file to use
   * @param width the texture width
   * @param height the texture height
   **/
  public DynamicTextureState(String name, int width, int height) {
    this.name = name;
    location = new ResourceLocation(ExtraGolems.MODID, "dynamic/" + new ResourceLocation(name).getPath());
    TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    texture = new DynamicTexture(width, height, true);
    NativeImage img = texture.getTextureData();
    
    // attempt to read a texture from the given location
    try (IResource res = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(name))) {
      NativeImage block = NativeImage.read(res.getInputStream());
      for (int i = 0; i < width; ++i) {
        for (int j = 0; j < height; ++j) {
          img.setPixelRGBA(i, j, block.getPixelRGBA(i % 16, j % 16));
        }
      }
    } catch (IOException e) {
      ExtraGolems.LOGGER.error("Error trying to make dynamic texture for " + name);
      texture.getTextureData().fillAreaRGBA(0, 0, width, height, 0xffffffff);
      e.printStackTrace();
    }
    // update texture
    texture.updateDynamicTexture();
    textureManager.loadTexture(location, texture);
    state = new RenderState.TextureState(location, false, false);
  }
}
