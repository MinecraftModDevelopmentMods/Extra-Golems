package com.golems.main;

import java.util.HashSet;
import java.util.Set;

import com.golems.entity.*;
import com.golems.util.GolemConfigSet;

import net.minecraftforge.common.config.Configuration;

/** Registers the config settings to adjust aspects of this mod. **/
public final class Config {

	private Config() {
		//
	}

	public static final String CATEGORY_OTHER = "_other_";

	public static GolemConfigSet BEDROCK;
	public static GolemConfigSet BONE;
	public static GolemConfigSet BOOKSHELF;
	public static GolemConfigSet CLAY;
	public static GolemConfigSet COAL;
	public static GolemConfigSet CRAFTING;
	public static GolemConfigSet DIAMOND;
	public static GolemConfigSet EMERALD;
	public static GolemConfigSet ENDSTONE;
	public static GolemConfigSet GLASS;
	public static GolemConfigSet GLOWSTONE;
	public static GolemConfigSet GOLD;
	public static GolemConfigSet HARD_CLAY;
	public static GolemConfigSet ICE;
	public static GolemConfigSet LAPIS;
	public static GolemConfigSet LEAF;
	public static GolemConfigSet MAGMA;
	public static GolemConfigSet MELON;
	public static GolemConfigSet MUSHROOM;
	public static GolemConfigSet NETHERBRICK;
	public static GolemConfigSet NETHERWART;
	public static GolemConfigSet OBSIDIAN;
	public static GolemConfigSet PRISMARINE;
	public static GolemConfigSet QUARTZ;
	public static GolemConfigSet RED_SANDSTONE;
	public static GolemConfigSet REDSTONE;
	public static GolemConfigSet SANDSTONE;
	public static GolemConfigSet SEA_LANTERN;
	public static GolemConfigSet SLIME;
	public static GolemConfigSet SPONGE;
	public static GolemConfigSet STAINED_CLAY;
	public static GolemConfigSet STAINED_GLASS;
	public static GolemConfigSet STRAW;
	public static GolemConfigSet TNT;
	public static GolemConfigSet WOOD;
	public static GolemConfigSet WOOL;

	public static boolean bedrockGolemCreativeOnly;
	public static boolean itemGolemHeadHasGlint;
	
	public static final Set<String> SECRET = new HashSet<String>();

	public static void mainRegistry(final Configuration config) {
		config.load();
		initGolemConfigSets(config);
		loadOther(config);
		config.save();
		// secret
		SECRET.add(decode(new int[]{ 127,119,133,118,109,133,61 }));
	}

