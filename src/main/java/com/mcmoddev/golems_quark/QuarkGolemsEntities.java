package com.mcmoddev.golems_quark;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.mcmoddev.golems.entity.CoalGolem;
import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.entity.NetherBrickGolem;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemContainer.SwimMode;
import com.mcmoddev.golems.util.GolemDescription;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems_quark.entity.CaveCrystalGolem;
import com.mcmoddev.golems_quark.entity.ColorSlimeGolem;
import com.mcmoddev.golems_quark.entity.GlowshroomGolem;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.automation.module.ColorSlimeModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.module.CompressedBlocksModule;
import vazkii.quark.building.module.DuskboundBlocksModule;
import vazkii.quark.building.module.FramedGlassModule;
import vazkii.quark.building.module.IronPlatesModule;
import vazkii.quark.building.module.LitLampModule;
import vazkii.quark.building.module.MidoriModule;
import vazkii.quark.building.module.MoreBrickTypesModule;
import vazkii.quark.building.module.MoreStoneVariantsModule;
import vazkii.quark.building.module.QuiltedWoolModule;
import vazkii.quark.building.module.SoulSandstoneModule;
import vazkii.quark.building.module.SturdyStoneModule;
import vazkii.quark.world.module.BiotiteModule;
import vazkii.quark.world.module.NewStoneTypesModule;
import vazkii.quark.world.module.underground.BrimstoneUndergroundBiomeModule;
import vazkii.quark.world.module.underground.CaveCrystalUndergroundBiomeModule;
import vazkii.quark.world.module.underground.ElderPrismarineUndergroundBiomeModule;
import vazkii.quark.world.module.underground.GlowshroomUndergroundBiomeModule;
import vazkii.quark.world.module.underground.PermafrostUndergroundBiomeModule;

public final class QuarkGolemsEntities {
  
  public static final String QUARK = AddonLoader.QUARK_MODID;
  public static final String MODID = AddonLoader.QUARK_GOLEMS_MODID;
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();
  
  private QuarkGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   * Registers entity types that are only enabled when Quark is installed.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Extra Golems: Quark - initEntityTypes");
    
    final ITextComponent descLight = new TranslationTextComponent("entitytip.lights_area").mergeStyle(TextFormatting.GOLD);
        
    // quilted wool names
    final String[] quiltedWoolTypes = new String[DyeColor.values().length];
    for(int i = 0, l = quiltedWoolTypes.length; i < l; i++) {
      quiltedWoolTypes[i] = DyeColor.values()[i] + "_quilted_wool";
    }
    
    final int red = 0xb02525;
    final int purple = 0x4e276e;
    final int gold = 0xc4452b;
    final int darkBlue = 0x1f986e;
    
