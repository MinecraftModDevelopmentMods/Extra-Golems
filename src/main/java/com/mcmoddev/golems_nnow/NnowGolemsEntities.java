package com.mcmoddev.golems_nnow;

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

public final class NnowGolemsEntities {
  public static final String NNOW = AddonLoader.NNOW_MODID;
  public static final String MODID = AddonLoader.NNOW_GOLEMS_MODID;
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();

  private NnowGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Extra Golems: NNOW - initEntityTypes");
    
    // ALUMINUM GOLEM
    register(GolemBuilders.aluminumGolem().setDynamicTexture(NNOW, "metals/aluminum_block")
        .build(), "aluminum_block");
    // BISMUTH GOLEM TODO
    
    // COPPER GOLEM
    register(GolemBuilders.copperGolem().setDynamicTexture(NNOW, "metals/copper_block")
        .build(), "copper_block");
    // IRIDIUM GOLEM TODO
    
    // LEAD GOLEM
    register(GolemBuilders.leadGolem().setDynamicTexture(NNOW, "metals/lead_block")
        .build(), "lead_block");
    // NICKEL GOLEM
    register(GolemBuilders.nickelGolem().setDynamicTexture(NNOW, "metals/nickel_block")
        .build(), "nickel_block");
    // OSMIUM GOLEM
    register(GolemBuilders.tinGolem().setDynamicTexture(NNOW, "metals/osmium_block")
        .build(), "osmium_block");
    // PLATINUM GOLEM
    register(GolemBuilders.platinumGolem().setDynamicTexture(NNOW, "metals/platinum_block")
        .build(), "platinum_block");
    // SILVER GOLEM
    register(GolemBuilders.silverGolem().setDynamicTexture(NNOW, "metals/silver_block")
        .build(), "silver_block");
    // TIN GOLEM
    register(GolemBuilders.tinGolem().setDynamicTexture(NNOW, "metals/tin_block")
        .build(), "tin_block");
    // URANIUM GOLEM
    register(GolemBuilders.uraniumGolem().setDynamicTexture(NNOW, "metals/uranium_block")
        .build(), "uranium_block");
    // ZINC GOLEM
    register(GolemBuilders.zincGolem().setDynamicTexture(NNOW, "metals/zinc_block")
        .build(), "zinc_block");
  }
  
  protected static void register(final GolemContainer cont, final String... blockNames) {
    // store the container for updating config later
    deferred.add(new DeferredContainer(cont, NNOW, blockNames));
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
