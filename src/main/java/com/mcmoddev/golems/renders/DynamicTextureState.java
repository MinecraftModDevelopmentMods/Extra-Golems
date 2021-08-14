package com.mcmoddev.golems.renders;

import java.io.IOException;

import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.DynamicTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.resources.ResourceLocation;

/**
 * Adapted from AtelierAmber [Amber (was Ashe)#5285]. Used with permission.
 **/
public class DynamicTextureState {
  
  public static final int TILES = 8;
  public ResourceLocation location;
  public ResourceLocation templateImage;
  public RenderStateShard.TextureStateShard state;
  public DynamicTexture texture;

  /**
   * @param blockName a ResourceLocation of a block image file
   * @param templateName a ResourceLocation of a template image file
   **/
  public DynamicTextureState(ResourceLocation blockName, ResourceLocation templateName) {
    location = new ResourceLocation(ExtraGolems.MODID, "dynamic/" + blockName.getPath());
    templateImage = templateName;
    
    try {
      // attempt to read the block and template textures
      Resource blockResource = Minecraft.getInstance().getResourceManager().getResource(blockName);
      Resource templateResource = Minecraft.getInstance().getResourceManager().getResource(templateName);
      NativeImage block = NativeImage.read(blockResource.getInputStream());
      NativeImage template = NativeImage.read(templateResource.getInputStream());
      final int blockWidth = block.getWidth();
      final int outputWidth = TILES * blockWidth;
      final int outputHeight = TILES * blockWidth;
      final int templateWidth = template.getWidth();
      final int templateHeight = template.getHeight();
      final float scale = outputWidth / templateWidth;
      // create a new texture and write each pixel
      texture = new DynamicTexture(outputWidth, outputHeight, true);
      NativeImage outputImg = texture.getPixels();
      for (int j = 0; j < outputHeight; ++j) {
        for (int i = 0; i < outputWidth; ++i) {
          int alpha = template.getLuminanceOrAlpha((int)(i / scale) % templateWidth, (int)(j / scale) % templateHeight);
          outputImg.setPixelRGBA(i, j, block.getPixelRGBA(i % blockWidth, j % blockWidth) & alpha);
        }
      }
    } catch (IOException e) {
      ExtraGolems.LOGGER.error("Error trying to make dynamic texture for " + blockName + " with template " + templateName);
      texture = new DynamicTexture(16 * TILES, 16 * TILES, true);
      texture.getPixels().fillRect(0, 0, 16 * TILES, 16 * TILES, 0xffffffff);
      e.printStackTrace();
    }
    // update texture
    texture.upload();
    TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    textureManager.register(location, texture);
    state = new RenderStateShard.TextureStateShard(location, false, false);
  }
}
