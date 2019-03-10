package com.mcmoddev.golems.util.config;

import com.mcmoddev.golems.main.ExtraGolems;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Taken in part from betteranimalsplus (who probably took it in part from gigaherz) by its_meow.
 */
@Mod.EventBusSubscriber(modid = ExtraGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExtraGolemsConfig {

	private static GolemConfiguration GOLEM_CONFIG = null;

	public static ForgeConfigSpec SERVER_CONFIG = null;

	public static void setupConfig() {
		final Pair<GolemConfiguration, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(GolemConfiguration::new);
		SERVER_CONFIG = specPair.getRight();
		GOLEM_CONFIG = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		ExtraGolems.LOGGER.debug("Loading {} {}", ExtraGolems.MODID, configEvent.getConfig().getFileName());
		if(configEvent.getConfig().getSpec() == SERVER_CONFIG) {
			GOLEM_CONFIG.loadData();
		}
	}
}
