package com.mcmoddev.golems.event;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.render.GolemRenderType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;

public class EGClientEvents {
  
  public static void addResources() {
    IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    if(resourceManager instanceof IReloadableResourceManager) {
      // reload golem render settings
      // ((ReloadableResourceManager)resourceManager).registerReloadListener(ExtraGolems.GOLEM_RENDER_SETTINGS);
      // reload dynamic texture map
      ((IReloadableResourceManager)resourceManager).addReloadListener(new ReloadListener<ModelBakery>() {
        @Override
        protected ModelBakery prepare(IResourceManager arg0, IProfiler arg1) { return null; }
        @Override
        protected void apply(ModelBakery arg0, IResourceManager arg1, IProfiler arg2) {
          GolemRenderType.reloadDynamicTextureMap();
        }
        @Override
        public String getSimpleName() { return "Extra Golems textures"; }
      });
    }
  }
  
}
