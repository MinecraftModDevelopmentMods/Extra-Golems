package com.mcmoddev.golems.proxy;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.event.EGClientEvents;
import com.mcmoddev.golems.event.EGClientModEvents;
import com.mcmoddev.golems.render.GolemRenderType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ClientProxy extends CommonProxy {
  @Override
  public void registerEventHandlers() {
    super.registerEventHandlers();
    MinecraftForge.EVENT_BUS.register(EGClientEvents.class);
    FMLJavaModLoadingContext.get().getModEventBus().register(EGClientModEvents.class);
    addResources();
  }
  
  private void addResources() {
    ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    if(resourceManager instanceof ReloadableResourceManager) {
      // reload render settings
      ((ReloadableResourceManager)resourceManager).registerReloadListener(ExtraGolems.PROXY.GOLEM_RENDER_SETTINGS);
      // reload dynamic texture map
      ((ReloadableResourceManager)resourceManager).registerReloadListener(new SimplePreparableReloadListener<ModelBakery>() {
        @Override
        protected ModelBakery prepare(ResourceManager arg0, ProfilerFiller arg1) { return null; }
        @Override
        protected void apply(ModelBakery arg0, ResourceManager arg1, ProfilerFiller arg2) {
          GolemRenderType.reloadDynamicTextureMap();
        }
      });
    }
  }
}
