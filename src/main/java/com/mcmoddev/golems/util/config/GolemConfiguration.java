package com.mcmoddev.golems.util.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemConfiguration {

	protected HashMap<GolemContainer, GolemConfigurationSection> sections = new HashMap<>();

	public GolemConfiguration(ForgeConfigSpec.Builder builder) {
		for(GolemContainer c : GolemRegistrar.golemList.values()) {
			sections.put(c, new GolemConfigurationSection(c, builder));
		}
	}
	/**
	 * Call on world load. Refills all containers with config values
	 */
	public void loadData() {
		for(GolemContainer c : this.sections.keySet()) {
			GolemConfigurationSection section = this.sections.get(c);
			c.attack = section.attack.get();
			c.health = section.health.get();
			c.canUseSpecial = section.allowSpecial.get();
		}
	}
}
