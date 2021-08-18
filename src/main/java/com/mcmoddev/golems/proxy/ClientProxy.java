package com.mcmoddev.golems.proxy;

import com.mcmoddev.golems.event.EGClientEvents;
import com.mcmoddev.golems.event.EGClientModEvents;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ClientProxy extends CommonProxy {
  @Override
  public void registerEventHandlers() {
    super.registerEventHandlers();
    MinecraftForge.EVENT_BUS.register(EGClientEvents.class);
    FMLJavaModLoadingContext.get().getModEventBus().register(EGClientModEvents.class);
  }
}
