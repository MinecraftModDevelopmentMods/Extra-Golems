package com.mcmoddev.golems;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Taken in part from betteranimalsplus (who probably took it in part from
 * gigaherz) by its_meow.
 */
@Mod.EventBusSubscriber(modid = ExtraGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EGConfig {

  public static EGConfigSpec GOLEM_CONFIG = null;

  public static ForgeConfigSpec COMMON_CONFIG = null;

  public static void setupConfig() {
    final Pair<EGConfigSpec, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EGConfigSpec::new);
    COMMON_CONFIG = specPair.getRight();
    GOLEM_CONFIG = specPair.getLeft();
  }

  @SubscribeEvent
  public static void onLoad(final ModConfig.Loading configEvent) {
    if (configEvent.getConfig().getSpec() == COMMON_CONFIG) {
      GOLEM_CONFIG.loadData();
    }
  }

  public static boolean bedrockGolemCreativeOnly() {
    return GOLEM_CONFIG != null && GOLEM_CONFIG.bedrockGolemCreativeOnly.get();
  }

  public static boolean pumpkinBuildsGolems() {
    return GOLEM_CONFIG != null && GOLEM_CONFIG.pumpkinBuildsGolem.get();
  }

  public static boolean enableFriendlyFire() {
    return GOLEM_CONFIG != null && GOLEM_CONFIG.enableFriendlyFire.get();
  }

  public static boolean enableUseSpellItem() {
    return GOLEM_CONFIG != null && GOLEM_CONFIG.enableUseItemSpell.get();
  }

  public static boolean enableHealGolems() {
    return GOLEM_CONFIG != null && GOLEM_CONFIG.enableHealGolems.get();
  }

  public static int villagerSummonChance() {
    return GOLEM_CONFIG != null ? GOLEM_CONFIG.villagerGolemSpawnChance.get() : 0;
  }

  public static List<ResourceLocation> getVillagerGolems() {
    return GOLEM_CONFIG != null ? GOLEM_CONFIG.loadVillagerGolemList() : new ArrayList<>();
  }
  
  public static boolean aprilFirst() {
    return GOLEM_CONFIG != null && GOLEM_CONFIG.aprilFirst();
  }
  
  public static boolean halloween() {
    return GOLEM_CONFIG != null && GOLEM_CONFIG.halloween();
  }
  
}
