package com.mcmoddev.golems;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public final class EGConfig {

	private final ForgeConfigSpec.BooleanValue BEDROCK_GOLEM_CREATIVE_ONLY;
	private final ForgeConfigSpec.BooleanValue PUMPKIN_BUILDS_GOLEMS;
	private final ForgeConfigSpec.BooleanValue ENABLE_FRIENDLY_FIRE;
	private final ForgeConfigSpec.BooleanValue ENABLE_USE_SPELL_ITEM;
	private final ForgeConfigSpec.BooleanValue ENABLE_HEAL_GOLEMS;
	private final ForgeConfigSpec.BooleanValue ENABLE_HOLIDAYS;
	private final ForgeConfigSpec.IntValue VILLAGER_GOLEM_SPAWN_CHANCE;
	private final ForgeConfigSpec.IntValue DEBUG_GOLEMS_PERMISSION_LEVEL;

	private boolean aprilFirst;
	private boolean halloween;
	private boolean pride;

	private boolean bedrockGolemCreativeOnly;
	private boolean pumpkinBuildsGolems;
	private boolean enableFriendlyFire;
	private boolean enableUseSpellItem;
	private boolean enableHealGolems;
	private boolean enableHolidays;
	private int villagerGolemSpawnChance;
	private int debugGolemsPermissionLevel;

	public EGConfig(final ForgeConfigSpec.Builder builder) {
		// Global values
		builder.push("general");
		BEDROCK_GOLEM_CREATIVE_ONLY = builder.comment("When true, only players in creative mode can use a Bedrock Golem spawn item")
				.define("bedrock_golem_creative_only", true);
		PUMPKIN_BUILDS_GOLEMS = builder.comment("When true, pumpkins can be used to build Extra Golems entities")
				.define("pumpkin_builds_golems", false);
		ENABLE_FRIENDLY_FIRE = builder.comment("When enabled, attacking a player-built golem will make it attack you")
				.define("friendly_fire", false);
		ENABLE_USE_SPELL_ITEM = builder
				.comment("When enabled, players can use the spell item on a carved pumpkin to convert it to a golem head in-world")
				.define("use_spell", true);
		ENABLE_HOLIDAYS = builder.comment("Super secret special days")
				.define("holidays", true);
		VILLAGER_GOLEM_SPAWN_CHANCE = builder.comment("Percent chance for a villager to summon an Extra Golems entity")
				.defineInRange("villager_summon_chance", 60, 0, 100);
		ENABLE_HEAL_GOLEMS = builder.comment("When enabled, giving blocks and items to golems can restore health")
				.define("heal_golems", true);
		DEBUG_GOLEMS_PERMISSION_LEVEL = builder
				.comment("The minimum permission level required to spawn golems from the guide book")
				.defineInRange("debug_golems_permission_level", 2, 0, 4);
		builder.pop();
	}

	public boolean isBedrockGolemCreativeOnly() {
		return bedrockGolemCreativeOnly;
	}

	public boolean pumpkinBuildsGolems() {
		return pumpkinBuildsGolems;
	}

	public boolean enableFriendlyFire() {
		return enableFriendlyFire;
	}

	public boolean enableUseSpellItem() {
		return enableUseSpellItem;
	}

	public boolean enableHealGolems() {
		return enableHealGolems;
	}

	public int villagerSummonChance() {
		return villagerGolemSpawnChance;
	}

	public boolean aprilFirst() {
		return enableHolidays && aprilFirst;
	}

	public boolean halloween() {
		return enableHolidays && halloween;
	}

	public boolean pride() {
		return enableHolidays && pride;
	}

	public int debugPermissionLevel() {
		return debugGolemsPermissionLevel;
	}

	public void bake() {
		// update the holiday configs
		final LocalDateTime now = LocalDateTime.now();
		aprilFirst = (now.getMonth() == Month.MARCH && now.getDayOfMonth() >= 31) || (now.getMonth() == Month.APRIL && now.getDayOfMonth() <= 2);
		halloween = (now.getMonth() == Month.OCTOBER && now.getDayOfMonth() >= 30) || (now.getMonth() == Month.NOVEMBER && now.getDayOfMonth() <= 2);
		pride = (now.getMonth() == Month.JUNE && now.getDayOfMonth() <= 3);
		// update config values
		bedrockGolemCreativeOnly = BEDROCK_GOLEM_CREATIVE_ONLY.get();
		pumpkinBuildsGolems = PUMPKIN_BUILDS_GOLEMS.get();
		enableFriendlyFire = ENABLE_FRIENDLY_FIRE.get();
		enableUseSpellItem = ENABLE_USE_SPELL_ITEM.get();
		enableHolidays = ENABLE_HOLIDAYS.get();
		enableHealGolems = ENABLE_HEAL_GOLEMS.get();
		villagerGolemSpawnChance = VILLAGER_GOLEM_SPAWN_CHANCE.get();
		this.debugGolemsPermissionLevel = DEBUG_GOLEMS_PERMISSION_LEVEL.get();
	}
}
