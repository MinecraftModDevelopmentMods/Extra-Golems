package com.mcmoddev.golems.renders;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class GolemRenderType extends RenderType {
  
  private static final Map<ResourceLocation, TextureState> prefabTextureMap = new HashMap<>();
  private static final Map<ResourceLocation, TextureState> dynamicTextureMap = new HashMap<>();

  public GolemRenderType(String name, VertexFormat vertexFormat, int glQuads, int i2, boolean b1,
      boolean b2, Runnable r1, Runnable r2) {
    super(name, vertexFormat, glQuads, i2, b1, b2, r1, r2);
  }
  
  private static TextureState getTextureState(final ResourceLocation texture, final boolean dynamic) {
    if(dynamic) {
      if(!dynamicTextureMap.containsKey(texture)) {
        // make a new texture state
        dynamicTextureMap.put(texture, new DynamicTextureState(texture.toString(), 128, 128).state);
      }
      return dynamicTextureMap.get(texture);
    } else {
      if(!prefabTextureMap.containsKey(texture)) {
        // make a new texture state
        prefabTextureMap.put(texture, new RenderState.TextureState(texture, false, false));
      }
      return prefabTextureMap.get(texture);
    }
  }
  
  public static RenderType getGolemCutout(final ResourceLocation texture, final boolean dynamic) {        
    return makeType(ExtraGolems.MODID + ":golem_cutout", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, false,
        RenderType.State.getBuilder()
        .transparency(NO_TRANSPARENCY)
        .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
        .alpha(DEFAULT_ALPHA)
        .cull(CULL_DISABLED)
        .lightmap(LIGHTMAP_ENABLED)
        .overlay(OVERLAY_ENABLED)
        .texture(getTextureState(texture, dynamic))
        .build(true));
  }
  
  public static RenderType getGolemTransparent(final ResourceLocation texture, final boolean dynamic) {    
    return makeType(ExtraGolems.MODID + ":golem_transparent", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, true, 
        RenderType.State.getBuilder()
        .texture(getTextureState(texture, dynamic))
        .transparency(TRANSLUCENT_TRANSPARENCY)
        .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
        .alpha(DEFAULT_ALPHA)
        .cull(CULL_DISABLED)
        .lightmap(LIGHTMAP_ENABLED)
        .overlay(OVERLAY_ENABLED)
        .build(true));
  }
  
  public static RenderType func_239265_a_(final ResourceLocation texture, final boolean dynamic) {
    return makeType("outline", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, 
        State.getBuilder()
        .texture(getTextureState(texture, dynamic))
        .cull(CullState.CULL_DISABLED)
        .depthTest(DEPTH_ALWAYS)
        .alpha(DEFAULT_ALPHA)
        .texturing(OUTLINE_TEXTURING)
        .fog(NO_FOG)
        .target(OUTLINE_TARGET)
        .func_230173_a_(RenderType.OutlineState.IS_OUTLINE));
  }
  
  public static RenderType getGolemOutline(final ResourceLocation texture, final boolean dynamic) {    
    return makeType(ExtraGolems.MODID + ":golem_transparent", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, true, 
        RenderType.State.getBuilder()
        .texture(getTextureState(texture, dynamic))
        .transparency(TRANSLUCENT_TRANSPARENCY)
        .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
        .alpha(DEFAULT_ALPHA)
        .cull(CULL_DISABLED)
        .lightmap(LIGHTMAP_ENABLED)
        .overlay(OVERLAY_ENABLED)
        .build(true));
  }
}
