package com.mcmoddev.golems.main;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.init.Blocks;

public class ExtraGolemsEntities {
	
	private ExtraGolemsEntities() {	}

	//public static final EntityType<GolemBase> CONCRETE = build(EntityConcreteGolem.class, GolemNames.CONCRETE_GOLEM, Blocks.CONCRETE); TODO: Fully implement these

	//public static final EntityType<GolemBase> HARDENED_CLAY = build(EntityHardenedClayGolem.class, GolemNames.TERRACOTTA_GOLEM, Blocks.HARDENED_CLAY);


	//public static final EntityType<GolemBase> STAINED_CLAY = build(EntityStainedClayGolem.class, GolemNames.STAINEDTERRACOTTA_GOLEM, Blocks.STAINED_HARDENED_CLAY);
	//public static final EntityType<GolemBase> STAINED_GLASS = build(EntityStainedGlassGolem.class, GolemNames.STAINEDGLASS_GOLEM, Blocks.STAINED_GLASS);
	
	//public static final EntityType<GolemBase> WOOD = build(EntityWoodenGolem.class, GolemNames.WOODEN_GOLEM, Blocks.LOG, Blocks.LOG2);
	//public static final EntityType<GolemBase> WOOL = build(EntityWoolGolem.class, GolemNames.WOOL_GOLEM, Blocks.WOOL);

