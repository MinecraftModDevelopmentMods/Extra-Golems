package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.function.Function;

public final class GolemEntityTypes {
	
	
	private GolemEntityTypes() {
		
	}

	//public static final EntityType<GolemBase> CONCRETE = build(EntityConcreteGolem.class, GolemNames.CONCRETE_GOLEM, Blocks.CONCRETE); TODO: Fully implement these

	//public static final EntityType<GolemBase> HARDENED_CLAY = build(EntityHardenedClayGolem.class, GolemNames.TERRACOTTA_GOLEM, Blocks.HARDENED_CLAY);


	//public static final EntityType<GolemBase> STAINED_CLAY = build(EntityStainedClayGolem.class, GolemNames.STAINEDTERRACOTTA_GOLEM, Blocks.STAINED_HARDENED_CLAY);
	//public static final EntityType<GolemBase> STAINED_GLASS = build(EntityStainedGlassGolem.class, GolemNames.STAINEDGLASS_GOLEM, Blocks.STAINED_GLASS);
	
	//public static final EntityType<GolemBase> WOOD = build(EntityWoodenGolem.class, GolemNames.WOODEN_GOLEM, Blocks.LOG, Blocks.LOG2);
	//public static final EntityType<GolemBase> WOOL = build(EntityWoolGolem.class, GolemNames.WOOL_GOLEM, Blocks.WOOL);

