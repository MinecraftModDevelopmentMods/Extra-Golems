package com.mcmoddev.golems.main;

import com.mcmoddev.golems.entity.EntityBedrockGolem;
import com.mcmoddev.golems.entity.EntityBoneGolem;
import com.mcmoddev.golems.entity.EntityBookshelfGolem;
import com.mcmoddev.golems.entity.EntityClayGolem;
import com.mcmoddev.golems.entity.EntityCoalGolem;
import com.mcmoddev.golems.entity.EntityConcreteGolem;
import com.mcmoddev.golems.entity.EntityCraftingGolem;
import com.mcmoddev.golems.entity.EntityDiamondGolem;
import com.mcmoddev.golems.entity.EntityEmeraldGolem;
import com.mcmoddev.golems.entity.EntityEndstoneGolem;
import com.mcmoddev.golems.entity.EntityGlassGolem;
import com.mcmoddev.golems.entity.EntityGlowstoneGolem;
import com.mcmoddev.golems.entity.EntityGoldGolem;
import com.mcmoddev.golems.entity.EntityIceGolem;
import com.mcmoddev.golems.entity.EntityLapisGolem;
import com.mcmoddev.golems.entity.EntityLeafGolem;
import com.mcmoddev.golems.entity.EntityMagmaGolem;
import com.mcmoddev.golems.entity.EntityMelonGolem;
import com.mcmoddev.golems.entity.EntityMushroomGolem;
import com.mcmoddev.golems.entity.EntityNetherBrickGolem;
import com.mcmoddev.golems.entity.EntityNetherWartGolem;
import com.mcmoddev.golems.entity.EntityObsidianGolem;
import com.mcmoddev.golems.entity.EntityPrismarineGolem;
import com.mcmoddev.golems.entity.EntityQuartzGolem;
import com.mcmoddev.golems.entity.EntityRedSandstoneGolem;
import com.mcmoddev.golems.entity.EntityRedstoneGolem;
import com.mcmoddev.golems.entity.EntityRedstoneLampGolem;
import com.mcmoddev.golems.entity.EntitySandstoneGolem;
import com.mcmoddev.golems.entity.EntitySeaLanternGolem;
import com.mcmoddev.golems.entity.EntitySlimeGolem;
import com.mcmoddev.golems.entity.EntitySpongeGolem;
import com.mcmoddev.golems.entity.EntityStainedClayGolem;
import com.mcmoddev.golems.entity.EntityStainedGlassGolem;
import com.mcmoddev.golems.entity.EntityStrawGolem;
import com.mcmoddev.golems.entity.EntityTNTGolem;
import com.mcmoddev.golems.entity.EntityTerracottaGolem;
import com.mcmoddev.golems.entity.EntityWoodenGolem;
import com.mcmoddev.golems.entity.EntityWoolGolem;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.events.GolemRegistrarEvent;
import com.mcmoddev.golems.util.BlockTagUtil;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemDescription;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;

public final class ExtraGolemsEntities {

	private ExtraGolemsEntities() {	}

