package com.mcmoddev.golems.util.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import com.mcmoddev.golems.util.config.special.GolemSpecialSection;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemConfiguration {

	public final Map<GolemContainer, GolemConfigurationSection> sections = new HashMap<>();
	public final Map<GolemSpecialContainer, GolemSpecialSection> specials = new HashMap<>();

	public final ForgeConfigSpec.BooleanValue bedrockGolemCreativeOnly;
	public final ForgeConfigSpec.BooleanValue pumpkinBuildsGolem;
	public final ForgeConfigSpec.BooleanValue enableFriendlyFire;
	public final ForgeConfigSpec.BooleanValue enableTextureInteract;
	public final ForgeConfigSpec.BooleanValue enableUseItemSpell;
	public final ForgeConfigSpec.BooleanValue enableHealGolems;
	public final ForgeConfigSpec.IntValue villagerGolemSpawnChance;

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
		this.pumpkinBuildsGolem = builder.comment("When true, pumpkins can be used to build this mod's golems")
			.define("pumpkin_builds_golems", false);
		this.enableFriendlyFire = builder.comment("When enabled, attacking a player-built golem will make it attack you")
			.define("friendly_fire", true);
		this.enableTextureInteract = builder.comment("When enabled, some golems will change their texture when clicked")
			.define("texture_interact", false);
		this.enableUseItemSpell = builder.comment("When enabled, players can 'use' the spell item on a carved pumpkin to convert it to a golem head in-world")
				.define("use_spell", true);
		this.villagerGolemSpawnChance = builder.comment("Percent chance for a villager to successfully summon an Extra Golems golem")
				.defineInRange("villager_summon_chance", 90, 0, 100);
		this.enableHealGolems = builder.comment("When enabled, giving blocks to golems can restore health")
				.define("heal_golems", true);

		builder.pop();

		// Categories for each Golem and their specials
		for (GolemContainer c : GolemRegistrar.golemList.values()) {
			builder.push(c.getName());
			sections.put(c, new GolemConfigurationSection(c, builder));
			builder.push("specials"); //golem.specials
			for (GolemSpecialContainer specialC : c.specialContainers.values()) {
				specials.put(specialC, new GolemSpecialSection(specialC, builder));
			}
			//Pops specials and the golem
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
			c.setAttack(section.attack.get());
			c.setHealth(section.health.get());
			c.setEnabled(section.enabled.get());

			for (GolemSpecialContainer specialC : c.specialContainers.values()) {
				specialC.value = specials.get(specialC).value;
			}
		}
	}

}
