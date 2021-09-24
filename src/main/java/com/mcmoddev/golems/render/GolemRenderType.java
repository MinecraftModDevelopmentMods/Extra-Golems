package com.mcmoddev.golems.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class GolemRenderType extends RenderType {
  
  private static final Map<ResourceLocation, DynamicTextureState> dynamicTextureMap = new HashMap<>();
  public GolemRenderType(String name, VertexFormat vertexFormat, int glQuads, int i2, boolean b1,
						 boolean b2, Runnable r1, Runnable r2) {
	super(name, vertexFormat, glQuads, i2, b1, b2, r1, r2);
  }

  public static void reloadDynamicTextureMap() {
	final Map<ResourceLocation, DynamicTextureState> copy = new HashMap<>(dynamicTextureMap);
	copy.entrySet().forEach(e -> dynamicTextureMap.put(e.getKey(), new DynamicTextureState(e.getKey(), e.getValue().sourceImage, e.getValue().templateImage)));
  }

  private static TextureState getTextureState(final ResourceLocation texture, final ResourceLocation template) {
	// lazy-load the texture state
	final ResourceLocation id = new ResourceLocation(texture.getNamespace(), "dynamic/" + template.getPath() + "/" + texture.getPath());
	if(!dynamicTextureMap.containsKey(id)) {
	  dynamicTextureMap.put(id, new DynamicTextureState(id, texture, template));
	}
	return dynamicTextureMap.get(id).state;
  }

  public static RenderType getGolemCutout(final ResourceLocation texture, final ResourceLocation template, final boolean dynamic) {
	if(!dynamic) {
	  return RenderType.getEntityCutoutNoCull(texture);
	}
	// make dynamic cutout type
	return makeType("golem_cutout", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, false,
			RenderType.State.getBuilder()
					.transparency(NO_TRANSPARENCY)
					.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
					.alpha(DEFAULT_ALPHA)
					.cull(CULL_DISABLED)
					.lightmap(LIGHTMAP_ENABLED)
					.overlay(OVERLAY_ENABLED)
					.texture(getTextureState(texture, template))
					.build(true));
  }

  public static RenderType getGolemTranslucent(final ResourceLocation texture, final ResourceLocation template, final boolean dynamic) {
	if(!dynamic) {
	  return RenderType.getEntityTranslucent(texture);
	}
	// make dynamic translucent type
	return makeType("golem_translucent", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, true,
			RenderType.State.getBuilder()
					.texture(getTextureState(texture, template))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
					.alpha(DEFAULT_ALPHA)
					.cull(CULL_DISABLED)
					.lightmap(LIGHTMAP_ENABLED)
					.overlay(OVERLAY_ENABLED)
					.build(true));
  }

  public static RenderType getGolemOutline(final ResourceLocation texture, final ResourceLocation template, final boolean dynamic) {
	if(!dynamic) {
	  return RenderType.getOutline(texture);
	}
	// make dynamic outline type
	return makeType("golem_outline", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256,
			State.getBuilder()
					.texture(getTextureState(texture, template))
					.cull(CullState.CULL_DISABLED)
					.depthTest(DEPTH_ALWAYS)
					.alpha(DEFAULT_ALPHA)
					.texturing(OUTLINE_TEXTURING)
					.fog(NO_FOG)
					.target(OUTLINE_TARGET)
					.build(RenderType.OutlineState.IS_OUTLINE));
  }
}
