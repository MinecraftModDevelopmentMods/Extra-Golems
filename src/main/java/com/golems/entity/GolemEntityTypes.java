package com.golems.entity;

import com.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;

import static com.golems.proxies.ProxyCommon.build;

public final class GolemEntityTypes {
	private GolemEntityTypes() {

	}

	public static final EntityType BEDROCK = build(EntityBedrockGolem.class, EntityBedrockGolem::new, GolemNames.BEDROCK_GOLEM, (Block) null);
	public static final EntityType BONE = build(EntityBoneGolem.class, EntityBoneGolem::new, GolemNames.BONE_GOLEM, Blocks.BONE_BLOCK);
	public static final EntityType BOOKSHELF = build(EntityBookshelfGolem.class, EntityBookshelfGolem::new, GolemNames.BOOKSHELF_GOLEM, Blocks.BOOKSHELF);
	public static final EntityType CLAY = build(EntityBookshelfGolem.class, EntityBookshelfGolem::new, GolemNames.BOOKSHELF_GOLEM, Blocks.BOOKSHELF);
	public static final EntityType COAL = build(EntityCoalGolem.class, EntityCoalGolem::new, GolemNames.COAL_GOLEM, Blocks.COAL_BLOCK);
	//public static final EntityType CONCRETE = build(EntityConcreteGolem.class, GolemNames.CONCRETE_GOLEM, Blocks.CONCRETE);
	public static final EntityType CRAFTING = build(EntityCraftingGolem.class, EntityCraftingGolem::new, GolemNames.CRAFTING_GOLEM, Blocks.CRAFTING_TABLE);
	public static final EntityType DIAMOND = build(EntityDiamondGolem.class, EntityDiamondGolem::new, GolemNames.DIAMOND_GOLEM, Blocks.DIAMOND_BLOCK);
	public static final EntityType EMERALD = build(EntityEmeraldGolem.class, EntityEmeraldGolem::new, GolemNames.EMERALD_GOLEM, Blocks.EMERALD_BLOCK);
	public static final EntityType ENDSTONE = build(EntityEndstoneGolem.class, EntityEndstoneGolem::new, GolemNames.ENDSTONE_GOLEM, Blocks.END_STONE);
	public static final EntityType GLASS = build(EntityGlassGolem.class, EntityGlassGolem::new, GolemNames.GLASS_GOLEM, Blocks.GLASS);
	public static final EntityType GLOWSTONE = build(EntityGlowstoneGolem.class, EntityGlowstoneGolem::new, GolemNames.GLOWSTONE_GOLEM, Blocks.GLOWSTONE);
	public static final EntityType GOLD = build(EntityGoldGolem.class, EntityGoldGolem::new, GolemNames.GOLD_GOLEM, Blocks.GOLD_BLOCK);
	//public static final EntityType HARDENED_CLAY = build(EntityHardenedClayGolem.class, GolemNames.TERRACOTTA_GOLEM, Blocks.HARDENED_CLAY);
	public static final EntityType ICE = build(EntityIceGolem.class, EntityIceGolem::new, GolemNames.ICE_GOLEM, Blocks.PACKED_ICE, Blocks.ICE);
	public static final EntityType LAPIS = build(EntityLapisGolem.class, EntityLapisGolem::new, GolemNames.LAPIS_GOLEM, Blocks.LAPIS_BLOCK);
	//public static final EntityType LEAF = build(EntityLeafGolem.class, GolemNames.LEAF_GOLEM, Blocks.LEAVES, Blocks.LEAVES2)
	public static final EntityType MAGMA = build(EntityMagmaGolem.class, EntityMagmaGolem::new, GolemNames.MAGMA_GOLEM, Blocks.MAGMA_BLOCK);
	public static final EntityType MELON = build(EntityMelonGolem.class, EntityMelonGolem::new, GolemNames.MELON_GOLEM, Blocks.MELON);
	public static final EntityType MUSHROOM = build(EntityMushroomGolem.class, EntityMushroomGolem::new, GolemNames.MUSHROOM_GOLEM, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK);
	public static final EntityType NETHER_BRICK = build(EntityNetherBrickGolem.class, EntityNetherBrickGolem::new, GolemNames.NETHERBRICK_GOLEM, Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS);
	public static final EntityType NETHER_WART = build(EntityNetherWartGolem.class, EntityNetherWartGolem::new, GolemNames.NETHERWART_GOLEM, Blocks.NETHER_WART_BLOCK);
	public static final EntityType OBSIDIAN = build(EntityObsidianGolem.class, EntityObsidianGolem::new, GolemNames.OBSIDIAN_GOLEM, Blocks.OBSIDIAN);
	public static final EntityType PRISMARINE = build(EntityPrismarineGolem.class, EntityPrismarineGolem::new, GolemNames.PRISMARINE_GOLEM, Blocks.PRISMARINE);
	public static final EntityType QUARTZ = build(EntityQuartzGolem.class, EntityQuartzGolem::new, GolemNames.QUARTZ_GOLEM, Blocks.QUARTZ_BLOCK);
	public static final EntityType RED_SANDSTONE = build(EntityRedSandstoneGolem.class, EntityRedSandstoneGolem::new, GolemNames.REDSANDSTONE_GOLEM, Blocks.RED_SANDSTONE);
	public static final EntityType REDSTONE = build(EntityRedstoneGolem.class, EntityRedstoneGolem::new, GolemNames.REDSTONE_GOLEM, Blocks.REDSTONE_BLOCK);
	public static final EntityType SANDSTONE = build(EntitySandstoneGolem.class, EntityRedSandstoneGolem::new, GolemNames.SANDSTONE_GOLEM, Blocks.SANDSTONE);
	public static final EntityType SEA_LANTERN = build(EntitySeaLanternGolem.class, EntitySeaLanternGolem::new, GolemNames.SEALANTERN_GOLEM, Blocks.SEA_LANTERN);
	public static final EntityType SLIME = build(EntitySlimeGolem.class, EntitySlimeGolem::new, GolemNames.SLIME_GOLEM, Blocks.SLIME_BLOCK);
	public static final EntityType SPONGE = build(EntitySpongeGolem.class, EntitySpongeGolem::new, GolemNames.SPONGE_GOLEM, Blocks.SPONGE);
	//public static final EntityType STAINED_CLAY = build(EntityStainedClayGolem.class, GolemNames.STAINEDTERRACOTTA_GOLEM, Blocks.STAINED_HARDENED_CLAY);
	//public static final EntityType STAINED_GLASS = build(EntityStainedGlassGolem.class, GolemNames.STAINEDGLASS_GOLEM, Blocks.STAINED_GLASS);
	public static final EntityType STRAW = build(EntityStrawGolem.class, EntityStrawGolem::new, GolemNames.STRAW_GOLEM, Blocks.HAY_BLOCK);
	public static final EntityType TNT = build(EntityTNTGolem.class, EntityTNTGolem::new, GolemNames.TNT_GOLEM, Blocks.TNT);
	//public static final EntityType WOOD = build(EntityWoodenGolem.class, GolemNames.WOODEN_GOLEM, Blocks.LOG, Blocks.LOG2);
	//public static final EntityType WOOL = build(EntityWoolGolem.class, GolemNames.WOOL_GOLEM, Blocks.WOOL);

}
