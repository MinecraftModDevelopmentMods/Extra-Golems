package com.golems.main;

import com.golems.entity.*;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemLookup;
import com.golems.util.GolemNames;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Registers the config settings to adjust aspects of this mod.
 **/
public final class Config {

	private Config() {
		//
	}

	public static final String CATEGORY_OTHER = "_other_";

	private static boolean pumpkinBuildsGolem;
	private static boolean bedrockGolemCreativeOnly;
	private static boolean useOreDictName;
	private static boolean enableInteractTexture;
	private static boolean enableFriendlyFire;
	private static boolean enableHealGolems;
	private static int villageGolemSpawnChance;
	private static String[] villageGolemSpawnsDesert = new String[] {
		GolemNames.STRAW_GOLEM, GolemNames.SANDSTONE_GOLEM, GolemNames.SANDSTONE_GOLEM,
		GolemNames.SANDSTONE_GOLEM, GolemNames.REDSANDSTONE_GOLEM, GolemNames.BONE_GOLEM,
		GolemNames.TERRACOTTA_GOLEM, GolemNames.ENDSTONE_GOLEM
	};
	private static String[] villageGolemSpawnsPlains = new String[] {
		GolemNames.WOODEN_GOLEM, GolemNames.STRAW_GOLEM, GolemNames.MELON_GOLEM,
		GolemNames.MUSHROOM_GOLEM, GolemNames.LEAF_GOLEM, GolemNames.WOOL_GOLEM
	};
	
	
	private static final List<Class<? extends GolemBase>> desertGolems = new ArrayList();
	private static final List<Class<? extends GolemBase>> plainsGolems = new ArrayList();
	
	public static final int RANDOM_HEAL_TIMER = 450;

	public static final Set<String> SECRET = new HashSet<>();

	public static void mainRegistry(final Configuration config) {
		config.load();
		GolemConfigSet.EMPTY = new GolemConfigSet(config, "test", false, 0.0D, 0.0F);
		initGolemConfigSets(config);
		loadOther(config);
		config.save();
		// secret
		SECRET.add(decode(new int[]{127, 119, 133, 118, 109, 133, 61}));
	}

