package com.mcmoddev.golems_quark;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.mcmoddev.golems.entity.CoalGolem;
import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.entity.NetherBrickGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemContainer.SwimMode;
import com.mcmoddev.golems.util.config.GolemDescription;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import com.mcmoddev.golems_quark.entity.CaveCrystalGolem;
import com.mcmoddev.golems_quark.entity.GenericGlowingGolem;
import com.mcmoddev.golems_quark.entity.IronPlateGolem;
import com.mcmoddev.golems_quark.entity.PermafrostGolem;
import com.mcmoddev.golems_quark.entity.QuiltedWoolGolem;
import com.mcmoddev.golems_quark.util.DeferredContainer;
import com.mcmoddev.golems_quark.util.QuarkGolemNames;

import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.module.CompressedBlocksModule;
import vazkii.quark.building.module.DuskboundBlocksModule;
import vazkii.quark.building.module.FramedGlassModule;
import vazkii.quark.building.module.IronPlatesModule;
import vazkii.quark.building.module.MidoriModule;
import vazkii.quark.building.module.QuiltedWoolModule;
import vazkii.quark.building.module.SoulSandstoneModule;
import vazkii.quark.building.module.SturdyStoneModule;
import vazkii.quark.world.module.BiotiteModule;
import vazkii.quark.world.module.NewStoneTypesModule;
import vazkii.quark.world.module.underground.BrimstoneUndergroundBiomeModule;
import vazkii.quark.world.module.underground.CaveCrystalUndergroundBiomeModule;
import vazkii.quark.world.module.underground.ElderPrismarineUndergroundBiomeModule;
import vazkii.quark.world.module.underground.PermafrostUndergroundBiomeModule;

public final class QuarkGolemsEntities {
  
  public static final String MODID = "golems_quark";
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();
  
  private QuarkGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   * Registers entity types that are only enabled when Quark is installed.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.info("Extra Golems: Quark - initEntityTypes");
    
    // Reusable description
    final GolemDescription descFireproof = new GolemDescription(new TranslationTextComponent("entitytip.is_fireproof").applyTextStyle(TextFormatting.GOLD));
    final GolemDescription descSwims = new GolemDescription(new TranslationTextComponent("entitytip.advanced_swim").applyTextStyle(TextFormatting.AQUA));
    
    // quilted wool names
    final String[] quiltedWoolTypes = new String[DyeColor.values().length];
    for(int i = 0, l = quiltedWoolTypes.length; i < l; i++) {
      quiltedWoolTypes[i] = DyeColor.values()[i] + "_quilted_wool";
    }
    // cave crystal names
    final String[] caveCrystalTypes = {
      "red_crystal", "orange_crystal", "yellow_crystal", "green_crystal", "blue_crystal", 
      "indigo_crystal", "violet_crystal", "white_crystal", "black_crystal"
    };
    
