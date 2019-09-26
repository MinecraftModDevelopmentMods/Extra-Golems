package com.mcmoddev.golems.util;

import java.util.function.Consumer;

public final class GolemNames {

	private GolemNames() {
		//
	}

	public static final String BEDROCK_GOLEM = "golem_bedrock";
	public static final String BONE_GOLEM = "golem_bone";
	public static final String BOOKSHELF_GOLEM = "golem_bookshelf";
	public static final String CLAY_GOLEM = "golem_clay";
	public static final String COAL_GOLEM = "golem_coal";
	public static final String CONCRETE_GOLEM = "golem_concrete";
	public static final String CORAL_GOLEM = "golem_coral";
	public static final String CRAFTING_GOLEM = "golem_crafting";
	public static final String DIAMOND_GOLEM = "golem_diamond";
	public static final String EMERALD_GOLEM = "golem_emerald";
	public static final String ENDSTONE_GOLEM = "golem_end_stone";
	public static final String GLASS_GOLEM = "golem_glass";
	public static final String GLOWSTONE_GOLEM = "golem_glowstone";
	public static final String GOLD_GOLEM = "golem_gold";
	public static final String ICE_GOLEM = "golem_ice";
	public static final String LAPIS_GOLEM = "golem_lapis";
	public static final String LEAF_GOLEM = "golem_leaves";
	public static final String MAGMA_GOLEM = "golem_magma";
	public static final String MELON_GOLEM = "golem_melon";
	public static final String MUSHROOM_GOLEM = "golem_shroom";
	public static final String NETHERBRICK_GOLEM = "golem_nether_brick";
	public static final String NETHERWART_GOLEM = "golem_nether_wart";
	public static final String OBSIDIAN_GOLEM = "golem_obsidian";
	public static final String PRISMARINE_GOLEM = "golem_prismarine";
	public static final String QUARTZ_GOLEM = "golem_quartz";
	public static final String REDSANDSTONE_GOLEM = "golem_red_sandstone";
	public static final String REDSTONE_GOLEM = "golem_redstone";
	public static final String REDSTONELAMP_GOLEM = "golem_redstone_lamp";
	public static final String SANDSTONE_GOLEM = "golem_sandstone";
	public static final String SEALANTERN_GOLEM = "golem_sea_lantern";
	public static final String SLIME_GOLEM = "golem_slime";
	public static final String SPONGE_GOLEM = "golem_sponge";
	public static final String STAINEDTERRACOTTA_GOLEM = "golem_stained_clay";
	public static final String STAINEDGLASS_GOLEM = "golem_stained_glass";
	public static final String STRAW_GOLEM = "golem_straw";
	public static final String TERRACOTTA_GOLEM = "golem_hardened_clay";
	public static final String TNT_GOLEM = "golem_tnt";
	public static final String WOODEN_GOLEM = "golem_wooden";
	public static final String WOOL_GOLEM = "golem_wool";

	/**
	 * Currently unused.
	 *
	 * @param nameIn a value from GolemNames
	 * @return the given name without the "golem_" prefix
	 */
	public static String strip(final String nameIn) {
		final String GOLEM = "golem_";
		if (nameIn.contains(GOLEM)) {
			return nameIn.replace(GOLEM, "");
		} 
		return nameIn;
	}
}
