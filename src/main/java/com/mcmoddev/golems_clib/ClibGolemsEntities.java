package com.mcmoddev.golems_clib;

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
import net.minecraft.util.SoundEvents;
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
    // AMETHYST GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.BRASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(98.0D).setAttack(7.25D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/brass"))
        .build(), "brass_block");
    // BRASS GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.BRASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(85.0D).setAttack(6.75D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setSound(SoundEvents.BLOCK_METAL_STEP).setDynamicTexture(CLIB, "brass_block")
        .addBlocks(new ResourceLocation("forge", "storage_blocks/brass"))
        .build(), "brass_block");
    // BRONZE GOLEM
    register(GolemBuilders.bronzeGolem().setDynamicTexture(CLIB, "bronze_block")
        .build(), "bronze_block");
    // CHROMIUM GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.CHROMIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(114.0D).setAttack(8.5D).setKnockbackResist(0.9D)
        .setSound(SoundEvents.BLOCK_METAL_STEP).setDynamicTexture(CLIB, "chromium_block")
        .addBlocks(new ResourceLocation("forge", "storage_blocks/chromium"))
        .build(), "chromium_block");
    // COBALT GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.COBALT_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(85.0D).setAttack(6.5D).setSpeed(0.28D).setKnockbackResist(1.0D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/cobalt"))
        .setDynamicTexture(CLIB, "cobalt_block")
        .build(), "cobalt_block");
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
    // OPAL GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.OPAL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(65.0D).setAttack(6.5D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setSound(SoundEvents.BLOCK_GLASS_STEP).setDynamicTexture(CLIB, "opal_block")
        .addBlocks(new ResourceLocation("forge", "storage_blocks/opal"))
        .build(), "opal_block");
    // PLATINUM GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.PLATINUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(86.0D).setAttack(6.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/platinum"))
        .setDynamicTexture(CLIB, "platinum_block")
        .build(), "platinum_block");
    // RUBY GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.RUBY_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(76.0D).setAttack(6.25D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setSound(SoundEvents.BLOCK_GLASS_STEP).setDynamicTexture(CLIB, "ruby_block")
        .addBlocks(new ResourceLocation("forge", "storage_blocks/ruby"))
        .build(), "ruby_block");
    // SALTPETRE GOLEM TODO
    register(new GolemContainer.Builder(ClibGolemNames.SALTPETRE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(48.0D).setAttack(2.6D).setKnockbackResist(0.6D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/saltpetre"))
        .setDynamicTexture(CLIB, "saltpetre_block")
        .build(), "saltpetre_block");
    // SAPPHIRE GOLEM TODO
    register(new GolemContainer.Builder(ClibGolemNames.SAPPHIRE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(70.0D).setAttack(6.5D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setSound(SoundEvents.BLOCK_GLASS_STEP).setDynamicTexture(CLIB, "sapphire_block")
        .addBlocks(new ResourceLocation("forge", "storage_blocks/sapphire"))
        .build(), "sapphire_block");
    // SIGNALUM GOLEM
    register(GolemBuilders.signalumGolem().setDynamicTexture(CLIB, "signalum_block")
        .build(), "signalum_block");
    // SILVER GOLEM
    register(GolemBuilders.silverGolem().setDynamicTexture(CLIB, "silver_block")
        .build(), "silver_block");
    // STEEL GOLEM
    register(GolemBuilders.steelGolem().setDynamicTexture(CLIB, "steel_block")
        .build(), "steel_block");
    // SULFUR GOLEM
    register(GolemBuilders.sulfurGolem().setDynamicTexture(CLIB, "sulfur_block")
        .build(), "sulfur_block");
    // TIN GOLEM
    register(GolemBuilders.tinGolem().setDynamicTexture(CLIB, "tin_block")
        .build(), "tin_block");
    // TITANIUM GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.TITANIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(146.0D).setAttack(7.9D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/titanium"))
        .setDynamicTexture(CLIB, "titanium_block")
        .build(), "titanium_block");
    // TUNGSTEN GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.TUNGSTEN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(68.0D).setAttack(5.9D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/tungsten"))
        .setDynamicTexture(CLIB, "tungsten_block")
        .build(), "tungsten_block");
    // ZINC GOLEM
    register(new GolemContainer.Builder(ClibGolemNames.ZINC_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(52.0D).setAttack(3.75D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/zinc"))
        .setDynamicTexture(CLIB, "zinc_block")
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
