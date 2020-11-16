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
        .build(), "storage_aluminum");
    // COAL COKE
    register(GolemBuilders.coalCokeGolem().setDynamicTexture(IE, "stone_decoration/coke")
        .build(), "coke");
    // CONCRETE GOLEM TODO
    
    // CONSTANTAN GOLEM
    register(GolemBuilders.constantanGolem().setDynamicTexture(IE, "metal/storage_constantan")
        .build(), "storage_constantan");
    // COPPER GOLEM
    register(GolemBuilders.copperGolem().setDynamicTexture(IE, "metal/storage_copper")
        .build(), "storage_copper");
    // COPPER COIL GOLEM TODO
    
    // CUSHION GOLEM TODO
    
    // ELECTRUM GOLEM
    register(GolemBuilders.electrumGolem().setDynamicTexture(IE, "metal/storage_electrum")
        .build(), "storage_electrum");
    // ELECTRUM COIL GOLEM TODO
   
    // HV COIL GOLEM TODO
    
    // INSULATING GLASS GOLEM TODO
    
    // LEAD GOLEM
    register(GolemBuilders.leadGolem().setDynamicTexture(IE, "metal/storage_lead")
        .build(), "storage_lead");
    // LEADED CONCRETE GOLEM TODO
    
    // NICKEL GOLEM
    register(GolemBuilders.nickelGolem().setDynamicTexture(IE, "metal/storage_nickel")
        .build(), "storage_nickel");
    // SILVER GOLEM
    register(GolemBuilders.silverGolem().setDynamicTexture(IE, "metal/storage_silver")
        .build(), "storage_silver");
    // STEEL GOLEM
    register(GolemBuilders.steelGolem().setDynamicTexture(IE, "metal/storage_steel")
        .build(), "storage_steel");
    // URANIUM GOLEM
    register(GolemBuilders.uraniumGolem().setDynamicTexture(IE, "metal/storage_uranium_side")
        .build(), "storage_uranium");
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
