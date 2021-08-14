package com.mcmoddev.golems.util.config;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import com.mcmoddev.golems.util.config.special.GolemSpecialSection;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemConfiguration {

  private final Map<GolemContainer, GolemConfigurationSection> sections = new HashMap<>();
  public final Map<GolemSpecialContainer, GolemSpecialSection> specials = new HashMap<>();

  protected final ForgeConfigSpec.BooleanValue bedrockGolemCreativeOnly;
  protected final ForgeConfigSpec.BooleanValue pumpkinBuildsGolem;
  protected final ForgeConfigSpec.BooleanValue enableFriendlyFire;
  protected final ForgeConfigSpec.BooleanValue enableTextureInteract;
  protected final ForgeConfigSpec.BooleanValue enableUseItemSpell;
  protected final ForgeConfigSpec.BooleanValue enableHealGolems;
  protected final ForgeConfigSpec.BooleanValue holidayTweaks;
  protected final ForgeConfigSpec.IntValue villagerGolemSpawnChance;
  private final ConfigValue<List<? extends String>> villagerGolemSpawns;
  private static final String[] defaultVillagerGolemSpawns = { GolemNames.BOOKSHELF_GOLEM, GolemNames.CLAY_GOLEM,
      GolemNames.COAL_GOLEM, GolemNames.CRAFTING_GOLEM, GolemNames.GLASS_GOLEM, GolemNames.GLOWSTONE_GOLEM, GolemNames.LEAF_GOLEM,
      GolemNames.MELON_GOLEM, GolemNames.MUSHROOM_GOLEM, GolemNames.OBSIDIAN_GOLEM, GolemNames.QUARTZ_GOLEM,
      GolemNames.REDSANDSTONE_GOLEM, GolemNames.SANDSTONE_GOLEM, GolemNames.STAINEDGLASS_GOLEM,
      GolemNames.STAINEDTERRACOTTA_GOLEM, GolemNames.STRAW_GOLEM, GolemNames.TERRACOTTA_GOLEM, GolemNames.WOODEN_GOLEM,
      GolemNames.WOOL_GOLEM };
  
  private boolean aprilFirst;
  private boolean halloween;

  public GolemConfiguration(ForgeConfigSpec.Builder builder) {
    // Global values
    builder.push("general");
    this.bedrockGolemCreativeOnly = builder.comment("When true, only players in creative mode can use a Bedrock Golem spawn item")
        .define("bedrock_golem_creative_only", true);
    this.pumpkinBuildsGolem = builder.comment("When true, pumpkins can be used to build this mod's golems").define("pumpkin_builds_golems", false);
    this.enableFriendlyFire = builder.comment("When enabled, attacking a player-built golem will make it attack you").define("friendly_fire", true);
    this.enableTextureInteract = builder.comment("When enabled, some golems will change their texture when clicked").define("texture_interact",
        false);
    this.enableUseItemSpell = builder
        .comment("When enabled, players can 'use' the spell item on a carved pumpkin to convert it to a golem head in-world")
        .define("use_spell", true);
    this.holidayTweaks = builder
        .comment("Super secret special days").define("holidays", true);
    this.villagerGolemSpawnChance = builder.comment("Percent chance for a villager to successfully summon an Extra Golems golem")
        .defineInRange("villager_summon_chance", 60, 0, 100);
    this.enableHealGolems = builder.comment("When enabled, giving blocks and items to golems can restore health").define("heal_golems", true);
    this.villagerGolemSpawns = builder.comment("Golems that can be summoned by villagers", "(Duplicate entries increase chances)")
        .defineList("villager_summon_golems", initVillagerGolemList(defaultVillagerGolemSpawns), o -> o instanceof String);

    builder.pop();

    // Categories for each Golem and their specials
    for (GolemContainer c : GolemRegistrar.golemList.values()) {
      builder.push(c.getName());
      sections.put(c, new GolemConfigurationSection(c, builder));
      builder.push("specials"); // golem.specials
      for (GolemSpecialContainer specialC : c.getSpecialContainers()) {
        specials.put(specialC, new GolemSpecialSection(specialC, builder));
      }
      // Pops specials and the golem
      builder.pop(2);
    }
  }

  /**
   * Call on world load. Refills all containers with config values
   */
  public void loadData() {
    for (Entry<GolemContainer, GolemConfigurationSection> e : this.sections.entrySet()) {
      GolemContainer c = e.getKey();
      GolemConfigurationSection section = e.getValue();
      // update attack, health, and spawn perms based on config
      c.setAttack(section.attack.get());
      c.setHealth(section.health.get());
      c.setEnabled(section.enabled.get());
      // update specials based on config
      for (GolemSpecialContainer specialC : c.getSpecialContainers()) {
        specialC.value = specials.get(specialC).value;
      }
    }
    // also update the holiday configs
    final LocalDateTime now = LocalDateTime.now();
    aprilFirst = (now.getMonth() == Month.MARCH && now.getDayOfMonth() >= 31) || (now.getMonth() == Month.APRIL && now.getDayOfMonth() <= 2);
    halloween = (now.getMonth() == Month.OCTOBER && now.getDayOfMonth() >= 30) || (now.getMonth() == Month.NOVEMBER && now.getDayOfMonth() <= 2);
  }

  private static List<String> initVillagerGolemList(final String[] names) {
    final List<String> list = new ArrayList<>();
    for (final String s : names) {
      list.add(ExtraGolems.MODID.concat(":").concat(s));
    }
    return list;
  }

  public List<GolemContainer> loadVillagerGolemList() {
    final List<GolemContainer> list = new ArrayList<>();
    for (final String s : villagerGolemSpawns.get()) {
      if (s != null && !s.isEmpty()) {
        final GolemContainer container = GolemRegistrar.getContainer(new ResourceLocation(s));
        if (container != null) {
          list.add(container);
        }
      }
    }
    return list;
  }
  
  public boolean aprilFirst() {
    return holidayTweaks.get() && aprilFirst;
  }
  
  public boolean halloween() {
    return holidayTweaks.get() && halloween;
  }
}
