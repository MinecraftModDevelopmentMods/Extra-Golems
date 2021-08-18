package com.mcmoddev.golems.event;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.gui.DispenserGolemScreen;
import com.mcmoddev.golems.render.GolemRenderType;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EGClientEvents {
  
  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerContainerRenderers");
    MenuScreens.register(EGRegistry.DISPENSER_GOLEM, DispenserGolemScreen::new);
  }
  
  @SubscribeEvent
  public static void addReloadListeners(final AddReloadListenerEvent event) {
    // reload render settings
    event.addListener(ExtraGolems.PROXY.GOLEM_RENDER_SETTINGS);
    // reload dynamic texture map
    event.addListener(new SimplePreparableReloadListener<ModelBakery>() {
      @Override
      protected ModelBakery prepare(ResourceManager arg0, ProfilerFiller arg1) { return null; }
      @Override
      protected void apply(ModelBakery arg0, ResourceManager arg1, ProfilerFiller arg2) {
        GolemRenderType.reloadDynamicTextureMap();
      }
    });
  }
}