	private static void initGolemConfigSets(final Configuration config) {
		int randomHealSec = RANDOM_HEAL_TIMER / 20;
		GolemLookup.addConfig(EntityBedrockGolem.class, new GolemConfigSet(config, "Bedrock Golem", 999.0D, 32.0F));
		GolemLookup.addConfig(EntityBoneGolem.class, new GolemConfigSet(config, "Bone Golem", 54.0D, 9.5F));
		GolemLookup.addConfig(EntityBookshelfGolem.class, new GolemConfigSet(config, "Bookshelf Golem", 28.0D, 1.5F)
			.addKey(EntityBookshelfGolem.ALLOW_SPECIAL, true, "Whether this golem can give itself potion effects"));
		GolemLookup.addConfig(EntityClayGolem.class, new GolemConfigSet(config, "Clay Golem", 20.0D, 2.0F));
		GolemLookup.addConfig(EntityCoalGolem.class, new GolemConfigSet(config, "Coal Golem", 14.0D, 2.5F)
			.addKey(EntityCoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness"));
		GolemLookup.addConfig(EntityConcreteGolem.class, 
				new GolemConfigSet(config, "Concrete Golem", 38.0D, 6.0F)
				.addKey(EntityConcreteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes"));
		GolemLookup.addConfig(EntityCraftingGolem.class, new GolemConfigSet(config, "Crafting Golem", 24.0D, 2.0F)
			.addKey(EntityCraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid"));
		GolemLookup.addConfig(EntityDiamondGolem.class, new GolemConfigSet(config, "Diamond Golem", 220.0D, 20.0F));
		GolemLookup.addConfig(EntityDispenserGolem.class, new GolemConfigSet(config, "Dispenser Golem", 78.0D, 5.2F)
				.addKey(EntityDispenserGolem.ALLOW_SPECIAL, true, "Whether the golem can shoot arrows")
				.addKey(EntityDispenserGolem.ARROW_DAMAGE, 4.25F, 0F, 50F, "Base amount of damage dealt per arrow")
				.addKey(EntityDispenserGolem.ARROW_SPEED, 30, 1, 12000, "Number of ticks between shooting arrows"));
		GolemLookup.addConfig(EntityEmeraldGolem.class, new GolemConfigSet(config, "Emerald Golem", 190.0D, 18.0F));
		GolemLookup.addConfig(EntityEndstoneGolem.class, new GolemConfigSet(config, "Endstone Golem", 50.0D, 8.0F)
			.addKey(EntityEndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport")
			.addKey(EntityEndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water"));
		GolemLookup.addConfig(EntityFurnaceGolem.class, new GolemConfigSet(config, "Furnace Golem", 88.0D, 6.5F)
				.addKey(EntityFurnaceGolem.FUEL_FACTOR, 8, 1, 24000, "Higher values make fuel last longer"));
		GolemLookup.addConfig(EntityGlassGolem.class, new GolemConfigSet(config, "Glass Golem", 8.0D, 13.0F));
		GolemLookup.addConfig(EntityGlowstoneGolem.class, new GolemConfigSet(config, "Glowstone Golem", 8.0D, 12.0F)
			.addKey(EntityGlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can light the area")
			.addKey(EntityGlowstoneGolem.FREQUENCY, 2, 1, 24000, "Number of ticks between updating light"));
		GolemLookup.addConfig(EntityGoldGolem.class, new GolemConfigSet(config, "Gold Golem", 80.0D, 8.0F));
		GolemLookup.addConfig(EntityHardenedClayGolem.class, new GolemConfigSet(config, "Hardened Clay Golem", 22.0D, 4.0F));
		GolemLookup.addConfig(EntityIceGolem.class, new GolemConfigSet(config, "Ice Golem", 18.0D, 6.0F)
			.addKey(EntityIceGolem.ALLOW_SPECIAL, true, "Whether this golem can freeze water and cool lava nearby")
			.addKey(EntityIceGolem.CAN_USE_REGULAR_ICE, true, "When true, the Ice Golem can be built with regular ice as well as packed ice")
			.addKey(EntityIceGolem.AOE, 3, 1, 8, "Radial distance at which this golem can freeze / cool liquids"));
		GolemLookup.addConfig(EntityLapisGolem.class, new GolemConfigSet(config, "Lapis Lazuli Golem", 50.0D, 1.5F)
			.addKey(EntityLapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects"));
		GolemLookup.addConfig(EntityLeafGolem.class, new GolemConfigSet(config, "Leaf Golem", 6.0D, 0.5F)
			.addKey(EntityLeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself"));
		GolemLookup.addConfig(EntityMagmaGolem.class, new GolemConfigSet(config, "Magma Golem", 46.0D, 4.5F)
			.addKey(EntityMagmaGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death")
			.addKey(EntityMagmaGolem.ALLOW_LAVA_SPECIAL, false, "Whether this golem can slowly melt cobblestone")
			.addKey(EntityMagmaGolem.MELT_DELAY, 240, 1, 24000, "Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
			.addKey(EntityMagmaGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire")
			.addKey(EntityMagmaGolem.ALLOW_WATER_DAMAGE, true, "When true, water will hurt this golem"));
		GolemLookup.addConfig(EntityMelonGolem.class, new GolemConfigSet(config, "Melon Golem", 18.0D, 1.5F)
			.addKey(EntityMelonGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (about every " + randomHealSec + " sec)")
			.addKey(EntityMelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly")
			.addKey(EntityMelonGolem.FREQUENCY, 240, 1, 24000, "Average number of ticks between planting flowers"));
		GolemLookup.addConfig(EntityMushroomGolem.class, new GolemConfigSet(config, "Mushroom Golem", 30.0D, 3.0F)
			.addKey(EntityMushroomGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal at night (about every " + randomHealSec + " sec)")
			.addKey(EntityMushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly")
			.addKey(EntityMushroomGolem.FREQUENCY, 420, 1, 24000, "Average number of ticks between planting mushrooms"));
		GolemLookup.addConfig(EntityNetherBrickGolem.class, new GolemConfigSet(config, "Nether Brick Golem", 25.0D, 6.5F)
			.addKey(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire"));
		GolemLookup.addConfig(EntityNetherWartGolem.class, new GolemConfigSet(config, "Nether Wart Golem", 22.0D, 1.5F)
			.addKey(EntityNetherWartGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal at night (about every " + randomHealSec + " sec)")
			.addKey(EntityNetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly")
			.addKey(EntityNetherWartGolem.FREQUENCY, 880, 1, 24000, "Average number of ticks between planting nether wart if enabled"));
		GolemLookup.addConfig(EntityObsidianGolem.class, new GolemConfigSet(config, "Obsidian Golem", 120.0D, 18.0F));
		GolemLookup.addConfig(EntityPrismarineGolem.class, new GolemConfigSet(config, "Prismarine Golem", 24.0D, 8.0F));
		GolemLookup.addConfig(EntityQuartzGolem.class, new GolemConfigSet(config, "Quartz Golem", 85.0D, 8.5F));
		GolemLookup.addConfig(EntityRedSandstoneGolem.class, new GolemConfigSet(config, "Red Sandstone Golem", 15.0D, 4.0F));
		GolemLookup.addConfig(EntityRedstoneGolem.class, new GolemConfigSet(config, "Redstone Golem", 18.0D, 2.0F)
			.addKey(EntityRedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power"));
		GolemLookup.addConfig(EntityRedstoneLampGolem.class, new GolemConfigSet(config, "Redstone Lamp Golem", 28.0F, 6.0F)
			.addKey(EntityRedstoneLampGolem.ALLOW_SPECIAL, true, "Whether this golem can light up the area")
			.addKey(EntityRedstoneLampGolem.FREQUENCY, 2, 1, 24000, "Number of ticks between updating light"));
		GolemLookup.addConfig(EntitySandstoneGolem.class, new GolemConfigSet(config, "Sandstone Golem", 15.0D, 4.0F));
		GolemLookup.addConfig(EntitySeaLanternGolem.class, new GolemConfigSet(config, "Sea Lantern Golem", 24.0D, 6.0F)
			.addKey(EntitySeaLanternGolem.ALLOW_SPECIAL, true, "Whether this golem lights up the area")
			.addKey(EntitySeaLanternGolem.FREQUENCY, 5, 1, 24000, "Number of ticks between updating light"));
		GolemLookup.addConfig(EntitySlimeGolem.class, new GolemConfigSet(config, "Slime Golem", 58.0D, 2.5F)
			.addKey(EntitySlimeGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death")
			.addKey(EntitySlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking")
			.addKey(EntitySlimeGolem.KNOCKBACK, 1.9412F, 0.001F, 10.0F, "How powerful the Slime Golem knockback is (Higher Value = Further Knockback)"));
		GolemLookup.addConfig(EntitySpongeGolem.class, new GolemConfigSet(config, "Sponge Golem", 20.0D, 1.5F)
			.addKey(EntitySpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water")
			.addKey(EntitySpongeGolem.PARTICLES, true, "Whether this golem should always drip water")
			.addKey(EntitySpongeGolem.RANGE, 4, 2, 8, "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
			.addKey(EntitySpongeGolem.INTERVAL, 20, 1, 24000, "Number of ticks between each water-check; increase to reduce lag"));
		GolemLookup.addConfig(EntityStainedClayGolem.class, new GolemConfigSet(config, "Stained Clay Golem", 26.0D, 3.0F));
		GolemLookup.addConfig(EntityStainedGlassGolem.class, new GolemConfigSet(config, "Stained Glass Golem", 9.0D, 12.0F));
		GolemLookup.addConfig(EntityStrawGolem.class, new GolemConfigSet(config, "Straw Golem", 10.0D, 1.0F)
				.addKey(EntityStrawGolem.ALLOW_SPECIAL, true, "Whether this golem can speed up crop growth")
				.addKey(EntityStrawGolem.SPECIAL_FREQ, 460, 1, 24000, "Minimum number of ticks between crop-boosts"));
		GolemLookup.addConfig(EntityTNTGolem.class, new GolemConfigSet(config, "TNT Golem", 14.0D, 2.5F)
			.addKey(EntityTNTGolem.ALLOW_SPECIAL, true, "Whether this golem can explode when fighting or dying"));
		GolemLookup.addConfig(EntityWoodenGolem.class, new GolemConfigSet(config, "Wooden Golem", 20.0D, 3.0F));
		GolemLookup.addConfig(EntityWoolGolem.class, new GolemConfigSet(config, "Wool Golem", 10.0D, 1.0F));
	}

	private static void loadOther(final Configuration config) {
		enableHealGolems = config.getBoolean("Heal Golems", CATEGORY_OTHER, true, 
				"When enabled, giving blocks to golems can restore health");
		enableFriendlyFire = config.getBoolean("Friendly Fire", CATEGORY_OTHER, true, 
				"When false, attacking a player-built golem will not make it attack you");
		bedrockGolemCreativeOnly = config.getBoolean("Bedrock Golem Creative Only", CATEGORY_OTHER,
			true, "When true, only players in creative mode can use a Bedrock Golem spawn item");
		pumpkinBuildsGolem = config.getBoolean("Pumpkin Builds Golems", CATEGORY_OTHER, false, 
				"(Experimental) When true, pumpkins can be used to build this mod's golems");
		useOreDictName = config.getBoolean("Use OreDict Blocks", CATEGORY_OTHER, true, 
				"When true, building a golem will work with any OreDictionary-registered blocks");
		enableInteractTexture = config.getBoolean("Interact Changes Texture", CATEGORY_OTHER, false, 
				"When true, some golems will change their texture when right clicked");
		villageGolemSpawnsDesert = config.getStringList("Desert Village Golem Spawns", CATEGORY_OTHER, villageGolemSpawnsDesert, 
				"The following golems will appear in villages in Desert biomes. (Duplicate entries increase chances)");
		villageGolemSpawnsPlains = config.getStringList("Plains Village Golem Spawns", CATEGORY_OTHER, villageGolemSpawnsPlains, 
				"The following golems will appear in villages in Plains biomes. (Duplicate entries increase chances)");
		villageGolemSpawnChance = config.getInt("Village Golem Spawn Chance", CATEGORY_OTHER, 60, 0, 100, 
				"Percent chance for each village chunk to include an Extra Golems golem. Set to 0 to disable");
	}
	
	public static boolean doesPumpkinBuildGolem() {
		return pumpkinBuildsGolem;
	}

	public static boolean isBedrockGolemCreativeOnly() {
		return bedrockGolemCreativeOnly;
	}
	
	public static int getVillageGolemSpawnChance() {
		return villageGolemSpawnChance;
	}
	
	public static boolean getUseOreDictBlocks() {
		return useOreDictName;
	}
	
	public static boolean interactChangesTexture() {
		return enableInteractTexture;
	}
	
	public static boolean enableFriendlyFire() {
		return enableFriendlyFire;
	}
	
	public static boolean enableHealGolems() {
		return enableHealGolems;
	}
	
	public static List<Class<? extends GolemBase>> getDesertGolems() {
		if(desertGolems.isEmpty()) {
			// populate the list from the config values found earlier
			for(final String s : villageGolemSpawnsDesert) {
				final ResourceLocation name = new ResourceLocation(ExtraGolems.MODID, s);
				EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(name);
				if(entityEntry != null && (GolemBase.class).isAssignableFrom(entityEntry.getEntityClass())) {
					desertGolems.add((Class<? extends GolemBase>)entityEntry.getEntityClass());
				} else ExtraGolems.LOGGER.error("Tried to parse an unknown entity from the config! Skipping '" + s + "' in \"Desert Village Golem Spawns\"");
			}
		}
		
		return desertGolems;
	}
	
	public static List<Class<? extends GolemBase>> getPlainsGolems() {
		if(plainsGolems.isEmpty()) {
			for(String s : villageGolemSpawnsPlains) {
				final ResourceLocation name = new ResourceLocation(ExtraGolems.MODID, s);
				EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(name);
				if(entityEntry != null && (GolemBase.class).isAssignableFrom(entityEntry.getEntityClass())) {
					plainsGolems.add((Class<? extends GolemBase>)entityEntry.getEntityClass());
				} else ExtraGolems.LOGGER.error("Tried to parse an unknown entity from the config! Skipping '" + s + "' in \"Plains Village Golem Spawns\"");
			}
		}
		
		return plainsGolems;
	}
	
	public static boolean matchesSecret(String in) {
		return in != null && in.length() > 0 && Config.SECRET.contains(in);
	}
	
	private static String decode(int[] iarray) {
		StringBuilder stringOut = new StringBuilder();
		for (int i : iarray) {
			int i2 = i - (Integer.parseInt(Character.toString((char) 65), 16) / 2 + (int) Math.floor(Math.PI * 2.32224619D));
			char c = (char) i2;
			stringOut.append(c);
		}
		return stringOut.toString();
	}
}
