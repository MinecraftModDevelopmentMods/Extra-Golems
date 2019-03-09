package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.util.GolemLookup;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.function.Function;

import static com.mcmoddev.golems.proxies.ProxyCommon.build;

public final class GolemEntityTypes {
	
	
	private GolemEntityTypes() {
		
	}

	//public static final EntityType<GolemBase> CONCRETE = build(EntityConcreteGolem.class, GolemNames.CONCRETE_GOLEM, Blocks.CONCRETE); TODO: Fully implement these

	//public static final EntityType<GolemBase> HARDENED_CLAY = build(EntityHardenedClayGolem.class, GolemNames.TERRACOTTA_GOLEM, Blocks.HARDENED_CLAY);

	//public static final EntityType<GolemBase> LEAF = build(EntityLeafGolem.class, GolemNames.LEAF_GOLEM, Blocks.LEAVES, Blocks.LEAVES2)

	//public static final EntityType<GolemBase> STAINED_CLAY = build(EntityStainedClayGolem.class, GolemNames.STAINEDTERRACOTTA_GOLEM, Blocks.STAINED_HARDENED_CLAY);
	//public static final EntityType<GolemBase> STAINED_GLASS = build(EntityStainedGlassGolem.class, GolemNames.STAINEDGLASS_GOLEM, Blocks.STAINED_GLASS);
	
	//public static final EntityType<GolemBase> WOOD = build(EntityWoodenGolem.class, GolemNames.WOODEN_GOLEM, Blocks.LOG, Blocks.LOG2);
	//public static final EntityType<GolemBase> WOOL = build(EntityWoolGolem.class, GolemNames.WOOL_GOLEM, Blocks.WOOL);

	static {
		buildEntity(EntityBedrockGolem.class, EntityBedrockGolem::new, GolemNames.BEDROCK_GOLEM, (Block) null);
		buildEntity(EntityBoneGolem.class, EntityBoneGolem::new, GolemNames.BONE_GOLEM, Blocks.BONE_BLOCK);
		buildEntity(EntityBookshelfGolem.class, EntityBookshelfGolem::new, GolemNames.BOOKSHELF_GOLEM, Blocks.BOOKSHELF);
		buildEntity(EntityBookshelfGolem.class, EntityBookshelfGolem::new, GolemNames.BOOKSHELF_GOLEM, Blocks.BOOKSHELF);
		buildEntity(EntityCoalGolem.class, EntityCoalGolem::new, GolemNames.COAL_GOLEM, Blocks.COAL_BLOCK);
		//buildEntity(EntityConcreteGolem.class, EntityConcreteGolem::new, GolemNames.CONCRETE_GOLEM /*TODO: use tags for these things*/);
		buildEntity(EntityCraftingGolem.class, EntityCraftingGolem::new, GolemNames.CRAFTING_GOLEM, Blocks.CRAFTING_TABLE);
		buildEntity(EntityDiamondGolem.class, EntityDiamondGolem::new, GolemNames.DIAMOND_GOLEM, Blocks.DIAMOND_BLOCK);
		buildEntity(EntityEmeraldGolem.class, EntityEmeraldGolem::new, GolemNames.EMERALD_GOLEM, Blocks.EMERALD_BLOCK);
		buildEntity(EntityEndstoneGolem.class, EntityEndstoneGolem::new, GolemNames.ENDSTONE_GOLEM, Blocks.END_STONE);
		buildEntity(EntityGlassGolem.class, EntityGlassGolem::new, GolemNames.GLASS_GOLEM, Blocks.GLASS);
		buildEntity(EntityGlowstoneGolem.class, EntityGlowstoneGolem::new, GolemNames.GLOWSTONE_GOLEM, Blocks.GLOWSTONE);
		buildEntity(EntityGoldGolem.class, EntityGoldGolem::new, GolemNames.GOLD_GOLEM, Blocks.GOLD_BLOCK);
		buildEntity(EntityIceGolem.class, EntityIceGolem::new, GolemNames.ICE_GOLEM, Blocks.PACKED_ICE, Blocks.ICE);
		buildEntity(EntityLapisGolem.class, EntityLapisGolem::new, GolemNames.LAPIS_GOLEM, Blocks.LAPIS_BLOCK);
		buildEntity(EntityMagmaGolem.class, EntityMagmaGolem::new, GolemNames.MAGMA_GOLEM, Blocks.MAGMA_BLOCK);
		buildEntity(EntityMelonGolem.class, EntityMelonGolem::new, GolemNames.MELON_GOLEM, Blocks.MELON);
		buildEntity(EntityMushroomGolem.class, EntityMushroomGolem::new, GolemNames.MUSHROOM_GOLEM, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK);
		buildEntity(EntityNetherBrickGolem.class, EntityNetherBrickGolem::new, GolemNames.NETHERBRICK_GOLEM, Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS);
		buildEntity(EntityNetherWartGolem.class, EntityNetherWartGolem::new, GolemNames.NETHERWART_GOLEM, Blocks.NETHER_WART_BLOCK);
		buildEntity(EntityObsidianGolem.class, EntityObsidianGolem::new, GolemNames.OBSIDIAN_GOLEM, Blocks.OBSIDIAN);
		buildEntity(EntityPrismarineGolem.class, EntityPrismarineGolem::new, GolemNames.PRISMARINE_GOLEM, Blocks.PRISMARINE);
		buildEntity(EntityQuartzGolem.class, EntityQuartzGolem::new, GolemNames.QUARTZ_GOLEM, Blocks.QUARTZ_BLOCK);
		buildEntity(EntityRedSandstoneGolem.class, EntityRedSandstoneGolem::new, GolemNames.REDSANDSTONE_GOLEM, Blocks.RED_SANDSTONE);
		buildEntity(EntityRedstoneGolem.class, EntityRedstoneGolem::new, GolemNames.REDSTONE_GOLEM, Blocks.REDSTONE_BLOCK);
		buildEntity(EntitySandstoneGolem.class, EntityRedSandstoneGolem::new, GolemNames.SANDSTONE_GOLEM, Blocks.SANDSTONE);
		buildEntity(EntitySeaLanternGolem.class, EntitySeaLanternGolem::new, GolemNames.SEALANTERN_GOLEM, Blocks.SEA_LANTERN);
		buildEntity(EntitySlimeGolem.class, EntitySlimeGolem::new, GolemNames.SLIME_GOLEM, Blocks.SLIME_BLOCK);
		buildEntity(EntitySpongeGolem.class, EntitySpongeGolem::new, GolemNames.SPONGE_GOLEM, Blocks.SPONGE);
		buildEntity(EntityStrawGolem.class, EntityStrawGolem::new, GolemNames.STRAW_GOLEM, Blocks.HAY_BLOCK);
		buildEntity(EntityTNTGolem.class, EntityTNTGolem::new, GolemNames.TNT_GOLEM, Blocks.TNT);
	}
	
	private static void buildEntity(final Class<? extends GolemBase> entityClass, Function<? super World, ? extends GolemBase> factoryIn,
					final String name, Block... blocks) {
		GolemLookup.addEntityType(entityClass, build(entityClass, factoryIn, name, blocks));
	}
}
