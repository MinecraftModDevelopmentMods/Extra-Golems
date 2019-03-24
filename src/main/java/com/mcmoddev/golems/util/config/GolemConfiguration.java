package com.mcmoddev.golems.util.config;

import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import com.mcmoddev.golems.util.config.special.GolemSpecialSection;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemConfiguration {

	public HashMap<GolemContainer, GolemConfigurationSection> sections = new HashMap<>();
	public HashMap<GolemSpecialContainer, GolemSpecialSection> specials = new HashMap<>();


	public GolemConfiguration(ForgeConfigSpec.Builder builder) {
		for(GolemContainer c : GolemRegistrar.golemList.values()) {
			sections.put(c, new GolemConfigurationSection(c, builder));
			if(c.specialContainers != null && !c.specialContainers.isEmpty()) {
				builder.push("specials");
				for(GolemSpecialContainer specialC : c.specialContainers.values()) {
					specials.put(specialC, new GolemSpecialSection(specialC, builder));
				}
				builder.pop();
			}
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

			for(GolemSpecialContainer specialC : c.specialContainers.values()) {
				specialC.value = specials.get(specialC).value;
			}
		}
	}

}
