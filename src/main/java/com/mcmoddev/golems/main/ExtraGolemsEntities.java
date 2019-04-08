package com.mcmoddev.golems.main;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.events.GolemRegistrarEvent;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public final class ExtraGolemsEntities {
	
	/* 
	 * The following tags are not added by regular minecraft.
	 * Technically they should be registered and built the vanilla way,
	 * but since we know they're not going to change, we can just
	 * make and use them like this (for now)
	 */
	
	public static final Tag<Block> TAG_CONCRETE = new Tag.Builder<Block>()
			.add(Blocks.BLACK_CONCRETE, Blocks.BLUE_CONCRETE, Blocks.BROWN_CONCRETE,
				Blocks.CYAN_CONCRETE, Blocks.GRAY_CONCRETE, Blocks.GREEN_CONCRETE,
				Blocks.LIGHT_BLUE_CONCRETE, Blocks.LIGHT_GRAY_CONCRETE, Blocks.LIME_CONCRETE,
				Blocks.MAGENTA_CONCRETE, Blocks.ORANGE_CONCRETE, Blocks.PINK_CONCRETE,
				Blocks.PURPLE_CONCRETE, Blocks.RED_CONCRETE, Blocks.WHITE_CONCRETE,
				Blocks.YELLOW_CONCRETE).build(new ResourceLocation(ExtraGolems.MODID, "concrete"));
	
	public static final Tag<Block> TAG_SANDSTONE = new Tag.Builder<Block>()
			.add(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.SMOOTH_SANDSTONE)
			.build(new ResourceLocation(ExtraGolems.MODID, "sandstone"));
	
	public static final Tag<Block> TAG_RED_SANDSTONE = new Tag.Builder<Block>()
			.add(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE)
			.build(new ResourceLocation(ExtraGolems.MODID, "red_sandstone"));
	
	public static final Tag<Block> TAG_PRISMARINE = new Tag.Builder<Block>()
			.add(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE, Blocks.PRISMARINE_BRICKS)
			.build(new ResourceLocation(ExtraGolems.MODID, "prismarine"));
	
	public static final Tag<Block> TAG_STAINED_GLASS = new Tag.Builder<Block>()
			.add(Blocks.BLACK_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS,
					Blocks.CYAN_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS,
					Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIME_STAINED_GLASS,
					Blocks.MAGENTA_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.PINK_STAINED_GLASS,
					Blocks.PURPLE_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS,
					Blocks.YELLOW_STAINED_GLASS)
			.build(new ResourceLocation(ExtraGolems.MODID, "stained_glass"));
	
	public static final Tag<Block> TAG_TERRACOTTA = new Tag.Builder<Block>()
			.add(Blocks.BLACK_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA,
					Blocks.CYAN_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.GREEN_TERRACOTTA,
					Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.LIME_TERRACOTTA,
					Blocks.MAGENTA_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.PINK_TERRACOTTA,
					Blocks.PURPLE_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.WHITE_TERRACOTTA,
					Blocks.YELLOW_TERRACOTTA)
			.build(new ResourceLocation(ExtraGolems.MODID, "terracotta"));
	
	public static final Tag<Block> TAG_QUARTZ = new Tag.Builder<Block>()
			.add(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.SMOOTH_QUARTZ)
			.build(new ResourceLocation(ExtraGolems.MODID, "quartz"));
	
	private ExtraGolemsEntities() {	}

	public static void initEntityTypes() {
		// BEDROCK GOLEM
		GolemRegistrar.registerGolem(EntityBedrockGolem.class, new GolemContainer.Builder(GolemNames.BEDROCK_GOLEM, EntityBedrockGolem.class, EntityBedrockGolem::new)
				.setHealth(999.0D).setAttack(32.0D).build());
		// BONE GOLEM
		GolemRegistrar.registerGolem(EntityBoneGolem.class, new GolemContainer.Builder(GolemNames.BONE_GOLEM, EntityBoneGolem.class, EntityBoneGolem::new)
				.setHealth(40.0D).setAttack(9.5D).setSpeed(0.30D).addBlocks(Blocks.BONE_BLOCK).build());
		// BOOKSHELF GOLEM
		GolemRegistrar.registerGolem(EntityBookshelfGolem.class, new GolemContainer.Builder(GolemNames.BOOKSHELF_GOLEM, EntityBookshelfGolem.class, EntityBookshelfGolem::new)
				.setHealth(28.0D).setAttack(1.5D).setSpeed(0.29D).addBlocks(Blocks.BOOKSHELF)
				.addSpecial(EntityBookshelfGolem.ALLOW_SPECIAL, Boolean.valueOf(true), "Whether this golem can give itself potion effects")
				.build());
		// CLAY GOLEM
		GolemRegistrar.registerGolem(EntityClayGolem.class, new GolemContainer.Builder(GolemNames.CLAY_GOLEM, EntityClayGolem.class, EntityClayGolem::new)
				.setHealth(20.0D).setAttack(2.0D).addBlocks(Blocks.CLAY).build());
		// COAL GOLEM
		GolemRegistrar.registerGolem(EntityCoalGolem.class,	new GolemContainer.Builder(GolemNames.COAL_GOLEM, EntityCoalGolem.class, EntityCoalGolem::new)
				.setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).addBlocks(Blocks.COAL_BLOCK)
				.addSpecial(EntityCoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness")
				.build());
		// CONCRETE GOLEM
		GolemRegistrar.registerGolem(EntityConcreteGolem.class, new GolemContainer.Builder(GolemNames.CONCRETE_GOLEM, EntityConcreteGolem.class, EntityConcreteGolem::new)
				.setHealth(38.0D).setAttack(5.0D).setSpeed(0.26D).addBlocks(TAG_CONCRETE)
				.addSpecial(EntityConcreteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes")
				.build());
		// CRAFTING GOLEM
		GolemRegistrar.registerGolem(EntityCraftingGolem.class,	new GolemContainer.Builder(GolemNames.CRAFTING_GOLEM, EntityCraftingGolem.class, EntityCraftingGolem::new)
				.setHealth(24.0D).setAttack(2.0D).setSpeed(0.29D).addBlocks(Blocks.CRAFTING_TABLE)
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
				.setHealth(50.0D).setAttack(8.0D).setSpeed(0.26D).addBlocks(Blocks.END_STONE)
				.addSpecial(EntityEndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport")
				.addSpecial(EntityEndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water")
				.build());
		// GLASS GOLEM
		GolemRegistrar.registerGolem(EntityGlassGolem.class,
				new GolemContainer.Builder(GolemNames.GLASS_GOLEM, EntityGlassGolem.class, EntityGlassGolem::new)
				.setHealth(8.0D).setAttack(13.0D).setSpeed(0.30D).addBlocks(Blocks.GLASS).build());
		// GLOWSTONE GOLEM
		GolemRegistrar.registerGolem(EntityGlowstoneGolem.class,
				new GolemContainer.Builder(GolemNames.GLOWSTONE_GOLEM, EntityGlowstoneGolem.class, EntityGlowstoneGolem::new)
				.setHealth(8.0D).setAttack(12.0D).setSpeed(0.26D).addBlocks(Blocks.GLOWSTONE)
				.addSpecial(EntityGlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can glow")
// Removed		.addSpecial(EntityGlowstoneGolem.FREQUENCY, Integer.valueOf(2), "Number of ticks between updating light")
				.build());
		// GOLD GOLEM
		GolemRegistrar.registerGolem(EntityGoldGolem.class,
				new GolemContainer.Builder(GolemNames.GOLD_GOLEM, EntityGoldGolem.class, EntityGoldGolem::new)
				.setHealth(80.0D).setAttack(8.0D).setSpeed(0.21D).addBlocks(Blocks.GOLD_BLOCK).build());
		// ICE GOLEM
		GolemRegistrar.registerGolem(EntityIceGolem.class,
				new GolemContainer.Builder(GolemNames.ICE_GOLEM, EntityIceGolem.class, EntityIceGolem::new)
				.setHealth(18.0D).setAttack(6.0D).setSpeed(0.27D).addBlocks(BlockTags.ICE)
				.addSpecial(EntityIceGolem.AOE, Integer.valueOf(3), "Radial distance at which this golem can freeze / cool liquids (0=disable)")
				.addSpecial(EntityIceGolem.FROST, false, "When true, this golem places frosted (temporary) ice")
				.build());
		// LAPIS GOLEM
		GolemRegistrar.registerGolem(EntityLapisGolem.class,
				new GolemContainer.Builder(GolemNames.LAPIS_GOLEM, EntityLapisGolem.class, EntityLapisGolem::new)
				.setHealth(50.0D).setAttack(1.5D).setSpeed(0.285D).addBlocks(Blocks.LAPIS_BLOCK)
				.addSpecial(EntityLapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects")
				.build());
		// LEAF GOLEM
		GolemRegistrar.registerGolem(EntityLeafGolem.class,
				new GolemContainer.Builder(GolemNames.LEAF_GOLEM, EntityLeafGolem.class, EntityLeafGolem::new)
				.setHealth(6.0D).setAttack(0.5D).setSpeed(0.31D).addBlocks(BlockTags.LEAVES)
				.addSpecial(EntityLeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself")
				.build());
		// MAGMA GOLEM
		GolemRegistrar.registerGolem(EntityMagmaGolem.class,
				new GolemContainer.Builder(GolemNames.MAGMA_GOLEM, EntityMagmaGolem.class, EntityMagmaGolem::new)
				.setHealth(46.0D).setAttack(4.5D).setSpeed(0.28D).addBlocks(Blocks.MAGMA_BLOCK)
				.addSpecial(EntityMagmaGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death")
				.addSpecial(EntityMagmaGolem.ALLOW_LAVA_SPECIAL, false, "Whether this golem can slowly melt cobblestone")
				.addSpecial(EntityMagmaGolem.MELT_DELAY, Integer.valueOf(240), "Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
				.addSpecial(EntityMagmaGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire")
				.addSpecial(EntityMagmaGolem.ALLOW_WATER_DAMAGE, true, "When true, water will hurt this golem")
				.build());
		// MELON GOLEM
		GolemRegistrar.registerGolem(EntityMelonGolem.class,
				new GolemContainer.Builder(GolemNames.MELON_GOLEM, EntityMelonGolem.class, EntityMelonGolem::new)
				.setHealth(18.0D).setAttack(1.5D).setSpeed(0.265D).addBlocks(Blocks.MELON)
				.addSpecial(EntityMelonGolem.ALLOW_HEALING, true, "Whether this golem can occasionally heal")
				.addSpecial(EntityMelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly")
				.addSpecial(EntityMelonGolem.FREQUENCY, Integer.valueOf(240), "Average number of ticks between planting flowers")
				.build());
		// MUSHROOM GOLEM
		GolemRegistrar.registerGolem(EntityMushroomGolem.class,
				new GolemContainer.Builder(GolemNames.MUSHROOM_GOLEM, EntityMushroomGolem.class, EntityMushroomGolem::new)
				.setHealth(30.0D).setAttack(3.0D).setSpeed(0.30D).addBlocks(Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK)
				.addSpecial(EntityMushroomGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)")
				.addSpecial(EntityMushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly")
				.addSpecial(EntityMushroomGolem.FREQUENCY, Integer.valueOf(420), "Average number of ticks between planting mushrooms")
				.build());
		// NETHER BRICK GOLEM
		GolemRegistrar.registerGolem(EntityNetherBrickGolem.class,
				new GolemContainer.Builder(GolemNames.NETHERBRICK_GOLEM, EntityNetherBrickGolem.class, EntityNetherBrickGolem::new)
				.setHealth(25.0D).setAttack(6.5D).setSpeed(0.28D).addBlocks(Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS)
				.addSpecial(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire")
				.build());
		// NETHER WART GOLEM
		GolemRegistrar.registerGolem(EntityNetherWartGolem.class,
				new GolemContainer.Builder(GolemNames.NETHERWART_GOLEM, EntityNetherWartGolem.class, EntityNetherWartGolem::new)
				.setHealth(22.0D).setAttack(1.5D).setSpeed(0.26D).addBlocks(Blocks.NETHER_WART_BLOCK)
				.addSpecial(EntityNetherWartGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)")
				.addSpecial(EntityNetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly")
				.addSpecial(EntityNetherWartGolem.FREQUENCY, Integer.valueOf(880), "Average number of ticks between planting nether wart (if enabled)")
				.build());
		// OBSIDIAN GOLEM
		GolemRegistrar.registerGolem(EntityObsidianGolem.class,
				new GolemContainer.Builder(GolemNames.OBSIDIAN_GOLEM, EntityObsidianGolem.class, EntityObsidianGolem::new)
				.setHealth(120.0D).setAttack(18.0D).setSpeed(0.23D).addBlocks(Blocks.OBSIDIAN).build());
		// PRISMARINE GOLEM
		GolemRegistrar.registerGolem(EntityPrismarineGolem.class,
				new GolemContainer.Builder(GolemNames.PRISMARINE_GOLEM, EntityPrismarineGolem.class, EntityPrismarineGolem::new)
				.setHealth(34.0D).setAttack(8.0D).addBlocks(TAG_PRISMARINE).build());
		// QUARTZ GOLEM
		GolemRegistrar.registerGolem(EntityQuartzGolem.class,
				new GolemContainer.Builder(GolemNames.QUARTZ_GOLEM, EntityQuartzGolem.class, EntityQuartzGolem::new)
				.setHealth(85.0D).setAttack(8.5D).setSpeed(0.28D).addBlocks(TAG_QUARTZ).build());
		// RED SANDSTONE GOLEM
		GolemRegistrar.registerGolem(EntityRedSandstoneGolem.class,
				new GolemContainer.Builder(GolemNames.REDSANDSTONE_GOLEM, EntityRedSandstoneGolem.class, EntityRedSandstoneGolem::new)
				.setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).addBlocks(TAG_RED_SANDSTONE).build());
		// REDSTONE GOLEM
		GolemRegistrar.registerGolem(EntityRedstoneGolem.class,
				new GolemContainer.Builder(GolemNames.REDSTONE_GOLEM, EntityRedstoneGolem.class, EntityRedstoneGolem::new)
				.setHealth(18.0D).setAttack(2.0D).setSpeed(0.26D).addBlocks(Blocks.REDSTONE_BLOCK)
				.addSpecial(EntityRedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power")
				.build());
		// SANDSTONE GOLEM
		GolemRegistrar.registerGolem(EntitySandstoneGolem.class,
				new GolemContainer.Builder(GolemNames.SANDSTONE_GOLEM, EntitySandstoneGolem.class, EntitySandstoneGolem::new)
				.setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).addBlocks(TAG_SANDSTONE).build());
		// SEA LANTERN GOLEM
		GolemRegistrar.registerGolem(EntitySeaLanternGolem.class,
				new GolemContainer.Builder(GolemNames.SEALANTERN_GOLEM, EntitySeaLanternGolem.class, EntitySeaLanternGolem::new)
				.setHealth(34.0D).setAttack(6.0D).setSpeed(0.26D).addBlocks(Blocks.SEA_LANTERN)
				.addSpecial(EntitySeaLanternGolem.ALLOW_SPECIAL, true, "Whether this golem lights up the area")
// Removed		.addSpecial(EntitySeaLanternGolem.FREQUENCY, Integer.valueOf(5), "Number of ticks between updating light")
				.build());
		// SLIME GOLEM
		GolemRegistrar.registerGolem(EntitySlimeGolem.class,
				new GolemContainer.Builder(GolemNames.SLIME_GOLEM, EntitySlimeGolem.class, EntitySlimeGolem::new)
				.setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).addBlocks(Blocks.SLIME_BLOCK)
				.addSpecial(EntitySlimeGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death")
				.addSpecial(EntitySlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking")
				.addSpecial(EntitySlimeGolem.KNOCKBACK, Double.valueOf(1.0412D), "Slime Golem knockback power (Higher Value = Further Knockback)")
				.build());
		// SPONGE GOLEM
		GolemRegistrar.registerGolem(EntitySpongeGolem.class,
				new GolemContainer.Builder(GolemNames.SPONGE_GOLEM, EntitySpongeGolem.class, EntitySpongeGolem::new)
				.setHealth(20.0D).setAttack(1.5D).addBlocks(Blocks.SPONGE, Blocks.WET_SPONGE)
				.addSpecial(EntitySpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water")
				.addSpecial(EntitySpongeGolem.RANGE, Integer.valueOf(5), "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
				.addSpecial(EntitySpongeGolem.INTERVAL, Integer.valueOf(10), "Number of ticks between each water-check; increase to reduce lag")
				.build());
		// STAINED GLASS GOLEM
		GolemRegistrar.registerGolem(EntityStainedGlassGolem.class, 
				new GolemContainer.Builder(GolemNames.STAINEDGLASS_GOLEM, EntityStainedGlassGolem.class, EntityStainedGlassGolem::new)
				.setHealth(9.0D).setAttack(12.0D).setSpeed(0.29D).addBlocks(TAG_STAINED_GLASS)
				.build());
		// STAINED TERRACOTTA GOLEM
		GolemRegistrar.registerGolem(EntityStainedClayGolem.class, 
				new GolemContainer.Builder(GolemNames.STAINEDTERRACOTTA_GOLEM, EntityStainedClayGolem.class, EntityStainedClayGolem::new)
				.setHealth(42.0D).setAttack(4.0D).setSpeed(0.22D).addBlocks(TAG_TERRACOTTA)
				.build());
		// STRAW GOLEM
		GolemRegistrar.registerGolem(EntityStrawGolem.class,
				new GolemContainer.Builder(GolemNames.STRAW_GOLEM, EntityStrawGolem.class, EntityStrawGolem::new)
				.setHealth(10.0D).setAttack(1.0D).setSpeed(0.32D).addBlocks(Blocks.HAY_BLOCK)
				.addSpecial(EntityStrawGolem.ALLOW_SPECIAL, true, "Whether this golem can speed up crop growth")
				.addSpecial(EntityStrawGolem.SPECIAL_FREQ, Integer.valueOf(460), "Minimum number of ticks between crop-boosts")
				.build());
		// TERRACOTTA GOLEM
		GolemRegistrar.registerGolem(EntityTerracottaGolem.class, 
				new GolemContainer.Builder(GolemNames.TERRACOTTA_GOLEM, EntityTerracottaGolem.class, EntityTerracottaGolem::new)
				.setHealth(42.0D).setAttack(4.0D).setSpeed(0.208D).addBlocks(Blocks.TERRACOTTA)
				.build());
		// TNT GOLEM
		GolemRegistrar.registerGolem(EntityTNTGolem.class, 
				new GolemContainer.Builder(GolemNames.TNT_GOLEM, EntityTNTGolem.class, EntityTNTGolem::new)
				.setHealth(14.0D).setAttack(2.5D).setSpeed(0.26D).addBlocks(Blocks.TNT)
				.addSpecial(EntityTNTGolem.ALLOW_SPECIAL, true, "Whether this golem can explode when fighting or dying")
				.build());
		// WOODEN GOLEM
		GolemRegistrar.registerGolem(EntityWoodenGolem.class,
				new GolemContainer.Builder(GolemNames.WOODEN_GOLEM, EntityWoodenGolem.class, EntityWoodenGolem::new)
				.setHealth(20.0D).setAttack(3.0D).setSpeed(0.298D).addBlocks(BlockTags.LOGS).build());
		// WOOL GOLEM
		GolemRegistrar.registerGolem(EntityWoolGolem.class,
				new GolemContainer.Builder(GolemNames.WOOL_GOLEM, EntityWoolGolem.class, EntityWoolGolem::new)
				.setHealth(10.0D).setAttack(1.0D).setSpeed(0.295D).addBlocks(BlockTags.WOOL).build());
		
		// fire GolemRegistrar event for any listening child mods (addons)
		MinecraftForge.EVENT_BUS.post(new GolemRegistrarEvent());
	}
}
