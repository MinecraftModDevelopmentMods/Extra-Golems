package com.mcmoddev.golems.render;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mcmoddev.golems.container.render.LayerRenderSettings;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GolemRenderType extends RenderType {

	private static final Set<ResourceLocation> loadedRenderSettings = new HashSet<>();
	private static final Map<ResourceLocation, DynamicTextureState> dynamicTextureMap = new HashMap<>();

	public GolemRenderType(String name, VertexFormat vertexFormat, VertexFormat.Mode glQuads, int i2, boolean b1,
						   boolean b2, Runnable r1, Runnable r2) {
		super(name, vertexFormat, glQuads, i2, b1, b2, r1, r2);
	}

	/**
	 * Lazy-loads the render settings using the registry access and the material ID.
	 * If this is the first time the settings are loaded, some preprocessing is performed.
	 * @param access the registry access
	 * @param material the material ID
	 * @return the golem render settings after some pre-processing
	 */
	public static GolemRenderSettings loadRenderSettings(final RegistryAccess access, final ResourceLocation material) {
		final Registry<GolemRenderSettings> registry = access.registryOrThrow(ExtraGolems.Keys.GOLEM_MODELS);
		GolemRenderSettings settings = registry.getOptional(material).orElse(GolemRenderSettings.EMPTY);
		if(!loadedRenderSettings.contains(material)) {
			loadedRenderSettings.add(material);
			GolemRenderSettings.ClientUtils.loadSettings(settings);
			LayerRenderSettings.ClientUtil.loadLayerRenderSettings(LayerRenderSettings.RAINBOW);
		}
		return settings;
	}

	/**
	 * Called when the user reloads render settings.
	 */
	public static void clearLoadedRenderSettings() {
		loadedRenderSettings.clear();
	}

	/**
	 * Called when the user reloads assets. Re-builds each texture currently in the dynamic texture map.
	 */
	public static void reloadDynamicTextureMap() {
		clearLoadedRenderSettings();
		final Map<ResourceLocation, DynamicTextureState> copy = new HashMap<>(dynamicTextureMap);
		copy.entrySet().forEach(e -> dynamicTextureMap.put(e.getKey(), new DynamicTextureState(e.getKey(), e.getValue().sourceImage, e.getValue().templateImage)));
	}

	private static TextureStateShard getTextureState(final ResourceLocation texture, final ResourceLocation template) {
		// lazy-load the texture state
		final ResourceLocation id = new ResourceLocation(texture.getNamespace(), "dynamic/" + template.getPath() + "/" + texture.getPath());
		if (!dynamicTextureMap.containsKey(id)) {
			dynamicTextureMap.put(id, new DynamicTextureState(id, texture, template));
		}
		return dynamicTextureMap.get(id).state;
	}

	public static RenderType getGolemCutout(final ResourceLocation texture, final ResourceLocation template, final boolean dynamic) {
		if (!dynamic) {
			return RenderType.entityCutoutNoCull(texture);
		}
		// make dynamic cutout type
		return create("golem_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
						.setTransparencyState(NO_TRANSPARENCY)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.setCullState(NO_CULL)
						.setTextureState(getTextureState(texture, template))
						.createCompositeState(true));
	}

	public static RenderType getGolemTranslucent(final ResourceLocation texture, final ResourceLocation template, final boolean dynamic) {
		if (!dynamic) {
			return RenderType.entityTranslucent(texture);
		}
		// make dynamic translucent type
		return create("golem_transparent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setCullState(NO_CULL)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.setTextureState(getTextureState(texture, template))
						.createCompositeState(true));
	}

	public static RenderType getGolemOutline(final ResourceLocation texture, final ResourceLocation template, final boolean dynamic) {
		if (!dynamic) {
			return RenderType.outline(texture);
		}
		// make dynamic outline type
		return create("golem_outline",
				DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_OUTLINE_SHADER)
						.setCullState(CullStateShard.NO_CULL)
						.setDepthTestState(NO_DEPTH_TEST)
						.setOutputState(OUTLINE_TARGET)
						.setTextureState(getTextureState(texture, template))
						.createCompositeState(RenderType.OutlineProperty.IS_OUTLINE));
	}
}