    // BASALT GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.BASALT_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(52.0D).setAttack(5.8D).setSpeed(0.28D).setKnockbackResist(0.8D)
		.immuneToFire().setDynamicTexture(QUARK, "polished_basalt").setVinesColor(g -> red).noVinesLighting().build(),
        "polished_basalt", "basalt_pillar", "chiseled_basalt_bricks", "basalt_pavement");
    // BIOTITE GOLEM
    final ResourceLocation eyesEnder = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/eyes/ender.png");
    softRegister(BiotiteModule.class, new GolemContainer.Builder(QuarkGolemNames.BIOTITE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(116.0D).setAttack(8.5D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setDynamicTexture(QUARK, "smooth_biotite").setEyesProvider(g -> eyesEnder).noEyesLighting()
        .setVinesColor(g -> purple).build(),
        "biotite_block", "chiseled_biotite_block", "smooth_biotite", "biotite_pillar");
    // BLAZE LANTERN GOLEM
    softRegister(CompressedBlocksModule.class, buildEnabledPredicate().and(m -> CompressedBlocksModule.enableBlazeLantern),
        new GolemContainer.Builder(QuarkGolemNames.BLAZELANTERN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(34.0D).setAttack(7.6D).setSpeed(0.26D).immuneToFire()
        .addDesc(new GolemDescription(descLight.copyRaw()))
        .setSwimMode(SwimMode.FLOAT).setLightLevel(15)
        .setDynamicTexture(QUARK, "blaze_lantern").setVinesColor(g -> gold).build(),
        "blaze_lantern");
    // BRIMSTONE GOLEM
    softRegister(BrimstoneUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.BRIMSTONE_GOLEM, NetherBrickGolem.class, NetherBrickGolem::new)
        .setModId(MODID).setHealth(25.0D).setAttack(6.5D).setSpeed(0.28D).setKnockbackResist(0.2D)
        .addSpecial(NetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
            new TranslationTextComponent("entitytip.lights_mobs_on_fire").mergeStyle(TextFormatting.RED))
        .immuneToFire().setDynamicTexture(QUARK, "brimstone").build(),
        "brimstone", "brimstone_bricks");
    // CAVE CRYSTAL GOLEM
    softRegister(CaveCrystalUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.CAVECRYSTAL_GOLEM, CaveCrystalGolem.class, CaveCrystalGolem::new)
        .setModId(MODID).setHealth(18.0D).setAttack(8.2D).setSpeed(0.29D).enableFallDamage()
        .addSpecial(CaveCrystalGolem.ALLOW_SPECIAL, true, "Whether this golem can glow",
            new TranslationTextComponent("entitytip.lights_area").mergeStyle(TextFormatting.LIGHT_PURPLE))
        .setLightLevel(11).setDynamicTexture(g -> ((CaveCrystalGolem)g).getTexture()).noVines().transparent().build(),
        "red_crystal", "orange_crystal", "yellow_crystal", "green_crystal", "blue_crystal", 
        "indigo_crystal", "violet_crystal", "white_crystal", "black_crystal");
    // CHARCOAL GOLEM
    softRegister(CompressedBlocksModule.class, buildEnabledPredicate().and(m -> CompressedBlocksModule.enableCharcoalBlock),
        new GolemContainer.Builder(QuarkGolemNames.CHARCOAL_GOLEM, CoalGolem.class, CoalGolem::new)
        .setModId(MODID).setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).setKnockbackResist(0.2D)
        .addSpecial(CoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness",
            new TranslationTextComponent("entitytip.blinds_creatures").mergeStyle(TextFormatting.GRAY))
        .addHealItem(Items.COAL, 0.25D).addHealItem(Items.CHARCOAL, 0.25D)
        .setDynamicTexture(QUARK, "charcoal_block").build(),
        "charcoal_block");
    // COLOR SLIME GOLEM
    softRegister(ColorSlimeModule.class, new GolemContainer.Builder(QuarkGolemNames.COLOR_SLIME_GOLEM, ColorSlimeGolem.class, ColorSlimeGolem::new)
        .setModId(MODID).setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).setKnockbackResist(0.35D)
        .addSpecial(ColorSlimeGolem.SPLITTING_CHILDREN, Integer.valueOf(2), "The number of mini-golems to spawn when this golem dies")
        .addSpecial(ColorSlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking",
            new TranslationTextComponent("entitytip.has_knockback").mergeStyle(TextFormatting.GREEN))
        .addSpecial(ColorSlimeGolem.KNOCKBACK, Double.valueOf(1.0412D), "Slime Golem knockback power (Higher Value = Further Knockback)")
        .addDesc(new GolemDescription(new TranslationTextComponent("entitytip.splits_upon_death").mergeStyle(TextFormatting.GREEN), 
            ColorSlimeGolem.SPLITTING_CHILDREN, c -> (Integer) c.get() > 0))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.ENTITY_SLIME_SQUISH).addHealItem(Items.SLIME_BALL, 0.25D)
        .setDynamicTexture(g -> ((ColorSlimeGolem)g).getTexture()).transparent().noVines().build(),
        "red_slime_block", "blue_slime_block", "cyan_slime_block", "magenta_slime_block", "yellow_slime_block");
    // DUSKBOUND GOLEM
    softRegister(DuskboundBlocksModule.class, new GolemContainer.Builder(QuarkGolemNames.DUSKBOUND_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(84.0D).setAttack(6.6D).setSpeed(0.26D).setKnockbackResist(0.6D)
        .setDynamicTexture(QUARK, "duskbound_block").setVinesColor(g -> purple).build(),
        "duskbound_block");
    // DUSKBOUND LAMP GOLEM
    softRegister(DuskboundBlocksModule.class, new GolemContainer.Builder(QuarkGolemNames.DUSKBOUNDLAMP_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(88.0D).setAttack(6.0D).setSpeed(0.26D).setLightLevel(15)
        .addDesc(new GolemDescription(descLight.copyRaw().mergeStyle(TextFormatting.LIGHT_PURPLE)))
        .setDynamicTexture(QUARK, "duskbound_lantern").setVinesColor(g -> purple).build(),
        "duskbound_lantern");
    // ELDER PRIMSARINE GOLEM
    softRegister(ElderPrismarineUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.ELDERPRISMARINE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(38.0D).setAttack(7.0D).setKnockbackResist(0.7D)
        .setSwimMode(SwimMode.SWIM).addHealItem(Items.PRISMARINE_SHARD, 0.25D)
        .setDynamicTexture(QUARK, "elder_prismarine_bricks").build(),
        "elder_prismarine", "elder_prismarine_bricks", "dark_elder_prismarine");
    // ELDER SEA LANTERN GOLEM
    softRegister(ElderPrismarineUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.ELDERSEALANTERN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(36.0D).setAttack(6.1D).setSpeed(0.26D).setKnockbackResist(0.9D)
        .addDesc(new GolemDescription(descLight.copyRaw().mergeStyle(TextFormatting.LIGHT_PURPLE)))
        .setSwimMode(SwimMode.SWIM).addHealItem(Items.PRISMARINE_SHARD, 0.25D).setLightLevel(15)
        .setDynamicTexture(QUARK, "elder_sea_lantern").build(),
        "elder_sea_lantern");
    // FRAMED GLASS GOLEM
    final ResourceLocation framedGlass = new ResourceLocation(MODID, "textures/entity/" + QuarkGolemNames.FRAMEDGLASS_GOLEM + ".png");
    softRegister(FramedGlassModule.class, new GolemContainer.Builder(QuarkGolemNames.FRAMEDGLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(16.0D).setAttack(8.5D).setSpeed(0.30D).enableFallDamage().setSound(SoundEvents.BLOCK_GLASS_STEP)
        .addHealItem(Items.GLASS, 0.25D).setStaticTexture(g -> framedGlass).noVines().build(),
        "framed_glass");
    // GLOWSHROOM GOLEM
    softRegister(GlowshroomUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.GLOWSHROOM_GOLEM, GlowshroomGolem.class, GlowshroomGolem::new)
        .setModId(MODID).setHealth(30.0D).setAttack(3.0D).setSpeed(0.30D)
        .addSpecial(GlowshroomGolem.FREQUENCY, Integer.valueOf(420), "Average number of ticks between planting glowshrooms")
        .addSpecial(GlowshroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant glowshrooms randomly",
            new TranslationTextComponent("entitytip.plants_shrooms").mergeStyle(TextFormatting.DARK_AQUA))
        .addSpecial(GlowshroomGolem.ALLOW_GLOWING, true, "Whether this golem can glow",
            new TranslationTextComponent("entitytip.lights_area").mergeStyle(TextFormatting.AQUA))
        .addSpecial(GlowshroomGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)", 
            new TranslationTextComponent("entitytip.heals").mergeStyle(TextFormatting.RED))
        .setDynamicTexture(QUARK, "glowshroom_stem").transparent().noVines().setLightLevel(14).build(),
        "glowshroom_block", "glowshroom_stem");
    // IRON PLATE GOLEM
    softRegister(IronPlatesModule.class, new GolemContainer.Builder(QuarkGolemNames.IRONPLATE_GOLEM, IronPlateGolem.class, IronPlateGolem::new)
        .setModId(MODID).setHealth(40.0D).setAttack(7.0D).setSpeed(0.26D).setKnockbackResist(1.0D)
        .setDynamicTexture(g -> ((IronPlateGolem)g).getTexture()).build(),
        "iron_plate", "rusty_iron_plate");
    // JASPER GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.JASPER_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(52.0D).setAttack(5.9D).setSpeed(0.28D).setKnockbackResist(0.8D)
        .setDynamicTexture(QUARK, "polished_jasper").build(),
        "polished_jasper", "jasper_pillar", "chiseled_jasper_bricks", "jasper_pavement");
    // LIMESTONE GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.LIMESTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(49.0D).setAttack(5.0D).setSpeed(0.27D).setKnockbackResist(0.8D)
        .setDynamicTexture(QUARK, "polished_limestone").build(),
        "polished_limestone", "limestone_pillar", "chiseled_limestone_bricks", "limestone_pavement");
    // MARBLE GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.MARBLE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(54.0D).setAttack(6.5D).setSpeed(0.28D).setKnockbackResist(0.8D)
        .setDynamicTexture(QUARK, "polished_marble").build(),
        "polished_marble", "marble_pillar", "chiseled_marble_bricks", "marble_pavement");
    // MIDORI GOLEM
    softRegister(MidoriModule.class, new GolemContainer.Builder(QuarkGolemNames.MIDORI_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(32.0D).setAttack(3.0D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setDynamicTexture(QUARK, "midori_pillar").build(),
        "midori_block", "midori_pillar");
    // PERMAFROST GOLEM
    softRegister(PermafrostUndergroundBiomeModule.class, new GolemContainer.Builder(QuarkGolemNames.PERMAFROST_GOLEM, PermafrostGolem.class, PermafrostGolem::new)
        .setModId(MODID).setHealth(42.0D).setAttack(4.0D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .addSpecial(PermafrostGolem.ALLOW_SPECIAL, Boolean.valueOf(true), "Whether this golem can apply slowness when attacking", 
            new TranslationTextComponent("entitytip.slows_creatures").mergeStyle(TextFormatting.AQUA))
        .setDynamicTexture(QUARK, "permafrost").setVinesColor(g -> darkBlue).build(),
        "permafrost", "permafrost_bricks");
    // QUILTED WOOL GOLEM
    softRegister(QuiltedWoolModule.class, new GolemContainer.Builder(QuarkGolemNames.QUILTEDWOOL_GOLEM, QuiltedWoolGolem.class, QuiltedWoolGolem::new)
        .setModId(MODID).setHealth(16.0D).setAttack(1.0D).setSpeed(0.295D).setKnockbackResist(0.2D)
        .setDynamicTexture(g -> ((QuiltedWoolGolem)g).getTexture()).noVines().build(),
        quiltedWoolTypes);
    // SLATE GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.SLATE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(50.0D).setAttack(5.8D).setSpeed(0.28D).setKnockbackResist(0.8D)
        .setDynamicTexture(QUARK, "polished_slate").build(),
        "polished_slate", "slate_pillar", "chiseled_slate_bricks", "slate_pavement");
    // SOUL SANDSTONE GOLEM
    softRegister(SoulSandstoneModule.class, new GolemContainer.Builder(QuarkGolemNames.SOULSANDSTONE_GOLEM, PermafrostGolem.class, PermafrostGolem::new)
        .setModId(MODID).setHealth(30.0D).setAttack(4.0D).setSpeed(0.28D)
        .addSpecial(PermafrostGolem.ALLOW_SPECIAL, Boolean.valueOf(true), "Whether this golem can apply slowness when attacking", 
            new TranslationTextComponent("entitytip.slows_creatures").mergeStyle(TextFormatting.DARK_GRAY))
        .setDynamicTexture(QUARK, "soul_sandstone").setVinesColor(g -> red).noVinesLighting().build(),
        "soul_sandstone", "soul_sandstone_bricks", "chiseled_soul_sandstone", "cut_soul_sandstone");
    // STURDY STONE GOLEM
    softRegister(SturdyStoneModule.class, new GolemContainer.Builder(QuarkGolemNames.STURDYSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(70.0D).setAttack(4.0D).setSpeed(0.27D).setKnockbackResist(1.0D)
        .setDynamicTexture("furnace_top").build(),
        "sturdy_stone");
    // TALLOW GOLEM
    softRegister(SturdyStoneModule.class, new GolemContainer.Builder(QuarkGolemNames.TALLOW_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(48.0D).setAttack(2.0D).setSpeed(0.27D).setDynamicTexture(QUARK, "tallow_block").build(),
        "tallow_block");
  }
  
  /**
   * Called when the InterModEnqueueEvent is sent to the main mod file. 
   * Used here to update some values after the Quark mod and its Modules are fully loaded.
   * @param event the event, not actually used here
   **/
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
    // go through each deferred container and add the correct blocks to their GolemContainer
    for(final DeferredContainer d : QuarkGolemsEntities.deferred) {
      final boolean enabled = d.enabled.test(d.module);
      d.container.setEnabled(enabled);
      addBlocks(d.container, AddonLoader.QUARK_MODID, d.blocks);
    }
    // add some quark blocks to existing golems
    // nether brick, magma brick, sandstone brick
    if(ModuleLoader.INSTANCE.isModuleEnabled(MoreBrickTypesModule.class)) {
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.NETHERBRICK_GOLEM)),
          AddonLoader.QUARK_MODID, "charred_nether_bricks");
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.MAGMA_GOLEM)),
          AddonLoader.QUARK_MODID, "magma_bricks");
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.SANDSTONE_GOLEM)),
          AddonLoader.QUARK_MODID, "sandstone_bricks");
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.REDSANDSTONE_GOLEM)),
          AddonLoader.QUARK_MODID, "red_sandstone_bricks");
    }
    // andesite, diorite, granite variants
    if(ModuleLoader.INSTANCE.isModuleEnabled(MoreStoneVariantsModule.class)) {
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.ANDESITE_GOLEM)),
          AddonLoader.QUARK_MODID, "chiseled_andesite_bricks", "andesite_pavement", "andesite_pillar");
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.DIORITE_GOLEM)),
          AddonLoader.QUARK_MODID, "chiseled_diorite_bricks", "diorite_pavement", "diorite_pillar");
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.GRANITE_GOLEM)),
          AddonLoader.QUARK_MODID, "chiseled_granite_bricks", "granite_pavement", "granite_pillar");
    }
    // lit redstone lamp
    if(ModuleLoader.INSTANCE.isModuleEnabled(LitLampModule.class)) {
      addBlocks(GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.REDSTONELAMP_GOLEM)),
          AddonLoader.QUARK_MODID, "lit_lamp");
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
  public static void addBlocks(final GolemContainer cont, final String modid, final String... blockNames) {
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
