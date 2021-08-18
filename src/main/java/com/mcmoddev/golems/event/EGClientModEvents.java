package com.mcmoddev.golems.event;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mcmoddev.golems.render.GolemRenderType;
import com.mcmoddev.golems.render.GolemRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class EGClientModEvents {
    
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":setupClient");
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
  
  @SubscribeEvent
  public static void registerModels(final ModelRegistryEvent event) {
   // ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerModels");
    
  }
  
  @SubscribeEvent
  public static void registerEntityLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEntityLayers");
    event.registerLayerDefinition(GolemRenderer.GOLEM_MODEL_RESOURCE , GolemModel::createBodyLayer);
  }
  
  @SubscribeEvent
  public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEntityRenderers");
    event.registerEntityRenderer(EGRegistry.GOLEM, m -> (new GolemRenderer<GolemBase>(m)));
  }
}
