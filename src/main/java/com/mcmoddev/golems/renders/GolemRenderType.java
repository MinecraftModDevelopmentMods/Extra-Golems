package com.mcmoddev.golems.renders;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class GolemRenderType extends RenderType {
  
  private static final Map<ResourceLocation, DynamicTextureState> dynamicTextureMap = new HashMap<>();
  
  public static final ResourceLocation TEMPLATE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/template.png");
  public static final ResourceLocation MUSHROOM_TEMPLATE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.MUSHROOM_GOLEM + "/template.png");
  public static final ResourceLocation WOOL_TEMPLATE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.WOOL_GOLEM + "/template.png");

  public GolemRenderType(String name, VertexFormat vertexFormat, int glQuads, int i2, boolean b1,
      boolean b2, Runnable r1, Runnable r2) {
    super(name, vertexFormat, glQuads, i2, b1, b2, r1, r2);
  }
  
  public static void reloadDynamicTextureMap() {
    final Map<ResourceLocation, DynamicTextureState> copy = new HashMap<>(dynamicTextureMap);
    copy.entrySet().forEach(e -> dynamicTextureMap.put(e.getKey(), new DynamicTextureState(e.getKey(), e.getValue().templateImage)));
  }
  
  private static TextureState getTextureState(final ResourceLocation texture, final ResourceLocation template) {
    // lazy-load the texture state
    if(!dynamicTextureMap.containsKey(texture)) {
      dynamicTextureMap.put(texture, new DynamicTextureState(texture, template));
    }
    return dynamicTextureMap.get(texture).state;
  }
  
  
  
  public static RenderType getGolemCutout(final ResourceLocation texture, final boolean dynamic) {
    return getGolemCutout(texture, TEMPLATE, dynamic);
  }
  

  public static RenderType getGolemCutout(final ResourceLocation texture, final ResourceLocation template, final boolean dynamic) {        
    if(!dynamic) {
      RenderType.getEntityCutoutNoCull(texture);
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
  
  public static RenderType getGolemTransparent(final ResourceLocation texture, final boolean dynamic) {
    if(!dynamic) {
      RenderType.getEntityTranslucent(texture);
    }
    // make dynamic translucent type
    return makeType("golem_transparent", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, true, 
        RenderType.State.getBuilder()
        .texture(getTextureState(texture, TEMPLATE))
        .transparency(TRANSLUCENT_TRANSPARENCY)
        .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
        .alpha(DEFAULT_ALPHA)
        .cull(CULL_DISABLED)
        .lightmap(LIGHTMAP_ENABLED)
        .overlay(OVERLAY_ENABLED)
        .build(true));
  }
  
  public static RenderType getGolemOutline(final ResourceLocation texture, final boolean dynamic) {
    if(!dynamic) {
      RenderType.getOutline(texture);
    }
    // make dynamic outline type
    return makeType("golem_outline", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, 
        State.getBuilder()
        .texture(getTextureState(texture, TEMPLATE))
        .cull(CullState.CULL_DISABLED)
        .depthTest(DEPTH_ALWAYS)
        .alpha(DEFAULT_ALPHA)
        .texturing(OUTLINE_TEXTURING)
        .fog(NO_FOG)
        .target(OUTLINE_TARGET)
        .func_230173_a_(RenderType.OutlineState.IS_OUTLINE));
  }
}
