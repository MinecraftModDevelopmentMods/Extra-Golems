package com.mcmoddev.golems_immersiveengineering;

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

public final class IEGolemsEntities {
  public static final String IE = AddonLoader.IE_MODID;
  public static final String MODID = AddonLoader.IE_GOLEMS_MODID;
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();

  private IEGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Extra Golems: Immersive Engineering - initEntityTypes");
    
    // ALUMINUM GOLEM
    register(GolemBuilders.aluminumGolem().setDynamicTexture(IE, "metal/storage_aluminum")
        .build(), "aluminum_block");
    // COAL COKE
    register(GolemBuilders.coalCokeGolem().setDynamicTexture(IE, "stone_decoration/coke")
        .build(), "coal_coke_block");
    // CONSTANTAN GOLEM
    register(GolemBuilders.constantanGolem().setDynamicTexture(IE, "metal/storage_constantan")
        .build(), "constantan_block");
    // COPPER GOLEM
    register(GolemBuilders.copperGolem().setDynamicTexture(IE, "metal/storage_copper")
        .build(), "copper_block");
    // ELECTRUM GOLEM
    register(GolemBuilders.electrumGolem().setDynamicTexture(IE, "metal/storage_electrum")
        .build(), "electrum_block");
    // LEAD GOLEM
    register(GolemBuilders.leadGolem().setDynamicTexture(IE, "metal/storage_lead")
        .build(), "lead_block");
    // NICKEL GOLEM
    register(GolemBuilders.nickelGolem().setDynamicTexture(IE, "metal/storage_nickel")
        .build(), "nickel_block");
    // SILVER GOLEM
    register(GolemBuilders.silverGolem().setDynamicTexture(IE, "metal/storage_silver")
        .build(), "silver_block");
    // STEEL GOLEM
    register(GolemBuilders.steelGolem().setDynamicTexture(IE, "metal/storage_steel")
        .build(), "steel_block");
    // URANIUM GOLEM
    register(GolemBuilders.uraniumGolem().setDynamicTexture(IE, "metal/storage_uranium_side")
        .build(), "uranium_block");
  }
  
  protected static void register(final GolemContainer cont, final String... blockNames) {
    // store the container for updating config later
    deferred.add(new DeferredContainer(cont, IE, blockNames));
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
