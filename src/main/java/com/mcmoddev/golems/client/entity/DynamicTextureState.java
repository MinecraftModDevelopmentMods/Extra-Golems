package com.mcmoddev.golems.client.entity;

import com.mcmoddev.golems.ExtraGolems;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.Optional;

/**
 * Adapted from AtelierAmber [Amber (was Ashe)#5285]. Used with permission.
 **/
public class DynamicTextureState {

	protected static final int TILES = 8;

	private final ResourceLocation location;
	private final ResourceLocation sourceImage;
	private final ResourceLocation templateImage;
	private final RenderStateShard.TextureStateShard state;
	private final DynamicTexture dynamicTexture;

	/**
	 * @param id           a unique ResourceLocation for the location of the DynamicTextureState
	 * @param blockName    a ResourceLocation of a block image file
	 * @param templateName a ResourceLocation of a template image file
	 **/
	public DynamicTextureState(ResourceLocation id, ResourceLocation blockName, ResourceLocation templateName) {
		location = id;
		sourceImage = blockName;
		templateImage = templateName;
		DynamicTexture texture;

		Optional<Resource> optionalBlockResource = Minecraft.getInstance().getResourceManager().getResource(blockName);
		Optional<Resource> optionalTemplateResource = Minecraft.getInstance().getResourceManager().getResource(templateName);

		if(optionalBlockResource.isPresent() && optionalTemplateResource.isPresent()) {
			// attempt to read the block and template textures
			try {
				Resource blockResource = optionalBlockResource.get();
				Resource templateResource = optionalTemplateResource.get();
				NativeImage block = NativeImage.read(blockResource.open());
				NativeImage template = NativeImage.read(templateResource.open());
				final int blockWidth = block.getWidth();
				final int outputWidth = TILES * blockWidth;
				final int outputHeight = TILES * blockWidth;
				final int templateWidth = template.getWidth();
				final int templateHeight = template.getHeight();
				final float scale = outputWidth / templateWidth;
				// create a new texture and write each pixel, multiplied by alpha channel of template
				texture = new DynamicTexture(outputWidth, outputHeight, true);
				NativeImage outputImg = texture.getPixels();
				for (int j = 0; j < outputHeight; ++j) {
					for (int i = 0; i < outputWidth; ++i) {
						int alpha = template.getLuminanceOrAlpha((int) (i / scale) % templateWidth, (int) (j / scale) % templateHeight);
						outputImg.setPixelRGBA(i, j, block.getPixelRGBA(i % blockWidth, j % blockWidth) & alpha);
					}
				}
			} catch (IOException e) {
				ExtraGolems.LOGGER.error("Error opening image resource for " + blockName + " with template " + templateName);
				texture = fallback();
				e.printStackTrace();
			}
		} else {
			ExtraGolems.LOGGER.error("Error locating image resource for " + blockName + " with template " + templateName);
			texture = fallback();
		}
		// update texture
		this.dynamicTexture = texture;
		this.dynamicTexture.upload();
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.register(location, this.dynamicTexture);
		this.state = new RenderStateShard.TextureStateShard(this.location, false, false);
	}

	/**
	 * @return a texture filled with white ({@code 0xFFFFFFFF}) pixels and no template applied
	 */
	private DynamicTexture fallback() {
		final DynamicTexture texture = new DynamicTexture(16 * TILES, 16 * TILES, true);
		texture.getPixels().fillRect(0, 0, 16 * TILES, 16 * TILES, 0xffffffff);
		return texture;
	}

	//// GETTERS ////

	public ResourceLocation getLocation() {
		return location;
	}

	public ResourceLocation getSourceImage() {
		return sourceImage;
	}

	public ResourceLocation getTemplateImage() {
		return templateImage;
	}

	public RenderStateShard.TextureStateShard getState() {
		return state;
	}

	public DynamicTexture getTexture() {
		return dynamicTexture;
	}
}