	/**
	 * Now I can change default attack and health values all from one place.
	 * :D
	 * @param config
	 */
	private static void initGolemConfigSets(final Configuration config) {
		BEDROCK = new GolemConfigSet(config, "Bedrock Golem", 999.0D, 32.0F);
		BONE = new GolemConfigSet(config, "Bone Golem", 54.0D, 9.5F);
		BOOKSHELF = new GolemConfigSet(config, "Bookshelf Golem", 28.0D, 1.5F)
				.addKey(EntityBookshelfGolem.ALLOW_SPECIAL, true, "Whether this golem can give itself potion effects");
		CLAY = new GolemConfigSet(config, "Clay Golem", 20.0D, 2.0F);
		COAL = new GolemConfigSet(config, "Coal Golem", 14.0D, 2.5F)
				.addKey(EntityCoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness");
		CRAFTING = new GolemConfigSet(config, "Crafting Golem", 24.0D, 2.0F)
				.addKey(EntityCraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid");
		DIAMOND = new GolemConfigSet(config, "Diamond Golem", 220.0D, 20.0F);
		EMERALD = new GolemConfigSet(config, "Emerald Golem", 190.0D, 18.0F);
		ENDSTONE = new GolemConfigSet(config, "Endstone Golem", 50.0D, 8.0F)
				.addKey(EntityEndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport")
				.addKey(EntityEndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water");
		GLASS = new GolemConfigSet(config, "Glass Golem", 8.0D, 13.0F);
		GLOWSTONE = new GolemConfigSet(config, "Glowstone Golem", 8.0D, 12.0F)
				.addKey(EntityGlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can place light sources randomly")
				.addKey(EntityGlowstoneGolem.FREQUENCY, 2, 1, 24000, "Number of ticks between placing light sources");
		GOLD = new GolemConfigSet(config, "Gold Golem", 80.0D, 8.0F);
		HARD_CLAY = new GolemConfigSet(config, "Hardened Clay Golem", 22.0D, 4.0F);
		ICE = new GolemConfigSet(config, "Ice Golem", 18.0D, 6.0F)
				.addKey(EntityIceGolem.ALLOW_SPECIAL, true,	"Whether this golem can freeze water and cool lava nearby")
				.addKey(EntityIceGolem.CAN_USE_REGULAR_ICE, true, "When true, the Ice Golem can be built with regular ice as well as packed ice")
				.addKey(EntityIceGolem.AOE, 3, 1, 8, "Radial distance at which this golem can freeze / cool liquids");
		LAPIS = new GolemConfigSet(config, "Lapis Lazuli Golem", 50.0D, 1.5F)
				.addKey(EntityLapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects");
		LEAF = new GolemConfigSet(config, "Leaf Golem", 6.0D, 0.5F)
				.addKey(EntityLeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself");
		MAGMA = new GolemConfigSet(config, "Magma Golem", 22.0D, 4.5F)
				.addKey(EntityMagmaGolem.ALLOW_LAVA_SPECIAL, true, "Whether this golem can slowly melt cobblestone")
				.addKey(EntityMagmaGolem.MELT_DELAY, 240, 1, 24000,	"Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
				.addKey(EntityMagmaGolem.ALLOW_FIRE_SPECIAL, false,	"Whether this golem can light creatures on fire");
		MELON = new GolemConfigSet(config, "Melon Golem", 18.0D, 1.5F)
				.addKey(EntityMelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly")
				.addKey(EntityMelonGolem.FREQUENCY, 240, 1, 24000, "Average number of ticks between planting flowers");
		MUSHROOM = new GolemConfigSet(config, "Mushroom Golem", 30.0D, 3.0F)
				.addKey(EntityMushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly")
				.addKey(EntityMushroomGolem.FREQUENCY, 420, 1, 24000, "Average number of ticks between planting mushrooms");
		NETHERBRICK = new GolemConfigSet(config, "Nether Brick Golem", 25.0D, 6.5F)
				.addKey(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire");
		NETHERWART = new GolemConfigSet(config, "Nether Wart Golem", 22.0D, 1.5F)
				.addKey(EntityNetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly")
				.addKey(EntityNetherWartGolem.FREQUENCY, 880, 1, 24000,	"Average number of ticks between planting nether wart if enabled");
		OBSIDIAN = new GolemConfigSet(config, "Obsidian Golem", 120.0D, 18.0F);
		PRISMARINE = new GolemConfigSet(config, "Prismarine Golem", 24.0D, 8.0F);
		QUARTZ = new GolemConfigSet(config, "Quartz Golem", 85.0D, 8.5F);
		RED_SANDSTONE = new GolemConfigSet(config, "Red Sandstone Golem", 15.0D, 4.0F);
		REDSTONE = new GolemConfigSet(config, "Redstone Golem", 18.0D, 2.0F)
				.addKey(EntityRedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power");
		SANDSTONE = new GolemConfigSet(config, "Sandstone Golem", 15.0D, 4.0F);
		SEA_LANTERN = new GolemConfigSet(config, "Sea Lantern Golem", 24.0D, 6.0F)
				.addKey(EntitySeaLanternGolem.ALLOW_SPECIAL, true, "When true, this golem cannot drown");
		SLIME = new GolemConfigSet(config, "Slime Golem", 85.0D, 2.5F)
				.addKey(EntitySlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking")
				.addKey(EntitySlimeGolem.KNOCKBACK, 2.0012F, 0.001F, 10.0F,	"How powerful the Slime Golem knockback is (Higher Value = Further Knockback)");
		SPONGE = new GolemConfigSet(config, "Sponge Golem", 20.0D, 1.5F)
				.addKey(EntitySpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water")
				.addKey(EntitySpongeGolem.PARTICLES, true, "Whether this golem should always drip water")
				.addKey(EntitySpongeGolem.RANGE, 4, 2, 8, "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
				.addKey(EntitySpongeGolem.INTERVAL, 80, 1, 24000, "Number of ticks between each water-check; increase to reduce lag");
		STAINED_CLAY = new GolemConfigSet(config, "Stained Clay Golem", 26.0D, 3.0F);
		STAINED_GLASS = new GolemConfigSet(config, "Stained Glass Golem", 9.0D, 12.0F);
		STRAW = new GolemConfigSet(config, "Straw Golem", 10.0D, 1.0F);
		TNT = new GolemConfigSet(config, "TNT Golem", 14.0D, 2.5F)
				.addKey(EntityTNTGolem.ALLOW_SPECIAL, true,	"Whether this golem can explode when fighting or dying");
		WOOD = new GolemConfigSet(config, "Wooden Golem", 20.0D, 3.0F);
		WOOL = new GolemConfigSet(config, "Wool Golem", 10.0D, 1.0F);
	}

	private static void loadOther(final Configuration config) {
		bedrockGolemCreativeOnly = config.getBoolean("Bedrock Golem Creative Only", CATEGORY_OTHER,
				true,
				"When true, only players in creative mode can use a Bedrock Golem spawn item");
		itemGolemHeadHasGlint = config.getBoolean("Golem Head Has Glint", CATEGORY_OTHER, true,
				"Whether the Golem Head item always has 'enchanted' effect");
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
