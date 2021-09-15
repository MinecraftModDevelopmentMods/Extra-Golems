package com.mcmoddev.golems.integration;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public final class AddonLoader {
  
  public static final String QUARK_MODID = "quark";
  public static final String QUARK_GOLEMS_MODID = "golems_quark";
  
  public static final String THERMAL_MODID = "thermal";
  public static final String THERMAL_GOLEMS_MODID = "golems_thermal";
  
  public static final String MEKANISM_MODID = "mekanism";
  public static final String MEKANISM_GOLEMS_MODID = "golems_mekanism";
  
  public static final String IE_MODID = "immersiveengineering";
  public static final String IE_GOLEMS_MODID = "golems_ie";
  
  public static final String CLIB_MODID = "clib";
  public static final String CLIB_GOLEMS_MODID = "golems_clib";
  
  public static final String NNOW_MODID = "nnow";
  public static final String NNOW_GOLEMS_MODID = "golems_nnow"; 
  
  private AddonLoader() {}
  
  public static boolean isQuarkLoaded() { return ModList.get().isLoaded(QUARK_MODID); }
  public static boolean isThermalLoaded() { return ModList.get().isLoaded(THERMAL_MODID); }
  public static boolean isMekanismLoaded() { return ModList.get().isLoaded(MEKANISM_MODID); }
  public static boolean isIELoaded() { return ModList.get().isLoaded(IE_MODID); }
  public static boolean isClibLoaded() { return ModList.get().isLoaded(CLIB_MODID); }
  public static boolean isNNOWLoaded() { return ModList.get().isLoaded(NNOW_MODID); }
  
  public static void initEntityTypes() {
    
  }
  
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
    
  }
  
  public static void setupEvent(final FMLCommonSetupEvent event) {
    
  }
  
}
