package com.mcmoddev.golems.integration;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public final class AddonLoader {
  
  public static final String QUARK_MODID = "quark";
  public static final String QUARK_GOLEMS_MODID = "golems_quark";
  
  public static final String THERMAL_MODID = "thermal";
  public static final String THERMAL_GOLEMS_MODID = "golems_thermal"; 
  
  private AddonLoader() {}
  
  public static boolean isQuarkLoaded() { return ModList.get().isLoaded(QUARK_MODID); }
  
  public static boolean isThermalLoaded() { return ModList.get().isLoaded(THERMAL_MODID); }
  
  public static void initEntityTypes() {
    if(isQuarkLoaded()) {
      com.mcmoddev.golems_quark.QuarkGolemsEntities.initEntityTypes();
    }
    if(isThermalLoaded()) {
      com.mcmoddev.golems_thermal.ThermalGolemsEntities.initEntityTypes();
    }
  }
  
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
    if(isQuarkLoaded()) {
      com.mcmoddev.golems_quark.QuarkGolemsEntities.interModEnqueueEvent(event);
    }
    if(isThermalLoaded()) {
      com.mcmoddev.golems_thermal.ThermalGolemsEntities.interModEnqueueEvent(event);
    }
  }
  
  public static void setupEvent(final FMLCommonSetupEvent event) {
    if(isQuarkLoaded()) {
      com.mcmoddev.golems_quark.QuarkGolemsEntities.setupEvent(event);
    }
    if(isThermalLoaded()) {
      com.mcmoddev.golems_thermal.ThermalGolemsEntities.setupEvent(event);
    }
  }
  
}
