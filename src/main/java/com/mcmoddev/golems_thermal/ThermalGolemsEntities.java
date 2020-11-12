package com.mcmoddev.golems_thermal;

import java.util.ArrayList;
import java.util.List;

import com.mcmoddev.golems.entity.CoalGolem;
import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.integration.DeferredContainer;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemContainer.SwimMode;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems_thermal.entity.ElectrumGolem;
import com.mcmoddev.golems_thermal.entity.EnderiumGolem;
import com.mcmoddev.golems_thermal.entity.RockwoolGolem;
import com.mcmoddev.golems_thermal.entity.RubberGolem;

import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ThermalGolemsEntities {
  
  public static final String THERMAL = AddonLoader.THERMAL_MODID;
  public static final String MODID = AddonLoader.THERMAL_GOLEMS_MODID;
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();


  private ThermalGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   * Registers entity types that are only enabled when Thermal Series is installed.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Extra Golems: Thermal - initEntityTypes");
    
    // quilted wool names
    final String[] rockwoolTypes = new String[DyeColor.values().length];
    for(int i = 0, l = rockwoolTypes.length; i < l; i++) {
      rockwoolTypes[i] = DyeColor.values()[i] + "_rockwool";
    }
    
    // APATITE GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.APATITE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/apatite_block")
        .build(), "apatite_block");
    // BRONZE GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.BRONZE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/bronze_block")
        .build(), "bronze_block");
    // CHARCOAL GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.CHARCOAL_GOLEM, CoalGolem.class, CoalGolem::new)
        .setModId(MODID).setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).setKnockbackResist(0.2D)
        .addSpecial(CoalGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict blindness",
            new TranslationTextComponent("entitytip.blinds_creatures").mergeStyle(TextFormatting.GRAY))
        .addHealItem(Items.COAL, 0.25D).addHealItem(Items.CHARCOAL, 0.25D)
        .setDynamicTexture(THERMAL, "storage/charcoal_block")
        .build(), "charcoal_block");
    // CINNABAR GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.CINNABAR_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/cinnabar_block")
        .build(), "cinnabar_block");
    // COAL COKE GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.COALCOKE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D)
        .setDynamicTexture(THERMAL, "storage/coal_coke_block")
        .build(), "coal_coke_block");
    // CONSTANTAN GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.CONSTANTAN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/constantan_block")
        .build(), "constantan_block");
    // COPPER GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.COPPER_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/copper_block")
        .build(), "copper_block");
    // CURED RUBBER GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.CUREDRUBBER_GOLEM, RubberGolem.class, RubberGolem::new)
        .setModId(MODID).setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).setKnockbackResist(0.35D)
        .addSpecial(RubberGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking",
            new TranslationTextComponent("entitytip.has_knockback").mergeStyle(TextFormatting.GREEN))
        .addSpecial(RubberGolem.KNOCKBACK, Double.valueOf(1.026D), "Rubber Golem knockback power (Higher Value = Further Knockback)")
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP)
        .setDynamicTexture(THERMAL, "storage/cured_rubber_block")
        .build(), "cured_rubber_block");
    // ELECTRUM GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.ELECTRUM_GOLEM, ElectrumGolem.class, ElectrumGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addSpecial(ElectrumGolem.IMMUNE_TO_MAGIC, true, "Whether the golem is immune to magic damage", 
            new TranslationTextComponent("entitytip.immune_to_magic").mergeStyle(TextFormatting.AQUA))
        .setDynamicTexture(THERMAL, "storage/electrum_block")
        .build(), "electrum_block");
    // ENDERIUM GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.ENDERIUM_GOLEM, EnderiumGolem.class, EnderiumGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addSpecial(EnderiumGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport",
            new TranslationTextComponent("entitytip.can_teleport").mergeStyle(TextFormatting.DARK_AQUA))
        .setDynamicTexture(THERMAL, "storage/enderium_block")
        .build(), "enderium_block");
    // ENDERIUM GLASS GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.ENDERIUMGLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D)
        .enableFallDamage().setSound(SoundEvents.BLOCK_GLASS_STEP)
        .setDynamicTexture(THERMAL, "glass/enderium_glass").noVines() // TODO static texture
        .build(), "enderium_glass");
    // GUNPOWDER GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.GUNPOWDER_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D)
        .setDynamicTexture(THERMAL, "storage/gunpowder_block")
        .build(), "gunpowder_block");
    // HARDENED GLASS GOLEM (obsidian_glass)
    register(new GolemContainer.Builder(ThermalGolemNames.HARDENEDGLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_GLASS_STEP)
        .setDynamicTexture(THERMAL, "glass/obsidian_glass").noVines() // TODO static texture
        .build(), "obsidian_glass");
    // INVAR GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.INVAR_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/invar_block")
        .build(), "invar_block");
    // LEAD GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.LEAD_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/lead_block")
        .build(), "lead_block");
    // LUMIUM GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.LUMIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP).setLightLevel(15)
        .setDynamicTexture(THERMAL, "storage/lumium_block")
        .build(), "lumium_block");
    // LUMIUM GLASS GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.LUMIUMGLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).enableFallDamage()
        .setLightLevel(15).setSound(SoundEvents.BLOCK_GLASS_STEP)
        .setDynamicTexture(THERMAL, "glass/lumium_glass").noVines() // TODO static texture
        .build(), "lumium_glass");
    // NICKEL GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.NICKEL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/nickel_block")
        .build(), "nickel_block");
    // NITER GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.NITER_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/niter_block")
        .build(), "niter_block");
    // RICH SLAG GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.RICHSLAG_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D)
        .setDynamicTexture(THERMAL, "storage/rich_slag_block")
        .build(), "rich_slag_block");
    // ROCKWOOL GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.ROCKWOOL_GOLEM, RockwoolGolem.class, RockwoolGolem::new)
        .setModId(MODID).setHealth(28.0D).setAttack(1.5D).setSpeed(0.285D).setKnockbackResist(0.2D)
        .immuneToFire().immuneToExplosions().setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_WOOL_STEP)
        .addHealItem(Items.STRING, 0.1D).setDynamicTexture("white_wool").noVines().hasCustomRender()
        .build(), rockwoolTypes);
    // ROSIN GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.ROSIN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/rosin_block_side")
        .build(), "rosin_block");
    // RUBBER GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.RUBBER_GOLEM, RubberGolem.class, RubberGolem::new)
        .setModId(MODID).setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).setKnockbackResist(0.35D)
        .addSpecial(RubberGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking",
            new TranslationTextComponent("entitytip.has_knockback").mergeStyle(TextFormatting.GREEN))
        .addSpecial(RubberGolem.KNOCKBACK, Double.valueOf(1.016D), "Rubber Golem knockback power (Higher Value = Further Knockback)")
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP)
        .setDynamicTexture(THERMAL, "storage/rubber_block")
        .build(), "rubber_block");
    // SAWDUST GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.SAWDUST_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/sawdust_block")
        .build(), "sawdust_block");
    // SIGNALUM GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.SIGNALUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/signalum_block")
        .build(), "signalum_block");
    // SIGNALUM GLASS GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.SIGNALUMGLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D)
        .enableFallDamage().setSound(SoundEvents.BLOCK_GLASS_STEP)
        .setDynamicTexture(THERMAL, "glass/signalum_glass").noVines() // TODO static texture
        .build(), "signalum_glass");
    // SLAG GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.SLAG_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D)
        .setDynamicTexture(THERMAL, "storage/slag_block")
        .build(), "slag_block");
    // SULFUR GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.SULFUR_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D)
        .setDynamicTexture(THERMAL, "storage/sulfur_block")
        .build(), "sulfur_block");
    // TIN GOLEM
    register(new GolemContainer.Builder(ThermalGolemNames.TIN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(90.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(THERMAL, "storage/tin_block")
        .build(), "tin_block");
  }
  
  protected static void register(final GolemContainer cont, final String... blockNames) {
    // store the container for updating config later
    deferred.add(new DeferredContainer(cont, THERMAL, blockNames));
    // actually register the container
    GolemRegistrar.registerGolem(cont);
  }
  
  
  /**
   * Called when the InterModEnqueueEvent is sent to the main mod file. 
   * @param event the event, not actually used here
   **/
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
    
  }

  /**
   * Called when the FMLCommonSetupEvent is sent to the main mod file. 
   * Used here to update some values after the Thermal series are fully loaded.
   * @param event the event, not actually used here
   **/
  public static void setupEvent(FMLCommonSetupEvent event) {
    for(final DeferredContainer d : deferred) {
      d.addBlocks();
    }
  }
 
  /**
   * Given a GolemContainer and a collection of block names, this method
   * adds those blocks as valid building material for the given golem.
   * If the blocks do not exist, nothing happens.
   * @param cont the GolemContainer to modify
   * @param modid the mod id of the blocks
   * @param blockNames all of the blocks to add
   **/
  protected static void addBlocks(final GolemContainer cont, final String modid, final String... blockNames) {
    if(null == cont) return;
    // add each block from the list of given names
    for(final String s : blockNames) {
      // see if the block exists
      final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(modid, s));
      // add that block as a building block for the golem
      if(block != null) {
        cont.addBlocks(block);
      }
    }
  }
}
