package com.mcmoddev.golems.integration;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public final class AddonLoader {
  
  public static final String QUARK_MODID = "quark";
  public static final String QUARK_GOLEMS_MODID = "golems_quark";
  
  private AddonLoader() {}
  
  public static void initEntityTypes() {
    if(ModList.get().isLoaded(QUARK_MODID)) {
      com.mcmoddev.golems_quark.QuarkGolemsEntities.initEntityTypes();
    }
  }
  
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
    if(ModList.get().isLoaded(QUARK_MODID)) {
      com.mcmoddev.golems_quark.QuarkGolemsEntities.interModEnqueueEvent(event);
    }
  }
  
}
