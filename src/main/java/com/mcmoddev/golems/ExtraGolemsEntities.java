package com.mcmoddev.golems;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.entity.base.DefinedTextureGolem;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemDescription;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems.util.GolemContainer.SwimMode;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.Tags;

public final class ExtraGolemsEntities {
  
  private ExtraGolemsEntities() {}

  public static void initEntityTypes() {    
    // Frequently-used GolemDescription components
    final MutableComponent descHeals = new TranslatableComponent("entitytip.heals").withStyle(ChatFormatting.LIGHT_PURPLE);
    final MutableComponent descSplits = new TranslatableComponent("entitytip.splits_upon_death");
    final MutableComponent descResist = new TranslatableComponent("effect.minecraft.resistance").withStyle(ChatFormatting.DARK_GRAY);
    final MutableComponent descPower = new TranslatableComponent("entitytip.emits_redstone_signal").withStyle(ChatFormatting.RED);
    final MutableComponent descLight = new TranslatableComponent("entitytip.lights_area").withStyle(ChatFormatting.GOLD);
    final String comSplits = "The number of mini-golems to spawn when this golem dies";
    
    // Block Tag resource locations
    final ResourceLocation tagConcrete = new ResourceLocation(ExtraGolems.MODID, "concrete");
    final ResourceLocation tagSandstone = new ResourceLocation(ExtraGolems.MODID, "sandstone");
    final ResourceLocation tagRedSandstone = new ResourceLocation(ExtraGolems.MODID, "red_sandstone");
    final ResourceLocation tagPrismarine = new ResourceLocation(ExtraGolems.MODID, "prismarine");
    final ResourceLocation tagTerracotta = new ResourceLocation(ExtraGolems.MODID, "colored_terracotta");
    final ResourceLocation tagQuartz = new ResourceLocation(ExtraGolems.MODID, "quartz");
    final ResourceLocation tagDeadCoral = new ResourceLocation(ExtraGolems.MODID, "dead_coral_block");
    
    // vines colors
    final int blue = 0x005a75; // 23157
    final int red = 0xb02525; // 11543845
    final int aqua = 0x1f986e; // 2070638
    final int brown = 0x4e3d24; // 5127460
    
    // ANDESITE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.ANDESITE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(52.0D).setAttack(6.0D).setSpeed(0.26D).setKnockbackResist(0.8D).addBlocks(Blocks.POLISHED_ANDESITE)
        .setDynamicTexture("polished_andesite").build());
    // BASALT GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BASALT_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(58.0D).setAttack(6.8D).setKnockbackResist(0.8D).addBlocks(Blocks.POLISHED_BASALT)
        .immuneToFire().setDynamicTexture("polished_basalt_side").setVinesColor(g -> blue).build());
    // BEDROCK GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BEDROCK_GOLEM, BedrockGolem.class, BedrockGolem::new)
        .setHealth(999.0D).setAttack(32.0D).setKnockbackResist(1.0D).immuneToExplosions()
        .addDesc(
            new GolemDescription(new TranslatableComponent("entitytip.indestructible").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD)),
            new GolemDescription(new TranslatableComponent("tooltip.creative_only_item").withStyle(ChatFormatting.DARK_RED)))
        .setDynamicTexture("bedrock").noGolemBookEntry().build());
    // BLACKSTONE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BLACKSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(58.0D).setAttack(6.8D).setKnockbackResist(0.8D)
        .addBlocks(Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
        .immuneToFire().setDynamicTexture("polished_blackstone").setVinesColor(g -> blue).hasCustomRender().build());
    // BONE GOLEM
    final ResourceLocation skeleton = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.BONE_GOLEM + "_skeleton.png");
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(40.0D).setAttack(9.5D).setSpeed(0.30D).addBlocks(Blocks.BONE_BLOCK)
        .setStaticTexture(g -> skeleton).noVines()
        .enableFallDamage().addHealItem(Items.BONE, 0.25D).addHealItem(Items.BONE_MEAL, 0.08D).build());
    // BOOKSHELF GOLEM
    final ResourceLocation eyesGlasses = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/eyes/glasses.png");
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BOOKSHELF_GOLEM, BookshelfGolem.class, BookshelfGolem::new)
        .setHealth(28.0D).setAttack(1.5D).setSpeed(0.29D).addBlocks(Blocks.BOOKSHELF)
        .addSpecial(BookshelfGolem.ALLOW_SPECIAL, true, "Whether this golem can give itself potion effects",
            new TranslatableComponent("entitytip.grants_self_potion_effects").withStyle(ChatFormatting.LIGHT_PURPLE))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.WOOD_STEP)
        .addHealItem(Items.BOOK, 0.25D).addHealItem(Items.OAK_PLANKS, 0.25D)
        .setDynamicTexture("bookshelf").setEyesProvider(g -> eyesGlasses).build());
    // CLAY GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CLAY_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(20.0D).setAttack(2.0D).addBlocks(Blocks.CLAY).setSound(SoundEvents.GRAVEL_STEP)
        .addHealItem(Items.CLAY_BALL, 0.25D).setDynamicTexture("clay").build());
    // COAL GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.COAL_GOLEM, CoalGolem.class, CoalGolem::new)
        .setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).setKnockbackResist(0.2D).addBlocks(Blocks.COAL_BLOCK)
        .addSpecial(CoalGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict blindness",
            new TranslatableComponent("entitytip.blinds_creatures").withStyle(ChatFormatting.GRAY))
        .addHealItem(Items.COAL, 0.25D).addHealItem(Items.CHARCOAL, 0.25D)
        .setDynamicTexture("coal_block").build());
    // CONCRETE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CONCRETE_GOLEM, ConcreteGolem.class, ConcreteGolem::new)
        .setHealth(47.0D).setAttack(5.0D).setSpeed(0.26D).addBlocks(tagConcrete)
        .addSpecial(ConcreteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes", descResist)
        .setDynamicTexture(g -> ((ConcreteGolem)g).getTexture()).build());
    // CORAL GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CORAL_GOLEM, CoralGolem.class, CoralGolem::new)
        .setHealth(38.0D).setAttack(6.0D).setSpeed(0.29D).setSwimMode(SwimMode.SWIM).setSound(SoundEvents.CORAL_BLOCK_STEP)
        .addSpecial(CoralGolem.ALLOW_HEALING, true, "Whether this golem can occasionally heal when wet", descHeals)
        .addSpecial(CoralGolem.DRY_TIMER, 425, "Number of ticks golem can stay out of water before drying out")
        .addBlocks(BlockTags.CORAL_BLOCKS).addBlocks(tagDeadCoral)
        .setDynamicTexture(g -> ((CoralGolem)g).getTexture()).build());
    // CRAFTING GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CRAFTING_GOLEM, CraftingGolem.class, CraftingGolem::new)
        .setHealth(24.0D).setAttack(2.0D).setSpeed(0.29D).addBlocks(Blocks.CRAFTING_TABLE)
        .addSpecial(CraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid",
            new TranslatableComponent("entitytip.click_open_crafting").withStyle(ChatFormatting.BLUE))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.WOOD_STEP)
        .addHealItem(Items.OAK_PLANKS, 0.25D).setDynamicTexture("crafting_table_front").noVines().build());
    // CRIMSON STEM GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CRIMSONSTEM_GOLEM, CrimsonStemGolem.class, CrimsonStemGolem::new)
        .setHealth(24.0D).setAttack(3.0D).setSpeed(0.289D).setKnockbackResist(0.2D).addBlocks(BlockTags.CRIMSON_STEMS)
        .addSpecial(CrimsonStemGolem.FREQUENCY, Integer.valueOf(520), "Average number of ticks between planting fungus")
        .addSpecial(CrimsonStemGolem.ALLOW_SPECIAL, true, "Whether this golem can plant crimson fungus", 
            new TranslatableComponent("entitytip.plants_x", new TranslatableComponent("block.minecraft.crimson_fungus")).withStyle(ChatFormatting.DARK_RED))
        .setDynamicTexture("crimson_stem").setVinesColor(g -> blue).immuneToFire().build());
    // CRYING OBSIDIAN GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CRYINGOBSIDIAN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(106.0D).setAttack(18.9D).setSpeed(0.23D).setKnockbackResist(0.8D).addBlocks(Blocks.CRYING_OBSIDIAN)
        .immuneToFire().immuneToExplosions().setDynamicTexture("crying_obsidian").noVines().noLighting().build());
    // DIAMOND GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.DIAMOND_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(220.0D).setAttack(20.0D).setKnockbackResist(0.8D).addBlocks(Blocks.DIAMOND_BLOCK)
        .addHealItem(Items.DIAMOND, 0.25D).setDynamicTexture("diamond_block").setVinesColor(g -> aqua).build());
    // DIORITE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.DIORITE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(50.0D).setAttack(6.0D).setSpeed(0.25D).setKnockbackResist(0.8D).addBlocks(Blocks.POLISHED_DIORITE)
        .setDynamicTexture("polished_diorite").build());
    // DISPENSER GOLEM
    final ResourceLocation dispenser = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.DISPENSER_GOLEM + ".png");
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.DISPENSER_GOLEM, DispenserGolem.class, DispenserGolem::new)
        .setHealth(68.0D).addSpecial(DispenserGolem.ALLOW_SPECIAL, true, "Whether the golem can shoot arrows",
            new TranslatableComponent("entitytip.shoots_arrows").withStyle(ChatFormatting.LIGHT_PURPLE))
        .addSpecial(DispenserGolem.ARROW_DAMAGE, Double.valueOf(4.25D), "Base amount of damage dealt per arrow")
        .addSpecial(DispenserGolem.ARROW_SPEED, 28, "Number of ticks between shooting arrows")
        .addDesc(new GolemDescription(new TranslatableComponent("entitytip.click_refill").withStyle(ChatFormatting.GRAY),
            DispenserGolem.ALLOW_SPECIAL))
        .addBlocks(Blocks.DISPENSER).addHealItem(Items.COBBLESTONE, 0.25D).setStaticTexture(g -> dispenser).build());
    // EMERALD GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.EMERALD_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(190.0D).setAttack(18.0D).setKnockbackResist(0.8D).addBlocks(Blocks.EMERALD_BLOCK)
        .addHealItem(Items.EMERALD, 0.25D).setDynamicTexture("emerald_block").build());
    // ENDSTONE GOLEM
    final ResourceLocation eyesEnder = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/eyes/ender.png");
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.ENDSTONE_GOLEM, EndstoneGolem.class, EndstoneGolem::new)
        .setHealth(50.0D).setAttack(8.0D).setSpeed(0.26D).setKnockbackResist(0.3D).addBlocks(Blocks.END_STONE, Blocks.END_STONE_BRICKS)
        .addSpecial(EndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water")
        .addSpecial(EndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport",
            new TranslatableComponent("entitytip.can_teleport").withStyle(ChatFormatting.DARK_AQUA))
        .setDynamicTexture("end_stone").setEyesProvider(g -> eyesEnder).noEyesLighting().build());
    // FURNACE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.FURNACE_GOLEM, FurnaceGolem.class, FurnaceGolem::new)
        .setHealth(88.0D).setAttack(6.5D).setSpeed(0.24D).setKnockbackResist(1.0D).addBlocks(Blocks.FURNACE)
        .addDesc(new GolemDescription(new TranslatableComponent("entitytip.use_fuel").withStyle(ChatFormatting.GRAY)))
        .addSpecial(FurnaceGolem.FUEL_FACTOR, 10, "Number of ticks between using fuel points").immuneToFire()
        .addHealItem(Items.COBBLESTONE, 0.25D).setStaticTexture(g -> ((FurnaceGolem)g).getTexture())
        .noLighting(g -> ((FurnaceGolem)g).hasFuel()).build());
    // GLASS GOLEM
    final ResourceLocation glass = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.GLASS_GOLEM + ".png");
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(8.0D).setAttack(13.0D).setSpeed(0.30D).addBlocks(Blocks.GLASS).enableFallDamage()
        .setSound(SoundEvents.GLASS_STEP).setStaticTexture(g -> glass).noVines().build());
    // GLOWSTONE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GLOWSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(8.0D).setAttack(12.0D).setSpeed(0.26D).enableFallDamage().addBlocks(Blocks.GLOWSTONE)
        .addDesc(new GolemDescription(descLight.plainCopy().withStyle(ChatFormatting.RED)))
        .immuneToFire().setLightLevel(15).setSwimMode(SwimMode.FLOAT)
        .setSound(SoundEvents.GLASS_STEP).addHealItem(Items.GLOWSTONE_DUST, 0.25D)
        .setDynamicTexture("glowstone").setVinesColor(g -> red).build());
    // GOLD GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GOLD_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(80.0D).setAttack(8.0D).setSpeed(0.21D).setKnockbackResist(1.0D).addBlocks(Blocks.GOLD_BLOCK)
        .addHealItem(Items.GOLD_INGOT, 0.25D).addHealItem(Items.GOLD_NUGGET, 0.025D).setDynamicTexture("gold_block").build());
    // GRANITE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GRANITE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(58.0D).setAttack(5.2D).setSpeed(0.25D).setKnockbackResist(0.8D).addBlocks(Blocks.POLISHED_GRANITE)
        .setDynamicTexture("polished_granite").build());
    // HONEY GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.HONEY_GOLEM, HoneyGolem.class, HoneyGolem::new)
        .setHealth(42.0D).setAttack(1.0D).setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.SLIME_BLOCK_STEP)
        .addBlocks(Blocks.HONEY_BLOCK).addHealItem(Items.HONEY_BOTTLE, 0.25D).addHealItem(Items.HONEYCOMB, 0.25D)
        .addSpecial(HoneyGolem.ALLOW_HONEY, Boolean.valueOf(true), "Whether this golem applies honey effect to mobs", 
            new TranslatableComponent("entitytip.sticky").withStyle(ChatFormatting.GOLD))
        .addSpecial(HoneyGolem.SPLITTING_CHILDREN, Integer.valueOf(2), comSplits)
        .addDesc(new GolemDescription(descSplits.plainCopy().withStyle(ChatFormatting.GOLD), 
            HoneyGolem.SPLITTING_CHILDREN, c -> (Integer) c.get() > 0))
        .setDynamicTexture("honey_block_top").transparent().noVines().build());
    // HONEYCOMB GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.HONEYCOMB_GOLEM, HoneycombGolem.class, HoneycombGolem::new)
        .setHealth(68.0D).setAttack(2.0D).setSpeed(0.27D).setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.CORAL_BLOCK_STEP)
        .addBlocks(Blocks.HONEYCOMB_BLOCK).addHealItem(Items.HONEYCOMB, 0.25D)
        .addSpecial(HoneycombGolem.SUMMON_BEE_CHANCE, Integer.valueOf(30), "Percent chance to summon a bee when attacked [0,100]")
        .addDesc(new GolemDescription(new TranslatableComponent("entitytip.summons_bees").withStyle(ChatFormatting.GOLD), 
            HoneycombGolem.SUMMON_BEE_CHANCE, c -> (Integer) c.get() > 0))
        .setDynamicTexture("honeycomb_block").noVines().build());
    // ICE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.ICE_GOLEM, IceGolem.class, IceGolem::new)
        .setHealth(18.0D).setAttack(6.0D).setSpeed(0.27D).addBlocks(BlockTags.ICE)
        .addSpecial(IceGolem.AOE, Integer.valueOf(3), "Radial distance at which this golem can freeze / cool liquids (0=disable)")
        .addSpecial(IceGolem.FROST, false, "When true, this golem places frosted (temporary) ice")
        .addDesc(new GolemDescription(new TranslatableComponent("entitytip.freezes_blocks").withStyle(ChatFormatting.AQUA), IceGolem.AOE,
            c -> (Integer) c.get() > 0))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.GLASS_STEP)
        .setDynamicTexture("ice").transparent().noVines().build());
    // KELP GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.KELP_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(34.0D).setAttack(2.0D).addBlocks(Blocks.DRIED_KELP_BLOCK)
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.GRASS_STEP)
        .addHealItem(Items.KELP, 0.35D).addHealItem(Items.DRIED_KELP, 0.25D)
        .setDynamicTexture("dried_kelp_top").setVinesColor(g -> brown).build());
    // LAPIS GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.LAPIS_GOLEM, LapisGolem.class, LapisGolem::new)
        .setHealth(50.0D).setAttack(1.5D).setSpeed(0.285D).addBlocks(Blocks.LAPIS_BLOCK)
        .addSpecial(LapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects",
            new TranslatableComponent("entitytip.attacks_use_potion_effects").withStyle(ChatFormatting.LIGHT_PURPLE))
        .addHealItem(Items.LAPIS_LAZULI, 0.25D).setDynamicTexture("lapis_block")
        .hasCustomRender().build());
    // LEAF GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.LEAF_GOLEM, LeafGolem.class, LeafGolem::new)
        .setHealth(6.0D).setAttack(0.5D).setSpeed(0.31D).setKnockbackResist(0.0D).addBlocks(BlockTags.LEAVES)
        .addSpecial(LeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself",
            new TranslatableComponent("effect.minecraft.regeneration").withStyle(ChatFormatting.DARK_GREEN).append(" ")
                .append(new TranslatableComponent("enchantment.level.1").withStyle(ChatFormatting.DARK_GREEN)))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.GRASS_STEP)
        .setDynamicTexture("oak_leaves").setTextureColor(g -> ((LeafGolem)g).getColor())
        .setVinesColor(g -> brown).build());
    // MAGMA GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MAGMA_GOLEM, MagmaGolem.class, MagmaGolem::new)
        .setHealth(46.0D).setAttack(4.5D).setSpeed(0.28D).addBlocks(Blocks.MAGMA_BLOCK)
        .addSpecial(MagmaGolem.MELT_DELAY, Integer.valueOf(240),
            "Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
        .addSpecial(MagmaGolem.ALLOW_WATER_DAMAGE, true, "When true, water will hurt this golem")
        .addSpecial(MagmaGolem.ALLOW_LAVA_SPECIAL, false, "Whether this golem can slowly melt cobblestone",
            new TranslatableComponent("entitytip.slowly_melts", new TranslatableComponent("block.minecraft.cobblestone"))
                .withStyle(ChatFormatting.RED))
        .addSpecial(MagmaGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
            new TranslatableComponent("entitytip.lights_mobs_on_fire").withStyle(ChatFormatting.GOLD))
        .addSpecial(MagmaGolem.SPLITTING_CHILDREN, Integer.valueOf(2), comSplits)
        .addDesc(new GolemDescription(descSplits.plainCopy().withStyle(ChatFormatting.RED), 
            MagmaGolem.SPLITTING_CHILDREN, c -> (Integer) c.get() > 0))
        .immuneToFire().addHealItem(Items.MAGMA_CREAM, 0.25D)
        .setDynamicTexture("magma").setVinesColor(g -> 0xb02525).noVinesLighting().noLighting().build());
    // MELON GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MELON_GOLEM, MelonGolem.class, MelonGolem::new)
        .setHealth(18.0D).setAttack(1.5D).setSpeed(0.265D).addBlocks(Blocks.MELON)
        .addSpecial(MelonGolem.ALLOW_HEALING, true, "Whether this golem can occasionally heal", descHeals)
        .addSpecial(MelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly",
            new TranslatableComponent("entitytip.plants_flowers", new TranslatableComponent("tile.flower1.name"))
                .withStyle(ChatFormatting.GREEN))
        .addSpecial(MelonGolem.FREQUENCY, Integer.valueOf(440), "Average number of ticks between planting flowers")
        .setSwimMode(SwimMode.FLOAT).addHealItem(Items.MELON_SLICE, 0.25D).addHealItem(Items.GLISTERING_MELON_SLICE, 0.35D)
        .setDynamicTexture("melon_side").build());
    // MUSHROOM GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MUSHROOM_GOLEM, MushroomGolem.class, MushroomGolem::new)
        .setHealth(30.0D).setAttack(3.0D).setSpeed(0.30D).addBlocks(Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK)
        .addSpecial(MushroomGolem.FREQUENCY, Integer.valueOf(620), "Average number of ticks between planting mushrooms")
        .addSpecial(MushroomGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)", descHeals)
        .addSpecial(MushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly",
            new TranslatableComponent("entitytip.plants_shrooms").withStyle(ChatFormatting.DARK_GREEN))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.GRASS_STEP)
        .addHealItem(Items.RED_MUSHROOM, 0.25D).addHealItem(Items.BROWN_MUSHROOM, 0.25D)
        .setDynamicTexture("mushroom_stem").hasCustomRender().build());
    // NETHER BRICK GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.NETHERBRICK_GOLEM, NetherBrickGolem.class, NetherBrickGolem::new)
        .setHealth(25.0D).setAttack(6.5D).setSpeed(0.28D).setKnockbackResist(0.2D)
        .addBlocks(Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS, Blocks.CHISELED_NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS)
        .addSpecial(NetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
            new TranslatableComponent("entitytip.lights_mobs_on_fire").withStyle(ChatFormatting.RED))
        .immuneToFire().addHealItem(Items.NETHER_BRICK, 0.25D)
        .setDynamicTexture("nether_bricks").setVinesColor(g -> red).noVinesLighting().build());
    // NETHERITE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.NETHERITE_GOLEM, NetheriteGolem.class, NetheriteGolem::new)
        .setHealth(298.0D).setAttack(28.0D).setKnockbackResist(1.0D).addBlocks(Blocks.NETHERITE_BLOCK)
        .addSpecial(NetheriteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes", descResist)
        .immuneToFire().immuneToExplosions().addHealItem(Items.NETHERITE_INGOT, 0.25D)
        .setDynamicTexture("netherite_block").noVines().build());
    // NETHER WART GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.NETHERWART_GOLEM, NetherWartGolem.class, NetherWartGolem::new)
        .setHealth(22.0D).setAttack(1.5D).setSpeed(0.26D).addBlocks(Blocks.NETHER_WART_BLOCK)
        .addSpecial(NetherWartGolem.FREQUENCY, Integer.valueOf(880), "Average number of ticks between planting nether wart (if enabled)")
        .addSpecial(NetherWartGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)", descHeals)
        .addSpecial(NetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly",
            new TranslatableComponent("entitytip.plants_x", new TranslatableComponent("block.minecraft.nether_wart")).withStyle(ChatFormatting.RED))
        .immuneToFire().setSound(SoundEvents.WOOD_STEP).setSwimMode(SwimMode.FLOAT)
        .addHealItem(Items.NETHER_WART, 0.25D).setDynamicTexture("nether_wart_block").build());
    // OBSIDIAN GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.OBSIDIAN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(120.0D).setAttack(18.0D).setSpeed(0.23D).setKnockbackResist(0.8D).addBlocks(Blocks.OBSIDIAN)
        .immuneToFire().immuneToExplosions().setDynamicTexture("obsidian").build());
    // PRISMARINE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.PRISMARINE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(34.0D).setAttack(8.0D).setKnockbackResist(0.7D).addBlocks(tagPrismarine)
        .setSwimMode(SwimMode.SWIM).addHealItem(Items.PRISMARINE_SHARD, 0.25D)
        .setDynamicTexture("prismarine_bricks").setVinesColor(g -> aqua).build());
    // PURPUR GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.PURPUR_GOLEM, PurpurGolem.class, PurpurGolem::new)
        .setHealth(82.0D).setAttack(3.0D).setSpeed(0.26D).setKnockbackResist(0.3D).addBlocks(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)
        .addSpecial(PurpurGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport",
            new TranslatableComponent("entitytip.can_teleport").withStyle(ChatFormatting.LIGHT_PURPLE))
        .setDynamicTexture("purpur_pillar").build());
    // QUARTZ GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.QUARTZ_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(85.0D).setAttack(8.5D).setSpeed(0.28D).setKnockbackResist(0.6D).addBlocks(tagQuartz)
        .setSound(SoundEvents.GLASS_STEP).addHealItem(Items.QUARTZ, 0.25D).setDynamicTexture("quartz_block_side").build());
    // RED SANDSTONE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSANDSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).setKnockbackResist(0.6D).addBlocks(tagRedSandstone)
        .setDynamicTexture("red_sandstone").addHealItem(Items.RED_SAND, 0.25D).build());
    // REDSTONE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(18.0D).setAttack(2.0D).setSpeed(0.26D).setKnockbackResist(0.1D).addBlocks(Blocks.REDSTONE_BLOCK)
        .addDesc(new GolemDescription(descPower.plainCopy().withStyle(ChatFormatting.RED)))
        .setPowerLevel(15).addHealItem(Items.REDSTONE, 0.25D).setDynamicTexture("redstone_block").noLighting().build());
    // REDSTONE LAMP GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSTONELAMP_GOLEM, RedstoneLampGolem.class, RedstoneLampGolem::new)
        .setHealth(28.0D).setAttack(6.0D).setSpeed(0.26D).addBlocks(Blocks.REDSTONE_LAMP)
        .addSpecial(RedstoneLampGolem.ALLOW_SPECIAL, true, "Whether this golem can light up the area",
            new TranslatableComponent("entitytip.lights_area_toggle").withStyle(ChatFormatting.GOLD))
        .enableFallDamage().setSound(SoundEvents.GLASS_STEP).addHealItem(Items.REDSTONE, 0.25D)
        .addHealItem(Items.GLOWSTONE_DUST, 0.25D).setDynamicTexture(g -> ((RedstoneLampGolem)g).getTexture()).build());
    // SANDSTONE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SANDSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).setKnockbackResist(0.6D).addBlocks(tagSandstone)
        .addHealItem(Items.SAND, 0.25D).setDynamicTexture("sandstone").build());
    // SEA LANTERN GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SEALANTERN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(34.0D).setAttack(6.0D).setSpeed(0.26D).setKnockbackResist(0.9D).addBlocks(Blocks.SEA_LANTERN)
        .addDesc(new GolemDescription(descLight.plainCopy().withStyle(ChatFormatting.GOLD)))
        .setLightLevel(15).setSound(SoundEvents.GLASS_STEP).setSwimMode(SwimMode.SWIM)
        .addHealItem(Items.PRISMARINE_SHARD, 0.25D).addHealItem(Items.PRISMARINE_CRYSTALS, 0.25D)
        .setDynamicTexture("sea_lantern").build());
    // SHROOMLIGHT GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SHROOMLIGHT_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(40.0D).setAttack(3.0D).setSpeed(0.3D).addBlocks(Blocks.SHROOMLIGHT)
        .addDesc(new GolemDescription(descLight.plainCopy().withStyle(ChatFormatting.RED)))
        .immuneToFire().setLightLevel(15).setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.SHROOMLIGHT_STEP)
        .setDynamicTexture("shroomlight").setVinesColor(g -> red).build());
    // SLIME GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SLIME_GOLEM, SlimeGolem.class, SlimeGolem::new)
        .setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).setKnockbackResist(0.35D).addBlocks(Blocks.SLIME_BLOCK)
        .addSpecial(SlimeGolem.SPLITTING_CHILDREN, Integer.valueOf(2), comSplits)
        .addSpecial(SlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking",
            new TranslatableComponent("entitytip.has_knockback").withStyle(ChatFormatting.GREEN))
        .addSpecial(SlimeGolem.KNOCKBACK, Double.valueOf(0.92D), "Slime Golem knockback power (Higher Value = Bigger Knockback)")
        .addDesc(new GolemDescription(descSplits.plainCopy().withStyle(ChatFormatting.GREEN), 
            SlimeGolem.SPLITTING_CHILDREN, c -> (Integer) c.get() > 0))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.SLIME_SQUISH).addHealItem(Items.SLIME_BALL, 0.25D)
        .setDynamicTexture("slime_block").transparent().noVines().build());
    // SMOOTH STONE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SMOOTHSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(60.0D).setAttack(5.4D).setSpeed(0.27D).setKnockbackResist(0.6D).addBlocks(Blocks.SMOOTH_STONE)
        .setDynamicTexture("smooth_stone").build());
    // SPONGE GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SPONGE_GOLEM, SpongeGolem.class, SpongeGolem::new)
        .setHealth(20.0D).setAttack(1.5D).addBlocks(Blocks.SPONGE, Blocks.WET_SPONGE)
        .addSpecial(SpongeGolem.RANGE, Integer.valueOf(5), "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
        .addSpecial(SpongeGolem.INTERVAL, Integer.valueOf(10), "Number of ticks between each water-check; increase to reduce lag")
        .addSpecial(SpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water",
            new TranslatableComponent("entitytip.absorbs_water").withStyle(ChatFormatting.GOLD))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.WOOL_STEP)
        .setDynamicTexture("sponge").build());
    // STAINED GLASS GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STAINEDGLASS_GOLEM, StainedGlassGolem.class, StainedGlassGolem::new)
        .setHealth(9.0D).setAttack(12.0D).setSpeed(0.29D).addBlocks(Tags.Blocks.STAINED_GLASS).enableFallDamage()
        .setSound(SoundEvents.GLASS_STEP).setDynamicTexture(g -> ((StainedGlassGolem)g).getTexture())
        .transparent().noVines().build());
    // STAINED TERRACOTTA GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STAINEDTERRACOTTA_GOLEM, StainedTerracottaGolem.class, StainedTerracottaGolem::new)
        .setHealth(42.0D).setAttack(4.0D).setSpeed(0.22D).setKnockbackResist(0.6D).addBlocks(tagTerracotta)
        .setDynamicTexture(g -> ((StainedTerracottaGolem)g).getTexture()).build());
    // STRAW GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STRAW_GOLEM, StrawGolem.class, StrawGolem::new)
        .setHealth(10.0D).setAttack(1.0D).setSpeed(0.32D).setKnockbackResist(0.0D).addBlocks(Blocks.HAY_BLOCK)
        .addSpecial(StrawGolem.SPECIAL_FREQ, Integer.valueOf(460), "Minimum number of ticks between crop-boosts")
        .addSpecial(StrawGolem.ALLOW_SPECIAL, true, "Whether this golem can speed up crop growth",
            new TranslatableComponent("entitytip.grows_crops").withStyle(ChatFormatting.GREEN))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.GRASS_STEP).addHealItem(Items.WHEAT, 0.25D)
        .setDynamicTexture("hay_block_side").build());
    // TERRACOTTA GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.TERRACOTTA_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(42.0D).setAttack(4.0D).setSpeed(0.208D).setKnockbackResist(0.6D).addBlocks(Blocks.TERRACOTTA).setDynamicTexture("terracotta").build());
    // TEXTURED GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.TEXTURED_GOLEM, DefinedTextureGolem.class, DefinedTextureGolem::new)
        .setHealth(76.0D).setAttack(6.0D).noGolemBookEntry()
        .setDynamicTexture(g -> ((DefinedTextureGolem)g).getTexture())
        .setTextureColor(g -> ((DefinedTextureGolem)g).getColor())
        .setVinesProvider(g -> ((DefinedTextureGolem)g).getVines())
        .setVinesColor(g -> ((DefinedTextureGolem)g).getVinesColor()).build());
    // TNT GOLEM
    final ResourceLocation eyesCreeper = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/eyes/creeper.png");
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.TNT_GOLEM, TNTGolem.class, TNTGolem::new)
        .setHealth(14.0D).setAttack(2.5D).setSpeed(0.26D).addBlocks(Blocks.TNT)
        .addSpecial(TNTGolem.ALLOW_SPECIAL, true, "Whether this golem can explode when fighting or dying",
            new TranslatableComponent("entitytip.explodes").withStyle(ChatFormatting.RED))
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.GRAVEL_STEP).addHealItem(Items.GUNPOWDER, 0.25D)
        .addHealItem(Items.SAND, 0.25D).setDynamicTexture("tnt_side").setEyesProvider(g -> eyesCreeper).noEyesLighting().build());
    // WARPED STEM GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.WARPEDSTEM_GOLEM, WarpedStemGolem.class, WarpedStemGolem::new)
        .setHealth(24.0D).setAttack(3.0D).setSpeed(0.289D).setKnockbackResist(0.2D).addBlocks(BlockTags.WARPED_STEMS)
        .addSpecial(WarpedStemGolem.FREQUENCY, Integer.valueOf(520), "Average number of ticks between planting fungus")
        .addSpecial(WarpedStemGolem.ALLOW_SPECIAL, true, "Whether this golem can plant warped fungus", 
            new TranslatableComponent("entitytip.plants_x", new TranslatableComponent("block.minecraft.warped_fungus")).withStyle(ChatFormatting.BLUE))
        .immuneToFire().setDynamicTexture("warped_stem").setVinesColor(g -> blue).build());
    // WOODEN GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.WOODEN_GOLEM, WoodenGolem.class, WoodenGolem::new)
        .setHealth(20.0D).setAttack(3.0D).setSpeed(0.298D).setKnockbackResist(0.2D).addBlocks(BlockTags.LOGS_THAT_BURN)
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.WOOD_STEP).addHealItem(Items.STICK, 0.1D)
        .setDynamicTexture(g -> ((WoodenGolem)g).getTexture()).build());
    // WOOL GOLEM
    GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.WOOL_GOLEM, WoolGolem.class, WoolGolem::new)
        .setHealth(10.0D).setAttack(1.0D).setSpeed(0.295D).setKnockbackResist(0.2D).addBlocks(BlockTags.WOOL)
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.WOOL_STEP)
        .addHealItem(Items.STRING, 0.1D).setDynamicTexture("white_wool").noVines().hasCustomRender().build());
  }
}
