package com.mcmoddev.golems.event;

import com.mcmoddev.golems.render.GolemRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class EGClientEvents {

	public static void addResources() {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if (resourceManager instanceof ReloadableResourceManager) {
			// reload dynamic texture map
			((ReloadableResourceManager) resourceManager).registerReloadListener(new SimplePreparableReloadListener<ModelBakery>() {
				@Override
				protected ModelBakery prepare(ResourceManager arg0, ProfilerFiller arg1) {
					return null;
				}

				@Override
				protected void apply(ModelBakery arg0, ResourceManager arg1, ProfilerFiller arg2) {
					GolemRenderType.reloadDynamicTextureMap();
				}

				@Override
				public String getName() {
					return "Extra Golems textures";
				}
			});
		}
	}

}
