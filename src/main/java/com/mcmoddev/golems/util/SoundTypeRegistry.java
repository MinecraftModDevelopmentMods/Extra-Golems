package com.mcmoddev.golems.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;

import java.util.Collections;
import java.util.Map;

public final class SoundTypeRegistry {

	private static final BiMap<ResourceLocation, SoundType> SOUND_TYPES_BIMAP = HashBiMap.create();
	public static final Map<ResourceLocation, SoundType> SOUND_TYPES = Collections.unmodifiableMap(SOUND_TYPES_BIMAP);

	public static final Codec<SoundType> CODEC = ResourceLocation.CODEC.xmap(SOUND_TYPES_BIMAP::get, value -> SOUND_TYPES_BIMAP.inverse().get(value));

	public static void register() {
		register("empty", SoundType.EMPTY);
		register("wood", SoundType.WOOD);
		register("gravel", SoundType.GRAVEL);
		register("grass", SoundType.GRASS);
		register("lily_pad", SoundType.LILY_PAD);
		register("stone", SoundType.STONE);
		register("metal", SoundType.METAL);
		register("glass", SoundType.GLASS);
		register("wool", SoundType.WOOL);
		register("sand", SoundType.SAND);
		register("snow", SoundType.SNOW);
		register("powder_snow", SoundType.POWDER_SNOW);
		register("ladder", SoundType.LADDER);
		register("anvil", SoundType.ANVIL);
		register("slime_block", SoundType.SLIME_BLOCK);
		register("honey_block", SoundType.HONEY_BLOCK);
		register("wet_grass", SoundType.WET_GRASS);
		register("coral_block", SoundType.CORAL_BLOCK);
		register("bamboo", SoundType.BAMBOO);
		register("bamboo_sapling", SoundType.BAMBOO_SAPLING);
		register("scaffolding", SoundType.SCAFFOLDING);
		register("sweet_berry_bush", SoundType.SWEET_BERRY_BUSH);
		register("crop", SoundType.CROP);
		register("hard_crop", SoundType.HARD_CROP);
		register("vine", SoundType.VINE);
		register("nether_wart", SoundType.NETHER_WART);
		register("lantern", SoundType.LANTERN);
		register("stem", SoundType.STEM);
		register("nylium", SoundType.NYLIUM);
		register("fungus", SoundType.FUNGUS);
		register("roots", SoundType.ROOTS);
		register("shroomlight", SoundType.SHROOMLIGHT);
		register("weeping_vines", SoundType.WEEPING_VINES);
		register("twisting_vines", SoundType.TWISTING_VINES);
		register("soul_sand", SoundType.SOUL_SAND);
		register("soul_soil", SoundType.SOUL_SOIL);
		register("basalt", SoundType.BASALT);
		register("wart_block", SoundType.WART_BLOCK);
		register("netherrack", SoundType.NETHERRACK);
		register("nether_bricks", SoundType.NETHER_BRICKS);
		register("nether_sprouts", SoundType.NETHER_SPROUTS);
		register("nether_ore", SoundType.NETHER_ORE);
		register("bone_block", SoundType.BONE_BLOCK);
		register("netherite_block", SoundType.NETHERITE_BLOCK);
		register("ancient_debris", SoundType.ANCIENT_DEBRIS);
		register("lodestone", SoundType.LODESTONE);
		register("chain", SoundType.CHAIN);
		register("nether_gold_ore", SoundType.NETHER_GOLD_ORE);
		register("gilded_blackstone", SoundType.GILDED_BLACKSTONE);
		register("candle", SoundType.CANDLE);
		register("amethyst", SoundType.AMETHYST);
		register("amethyst_cluster", SoundType.AMETHYST_CLUSTER);
		register("small_amethyst_bud", SoundType.SMALL_AMETHYST_BUD);
		register("medium_amethyst_bud", SoundType.MEDIUM_AMETHYST_BUD);
		register("large_amethyst_bud", SoundType.LARGE_AMETHYST_BUD);
		register("tuff", SoundType.TUFF);
		register("calcite", SoundType.CALCITE);
		register("dripstone_block", SoundType.DRIPSTONE_BLOCK);
		register("pointed_dripstone", SoundType.POINTED_DRIPSTONE);
		register("copper", SoundType.COPPER);
		register("cave_vines", SoundType.CAVE_VINES);
		register("spore_blossom", SoundType.SPORE_BLOSSOM);
		register("azalea", SoundType.AZALEA);
		register("flowering_azalea", SoundType.FLOWERING_AZALEA);
		register("moss_carpet", SoundType.MOSS_CARPET);
		register("pink_petals", SoundType.PINK_PETALS);
		register("moss", SoundType.MOSS);
		register("big_dripleaf", SoundType.BIG_DRIPLEAF);
		register("small_dripleaf", SoundType.SMALL_DRIPLEAF);
		register("rooted_dirt", SoundType.ROOTED_DIRT);
		register("hanging_roots", SoundType.HANGING_ROOTS);
		register("azalea_leaves", SoundType.AZALEA_LEAVES);
		register("sculk_sensor", SoundType.SCULK_SENSOR);
		register("sculk_catalyst", SoundType.SCULK_CATALYST);
		register("sculk", SoundType.SCULK);
		register("sculk_vein", SoundType.SCULK_VEIN);
		register("sculk_shrieker", SoundType.SCULK_SHRIEKER);
		register("glow_lichen", SoundType.GLOW_LICHEN);
		register("deepslate", SoundType.DEEPSLATE);
		register("deepslate_bricks", SoundType.DEEPSLATE_BRICKS);
		register("deepslate_tiles", SoundType.DEEPSLATE_TILES);
		register("polished_deepslate", SoundType.POLISHED_DEEPSLATE);
		register("froglight", SoundType.FROGLIGHT);
		register("frogspawn", SoundType.FROGSPAWN);
		register("mangrove_roots", SoundType.MANGROVE_ROOTS);
		register("muddy_mangrove_roots", SoundType.MUDDY_MANGROVE_ROOTS);
		register("mud", SoundType.MUD);
		register("mud_bricks", SoundType.MUD_BRICKS);
		register("packed_mud", SoundType.PACKED_MUD);
		register("hanging_sign", SoundType.HANGING_SIGN);
		register("nether_wood_hanging_sign", SoundType.NETHER_WOOD_HANGING_SIGN);
		register("bamboo_wood_hanging_sign", SoundType.BAMBOO_WOOD_HANGING_SIGN);
		register("bamboo_wood", SoundType.BAMBOO_WOOD);
		register("nether_wood", SoundType.NETHER_WOOD);
		register("cherry_wood", SoundType.CHERRY_WOOD);
		register("cherry_sapling", SoundType.CHERRY_SAPLING);
		register("cherry_leaves", SoundType.CHERRY_LEAVES);
		register("cherry_wood_hanging_sign", SoundType.CHERRY_WOOD_HANGING_SIGN);
		register("chiseled_bookshelf", SoundType.CHISELED_BOOKSHELF);
		register("suspicious_sand", SoundType.SUSPICIOUS_SAND);
		register("suspicious_gravel", SoundType.SUSPICIOUS_GRAVEL);
		register("decorated_pot", SoundType.DECORATED_POT);
		register("decorated_pot_cracked", SoundType.DECORATED_POT_CRACKED);

	}

	public static SoundType register(final String id, final SoundType soundType) {
		return register(new ResourceLocation(id), soundType);
	}

	public static SoundType register(final ResourceLocation id, final SoundType soundType) {
		SOUND_TYPES_BIMAP.put(id, soundType);
		return soundType;
	}
}