	public static void initEntityTypes() {
		// BEDROCK GOLEM
		GolemRegistrar.registerGolem(EntityBedrockGolem.class, new GolemContainer.Builder(GolemNames.BEDROCK_GOLEM, EntityBedrockGolem.class, EntityBedrockGolem::new)
				.setHealth(999.0D).setAttack(32.0D).build());
		// BONE GOLEM
		GolemRegistrar.registerGolem(EntityBoneGolem.class, new GolemContainer.Builder(GolemNames.BONE_GOLEM, EntityBoneGolem.class, EntityBoneGolem::new)
				.setHealth(54.0D).setAttack(9.5D).addBlocks(Blocks.BONE_BLOCK).build());
		// BOOKSHELF GOLEM
		GolemRegistrar.registerGolem(EntityBookshelfGolem.class, new GolemContainer.Builder(GolemNames.BOOKSHELF_GOLEM, EntityBookshelfGolem.class, EntityBookshelfGolem::new)
				.setHealth(28.0D).setAttack(1.5D).addBlocks(Blocks.BOOKSHELF)
				.addSpecial(EntityBookshelfGolem.ALLOW_SPECIAL, Boolean.valueOf(true), "Whether this golem can give itself potion effects")
				.build());
		// CLAY GOLEM
		GolemRegistrar.registerGolem(EntityClayGolem.class, new GolemContainer.Builder(GolemNames.CLAY_GOLEM, EntityClayGolem.class, EntityClayGolem::new)
				.setHealth(20.0D).setAttack(2.0D).addBlocks(Blocks.CLAY).build());
		// COAL GOLEM
		GolemRegistrar.registerGolem(EntityCoalGolem.class,	new GolemContainer.Builder(GolemNames.COAL_GOLEM, EntityCoalGolem.class, EntityCoalGolem::new)
				.setHealth(14.0D).setAttack(2.5D).addBlocks(Blocks.COAL_BLOCK)
				.addSpecial(EntityCoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness")
				.build());
		// CRAFTING GOLEM
		GolemRegistrar.registerGolem(EntityCraftingGolem.class,	new GolemContainer.Builder(GolemNames.CRAFTING_GOLEM, EntityCraftingGolem.class, EntityCraftingGolem::new)
				.setHealth(24.0D).setAttack(2.0D).addBlocks(Blocks.CRAFTING_TABLE)
				.addSpecial(EntityCraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid")
				.build());
		// DIAMOND GOLEM
		GolemRegistrar.registerGolem(EntityDiamondGolem.class,
				new GolemContainer.Builder(GolemNames.DIAMOND_GOLEM, EntityDiamondGolem.class, EntityDiamondGolem::new)
				.setHealth(220.0D).setAttack(20.0D).addBlocks(Blocks.DIAMOND_BLOCK).build());
		// EMERALD GOLEM
		GolemRegistrar.registerGolem(EntityEmeraldGolem.class,
				new GolemContainer.Builder(GolemNames.EMERALD_GOLEM, EntityEmeraldGolem.class, EntityEmeraldGolem::new)
				.setHealth(190.0D).setAttack(18.0D).addBlocks(Blocks.EMERALD_BLOCK).build());
		// ENDSTONE GOLEM
		GolemRegistrar.registerGolem(EntityEndstoneGolem.class,
				new GolemContainer.Builder(GolemNames.ENDSTONE_GOLEM, EntityEndstoneGolem.class, EntityEndstoneGolem::new)
				.setHealth(50.0D).setAttack(8.0D).addBlocks(Blocks.END_STONE)
				.addSpecial(EntityEndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport")
				.addSpecial(EntityEndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water")
				.build());
		// GLASS GOLEM
		GolemRegistrar.registerGolem(EntityGlassGolem.class,
				new GolemContainer.Builder(GolemNames.GLASS_GOLEM, EntityGlassGolem.class, EntityGlassGolem::new)
				.setHealth(8.0D).setAttack(13.0D).addBlocks(Blocks.GLASS).build());
		// GLOWSTONE GOLEM
		GolemRegistrar.registerGolem(EntityGlowstoneGolem.class,
				new GolemContainer.Builder(GolemNames.GLOWSTONE_GOLEM, EntityGlowstoneGolem.class, EntityGlowstoneGolem::new)
				.setHealth(8.0D).setAttack(12.0D).addBlocks(Blocks.GLOWSTONE)
				.addSpecial(EntityGlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can glow")
				.addSpecial(EntityGlowstoneGolem.FREQUENCY, Integer.valueOf(2), "Number of ticks between updating light")
				.build());
		// GOLD GOLEM
		GolemRegistrar.registerGolem(EntityGoldGolem.class,
				new GolemContainer.Builder(GolemNames.GOLD_GOLEM, EntityGoldGolem.class, EntityGoldGolem::new)
				.setHealth(80.0D).setAttack(8.0D).addBlocks(Blocks.GOLD_BLOCK).build());
		// ICE GOLEM
		GolemRegistrar.registerGolem(EntityIceGolem.class,
				new GolemContainer.Builder(GolemNames.ICE_GOLEM, EntityIceGolem.class, EntityIceGolem::new)
				.setHealth(18.0D).setAttack(6.0D).addBlocks(Blocks.PACKED_ICE, Blocks.ICE)
				.addSpecial(EntityIceGolem.AOE, Integer.valueOf(3), "Radial distance at which this golem can freeze / cool liquids (0=disable)")
				.build());
		// LAPIS GOLEM
		GolemRegistrar.registerGolem(EntityLapisGolem.class,
				new GolemContainer.Builder(GolemNames.LAPIS_GOLEM, EntityLapisGolem.class, EntityLapisGolem::new)
				.setHealth(50.0D).setAttack(1.5D).addBlocks(Blocks.LAPIS_BLOCK)
				.addSpecial(EntityLapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects")
				.build());
		// LEAF GOLEM
		GolemRegistrar.registerGolem(EntityLeafGolem.class,
				new GolemContainer.Builder(GolemNames.LEAF_GOLEM, EntityLeafGolem.class, EntityLeafGolem::new)
				.setHealth(6.0D).setAttack(0.5D).addBlocks(Blocks.OAK_LEAVES /* TODO block tags */)
				.addSpecial(EntityLeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself")
				.build());
		// MAGMA GOLEM
		GolemRegistrar.registerGolem(EntityMagmaGolem.class,
				new GolemContainer.Builder(GolemNames.MAGMA_GOLEM, EntityMagmaGolem.class, EntityMagmaGolem::new)
				.setHealth(46.0D).setAttack(4.5D).addBlocks(Blocks.MAGMA_BLOCK)
				.addSpecial(EntityMagmaGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death")
				.addSpecial(EntityMagmaGolem.ALLOW_LAVA_SPECIAL, false, "Whether this golem can slowly melt cobblestone")
				.addSpecial(EntityMagmaGolem.MELT_DELAY, Integer.valueOf(240), "Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
				.addSpecial(EntityMagmaGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire")
				.addSpecial(EntityMagmaGolem.ALLOW_WATER_DAMAGE, true, "When true, water will hurt this golem")
				.build());
		// MELON GOLEM
		GolemRegistrar.registerGolem(EntityMelonGolem.class,
				new GolemContainer.Builder(GolemNames.MELON_GOLEM, EntityMelonGolem.class, EntityMelonGolem::new)
				.setHealth(18.0D).setAttack(1.5D).addBlocks(Blocks.MELON)
				.addSpecial(EntityMelonGolem.ALLOW_HEALING, true, "Whether this golem can occasionally heal")
				.addSpecial(EntityMelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly")
				.addSpecial(EntityMelonGolem.FREQUENCY, Integer.valueOf(240), "Average number of ticks between planting flowers")
				.build());
		// MUSHROOM GOLEM
		GolemRegistrar.registerGolem(EntityMushroomGolem.class,
				new GolemContainer.Builder(GolemNames.MUSHROOM_GOLEM, EntityMushroomGolem.class, EntityMushroomGolem::new)
				.setHealth(30.0D).setAttack(3.0D).addBlocks(Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK)
				.addSpecial(EntityMushroomGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)")
				.addSpecial(EntityMushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly")
				.addSpecial(EntityMushroomGolem.FREQUENCY, Integer.valueOf(420), "Average number of ticks between planting mushrooms")
				.build());
		// NETHER BRICK GOLEM
		GolemRegistrar.registerGolem(EntityNetherBrickGolem.class,
				new GolemContainer.Builder(GolemNames.NETHERBRICK_GOLEM, EntityNetherBrickGolem.class, EntityNetherBrickGolem::new)
				.setHealth(25.0D).setAttack(6.5D).addBlocks(Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS)
				.addSpecial(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire")
				.build());
		// NETHER WART GOLEM
		GolemRegistrar.registerGolem(EntityNetherWartGolem.class,
				new GolemContainer.Builder(GolemNames.NETHERWART_GOLEM, EntityNetherWartGolem.class, EntityNetherWartGolem::new)
				.setHealth(22.0D).setAttack(1.5D).addBlocks(Blocks.NETHER_WART_BLOCK)
				.addSpecial(EntityNetherWartGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)")
				.addSpecial(EntityNetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly")
				.addSpecial(EntityNetherWartGolem.FREQUENCY, Integer.valueOf(880), "Average number of ticks between planting nether wart (if enabled)")
				.build());
		// OBSIDIAN GOLEM
		GolemRegistrar.registerGolem(EntityObsidianGolem.class,
				new GolemContainer.Builder(GolemNames.OBSIDIAN_GOLEM, EntityObsidianGolem.class, EntityObsidianGolem::new)
				.setHealth(120.0D).setAttack(18.0D).addBlocks(Blocks.OBSIDIAN).build());
		// PRISMARINE GOLEM
		GolemRegistrar.registerGolem(EntityPrismarineGolem.class,
				new GolemContainer.Builder(GolemNames.PRISMARINE_GOLEM, EntityPrismarineGolem.class, EntityPrismarineGolem::new)
				.setHealth(24.0D).setAttack(8.0D).addBlocks(Blocks.PRISMARINE).build());
		// QUARTZ GOLEM
		GolemRegistrar.registerGolem(EntityQuartzGolem.class,
				new GolemContainer.Builder(GolemNames.QUARTZ_GOLEM, EntityQuartzGolem.class, EntityQuartzGolem::new)
				.setHealth(85.0D).setAttack(8.5D).addBlocks(Blocks.QUARTZ_BLOCK).build());
		// RED SANDSTONE GOLEM
		GolemRegistrar.registerGolem(EntityRedSandstoneGolem.class,
				new GolemContainer.Builder(GolemNames.REDSANDSTONE_GOLEM, EntityRedSandstoneGolem.class, EntityRedSandstoneGolem::new)
				.setHealth(15.0D).setAttack(4.0D).addBlocks(Blocks.RED_SANDSTONE /* TODO block tags */).build());
		// REDSTONE GOLEM
		GolemRegistrar.registerGolem(EntityRedstoneGolem.class,
				new GolemContainer.Builder(GolemNames.REDSTONE_GOLEM, EntityRedstoneGolem.class, EntityRedstoneGolem::new)
				.setHealth(18.0D).setAttack(2.0D).addBlocks(Blocks.REDSTONE_BLOCK)
				.addSpecial(EntityRedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power")
				.build());
		// SANDSTONE GOLEM
		GolemRegistrar.registerGolem(EntitySandstoneGolem.class,
				new GolemContainer.Builder(GolemNames.SANDSTONE_GOLEM, EntitySandstoneGolem.class, EntitySandstoneGolem::new)
				.setHealth(15.0D).setAttack(4.0D).addBlocks(Blocks.SANDSTONE /* TODO block tags */).build());
		// SEA LANTERN GOLEM
		GolemRegistrar.registerGolem(EntitySeaLanternGolem.class,
				new GolemContainer.Builder(GolemNames.SEALANTERN_GOLEM, EntitySeaLanternGolem.class, EntitySeaLanternGolem::new)
				.setHealth(24.0D).setAttack(6.0D).addBlocks(Blocks.SEA_LANTERN)
				.addSpecial(EntitySeaLanternGolem.ALLOW_SPECIAL, true, "Whether this golem lights up the area")
				.addSpecial(EntitySeaLanternGolem.FREQUENCY, Integer.valueOf(5), "Number of ticks between updating light")
				.build());
		// SLIME GOLEM
		GolemRegistrar.registerGolem(EntitySlimeGolem.class,
				new GolemContainer.Builder(GolemNames.SLIME_GOLEM, EntitySlimeGolem.class, EntitySlimeGolem::new)
				.setHealth(58.0D).setAttack(2.5D).addBlocks(Blocks.SLIME_BLOCK)
				.addSpecial(EntitySlimeGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death")
				.addSpecial(EntitySlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking")
				.addSpecial(EntitySlimeGolem.KNOCKBACK, Double.valueOf(1.1412D), "Slime Golem knockback power (Higher Value = Further Knockback)")
				.build());
		// SPONGE GOLEM
		GolemRegistrar.registerGolem(EntitySpongeGolem.class,
				new GolemContainer.Builder(GolemNames.SPONGE_GOLEM, EntitySpongeGolem.class, EntitySpongeGolem::new)
				.setHealth(20.0D).setAttack(1.5D).addBlocks(Blocks.SPONGE, Blocks.WET_SPONGE)
				.addSpecial(EntitySpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water")
				.addSpecial(EntitySpongeGolem.RANGE, Integer.valueOf(4), "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
				.addSpecial(EntitySpongeGolem.INTERVAL, Integer.valueOf(10), "Number of ticks between each water-check; increase to reduce lag")
				.build());
		// STRAW GOLEM
		GolemRegistrar.registerGolem(EntityStrawGolem.class,
				new GolemContainer.Builder(GolemNames.STRAW_GOLEM, EntityStrawGolem.class, EntityStrawGolem::new)
				.setHealth(10.0D).setAttack(1.0D).addBlocks(Blocks.HAY_BLOCK)
				.addSpecial(EntityStrawGolem.ALLOW_SPECIAL, true, "Whether this golem can speed up crop growth")
				.addSpecial(EntityStrawGolem.SPECIAL_FREQ, Integer.valueOf(460), "Minimum number of ticks between crop-boosts")
				.build());
		// TNT GOLEM
		GolemRegistrar.registerGolem(EntityTNTGolem.class, 
				new GolemContainer.Builder(GolemNames.TNT_GOLEM, EntityTNTGolem.class, EntityTNTGolem::new)
				.setHealth(14.0D).setAttack(2.5D).addBlocks(Blocks.TNT)
				.addSpecial(EntityTNTGolem.ALLOW_SPECIAL, true, "Whether this golem can explode when fighting or dying")
				.build());
		// WOODEN GOLEM
		GolemRegistrar.registerGolem(EntityWoodenGolem.class,
				new GolemContainer.Builder(GolemNames.WOODEN_GOLEM, EntityWoodenGolem.class, EntityWoodenGolem::new)
				.setHealth(20.0D).setAttack(3.0D).addBlocks(Blocks.OAK_LOG /* TODO block tags */).build());
		// WOOL GOLEM
		GolemRegistrar.registerGolem(EntityWoolGolem.class,
				new GolemContainer.Builder(GolemNames.WOOL_GOLEM, EntityWoolGolem.class, EntityWoolGolem::new)
				.setHealth(10.0D).setAttack(1.0D).addBlocks(Blocks.WHITE_WOOL /* TODO block tags */).build());
	}
}
