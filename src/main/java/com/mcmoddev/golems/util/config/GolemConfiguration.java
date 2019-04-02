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
	
	public ForgeConfigSpec.BooleanValue bedrockGolemCreativeOnly;
	public ForgeConfigSpec.BooleanValue pumpkinBuildsGolem;
	public ForgeConfigSpec.BooleanValue enableFriendlyFire;

// TODO implement the following config values
//		villageGolemSpawnsDesert = config.getStringList("Desert Village Golem Spawns", CATEGORY_OTHER, villageGolemSpawnsDesert,
//				"The following golems will appear in villages in Desert biomes. (Duplicate entries increase chances)");
//		villageGolemSpawnsPlains = config.getStringList("Plains Village Golem Spawns", CATEGORY_OTHER, villageGolemSpawnsPlains,
//				"The following golems will appear in villages in Plains biomes. (Duplicate entries increase chances)");
//		villageGolemSpawnChance = config.getInt("Village Golem Spawn Chance", CATEGORY_OTHER, 60, 0, 100,
//				"Percent chance for each village chunk to include an Extra Golems golem. Set to 0 to disable");


	public GolemConfiguration(ForgeConfigSpec.Builder builder) {
		// Global values
		builder.push("general");
		this.bedrockGolemCreativeOnly = builder.comment("When true, only players in creative mode can use a Bedrock Golem spawn item")
				.define("bedrock_golem_creative_only", true);
		this.pumpkinBuildsGolem = builder.comment("(Experimental) When true, pumpkins can be used to build this mod's golems")
				.define("pumpkin_builds_golems", false);
		this.enableFriendlyFire = builder.comment("When enabled, attacking player-built golems will make them attack you")
				.define("friendly_fire", true);
		
		builder.pop();
		
		// Categories for each Golem and their specials
		for(GolemContainer c : GolemRegistrar.golemList.values()) {
			builder.push(c.getName());
			sections.put(c, new GolemConfigurationSection(c, builder));
				builder.push("specials"); //golem.specials
				for(GolemSpecialContainer specialC : c.specialContainers.values()) {
					specials.put(specialC, new GolemSpecialSection(specialC, builder));
				}
				builder.pop(2); //Pops specials and the golem
		}
	}
	
	/**
	 * Call on world load. Refills all containers with config values
	 */
	public void loadData() {
		for(GolemContainer c : this.sections.keySet()) {
			GolemConfigurationSection section = this.sections.get(c);
			c.setAttack(section.attack.get());
			c.setHealth(section.health.get());

			for(GolemSpecialContainer specialC : c.specialContainers.values()) {
				specialC.value = specials.get(specialC).value;
			}
		}
	}

}
