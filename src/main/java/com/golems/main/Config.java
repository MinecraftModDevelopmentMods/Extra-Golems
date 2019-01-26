package com.golems.main;

import java.util.HashSet;
import java.util.Set;

import com.golems.entity.*;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemLookup;

import net.minecraftforge.common.config.Configuration;

/** Registers the config settings to adjust aspects of this mod. **/
public final class Config {

	private Config() {
		//
	}

	public static final String CATEGORY_OTHER = "_other_";

	private static boolean bedrockGolemCreativeOnly;
	private static boolean itemGolemHeadHasGlint;
	
	public static final Set<String> SECRET = new HashSet<String>();

	public static void mainRegistry(final Configuration config) {
		config.load();
		GolemConfigSet.EMPTY = new GolemConfigSet(config, "test", false, 0.0D, 0.0F);
		initGolemConfigSets(config);
		loadOther(config);
		config.save();
		// secret
		SECRET.add(decode(new int[]{ 127,119,133,118,109,133,61 }));
	}

	private static void initGolemConfigSets(final Configuration config) {
		GolemLookup.addConfig(EntityBedrockGolem.class, new GolemConfigSet(config, "Bedrock Golem", 999.0D, 32.0F));
		GolemLookup.addConfig(EntityBoneGolem.class, new GolemConfigSet(config, "Bone Golem", 54.0D, 9.5F));
		GolemLookup.addConfig(EntityBookshelfGolem.class, new GolemConfigSet(config, "Bookshelf Golem", 28.0D, 1.5F)
				.addKey(EntityBookshelfGolem.ALLOW_SPECIAL, true, "Whether this golem can give itself potion effects"));
		GolemLookup.addConfig(EntityClayGolem.class, new GolemConfigSet(config, "Clay Golem", 20.0D, 2.0F));
		GolemLookup.addConfig(EntityCoalGolem.class, new GolemConfigSet(config, "Coal Golem", 14.0D, 2.5F)
				.addKey(EntityCoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness"));
		GolemLookup.addConfig(EntityCraftingGolem.class, new GolemConfigSet(config, "Crafting Golem", 24.0D, 2.0F)
				.addKey(EntityCraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid"));
		GolemLookup.addConfig(EntityDiamondGolem.class, new GolemConfigSet(config, "Diamond Golem", 220.0D, 20.0F));
		GolemLookup.addConfig(EntityEmeraldGolem.class, new GolemConfigSet(config, "Emerald Golem", 190.0D, 18.0F));
		GolemLookup.addConfig(EntityEndstoneGolem.class, new GolemConfigSet(config, "Endstone Golem", 50.0D, 8.0F)
				.addKey(EntityEndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport")
				.addKey(EntityEndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water"));
		GolemLookup.addConfig(EntityGlassGolem.class, new GolemConfigSet(config, "Glass Golem", 8.0D, 13.0F));
		GolemLookup.addConfig(EntityGlowstoneGolem.class, new GolemConfigSet(config, "Glowstone Golem", 8.0D, 12.0F)
				.addKey(EntityGlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can place light sources randomly")
				.addKey(EntityGlowstoneGolem.FREQUENCY, 2, 1, 24000, "Number of ticks between placing light sources"));
		GolemLookup.addConfig(EntityGoldGolem.class, new GolemConfigSet(config, "Gold Golem", 80.0D, 8.0F));
		GolemLookup.addConfig(EntityHardenedClayGolem.class, new GolemConfigSet(config, "Hardened Clay Golem", 22.0D, 4.0F));
		GolemLookup.addConfig(EntityIceGolem.class, new GolemConfigSet(config, "Ice Golem", 18.0D, 6.0F)
				.addKey(EntityIceGolem.ALLOW_SPECIAL, true,	"Whether this golem can freeze water and cool lava nearby")
				.addKey(EntityIceGolem.CAN_USE_REGULAR_ICE, true, "When true, the Ice Golem can be built with regular ice as well as packed ice")
				.addKey(EntityIceGolem.AOE, 3, 1, 8, "Radial distance at which this golem can freeze / cool liquids"));
		GolemLookup.addConfig(EntityLapisGolem.class, new GolemConfigSet(config, "Lapis Lazuli Golem", 50.0D, 1.5F)
				.addKey(EntityLapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects"));
		GolemLookup.addConfig(EntityLeafGolem.class, new GolemConfigSet(config, "Leaf Golem", 6.0D, 0.5F)
				.addKey(EntityLeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself"));
		GolemLookup.addConfig(EntityMagmaGolem.class, new GolemConfigSet(config, "Magma Golem", 22.0D, 4.5F)
				.addKey(EntityMagmaGolem.ALLOW_LAVA_SPECIAL, true, "Whether this golem can slowly melt cobblestone")
				.addKey(EntityMagmaGolem.MELT_DELAY, 240, 1, 24000,	"Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
				.addKey(EntityMagmaGolem.ALLOW_FIRE_SPECIAL, false,	"Whether this golem can light creatures on fire"));
		GolemLookup.addConfig(EntityMelonGolem.class, new GolemConfigSet(config, "Melon Golem", 18.0D, 1.5F)
				.addKey(EntityMelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly")
				.addKey(EntityMelonGolem.FREQUENCY, 240, 1, 24000, "Average number of ticks between planting flowers"));
		GolemLookup.addConfig(EntityMushroomGolem.class, new GolemConfigSet(config, "Mushroom Golem", 30.0D, 3.0F)
				.addKey(EntityMushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly")
				.addKey(EntityMushroomGolem.FREQUENCY, 420, 1, 24000, "Average number of ticks between planting mushrooms"));
		GolemLookup.addConfig(EntityNetherBrickGolem.class, new GolemConfigSet(config, "Nether Brick Golem", 25.0D, 6.5F)
				.addKey(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire"));
		GolemLookup.addConfig(EntityNetherWartGolem.class, new GolemConfigSet(config, "Nether Wart Golem", 22.0D, 1.5F)
				.addKey(EntityNetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly")
				.addKey(EntityNetherWartGolem.FREQUENCY, 880, 1, 24000,	"Average number of ticks between planting nether wart if enabled"));
		GolemLookup.addConfig(EntityObsidianGolem.class, new GolemConfigSet(config, "Obsidian Golem", 120.0D, 18.0F));
		GolemLookup.addConfig(EntityPrismarineGolem.class, new GolemConfigSet(config, "Prismarine Golem", 24.0D, 8.0F));
		GolemLookup.addConfig(EntityQuartzGolem.class, new GolemConfigSet(config, "Quartz Golem", 85.0D, 8.5F));
		GolemLookup.addConfig(EntityRedSandstoneGolem.class, new GolemConfigSet(config, "Red Sandstone Golem", 15.0D, 4.0F));
		GolemLookup.addConfig(EntityRedstoneGolem.class, new GolemConfigSet(config, "Redstone Golem", 18.0D, 2.0F)
				.addKey(EntityRedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power"));
		GolemLookup.addConfig(EntitySandstoneGolem.class, new GolemConfigSet(config, "Sandstone Golem", 15.0D, 4.0F));
		GolemLookup.addConfig(EntitySeaLanternGolem.class, new GolemConfigSet(config, "Sea Lantern Golem", 24.0D, 6.0F)
				.addKey(EntitySeaLanternGolem.ALLOW_SPECIAL, true, "Whether this golem can place light sources")
				.addKey(EntitySeaLanternGolem.FREQUENCY, 5, 1, 24000, "Number of ticks between placing light sources"));
		GolemLookup.addConfig(EntitySlimeGolem.class, new GolemConfigSet(config, "Slime Golem", 85.0D, 2.5F)
				.addKey(EntitySlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking")
				.addKey(EntitySlimeGolem.KNOCKBACK, 2.0012F, 0.001F, 10.0F,	"How powerful the Slime Golem knockback is (Higher Value = Further Knockback)"));
		GolemLookup.addConfig(EntitySpongeGolem.class, new GolemConfigSet(config, "Sponge Golem", 20.0D, 1.5F)
				.addKey(EntitySpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water")
				.addKey(EntitySpongeGolem.PARTICLES, true, "Whether this golem should always drip water")
				.addKey(EntitySpongeGolem.RANGE, 4, 2, 8, "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
				.addKey(EntitySpongeGolem.INTERVAL, 80, 1, 24000, "Number of ticks between each water-check; increase to reduce lag"));
		GolemLookup.addConfig(EntityStainedClayGolem.class, new GolemConfigSet(config, "Stained Clay Golem", 26.0D, 3.0F));
		GolemLookup.addConfig(EntityStainedGlassGolem.class, new GolemConfigSet(config, "Stained Glass Golem", 9.0D, 12.0F));
		GolemLookup.addConfig(EntityStrawGolem.class, new GolemConfigSet(config, "Straw Golem", 10.0D, 1.0F));
		GolemLookup.addConfig(EntityTNTGolem.class, new GolemConfigSet(config, "TNT Golem", 14.0D, 2.5F)
				.addKey(EntityTNTGolem.ALLOW_SPECIAL, true,	"Whether this golem can explode when fighting or dying"));
		GolemLookup.addConfig(EntityWoodenGolem.class, new GolemConfigSet(config, "Wooden Golem", 20.0D, 3.0F));
		GolemLookup.addConfig(EntityWoolGolem.class, new GolemConfigSet(config, "Wool Golem", 10.0D, 1.0F));
	}

	private static void loadOther(final Configuration config) {
		bedrockGolemCreativeOnly = config.getBoolean("Bedrock Golem Creative Only", CATEGORY_OTHER,
				true,
				"When true, only players in creative mode can use a Bedrock Golem spawn item");
		itemGolemHeadHasGlint = config.getBoolean("Golem Head Has Glint", CATEGORY_OTHER, true,
				"Whether the Golem Head item always has 'enchanted' effect");
	}
	
	public static boolean isBedrockGolemCreativeOnly() {
		return bedrockGolemCreativeOnly;
	}
	
	public static boolean golemHeadHasGlint() {
		return itemGolemHeadHasGlint;
	}
	
	public static boolean matchesSecret(String in) {
		return in != null && in.length() > 0 && Config.SECRET.contains(in);
	}
	
	private static String decode(int[] iarray) {
		String stringOut = "";
		for(int i : iarray) {
			int i2 = i - (Integer.parseInt(Character.valueOf((char)65).toString(), 16) / 2 + (int)Math.floor(Math.PI * 2.32224619D));
			char c = Character.valueOf((char)i2);
			stringOut += c;
		}
		return stringOut;
	}
}