	public static void initEntityTypes() {
		// Repeated GolemDescription components
		final GolemDescription FIREPROOF = new GolemDescription(new TranslationTextComponent("entitytip.is_fireproof").applyTextStyle(TextFormatting.GOLD));
		//// technically they all breathe underwater, but we add this to a couple especially sea-worthy golems anyway.
		final GolemDescription BREATHE_WATER = new GolemDescription(new TranslationTextComponent("entitytip.breathes_underwater").applyTextStyle(TextFormatting.AQUA));
		final ITextComponent HEALS = new TranslationTextComponent("entitytip.heals").applyTextStyle(TextFormatting.LIGHT_PURPLE);
		// used for loot tables in multi-colored golems
		final String[] RANGE = new String[GolemMultiColorized.DYE_COLORS.length];
		for(int i = 0, l = RANGE.length; i < l; i++) {
			RANGE[i] = String.valueOf(i);
		}
		
		// BEDROCK GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(
				GolemNames.BEDROCK_GOLEM, EntityBedrockGolem.class, EntityBedrockGolem::new)
				.setHealth(999.0D).setAttack(32.0D)
				.addDesc(new GolemDescription(new TranslationTextComponent("entitytip.indestructible")
							.applyTextStyles(TextFormatting.WHITE, TextFormatting.BOLD)),
						new GolemDescription(new TranslationTextComponent("tooltip.creative_only_item")
							.applyTextStyle(TextFormatting.DARK_RED)))
				.build());
		// BONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BONE_GOLEM, EntityBoneGolem.class, EntityBoneGolem::new)
				.setHealth(40.0D).setAttack(9.5D).setSpeed(0.30D).addBlocks(Blocks.BONE_BLOCK).addLootTable(GolemNames.BONE_GOLEM).build());
		// BOOKSHELF GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BOOKSHELF_GOLEM, EntityBookshelfGolem.class, EntityBookshelfGolem::new)
				.setHealth(28.0D).setAttack(1.5D).setSpeed(0.29D).addBlocks(Blocks.BOOKSHELF).addLootTable(GolemNames.BOOKSHELF_GOLEM)
				.addSpecial(EntityBookshelfGolem.ALLOW_SPECIAL, true, "Whether this golem can give itself potion effects",
						new TranslationTextComponent("entitytip.grants_self_potion_effects").applyTextStyle(TextFormatting.LIGHT_PURPLE))
				.build());
		// CLAY GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CLAY_GOLEM, EntityClayGolem.class, EntityClayGolem::new)
				.setHealth(20.0D).setAttack(2.0D).addBlocks(Blocks.CLAY).addLootTable(GolemNames.CLAY_GOLEM).build());
		// COAL GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.COAL_GOLEM, EntityCoalGolem.class, EntityCoalGolem::new)
				.setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).addBlocks(Blocks.COAL_BLOCK).addLootTable(GolemNames.COAL_GOLEM)
				.addSpecial(EntityCoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness",
						new TranslationTextComponent("entitytip.blinds_creatures").applyTextStyle(TextFormatting.GRAY))
				.build());
		// CONCRETE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CONCRETE_GOLEM, EntityConcreteGolem.class, EntityConcreteGolem::new)
				.setHealth(38.0D).setAttack(5.0D).setSpeed(0.26D).addBlocks(BlockTagUtil.TAG_CONCRETE)
				.addSpecial(EntityConcreteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes",
						new TranslationTextComponent("effect.minecraft.resistance").applyTextStyle(TextFormatting.DARK_GRAY))
				.addLootTables(GolemNames.CONCRETE_GOLEM, RANGE).build());
		// CRAFTING GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CRAFTING_GOLEM, EntityCraftingGolem.class, EntityCraftingGolem::new)
				.setHealth(24.0D).setAttack(2.0D).setSpeed(0.29D).addBlocks(Blocks.CRAFTING_TABLE).addLootTable(GolemNames.CRAFTING_GOLEM)
				.addSpecial(EntityCraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid",
						new TranslationTextComponent("entitytip.click_open_crafting").applyTextStyle(TextFormatting.BLUE))
				.build());
		// DIAMOND GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.DIAMOND_GOLEM, EntityDiamondGolem.class, EntityDiamondGolem::new)
				.setHealth(220.0D).setAttack(20.0D).addBlocks(Blocks.DIAMOND_BLOCK).addLootTable(GolemNames.DIAMOND_GOLEM).build());
		// EMERALD GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.EMERALD_GOLEM, EntityEmeraldGolem.class, EntityEmeraldGolem::new)
				.setHealth(190.0D).setAttack(18.0D).addBlocks(Blocks.EMERALD_BLOCK).addLootTable(GolemNames.EMERALD_GOLEM).build());
		// ENDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.ENDSTONE_GOLEM, EntityEndstoneGolem.class, EntityEndstoneGolem::new)
				.setHealth(50.0D).setAttack(8.0D).setSpeed(0.26D).addBlocks(Blocks.END_STONE, Blocks.END_STONE_BRICKS)
				.addSpecial(EntityEndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water")
				.addSpecial(EntityEndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport",
						new TranslationTextComponent("entitytip.can_teleport").applyTextStyle(TextFormatting.DARK_AQUA))
				.addLootTable(GolemNames.ENDSTONE_GOLEM).build());
		// GLASS GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GLASS_GOLEM, EntityGlassGolem.class, EntityGlassGolem::new)
				.setHealth(8.0D).setAttack(13.0D).setSpeed(0.30D).addBlocks(Blocks.GLASS).addLootTable(GolemNames.GLASS_GOLEM).build());
		// GLOWSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GLOWSTONE_GOLEM, EntityGlowstoneGolem.class, EntityGlowstoneGolem::new)
				.setHealth(8.0D).setAttack(12.0D).setSpeed(0.26D).addBlocks(Blocks.GLOWSTONE).addLootTable(GolemNames.GLOWSTONE_GOLEM)
				.addSpecial(EntityGlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can glow",
						new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.RED))
				.immuneToFire().addDesc(FIREPROOF).build());
		// GOLD GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GOLD_GOLEM, EntityGoldGolem.class, EntityGoldGolem::new)
				.setHealth(80.0D).setAttack(8.0D).setSpeed(0.21D).addBlocks(Blocks.GOLD_BLOCK).addLootTable(GolemNames.GOLD_GOLEM).build());
		// ICE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.ICE_GOLEM, EntityIceGolem.class, EntityIceGolem::new)
				.setHealth(18.0D).setAttack(6.0D).setSpeed(0.27D).addBlocks(BlockTags.ICE).addLootTable(GolemNames.ICE_GOLEM)
				.addSpecial(EntityIceGolem.AOE, Integer.valueOf(3), "Radial distance at which this golem can freeze / cool liquids (0=disable)")
				.addSpecial(EntityIceGolem.FROST, false, "When true, this golem places frosted (temporary) ice")
				.addDesc(new GolemDescription(new TranslationTextComponent("entitytip.freezes_blocks")
					.applyTextStyle(TextFormatting.AQUA), EntityIceGolem.AOE, c -> (Integer)c.get() > 0))
				.build());
		// LAPIS GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.LAPIS_GOLEM, EntityLapisGolem.class, EntityLapisGolem::new)
				.setHealth(50.0D).setAttack(1.5D).setSpeed(0.285D).addBlocks(Blocks.LAPIS_BLOCK).addLootTable(GolemNames.LAPIS_GOLEM)
				.addSpecial(EntityLapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects",
						new TranslationTextComponent("entitytip.attacks_use_potion_effects")
						.applyTextStyle(TextFormatting.LIGHT_PURPLE))
				.build());
		// LEAF GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.LEAF_GOLEM, EntityLeafGolem.class, EntityLeafGolem::new)
				.setHealth(6.0D).setAttack(0.5D).setSpeed(0.31D).addBlocks(BlockTags.LEAVES).addLootTable(GolemNames.LEAF_GOLEM)
				.addSpecial(EntityLeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself",
						new TranslationTextComponent("effect.minecraft.regeneration").applyTextStyle(TextFormatting.DARK_GREEN)
						.appendText(" ").appendSibling(new TranslationTextComponent("enchantment.level.1")))
				.build());
		// MAGMA GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MAGMA_GOLEM, EntityMagmaGolem.class, EntityMagmaGolem::new)
				.setHealth(46.0D).setAttack(4.5D).setSpeed(0.28D).addBlocks(Blocks.MAGMA_BLOCK).addLootTable(GolemNames.MAGMA_GOLEM)
				.addSpecial(EntityMagmaGolem.MELT_DELAY, Integer.valueOf(240), "Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
				.addSpecial(EntityMagmaGolem.ALLOW_WATER_DAMAGE, true, "When true, water will hurt this golem")
				.addSpecial(EntityMagmaGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death",
						new TranslationTextComponent("entitytip.splits_upon_death").applyTextStyle(TextFormatting.RED))
				.addSpecial(EntityMagmaGolem.ALLOW_LAVA_SPECIAL, false, "Whether this golem can slowly melt cobblestone",
						new TranslationTextComponent("entitytip.slowly_melts", 
								new TranslationTextComponent("block.minecraft.cobblestone")).applyTextStyle(TextFormatting.RED))
				.addSpecial(EntityMagmaGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
						new TranslationTextComponent("entitytip.lights_mobs_on_fire").applyTextStyle(TextFormatting.GOLD))
				.immuneToFire().addDesc(FIREPROOF).build());
		// MELON GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MELON_GOLEM, EntityMelonGolem.class, EntityMelonGolem::new)
				.setHealth(18.0D).setAttack(1.5D).setSpeed(0.265D).addBlocks(Blocks.MELON).addLootTable(GolemNames.MELON_GOLEM)
				.addSpecial(EntityMelonGolem.ALLOW_HEALING, true, "Whether this golem can occasionally heal", HEALS)
				.addSpecial(EntityMelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly",
						new TranslationTextComponent("entitytip.plants_flowers", 
								new TranslationTextComponent("tile.flower1.name")).applyTextStyle(TextFormatting.GREEN))
				.addSpecial(EntityMelonGolem.FREQUENCY, Integer.valueOf(240), "Average number of ticks between planting flowers")
				.build());
		// MUSHROOM GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MUSHROOM_GOLEM, EntityMushroomGolem.class, EntityMushroomGolem::new)
				.setHealth(30.0D).setAttack(3.0D).setSpeed(0.30D).addBlocks(Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK)
				.addSpecial(EntityMushroomGolem.FREQUENCY, Integer.valueOf(420), "Average number of ticks between planting mushrooms")
				.addSpecial(EntityMushroomGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)", HEALS)
				.addSpecial(EntityMushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly",
						new TranslationTextComponent("entitytip.plants_shrooms").applyTextStyle(TextFormatting.DARK_GREEN))
				.addLootTables(GolemNames.MUSHROOM_GOLEM, EntityMushroomGolem.SHROOM_TYPES).build());
		// NETHER BRICK GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.NETHERBRICK_GOLEM, EntityNetherBrickGolem.class, EntityNetherBrickGolem::new)
				.setHealth(25.0D).setAttack(6.5D).setSpeed(0.28D).addBlocks(Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS)
				.addSpecial(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
						new TranslationTextComponent("entitytip.lights_mobs_on_fire").applyTextStyle(TextFormatting.RED))
				.immuneToFire().addDesc(FIREPROOF).addLootTable(GolemNames.NETHERBRICK_GOLEM).build());
		// NETHER WART GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.NETHERWART_GOLEM, EntityNetherWartGolem.class, EntityNetherWartGolem::new)
				.setHealth(22.0D).setAttack(1.5D).setSpeed(0.26D).addBlocks(Blocks.NETHER_WART_BLOCK).addLootTable(GolemNames.NETHERWART_GOLEM)
				.addSpecial(EntityNetherWartGolem.FREQUENCY, Integer.valueOf(880), "Average number of ticks between planting nether wart (if enabled)")
				.addSpecial(EntityNetherWartGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)", HEALS)
				.addSpecial(EntityNetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly",
						new TranslationTextComponent("entitytip.plants_warts").applyTextStyle(TextFormatting.RED))
				.immuneToFire().addDesc(FIREPROOF).build());
		// OBSIDIAN GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.OBSIDIAN_GOLEM, EntityObsidianGolem.class, EntityObsidianGolem::new)
				.setHealth(120.0D).setAttack(18.0D).setSpeed(0.23D).addBlocks(Blocks.OBSIDIAN).immuneToFire().addDesc(FIREPROOF)
				.addLootTable(GolemNames.OBSIDIAN_GOLEM).build());
		// PRISMARINE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.PRISMARINE_GOLEM, EntityPrismarineGolem.class, EntityPrismarineGolem::new)
				.setHealth(34.0D).setAttack(8.0D).addBlocks(BlockTagUtil.TAG_PRISMARINE).addLootTable(GolemNames.PRISMARINE_GOLEM)
				.addDesc(BREATHE_WATER).build());
		// QUARTZ GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.QUARTZ_GOLEM, EntityQuartzGolem.class, EntityQuartzGolem::new)
				.setHealth(85.0D).setAttack(8.5D).setSpeed(0.28D).addBlocks(BlockTagUtil.TAG_QUARTZ).addLootTable(GolemNames.QUARTZ_GOLEM).build());
		// RED SANDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSANDSTONE_GOLEM, EntityRedSandstoneGolem.class, EntityRedSandstoneGolem::new)
				.setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).addBlocks(BlockTagUtil.TAG_RED_SANDSTONE)
				.addLootTable(GolemNames.REDSANDSTONE_GOLEM).build());
		// REDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSTONE_GOLEM, EntityRedstoneGolem.class, EntityRedstoneGolem::new)
				.setHealth(18.0D).setAttack(2.0D).setSpeed(0.26D).addBlocks(Blocks.REDSTONE_BLOCK).addLootTable(GolemNames.REDSTONE_GOLEM)
				.addSpecial(EntityRedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power",
						new TranslationTextComponent("entitytip.emits_redstone_signal").applyTextStyle(TextFormatting.RED))
				.build());
		// REDSTONE LAMP GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSTONELAMP_GOLEM, EntityRedstoneLampGolem.class, EntityRedstoneLampGolem::new)
				.setHealth(28.0D).setAttack(6.0D).setSpeed(0.26D).addBlocks(Blocks.REDSTONE_LAMP)
				.addSpecial(EntityRedstoneLampGolem.ALLOW_SPECIAL, true, "Whether this golem can light up the area", 
						new TranslationTextComponent("entitytip.lights_area_toggle").applyTextStyle(TextFormatting.GOLD))
				.addLootTables(GolemNames.REDSTONELAMP_GOLEM, EntityRedstoneLampGolem.VARIANTS).build());
		// SANDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SANDSTONE_GOLEM, EntitySandstoneGolem.class, EntitySandstoneGolem::new)
				.setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).addBlocks(BlockTagUtil.TAG_SANDSTONE).addLootTable(GolemNames.SANDSTONE_GOLEM)
				.build());
		// SEA LANTERN GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SEALANTERN_GOLEM, EntitySeaLanternGolem.class, EntitySeaLanternGolem::new)
				.setHealth(34.0D).setAttack(6.0D).setSpeed(0.26D).addBlocks(Blocks.SEA_LANTERN).addLootTable(GolemNames.SEALANTERN_GOLEM)
				.addSpecial(EntitySeaLanternGolem.ALLOW_SPECIAL, true, "Whether this golem lights up the area",
						new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.GOLD))
				.addDesc(BREATHE_WATER).build());
		// SLIME GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SLIME_GOLEM, EntitySlimeGolem.class, EntitySlimeGolem::new)
				.setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).addBlocks(Blocks.SLIME_BLOCK).addLootTable(GolemNames.SLIME_GOLEM)
				.addSpecial(EntitySlimeGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death",
						new TranslationTextComponent("entitytip.splits_upon_death").applyTextStyle(TextFormatting.GREEN))
				.addSpecial(EntitySlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking",
						new TranslationTextComponent("entitytip.has_knockback").applyTextStyle(TextFormatting.GREEN))
				.addSpecial(EntitySlimeGolem.KNOCKBACK, Double.valueOf(1.0412D), "Slime Golem knockback power (Higher Value = Further Knockback)")
				.build());
		// SPONGE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SPONGE_GOLEM, EntitySpongeGolem.class, EntitySpongeGolem::new)
				.setHealth(20.0D).setAttack(1.5D).addBlocks(Blocks.SPONGE, Blocks.WET_SPONGE).addLootTable(GolemNames.SPONGE_GOLEM)
				.addSpecial(EntitySpongeGolem.RANGE, Integer.valueOf(5), "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
				.addSpecial(EntitySpongeGolem.INTERVAL, Integer.valueOf(10), "Number of ticks between each water-check; increase to reduce lag")
				.addSpecial(EntitySpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water",
						new TranslationTextComponent("entitytip.absorbs_water").applyTextStyle(TextFormatting.GOLD))
				.build());
		// STAINED GLASS GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STAINEDGLASS_GOLEM, EntityStainedGlassGolem.class, EntityStainedGlassGolem::new)
				.setHealth(9.0D).setAttack(12.0D).setSpeed(0.29D).addBlocks(BlockTagUtil.TAG_STAINED_GLASS)
				.addLootTables(GolemNames.STAINEDGLASS_GOLEM, RANGE).build());
		// STAINED TERRACOTTA GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STAINEDTERRACOTTA_GOLEM, EntityStainedClayGolem.class, EntityStainedClayGolem::new)
				.setHealth(42.0D).setAttack(4.0D).setSpeed(0.22D).addBlocks(BlockTagUtil.TAG_TERRACOTTA)
				.addLootTables(GolemNames.STAINEDTERRACOTTA_GOLEM, RANGE).build());
		// STRAW GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STRAW_GOLEM, EntityStrawGolem.class, EntityStrawGolem::new)
				.setHealth(10.0D).setAttack(1.0D).setSpeed(0.32D).addBlocks(Blocks.HAY_BLOCK).addLootTable(GolemNames.STRAW_GOLEM)
				.addSpecial(EntityStrawGolem.SPECIAL_FREQ, Integer.valueOf(460), "Minimum number of ticks between crop-boosts")
				.addSpecial(EntityStrawGolem.ALLOW_SPECIAL, true, "Whether this golem can speed up crop growth",
						new TranslationTextComponent("entitytip.grows_crops").applyTextStyle(TextFormatting.GREEN))
				.build());
		// TERRACOTTA GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.TERRACOTTA_GOLEM, EntityTerracottaGolem.class, EntityTerracottaGolem::new)
				.setHealth(42.0D).setAttack(4.0D).setSpeed(0.208D).addBlocks(Blocks.TERRACOTTA).addLootTable(GolemNames.TERRACOTTA_GOLEM)
				.build());
		// TNT GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.TNT_GOLEM, EntityTNTGolem.class, EntityTNTGolem::new)
				.setHealth(14.0D).setAttack(2.5D).setSpeed(0.26D).addBlocks(Blocks.TNT)
				.addSpecial(EntityTNTGolem.ALLOW_SPECIAL, true, "Whether this golem can explode when fighting or dying",
						new TranslationTextComponent("entitytip.explodes").applyTextStyle(TextFormatting.RED))
				.addLootTable(GolemNames.TNT_GOLEM).build());
		// WOODEN GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.WOODEN_GOLEM, EntityWoodenGolem.class, EntityWoodenGolem::new)
				.setHealth(20.0D).setAttack(3.0D).setSpeed(0.298D).addBlocks(BlockTags.LOGS)
				.addLootTables(GolemNames.WOODEN_GOLEM, EntityWoodenGolem.WOOD_TYPES).build());
		// WOOL GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.WOOL_GOLEM, EntityWoolGolem.class, EntityWoolGolem::new)
				.setHealth(10.0D).setAttack(1.0D).setSpeed(0.295D).addBlocks(BlockTags.WOOL)
				.addLootTables(GolemNames.WOOL_GOLEM, RANGE).build());
		
		// fire GolemRegistrar event for any listening child mods (addons)
		MinecraftForge.EVENT_BUS.post(new GolemRegistrarEvent());
	}
}
