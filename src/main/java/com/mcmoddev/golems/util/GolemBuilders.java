package com.mcmoddev.golems.util;

import com.mcmoddev.golems.entity.CoalGolem;
import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.entity.modded.ElectrumGolem;
import com.mcmoddev.golems.entity.modded.EnderiumGolem;
import com.mcmoddev.golems.entity.modded.RubberGolem;
import com.mcmoddev.golems.util.GolemContainer.SwimMode;

import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public final class GolemBuilders {
  
  public static final String ALUMINUM_GOLEM = "golem_aluminum"; // clib, nnow, ie
  public static final String BRASS_GOLEM = "golem_brass"; // clib
  public static final String BRONZE_GOLEM = "golem_bronze"; // clib, thermal
  public static final String CHARCOAL_GOLEM = "golem_charcoal"; // quark, thermal
  public static final String COALCOKE_GOLEM = "golem_coal_coke"; // thermal, ie
  public static final String COBALT_GOLEM = "golem_cobalt"; // clib
  public static final String CONSTANTAN_GOLEM = "golem_constantan"; // thermal, ie
  public static final String COPPER_GOLEM = "golem_copper"; // clib, mek, thermal, nnow, ie
  public static final String ELECTRUM_GOLEM = "golem_electrum"; // clib, thermal, ie
  public static final String ENDERIUM_GOLEM = "golem_enderium"; // clib, thermal
  public static final String INVAR_GOLEM = "golem_invar"; // clib, thermal
  public static final String LEAD_GOLEM = "golem_lead"; // clib, thermal, nnow, ie
  public static final String LUMIUM_GOLEM = "golem_lumium"; // clib, thermal
  public static final String NICKEL_GOLEM = "golem_nickel"; // clib, nnow, thermal, ie
  public static final String OSMIUM_GOLEM = "golem_osmium"; // nnow, mek
  public static final String PLATINUM_GOLEM = "golem_platinum"; // clib, nnow
  public static final String RUBBER_GOLEM = "golem_rubber"; // thermal, clib?
  public static final String SIGNALUM_GOLEM = "golem_signalum"; // clib, thermal
  public static final String SILVER_GOLEM = "golem_silver"; // clib, nnow, ie
  public static final String STEEL_GOLEM = "golem_steel"; // clib, mek, ie
  public static final String TIN_GOLEM = "golem_tin"; // clib, nnow, mek, thermal
  public static final String URANIUM_GOLEM = "golem_uranium"; // clib, nnow, ie
  public static final String ZINC_GOLEM = "golem_zinc"; // clib, nnow
  
//  public static void init(final boolean quark, final boolean thermal, final boolean mek, 
//      final boolean ie, final boolean clib, final boolean nnow) {
//    ResourceLocation aluminum = null, brass = null, bronze = null, charcoal = null, 
//        cobalt = null, constantan = null, coalCoke = null, copper = null,
//        electrum = null, enderium = null, invar = null, lead = null, lumium = null,
//        nickel = null, osmium = null, platinum = null, rubber = null, signalum = null,
//        silver = null, steel = null, tin = null, uranium = null, zinc = null;
//    if(clib) {
//      aluminum = new ResourceLocation(CLIB, "aluminum_block");
//      brass = new ResourceLocation(CLIB, "brass_block");
//      bronze = new ResourceLocation(CLIB, "bronze_block");
//      cobalt = new ResourceLocation(CLIB, "cobalt_block");
//      copper = new ResourceLocation(CLIB, "copper_block");
//      electrum = new ResourceLocation(CLIB, "electrum_block");
//      enderium = new ResourceLocation(CLIB, "enderium_block");
//      invar = new ResourceLocation(CLIB, "invar_block");
//      lead = new ResourceLocation(CLIB, "lead_block");
//      lumium = new ResourceLocation(CLIB, "lumium_block");
//      nickel = new ResourceLocation(CLIB, "nickel_block");
//      platinum = new ResourceLocation(CLIB, "platinum_block");
//      signalum = new ResourceLocation(CLIB, "signalum_block");
//      silver = new ResourceLocation(CLIB, "silver_block");
//      steel = new ResourceLocation(CLIB, "steel_block");
//      tin = new ResourceLocation(CLIB, "tin_block");
//      uranium = new ResourceLocation(CLIB, "uranium_block");
//      zinc = new ResourceLocation(CLIB, "zinc_block");
//    }
//    if(nnow) {
//      aluminum = new ResourceLocation(NNOW, "metals/aluminum_block");
//      copper = new ResourceLocation(NNOW, "metals/copper_block");
//      lead = new ResourceLocation(NNOW, "metals/lead_block");
//      nickel = new ResourceLocation(NNOW, "metals/nickel_block");
//      osmium = new ResourceLocation(NNOW, "metals/osmium_block");
//      platinum = new ResourceLocation(NNOW, "metals/platinum_block");
//      silver = new ResourceLocation(NNOW, "metals/silver_block");
//      tin = new ResourceLocation(NNOW, "metals/tin_block");
//      uranium = new ResourceLocation(NNOW, "metals/uranium_block");
//      zinc = new ResourceLocation(NNOW, "metals/zinc_block");
//    }
//    if(quark) {
//      charcoal = new ResourceLocation(QUARK, "charcoal_block");
//    }
//    if(ie) {
//      aluminum = new ResourceLocation(IE, "metal/storage_aluminum");
//      coalCoke = new ResourceLocation(IE, "stone_decoration/coke");
//      constantan = new ResourceLocation(IE, "metal/storage_constantan");
//      copper = new ResourceLocation(IE, "metal/storage_copper");
//      electrum = new ResourceLocation(IE, "metal/storage_electrum");
//      lead = new ResourceLocation(IE, "metal/storage_lead");
//      nickel = new ResourceLocation(IE, "metal/storage_nickel");
//      silver = new ResourceLocation(IE, "metal/storage_silver");
//      steel = new ResourceLocation(IE, "metal/storage_steel");
//      uranium = new ResourceLocation(IE, "metal/storage_uranium_side");
//    }
//    if(mek) {
//      
//    }
//    if(thermal) {
//      bronze = new ResourceLocation(THERMAL, "storage/bronze_block");
//      charcoal = new ResourceLocation(THERMAL, "storage/charcoal_block");
//      rubber = new ResourceLocation(THERMAL, "storage/rubber_block");
//    }
//    
//    aluminumGolem(aluminum);
//    brassGolem(brass);
//    bronzeGolem(bronze);
//    charcoalGolem(charcoal);
//    coalCokeGolem(coalCoke);
//    cobaltGolem(cobalt);
//    constantanGolem(constantan);
//    copperGolem(copper);
//    electrumGolem(electrum);
//    enderiumGolem(enderium);
//    invarGolem(invar);
//    leadGolem(lead);
//    lumiumGolem(lumium);
//    nickelGolem(nickel);
//    osmiumGolem(osmium);
//    platinumGolem(platinum);
//    rubberGolem(rubber);
//    signalumGolem(signalum);
//    silverGolem(silver);
//    steelGolem(steel);
//    tinGolem(tin);
//    uraniumGolem(uranium);
//    zincGolem(zinc);
//  }
  
  public static GolemContainer.Builder aluminumGolem() {
    return new GolemContainer.Builder(ALUMINUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(48.0D).setAttack(4.25D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/aluminum"));
  }
  
  public static GolemContainer.Builder brassGolem() {
    return new GolemContainer.Builder(BRONZE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(94.0D).setAttack(6.75D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/brass"));
  }
  
  public static GolemContainer.Builder bronzeGolem() {
    return new GolemContainer.Builder(BRONZE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(104.0D).setAttack(7.25D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/bronze"));
  }

  public static GolemContainer.Builder charcoalGolem() {
    return new GolemContainer.Builder(CHARCOAL_GOLEM, CoalGolem.class, CoalGolem::new)
        .setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).setKnockbackResist(0.2D)
        .addSpecial(CoalGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict blindness",
            new TranslationTextComponent("entitytip.blinds_creatures").mergeStyle(TextFormatting.GRAY))
        .addHealItem(Items.COAL, 0.25D).addHealItem(Items.CHARCOAL, 0.25D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/charcoal"));
  }

  public static GolemContainer.Builder constantanGolem() {
    return new GolemContainer.Builder(CONSTANTAN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(96.0D).setAttack(6.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/constantan"));
  }

  public static GolemContainer.Builder coalCokeGolem() {
    return new GolemContainer.Builder(COALCOKE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(28.0D).setAttack(3.25D).setSpeed(0.28D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/coal_coke"));
  }

  public static GolemContainer.Builder copperGolem() {
    return new GolemContainer.Builder(COPPER_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(82.0D).setAttack(5.25D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/copper"));
  }

  public static GolemContainer.Builder electrumGolem() {
    return new GolemContainer.Builder(ELECTRUM_GOLEM, ElectrumGolem.class, ElectrumGolem::new)
        .setHealth(88.0D).setAttack(5.7D).setSpeed(0.32D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addSpecial(ElectrumGolem.IMMUNE_TO_MAGIC, true, "Whether the golem is immune to magic damage",
            new TranslationTextComponent("entitytip.immune_to_magic").mergeStyle(TextFormatting.YELLOW))
        .addBlocks(new ResourceLocation("forge", "storage_blocks/electrum"));
  }
  
  public static GolemContainer.Builder enderiumGolem() {
    return new GolemContainer.Builder(ENDERIUM_GOLEM, EnderiumGolem.class, EnderiumGolem::new)
        .setHealth(134.0D).setAttack(8.5D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addSpecial(EnderiumGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport",
            new TranslationTextComponent("entitytip.can_teleport").mergeStyle(TextFormatting.DARK_AQUA))
        .addBlocks(new ResourceLocation("forge", "storage_blocks/enderium"));
  }
  
  public static GolemContainer.Builder invarGolem() {
    return new GolemContainer.Builder(INVAR_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(94.0D).setAttack(6.75D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/invar"));
  }
  
  public static GolemContainer.Builder leadGolem() {
    return new GolemContainer.Builder(LEAD_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(66.0D).setAttack(4.5D).setKnockbackResist(1.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/lead"));
  }
  
  public static GolemContainer.Builder lumiumGolem() {
    return new GolemContainer.Builder(LUMIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(102.0D).setAttack(5.0D).setSound(SoundEvents.BLOCK_METAL_STEP).setLightLevel(15)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/lumium"));
  }
  
  public static GolemContainer.Builder nickelGolem() {
    return new GolemContainer.Builder(NICKEL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(86.0D).setAttack(6.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/lumium"));
  }
  
  public static GolemContainer.Builder osmiumGolem() { // TODO stats
    return new GolemContainer.Builder(OSMIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(86.0D).setAttack(6.0D).setKnockbackResist(1.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/osmium"));
  }
  
  public static GolemContainer.Builder platinumGolem() { // TODO stats
    return new GolemContainer.Builder(PLATINUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(86.0D).setAttack(6.0D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/platinum"));
  }
  
  public static GolemContainer.Builder rubberGolem() {
    return new GolemContainer.Builder(RUBBER_GOLEM, RubberGolem.class, RubberGolem::new)
        .setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).setKnockbackResist(0.35D)
        .addSpecial(RubberGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking",
            new TranslationTextComponent("entitytip.has_knockback").mergeStyle(TextFormatting.GREEN))
        .addSpecial(RubberGolem.KNOCKBACK, Double.valueOf(0.44D), "Rubber Golem knockback power (Higher Value = Bigger Knockback)")
        .setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP);
  }
  
  public static GolemContainer.Builder signalumGolem() {
    return new GolemContainer.Builder(SIGNALUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(124.0D).setAttack(7.5D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/signalum"));
  }
  
  public static GolemContainer.Builder silverGolem() { // TODO stats
    return new GolemContainer.Builder(SILVER_GOLEM, ElectrumGolem.class, ElectrumGolem::new)
        .setHealth(94.0D).setAttack(5.5D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addSpecial(ElectrumGolem.IMMUNE_TO_MAGIC, true, "Whether the golem is immune to magic damage",
            new TranslationTextComponent("entitytip.immune_to_magic").mergeStyle(TextFormatting.LIGHT_PURPLE))
        .addBlocks(new ResourceLocation("forge", "storage_blocks/silver"));
  }
  
  public static GolemContainer.Builder steelGolem() { // TODO stats
    return new GolemContainer.Builder(STEEL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(140.0D).setAttack(8.5D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/steel"));
  }
  
  public static GolemContainer.Builder tinGolem() {
    return new GolemContainer.Builder(TIN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(72.0D).setAttack(5.75D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/tin"));
  }
  
  public static GolemContainer.Builder uraniumGolem() { // TODO stats, abilities
    return new GolemContainer.Builder(URANIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(72.0D).setAttack(5.75D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/uranium"));
  }
  
  public static GolemContainer.Builder zincGolem() { // TODO stats
    return new GolemContainer.Builder(ZINC_GOLEM, GenericGolem.class, GenericGolem::new)
        .setHealth(52.0D).setAttack(3.75D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/zinc"));
  }
}
