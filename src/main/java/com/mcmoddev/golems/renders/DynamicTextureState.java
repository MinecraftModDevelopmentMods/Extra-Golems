package com.mcmoddev.golems.renders;

import java.io.IOException;
import java.util.Random;

import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResource;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;

/**
 * Adapted from Amber (was Ashe)#5285 from Discord. Used with permission
 **/
public class DynamicTextureState {

  public String name;
  public ResourceLocation location;
  public RenderState.TextureState state;
  public DynamicTexture texture;

  public DynamicTextureState(String name, int width, int height) {
    this.name = name;
    location = new ResourceLocation(ExtraGolems.MODID, "dynamic/" + name);
    TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    texture = new DynamicTexture(width, height, true);
    NativeImage img = texture.getTextureData();
    
    
    try (IResource res = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation("textures/blocks/melon.png"))) {
      texture.setTextureData(NativeImage.read(res.getInputStream()));
    } catch (IOException e) {
      ExtraGolems.LOGGER.error("Error trying to make dynamic texture for " + name);
      e.printStackTrace();
    }
    
    Random rand = new Random();
    for (int i = 0; i < width; ++i) {
      for (int j = 0; j < height; ++j) {
        // TODO: here is where we update the texture using the block
        img.setPixelRGBA(i, j, ColorHelper.PackedColor.packColor(255, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
      }
    }
    // texture.getTextureData().fillAreaRGBA(0, 0, width, height, TextureUtil.colorToInt(255, 255, 255, 255));
    texture.updateDynamicTexture();
    textureManager.loadTexture(location, texture);
    state = new RenderState.TextureState(location, false, false);
  }
}