	public static void init() {
		buildEntity(EntityBedrockGolem.class, EntityBedrockGolem::new, GolemNames.BEDROCK_GOLEM, 999.0D, 32.0D, false, (Block) null);
		buildEntity(EntityBoneGolem.class, EntityBoneGolem::new, GolemNames.BONE_GOLEM, 54.0D, 9.5D, false, Blocks.BONE_BLOCK);
		buildEntity(EntityBookshelfGolem.class, EntityBookshelfGolem::new, GolemNames.BOOKSHELF_GOLEM, 28.0D, 1.5D, true, Blocks.BOOKSHELF);
		buildEntity(EntityClayGolem.class, EntityClayGolem::new, GolemNames.CLAY_GOLEM, 20.0D, 2.0D, false, Blocks.CLAY);
		buildEntity(EntityCoalGolem.class, EntityCoalGolem::new, GolemNames.COAL_GOLEM, 14.0D, 2.5D, false, Blocks.COAL_BLOCK);
		//buildEntity(EntityConcreteGolem.class, EntityConcreteGolem::new, GolemNames.CONCRETE_GOLEM /*TODO: use tags for these things*/);
		buildEntity(EntityCraftingGolem.class, EntityCraftingGolem::new, GolemNames.CRAFTING_GOLEM, 24.0D, 2.0D, true, Blocks.CRAFTING_TABLE);
		buildEntity(EntityDiamondGolem.class, EntityDiamondGolem::new, GolemNames.DIAMOND_GOLEM, 220.0D, 20.0D, false, Blocks.DIAMOND_BLOCK);
		buildEntity(EntityEmeraldGolem.class, EntityEmeraldGolem::new, GolemNames.EMERALD_GOLEM, 190.0D, 18.0D, false, Blocks.EMERALD_BLOCK);
		buildEntity(EntityEndstoneGolem.class, EntityEndstoneGolem::new, GolemNames.ENDSTONE_GOLEM, 50.0D, 8.0D, true, Blocks.END_STONE);
		buildEntity(EntityGlassGolem.class, EntityGlassGolem::new, GolemNames.GLASS_GOLEM, 8.0D, 13.0D, false, Blocks.GLASS);
		buildEntity(EntityGlowstoneGolem.class, EntityGlowstoneGolem::new, GolemNames.GLOWSTONE_GOLEM, 8.0D, 12.0D, true, Blocks.GLOWSTONE);
		buildEntity(EntityGoldGolem.class, EntityGoldGolem::new, GolemNames.GOLD_GOLEM, 80.0D, 8.0D, false, Blocks.GOLD_BLOCK);
		buildEntity(EntityIceGolem.class, EntityIceGolem::new, GolemNames.ICE_GOLEM, 18.0D, 6.0D, true, Blocks.PACKED_ICE, Blocks.ICE);
		buildEntity(EntityLapisGolem.class, EntityLapisGolem::new, GolemNames.LAPIS_GOLEM, 50.0D, 1.5D, true, Blocks.LAPIS_BLOCK);
		buildEntity(EntityLeafGolem.class, EntityLeafGolem::new, GolemNames.LEAF_GOLEM, 6.0D, 0.5D, true, Blocks.OAK_LEAVES /* TODO block tags */);
		buildEntity(EntityMagmaGolem.class, EntityMagmaGolem::new, GolemNames.MAGMA_GOLEM, 46.0D, 4.5D, true, Blocks.MAGMA_BLOCK);
		buildEntity(EntityMelonGolem.class, EntityMelonGolem::new, GolemNames.MELON_GOLEM, 18.0D, 1.5D, true, Blocks.MELON);
		buildEntity(EntityMushroomGolem.class, EntityMushroomGolem::new, GolemNames.MUSHROOM_GOLEM, 30.0D, 3.0D, true, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK);
		buildEntity(EntityNetherBrickGolem.class, EntityNetherBrickGolem::new, GolemNames.NETHERBRICK_GOLEM, 25.0D, 6.5D, true, Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS);
		buildEntity(EntityNetherWartGolem.class, EntityNetherWartGolem::new, GolemNames.NETHERWART_GOLEM, 22.0D, 1.5D, true, Blocks.NETHER_WART_BLOCK);
		buildEntity(EntityObsidianGolem.class, EntityObsidianGolem::new, GolemNames.OBSIDIAN_GOLEM, 120.0D, 18.0D, false, Blocks.OBSIDIAN);
		buildEntity(EntityPrismarineGolem.class, EntityPrismarineGolem::new, GolemNames.PRISMARINE_GOLEM, 24.0D, 8.0D, false, Blocks.PRISMARINE);
		buildEntity(EntityQuartzGolem.class, EntityQuartzGolem::new, GolemNames.QUARTZ_GOLEM, 85.0D, 8.5D, false, Blocks.QUARTZ_BLOCK);
		buildEntity(EntityRedSandstoneGolem.class, EntityRedSandstoneGolem::new, GolemNames.REDSANDSTONE_GOLEM, 15.0D, 4.0D, false, Blocks.RED_SANDSTONE);
		buildEntity(EntityRedstoneGolem.class, EntityRedstoneGolem::new, GolemNames.REDSTONE_GOLEM, 18.0D, 2.0D, true, Blocks.REDSTONE_BLOCK);
		buildEntity(EntitySandstoneGolem.class, EntityRedSandstoneGolem::new, GolemNames.SANDSTONE_GOLEM, 15.0D, 4.0D, false, Blocks.SANDSTONE);
		buildEntity(EntitySeaLanternGolem.class, EntitySeaLanternGolem::new, GolemNames.SEALANTERN_GOLEM, 24.0D, 6.0D, true, Blocks.SEA_LANTERN);
		buildEntity(EntitySlimeGolem.class, EntitySlimeGolem::new, GolemNames.SLIME_GOLEM, 58.0D, 2.5D, true, Blocks.SLIME_BLOCK);
		buildEntity(EntitySpongeGolem.class, EntitySpongeGolem::new, GolemNames.SPONGE_GOLEM, 20.0D, 1.5D, true, Blocks.SPONGE);
		buildEntity(EntityStrawGolem.class, EntityStrawGolem::new, GolemNames.STRAW_GOLEM, 10.0D, 1.0D, true, Blocks.HAY_BLOCK);
		buildEntity(EntityTNTGolem.class, EntityTNTGolem::new, GolemNames.TNT_GOLEM, 14.0D, 2.5D, true, Blocks.TNT);
		buildEntity(EntityWoodenGolem.class, EntityWoodenGolem::new, GolemNames.WOODEN_GOLEM, 20.0D, 3.0D, false, Blocks.OAK_LOG /* TODO block tags */);
	}
	//TODO: Take a look again later
	private static void buildEntity(final Class<? extends GolemBase> entityClass, Function<? super World, ? extends GolemBase> factoryIn,
			final String name, final double baseHealth, final double baseAttack, final boolean useSpecial,
			final Block... blocks) {
		final GolemContainer.Builder containerBuilder = 
				new GolemContainer.Builder(ExtraGolems.MODID, name, entityClass, factoryIn)
				.addValidBlocks(blocks).setHealth(baseHealth).setAttack(baseAttack);
		GolemRegistrar.registerGolem(entityClass, containerBuilder.build());
	}
}
