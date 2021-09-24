package com.mcmoddev.golems.render;

import java.io.IOException;

import com.mcmoddev.golems.ExtraGolems;
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
  public ResourceLocation sourceImage;
  public ResourceLocation templateImage;
  public RenderState.TextureState state;
  public DynamicTexture texture;

  /**
   * @param id a unique ResourceLocation for the location of the DynamicTextureState
   * @param blockName a ResourceLocation of a block image file
   * @param templateName a ResourceLocation of a template image file
   **/
  public DynamicTextureState(ResourceLocation id, ResourceLocation blockName, ResourceLocation templateName) {
    location = id;
    sourceImage = blockName;
    templateImage = templateName;
    
    try {
      // attempt to read the block and template textures
      IResource blockResource = Minecraft.getInstance().getResourceManager().getResource(blockName);
      IResource templateResource = Minecraft.getInstance().getResourceManager().getResource(templateName);
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
      NativeImage outputImg = texture.getTextureData();
      for (int j = 0; j < outputHeight; ++j) {
        for (int i = 0; i < outputWidth; ++i) {
          int alpha = template.getPixelLuminanceOrAlpha((int)(i / scale) % templateWidth, (int)(j / scale) % templateHeight);
          outputImg.setPixelRGBA(i, j, block.getPixelRGBA(i % blockWidth, j % blockWidth) & alpha);
        }
      }
    } catch (IOException e) {
      ExtraGolems.LOGGER.error("Error trying to make dynamic texture for " + blockName + " with template " + templateName);
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
