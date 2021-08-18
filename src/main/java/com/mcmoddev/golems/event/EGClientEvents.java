package com.mcmoddev.golems.event;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.screen.DispenserGolemScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EGClientEvents {
  
  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerContainerRenderers");
    MenuScreens.register(EGRegistry.DISPENSER_GOLEM, DispenserGolemScreen::new);
  }
}
