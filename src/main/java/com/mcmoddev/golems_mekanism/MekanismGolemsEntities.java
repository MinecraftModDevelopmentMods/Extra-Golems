package com.mcmoddev.golems_mekanism;

import java.util.ArrayList;
import java.util.List;

import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.integration.DeferredContainer;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemBuilders;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public final class MekanismGolemsEntities {
  public static final String MEK = AddonLoader.MEKANISM_MODID;
  public static final String MODID = AddonLoader.MEKANISM_GOLEMS_MODID;
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();

  private MekanismGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Extra Golems: Mekanism - initEntityTypes");
    
    // BRONZE GOLEM
    register(GolemBuilders.bronzeGolem().setDynamicTexture(MEK, "block_bronze")
        .build(), "block_bronze");
    // CHARCOAL GOLEM
    register(GolemBuilders.charcoalGolem().setDynamicTexture(MEK, "block_charcoal")
        .build(), "block_charcoal");
    // COMPRESSED CARBON TODO
    
    // COMPRESSED DIAMOND TODO
    
    // COMPRESSED OBSIDIAN TODO
    
    // COMPRESSED REDSTONE TODO
    
    // COPPER GOLEM
    register(GolemBuilders.copperGolem().setDynamicTexture(MEK, "block_copper")
        .build(), "block_copper");
    // FLOURITE GOLEM TODO
    
    // LEAD GOLEM
    register(GolemBuilders.leadGolem().setDynamicTexture(MEK, "block_lead")
        .build(), "block_lead");
    // OSMIUM GOLEM
    register(GolemBuilders.osmiumGolem().setDynamicTexture(MEK, "block_osmium")
        .build(), "block_osmium");
    // PLASTIC GOLEM TODO
    
    // REFINED GLOWSTONE TODO
    
    // REFINED OBSIDIAN TODO
    
    // SALT GOLEM TODO
    register(new GolemContainer.Builder(MekanismGolemNames.SALT_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(48.0D).setAttack(2.6D).setKnockbackResist(0.6D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/salt"))
        .setDynamicTexture(MEK, "block_salt")
        .build(), "block_salt");
    // STEEL GOLEM
    register(GolemBuilders.steelGolem().setDynamicTexture(MEK, "block_steel")
        .build(), "block_steel");
    // TIN GOLEM
    register(GolemBuilders.tinGolem().setDynamicTexture(MEK, "block_tin")
        .build(), "block_tin");
    // URANIUM GOLEM
    register(GolemBuilders.uraniumGolem().setDynamicTexture(MEK, "block_uranium")
        .build(), "block_uranium");
  }
  
  protected static void register(final GolemContainer cont, final String... blockNames) {
    // store the container for updating config later
    deferred.add(new DeferredContainer(cont, MEK, blockNames));
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
