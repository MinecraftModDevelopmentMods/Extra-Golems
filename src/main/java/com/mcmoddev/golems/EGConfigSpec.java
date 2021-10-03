package com.mcmoddev.golems;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.mcmoddev.golems.golem_stats.GolemContainer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class EGConfigSpec {

  protected final ForgeConfigSpec.BooleanValue bedrockGolemCreativeOnly;
  protected final ForgeConfigSpec.BooleanValue pumpkinBuildsGolem;
  protected final ForgeConfigSpec.BooleanValue enableFriendlyFire;
  protected final ForgeConfigSpec.BooleanValue enableUseItemSpell;
  protected final ForgeConfigSpec.BooleanValue enableHealGolems;
  protected final ForgeConfigSpec.BooleanValue holidayTweaks;
  protected final ForgeConfigSpec.IntValue villagerGolemSpawnChance;
  private final ConfigValue<List<? extends String>> villagerGolemSpawns;
  private static final String[] defaultVillagerGolemSpawns = {
      "golems:bookshelf", "golems:clay", "golems:coal", "golems:crafting",
      "golems:glass", "golems:glowstone", "golems:hay", "golems:leaves", 
      "golems:log", "golems:melon", "golems:mushroom", "golems:obsidian", 
      "golems:quartz", "golems:red_sandstone", "golems:sandstone", 
      "golems:terracotta", "golems:wool"
  };
  
  private boolean aprilFirst;
  private boolean halloween;

  public EGConfigSpec(ForgeConfigSpec.Builder builder) {
    // Global values
    builder.push("general");
    this.bedrockGolemCreativeOnly = builder.comment("When true, only players in creative mode can use a Bedrock Golem spawn item")
        .define("bedrock_golem_creative_only", true);
    this.pumpkinBuildsGolem = builder.comment("When true, pumpkins can be used to build this mod's golems")
        .define("pumpkin_builds_golems", false);
    this.enableFriendlyFire = builder.comment("When enabled, attacking a player-built entity will make it attack you")
        .define("friendly_fire", true);
    this.enableUseItemSpell = builder
        .comment("When enabled, players can 'use' the spell item on a carved pumpkin to convert it to a entity head in-world")
        .define("use_spell", true);
    this.holidayTweaks = builder.comment("Super secret special days").define("holidays", true);
    this.villagerGolemSpawnChance = builder.comment("Percent chance for a villager to successfully summon an Extra Golems entity")
        .defineInRange("villager_summon_chance", 60, 0, 100);
    this.enableHealGolems = builder.comment("When enabled, giving blocks and items to golems can restore health")
        .define("heal_golems", true);
    this.villagerGolemSpawns = builder.comment("Golems that can be summoned by villagers", "(Duplicate entries increase chances)")
        .defineList("villager_summon_golems", Lists.newArrayList(defaultVillagerGolemSpawns), o -> o instanceof String);

    builder.pop();
  }

  /**
   * Call on world load. Refills all containers with config values
   */
  public void loadData() {
    // also update the holiday configs
    final LocalDateTime now = LocalDateTime.now();
    aprilFirst = (now.getMonth() == Month.MARCH && now.getDayOfMonth() >= 31) || (now.getMonth() == Month.APRIL && now.getDayOfMonth() <= 2);
    halloween = (now.getMonth() == Month.OCTOBER && now.getDayOfMonth() >= 30) || (now.getMonth() == Month.NOVEMBER && now.getDayOfMonth() <= 2);
  }

  public List<ResourceLocation> loadVillagerGolemList() {
    final List<ResourceLocation> list = new ArrayList<>();
    for (final String s : villagerGolemSpawns.get()) {
      if (s != null && !s.isEmpty()) {
        final Optional<GolemContainer> container = ExtraGolems.GOLEM_CONTAINERS.get(new ResourceLocation(s));
        container.ifPresent(c -> list.add(new ResourceLocation(s)));
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
