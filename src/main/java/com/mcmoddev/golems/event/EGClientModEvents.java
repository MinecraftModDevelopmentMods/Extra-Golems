package com.mcmoddev.golems.event;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemRenderer;
import com.mcmoddev.golems.render.model.GolemModel;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class EGClientModEvents {
    
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":setupClient");
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
