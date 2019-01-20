package com.golems.main;

import com.golems.util.GolemConfigSet;
import com.golems.util.GolemLookup;
import net.minecraftforge.common.config.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * Registers the config settings to adjust aspects of this mod.
 **/
public final class Config {

	private Config() {
		//
	}

	public static final String CATEGORY_OTHER = "_other_";

	public static boolean bedrockGolemCreativeOnly;
	public static boolean itemGolemHeadHasGlint;

	public static final Set<String> SECRET = new HashSet<String>();

	public static void mainRegistry(final Configuration config) {
		config.load();
		GolemConfigSet.EMPTY = new GolemConfigSet(config, "test", false, 0.0D, 0.0F);
		initGolemConfigSets(config);
		loadOther(config);
		config.save();
		// secret
		SECRET.add(decode(new int[]{127, 119, 133, 118, 109, 133, 61}));
	}

	/**
	 * Moved to GolemLookup.java
	 *
	 * @param config
	 */
	private static void initGolemConfigSets(final Configuration config) {
		GolemLookup.initGolemConfigSets(config);
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
		StringBuilder stringOut = new StringBuilder();
		for (int i : iarray) {
			int i2 = i - (Integer.parseInt(Character.valueOf((char) 65).toString(), 16) / 2 + (int) Math.floor(Math.PI * 2.32224619D));
			char c = (char) i2;
			stringOut.append(c);
		}
		return stringOut.toString();
	}
}
