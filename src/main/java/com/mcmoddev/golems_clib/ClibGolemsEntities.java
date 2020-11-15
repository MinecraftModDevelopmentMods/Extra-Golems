package com.mcmoddev.golems_clib;

import java.util.ArrayList;
import java.util.List;

import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.integration.DeferredContainer;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemBuilders;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public final class ClibGolemsEntities {
  public static final String CLIB = AddonLoader.CLIB_MODID;
  public static final String MODID = AddonLoader.CLIB_GOLEMS_MODID;
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();

  private ClibGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Extra Golems: CLib - initEntityTypes");
    
    // ALUMINUM GOLEM
    register(GolemBuilders.aluminumGolem().setDynamicTexture(CLIB, "aluminum_block")
        .build(), "aluminum_block");
    // AMETHYST GOLEM TODO
    
    // BRONZE GOLEM
    register(GolemBuilders.bronzeGolem().setDynamicTexture(CLIB, "bronze_block")
        .build(), "bronze_block");
    // COBALT GOLEM TODO
    
    // COPPER GOLEM
    register(GolemBuilders.copperGolem().setDynamicTexture(CLIB, "copper_block")
        .build(), "copper_block");
    // ELECTRUM GOLEM
    register(GolemBuilders.electrumGolem().setDynamicTexture(CLIB, "electrum_block")
        .build(), "electrum_block");
    // ENDERIUM GOLEM
    register(GolemBuilders.enderiumGolem().setDynamicTexture(CLIB, "enderium_block")
        .build(), "enderium_block");
    // INVAR GOLEM
    register(GolemBuilders.invarGolem().setDynamicTexture(CLIB, "invar_block")
        .build(), "invar_block");
    // LEAD GOLEM
    register(GolemBuilders.leadGolem().setDynamicTexture(CLIB, "lead_block")
        .build(), "lead_block");
    // LUMIUM GOLEM
    register(GolemBuilders.lumiumGolem().setDynamicTexture(CLIB, "lumium_block")
        .build(), "lumium_block");
    // NICKEL GOLEM
    register(GolemBuilders.nickelGolem().setDynamicTexture(CLIB, "nickel_block")
        .build(), "nickel_block");
    // OPAL GOLEM TODO
    
    // PLATINUM GOLEM
    register(GolemBuilders.platinumGolem().setDynamicTexture(CLIB, "platinum_block")
        .build(), "platinum_block");
    // RUBBER GOLEM? TODO
    
    // RUBY GOLEM TODO
    
    // SALTPETRE GOLEM TODO
    
    // SAPPHIRE GOLEM TODO
    
    // SIGNALUM GOLEM
    register(GolemBuilders.signalumGolem().setDynamicTexture(CLIB, "signalum_block")
        .build(), "signalum_block");
    // SILVER GOLEM
    register(GolemBuilders.silverGolem().setDynamicTexture(CLIB, "silver_block")
        .build(), "silver_block");
    // STEEL GOLEM
    register(GolemBuilders.steelGolem().setDynamicTexture(CLIB, "steel_block")
        .build(), "steel_block");
    // SULFUR GOLEM TODO
    
    // TIN GOLEM
    register(GolemBuilders.tinGolem().setDynamicTexture(CLIB, "tin_block")
        .build(), "tin_block");
    // TITANIUM GOLEM TODO
    
    // TUNGSTEN GOLEM TODO
    
    // URANIUM GOLEM
    register(GolemBuilders.uraniumGolem().setDynamicTexture(CLIB, "uranium_block")
        .build(), "uranium_block");
    // ZINC GOLEM
    register(GolemBuilders.zincGolem().setDynamicTexture(CLIB, "zinc_block")
        .build(), "zinc_block");
  }
  
  protected static void register(final GolemContainer cont, final String... blockNames) {
    // store the container for updating config later
    deferred.add(new DeferredContainer(cont, CLIB, blockNames));
    // actually register the container
    GolemRegistrar.registerGolem(cont);
  }
  
  
  /**
   * Called when the InterModEnqueueEvent is sent to the main mod file. 
   * @param event the event, not actually used here
   **/
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) { }

  /**
   * Called when the FMLCommonSetupEvent is sent to the main mod file. 
   * @param event the event, not actually used here
   **/
  public static void setupEvent(FMLCommonSetupEvent event) {
    for(final DeferredContainer d : deferred) {
      d.addBlocks();
    }
  }
}
