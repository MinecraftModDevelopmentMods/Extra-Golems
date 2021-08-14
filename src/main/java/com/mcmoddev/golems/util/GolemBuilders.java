package com.mcmoddev.golems.util;

import com.mcmoddev.golems.entity.CoalGolem;
import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.entity.modded.ElectrumGolem;
import com.mcmoddev.golems.entity.modded.EnderiumGolem;
import com.mcmoddev.golems.entity.modded.UraniumGolem;

import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

public final class GolemBuilders {
  
  public static final String MODID = "golems_misc";
  
  public static final String ALUMINUM_GOLEM = "golem_aluminum"; // clib, ie
  public static final String BRONZE_GOLEM = "golem_bronze"; // clib, thermal
  public static final String CHARCOAL_GOLEM = "golem_charcoal"; // quark, thermal
  public static final String COALCOKE_GOLEM = "golem_coal_coke"; // thermal, ie
  public static final String CONSTANTAN_GOLEM = "golem_constantan"; // thermal, ie
  public static final String COPPER_GOLEM = "golem_copper"; // clib, mek, thermal, ie
  public static final String ELECTRUM_GOLEM = "golem_electrum"; // clib, thermal, ie
  public static final String ENDERIUM_GOLEM = "golem_enderium"; // clib, thermal
  public static final String INVAR_GOLEM = "golem_invar"; // clib, thermal
  public static final String LEAD_GOLEM = "golem_lead"; // clib, thermal, ie
  public static final String LUMIUM_GOLEM = "golem_lumium"; // clib, thermal
  public static final String NICKEL_GOLEM = "golem_nickel"; // clib, thermal, ie
  public static final String OSMIUM_GOLEM = "golem_osmium"; // mek ONLY
  public static final String PLATINUM_GOLEM = "golem_platinum"; // clib, thermal
  public static final String RUBY_GOLEM = "golem_ruby"; // clib, thermal
  public static final String SAPPHIRE_GOLEM = "golem_sapphire"; // clib, thermal
  public static final String SIGNALUM_GOLEM = "golem_signalum"; // clib, thermal
  public static final String SILVER_GOLEM = "golem_silver"; // clib, ie
  public static final String STEEL_GOLEM = "golem_steel"; // clib, mek, ie
  public static final String SULFUR_GOLEM = "golem_sulfur"; // thermal ONLY
  public static final String TIN_GOLEM = "golem_tin"; // clib, mek, thermal
  public static final String URANIUM_GOLEM = "golem_uranium"; // ie, mek
  