    // BASALT GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.BASALT_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(52.0D).setAttack(5.8D).setSpeed(0.28D).setKnockback(0.8D)
		.immuneToFire().addDesc(descFireproof).basicTexture().build(),
        "polished_basalt", "basalt_pillar", "chiseled_basalt_bricks", "basalt_pavement");
    // BIOTITE GOLEM
    softRegister(BiotiteModule.class, new GolemContainer.Builder(QuarkGolemNames.BIOTITE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(116.0D).setAttack(8.5D).setSpeed(0.28D).setKnockback(0.6D).basicTexture().build(),
        "biotite_block", "chiseled_biotite_block", "smooth_biotite", "biotite_pillar");
    // BLAZE LANTERN GOLEM
    softRegister(CompressedBlocksModule.class, buildEnabledPredicate().and(m -> CompressedBlocksModule.enableBlazeLantern),
        new GolemContainer.Builder(QuarkGolemNames.BLAZELANTERN_GOLEM, GenericGlowingGolem.class, GenericGlowingGolem::new)
        .setModId(MODID).setHealth(34.0D).setAttack(7.6D).setSpeed(0.26D).immuneToFire()
        .addSpecial(GenericGlowingGolem.ALLOW_SPECIAL, true, "Whether this golem can glow",
            new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.GOLD))
        .addDesc(descFireproof).setSwimMode(SwimMode.FLOAT).basicTexture().build(),
        "blaze_lantern");
    // BRIMSTONE GOLEM
    softRegister(BrimstoneUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.BRIMSTONE_GOLEM, NetherBrickGolem.class, NetherBrickGolem::new)
        .setModId(MODID).setHealth(25.0D).setAttack(6.5D).setSpeed(0.28D).setKnockback(0.2D)
        .addSpecial(NetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
            new TranslationTextComponent("entitytip.lights_mobs_on_fire").applyTextStyle(TextFormatting.RED))
        .immuneToFire().addDesc(descFireproof).basicTexture().build(),
        "brimstone", "brimstone_bricks");
    // CAVE CRYSTAL GOLEM
    softRegister(CaveCrystalUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.CAVECRYSTAL_GOLEM, CaveCrystalGolem.class, CaveCrystalGolem::new)
        .setModId(MODID).setHealth(18.0D).setAttack(8.2D).setSpeed(0.29D).basicTexture().enableFallDamage()
        .addSpecial(CaveCrystalGolem.ALLOW_SPECIAL, true, "Whether this golem can glow",
            new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.LIGHT_PURPLE))
        .build(),
        caveCrystalTypes);
    // CHARCOAL GOLEM
    softRegister(CompressedBlocksModule.class, buildEnabledPredicate().and(m -> CompressedBlocksModule.enableCharcoalBlock),
        new GolemContainer.Builder(QuarkGolemNames.CHARCOAL_GOLEM, CoalGolem.class, CoalGolem::new)
        .setModId(MODID).setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).setKnockback(0.2D).basicTexture()
        .addSpecial(CoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness",
            new TranslationTextComponent("entitytip.blinds_creatures").applyTextStyle(TextFormatting.GRAY))
        .addHealItem(Items.COAL, 0.25D).addHealItem(Items.CHARCOAL, 0.25D).build(),
        "charcoal_block");
    // DUSKBOUND GOLEM
    softRegister(DuskboundBlocksModule.class, new GolemContainer.Builder(QuarkGolemNames.DUSKBOUND_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(84.0D).setAttack(6.6D).setSpeed(0.26D).setKnockback(0.6D).basicTexture().build(),
        "duskbound_block");
    // DUSKBOUND LAMP GOLEM
    softRegister(DuskboundBlocksModule.class, new GolemContainer.Builder(QuarkGolemNames.DUSKBOUNDLAMP_GOLEM, GenericGlowingGolem.class, GenericGlowingGolem::new)
        .setModId(MODID).setHealth(88.0D).setAttack(6.0D).setSpeed(0.26D)
        .addSpecial(GenericGlowingGolem.ALLOW_SPECIAL, true, "Whether this golem can glow",
            new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.LIGHT_PURPLE))
        .basicTexture().build(),
        "duskbound_lantern");
    // ELDER PRIMSARINE GOLEM
    softRegister(ElderPrismarineUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.ELDERPRISMARINE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(38.0D).setAttack(7.0D).setKnockback(0.7D).basicTexture()
        .addDesc(descSwims).setSwimMode(SwimMode.SWIM).addHealItem(Items.PRISMARINE_SHARD, 0.25D).build(),
        "elder_prismarine", "elder_prismarine_bricks", "dark_elder_prismarine");
    // ELDER SEA LANTERN GOLEM
    softRegister(ElderPrismarineUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.ELDERSEALANTERN_GOLEM, GenericGlowingGolem.class, GenericGlowingGolem::new)
        .setModId(MODID).setHealth(36.0D).setAttack(6.1D).setSpeed(0.26D).setKnockback(0.9D)
        .addSpecial(GenericGlowingGolem.ALLOW_SPECIAL, true, "Whether this golem can glow",
            new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.LIGHT_PURPLE))
        .addDesc(descSwims).setSwimMode(SwimMode.SWIM).basicTexture()
        .addHealItem(Items.PRISMARINE_SHARD, 0.25D).build(),
        "elder_sea_lantern");
    // FRAMED GLASS GOLEM
    softRegister(FramedGlassModule.class, new GolemContainer.Builder(QuarkGolemNames.FRAMEDGLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(16.0D).setAttack(8.5D).setSpeed(0.30D).basicTexture().enableFallDamage()
        .setSound(SoundEvents.BLOCK_GLASS_STEP).build(),
        "framed_glass");
    // IRON PLATE GOLEM
    softRegister(IronPlatesModule.class, new GolemContainer.Builder(QuarkGolemNames.IRONPLATE_GOLEM, IronPlateGolem.class, IronPlateGolem::new)
        .setModId(MODID).setHealth(40.0D).setAttack(7.0D).setSpeed(0.26D).setKnockback(1.0D).basicTexture().build(),
        "iron_plate", "rusty_iron_plate");
    // JASPER GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.JASPER_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(52.0D).setAttack(5.9D).setSpeed(0.28D).setKnockback(0.8D).basicTexture().build(),
        "polished_jasper", "jasper_pillar", "chiseled_jasper_bricks", "jasper_pavement");
    // LIMESTONE GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.LIMESTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(49.0D).setAttack(5.0D).setSpeed(0.27D).setKnockback(0.8D).basicTexture().build(),
        "polished_limestone", "limestone_pillar", "chiseled_limestone_bricks", "limestone_pavement");
    // MARBLE GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.MARBLE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(54.0D).setAttack(6.5D).setSpeed(0.28D).setKnockback(0.8D).basicTexture().build(),
        "polished_marble", "marble_pillar", "chiseled_marble_bricks", "marble_pavement");
    // MIDORI GOLEM
    softRegister(MidoriModule.class, new GolemContainer.Builder(QuarkGolemNames.MIDORI_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(32.0D).setAttack(3.0D).setSpeed(0.28D).setKnockback(0.6D).basicTexture().build(),
        "midori_block", "midori_pillar");
    // PERMAFROST GOLEM
    softRegister(PermafrostUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.PERMAFROST_GOLEM, PermafrostGolem.class, PermafrostGolem::new)
        .setModId(MODID).setHealth(42.0D).setAttack(4.0D).setSpeed(0.28D).setKnockback(0.6D).basicTexture()
        .addSpecial(PermafrostGolem.ALLOW_SPECIAL, Boolean.valueOf(true), "Whether this golem can apply slowness when attacking", 
            new TranslationTextComponent("entitytip.slows_creatures").applyTextStyle(TextFormatting.AQUA))
        .build(),
        "permafrost", "permafrost_bricks");
    // QUILTED WOOL GOLEM
    softRegister(QuiltedWoolModule.class, new GolemContainer.Builder(QuarkGolemNames.QUILTEDWOOL_GOLEM, QuiltedWoolGolem.class, QuiltedWoolGolem::new)
        .setModId(MODID).setHealth(16.0D).setAttack(1.0D).setSpeed(0.295D).setKnockback(0.2D).basicTexture().build(),
        quiltedWoolTypes);
    // SLATE GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.SLATE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(50.0D).setAttack(5.8D).setSpeed(0.28D).setKnockback(0.8D).basicTexture().build(),
        "polished_slate", "slate_pillar", "chiseled_slate_bricks", "slate_pavement");
    // SOUL SANDSTONE GOLEM
    softRegister(SoulSandstoneModule.class, new GolemContainer.Builder(QuarkGolemNames.SOULSANDSTONE_GOLEM, PermafrostGolem.class, PermafrostGolem::new)
        .setModId(MODID).setHealth(30.0D).setAttack(4.0D).setSpeed(0.28D).basicTexture()
        .addSpecial(PermafrostGolem.ALLOW_SPECIAL, Boolean.valueOf(true), "Whether this golem can apply slowness when attacking", 
            new TranslationTextComponent("entitytip.slows_creatures").applyTextStyle(TextFormatting.DARK_GRAY))
        .build(),
        "soul_sandstone", "soul_sandstone_bricks", "chiseled_soul_sandstone", "cut_soul_sandstone");
    // STURDY STONE GOLEM
    softRegister(SturdyStoneModule.class, new GolemContainer.Builder(QuarkGolemNames.STURDYSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(70.0D).setAttack(4.0D).setSpeed(0.27D).setKnockback(1.0D).basicTexture().build(),
        "sturdy_stone");
  }
  
  /**
   * Called when the InterModEnqueueEvent is sent to the main mod file. 
   * Used here to update some values after the Quark mod and its Modules are fully loaded.
   * @param event the event
   **/
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
    for(final DeferredContainer d : QuarkGolemsEntities.deferred) {
      final boolean enabled = d.enabled.test(d.module);
      d.container.setEnabled(enabled);
      // try to add the blocks
      for(final String s : d.blocks) {
        // see if the block exists
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", s));
        // add that block as a building block for the golem
        if(block != null) {
          d.container.addBlocks(block);
        }
      }
    }
  }
  
  /**
   * Calls {@link #softRegister(Class, Predicate, GolemContainer, String...)} with
   * the result value of {@link #buildEnabledPredicate()}.
   * @param module the Quark Module that uses the blocks associated with this golem
   * @param cont the fully-built GolemContainer
   * @param blockNames names of blocks to use to build this golem. 
   * It is assumed that they are all in the "quark" namespace
   * @see #buildEnabledPredicate()
   **/
  public static void softRegister(final Class<? extends Module> module, final GolemContainer cont, 
      final String... blockNames) {
    softRegister(module, buildEnabledPredicate(), cont, blockNames);
  }  
  
  /**
   * Registers the GolemContainer to the GolemRegistrar.
   * Creates a DeferredContainer to update with blocks and config options later.
   * @param module the Quark Module that uses the blocks associated with this golem
   * @param pred a Predicate to determine whether the golem will be enabled
   * @param cont the fully-built GolemContainer
   * @param blockNames names of blocks to use to build this golem. 
   * It is assumed that they are all in the "quark" namespace
   **/
  public static void softRegister(final Class<? extends Module> module, 
      final Predicate<Class<? extends Module>> pred,
      final GolemContainer cont, final String... blockNames) {
    // store the container for updating config later
    deferred.add(new DeferredContainer(cont, module, blockNames, pred));
    // actually register the container
    GolemRegistrar.registerGolem(cont);
  }
  
  /**
   * Builds a very simple predicate to use when the golems "enabled"
   * property is updated later.
   * @return {@code m -> ModuleLoader.INSTANCE.isModuleEnabled(m)}
   **/
  private static Predicate<Class<? extends Module>> buildEnabledPredicate() {
    return m -> ModuleLoader.INSTANCE.isModuleEnabled(m);
  }
}
