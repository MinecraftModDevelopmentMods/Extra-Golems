package com.mcmoddev.golems.event;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mcmoddev.golems.render.GolemRenderType;
import com.mcmoddev.golems.render.GolemRenderer;
import com.mcmoddev.golems.screen.DispenserGolemScreen;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class EGClientModEvents {
    
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":setupClient");
  }
  
  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerContainerRenderers");
	ScreenManager.registerFactory(EGRegistry.DISPENSER_GOLEM, DispenserGolemScreen::new);
  }
  
  @SubscribeEvent
  public static void registerEntityRenderers(final RegistryEvent.Register<EntityType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEntityRenderers");
	RenderingRegistry.registerEntityRenderingHandler(EGRegistry.GOLEM, GolemRenderer::new);
  }
}