  public static GolemContainer.Builder aluminumGolem() {
    return new GolemContainer.Builder(ALUMINUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(48.0D).setAttack(4.25D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/aluminum"));
  }
  
  public static GolemContainer.Builder bronzeGolem() {
    return new GolemContainer.Builder(BRONZE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(104.0D).setAttack(7.25D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/bronze"));
  }

  public static GolemContainer.Builder charcoalGolem() {
    return new GolemContainer.Builder(CHARCOAL_GOLEM, CoalGolem.class, CoalGolem::new)
        .setModId(MODID).setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).setKnockbackResist(0.2D)
        .addSpecial(CoalGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict blindness",
            new TranslatableComponent("entitytip.blinds_creatures").withStyle(ChatFormatting.GRAY))
        .addHealItem(Items.COAL, 0.25D).addHealItem(Items.CHARCOAL, 0.25D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/charcoal"));
  }

  public static GolemContainer.Builder constantanGolem() {
    return new GolemContainer.Builder(CONSTANTAN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(96.0D).setAttack(6.0D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/constantan"));
  }

  public static GolemContainer.Builder coalCokeGolem() {
    return new GolemContainer.Builder(COALCOKE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(28.0D).setAttack(3.25D).setSpeed(0.28D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/coal_coke"));
  }

  public static GolemContainer.Builder copperGolem() {
    return new GolemContainer.Builder(COPPER_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(82.0D).setAttack(5.25D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/copper"));
  }

  public static GolemContainer.Builder electrumGolem() {
    return new GolemContainer.Builder(ELECTRUM_GOLEM, ElectrumGolem.class, ElectrumGolem::new)
        .setModId(MODID).setHealth(88.0D).setAttack(5.7D).setSpeed(0.32D).setSound(SoundEvents.METAL_STEP)
        .addSpecial(ElectrumGolem.IMMUNE_TO_MAGIC, true, "Whether the golem is immune to magic damage",
            new TranslatableComponent("entitytip.immune_to_magic").withStyle(ChatFormatting.YELLOW))
        .addBlocks(new ResourceLocation("forge", "storage_blocks/electrum"));
  }
  
  public static GolemContainer.Builder enderiumGolem() {
    return new GolemContainer.Builder(ENDERIUM_GOLEM, EnderiumGolem.class, EnderiumGolem::new)
        .setModId(MODID).setHealth(134.0D).setAttack(8.5D).setSound(SoundEvents.METAL_STEP)
        .addSpecial(EnderiumGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport",
            new TranslatableComponent("entitytip.can_teleport").withStyle(ChatFormatting.DARK_AQUA))
        .addBlocks(new ResourceLocation("forge", "storage_blocks/enderium"));
  }
  
  public static GolemContainer.Builder invarGolem() {
    return new GolemContainer.Builder(INVAR_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(94.0D).setAttack(6.75D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/invar"));
  }
  
  public static GolemContainer.Builder leadGolem() {
    return new GolemContainer.Builder(LEAD_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(66.0D).setAttack(4.5D).setKnockbackResist(1.0D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/lead"));
  }
  
  public static GolemContainer.Builder lumiumGolem() {
    return new GolemContainer.Builder(LUMIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(102.0D).setAttack(5.0D).setSound(SoundEvents.METAL_STEP).setLightLevel(15)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/lumium"));
  }
  
  public static GolemContainer.Builder nickelGolem() {
    return new GolemContainer.Builder(NICKEL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(86.0D).setAttack(6.0D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/nickel"));
  }
  
  public static GolemContainer.Builder osmiumGolem() {
    return new GolemContainer.Builder(OSMIUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(106.0D).setAttack(6.0D).setKnockbackResist(1.0D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/osmium"));
  }
  
  public static GolemContainer.Builder platinumGolem() {
    return new GolemContainer.Builder(PLATINUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(86.0D).setAttack(6.0D).setKnockbackResist(0.8D)
        .setSound(SoundEvents.METAL_STEP).addBlocks(new ResourceLocation("forge", "storage_blocks/platinum"));
  }
  
  public static GolemContainer.Builder rubyGolem() {
    return new GolemContainer.Builder(RUBY_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(76.0D).setAttack(6.25D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setSound(SoundEvents.GLASS_STEP).addBlocks(new ResourceLocation("forge", "storage_blocks/ruby"));
  }
  
  public static GolemContainer.Builder signalumGolem() {
    return new GolemContainer.Builder(SIGNALUM_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(124.0D).setAttack(7.5D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/signalum"));
  }
  
  public static GolemContainer.Builder silverGolem() {
    return new GolemContainer.Builder(SILVER_GOLEM, ElectrumGolem.class, ElectrumGolem::new)
        .setModId(MODID).setHealth(94.0D).setAttack(5.5D).setSound(SoundEvents.METAL_STEP)
        .addSpecial(ElectrumGolem.IMMUNE_TO_MAGIC, true, "Whether the golem is immune to magic damage",
            new TranslatableComponent("entitytip.immune_to_magic").withStyle(ChatFormatting.LIGHT_PURPLE))
        .addBlocks(new ResourceLocation("forge", "storage_blocks/silver"));
  }
  
  public static GolemContainer.Builder steelGolem() {
    return new GolemContainer.Builder(STEEL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(140.0D).setAttack(8.5D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/steel"));
  }
  
  public static GolemContainer.Builder sapphireGolem() {
    return new GolemContainer.Builder(SAPPHIRE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(70.0D).setAttack(6.5D).setSpeed(0.28D).setKnockbackResist(0.6D)
        .setSound(SoundEvents.GLASS_STEP).addBlocks(new ResourceLocation("forge", "storage_blocks/sapphire"));
  }
  
  public static GolemContainer.Builder sulfurGolem() {
    return new GolemContainer.Builder(SULFUR_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(54.0D).setAttack(4.0D)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/sulfur"));
  }
  
  public static GolemContainer.Builder tinGolem() {
    return new GolemContainer.Builder(TIN_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(72.0D).setAttack(5.75D).setSound(SoundEvents.METAL_STEP)
        .addBlocks(new ResourceLocation("forge", "storage_blocks/tin"));
  }
  
  public static GolemContainer.Builder uraniumGolem() {
    return new GolemContainer.Builder(URANIUM_GOLEM, UraniumGolem.class, UraniumGolem::new)
        .setModId(MODID).setHealth(72.0D).setAttack(5.75D).setSound(SoundEvents.METAL_STEP)
        .addSpecial(UraniumGolem.ALLOW_POISON, true, "Whether this golem can poison nearby creatures",
            new TranslatableComponent("entitytip.poisons_mobs").withStyle(ChatFormatting.DARK_GREEN))
        .addSpecial(UraniumGolem.AOE, 1.75D, "Poison effect radius")
        .addSpecial(UraniumGolem.DURATION, 42, "Poison effect length (in ticks)")
        .addSpecial(UraniumGolem.AMPLIFIER, 2, "Poison effect amplifier")
        .addBlocks(new ResourceLocation("forge", "storage_blocks/uranium"));
  }
}
