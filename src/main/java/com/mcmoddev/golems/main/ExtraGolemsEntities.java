package com.mcmoddev.golems.main;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.events.GolemRegistrarEvent;
import com.mcmoddev.golems.util.BlockTagUtil;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemContainer.SwimMode;
import com.mcmoddev.golems.util.config.GolemDescription;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;

public final class ExtraGolemsEntities {

	private ExtraGolemsEntities() {
	}

	public static void initEntityTypes() {
		// Frequently-used GolemDescription components
		final GolemDescription FIREPROOF = new GolemDescription(new TranslationTextComponent("entitytip.is_fireproof").applyTextStyle(TextFormatting.GOLD));
		final GolemDescription SWIMS = new GolemDescription(new TranslationTextComponent("entitytip.advanced_swim").applyTextStyle(TextFormatting.AQUA));
		final ITextComponent HEALS = new TranslationTextComponent("entitytip.heals").applyTextStyle(TextFormatting.LIGHT_PURPLE);
		// used for loot tables in multi-colored golems
		final String[] RANGE = new String[GolemMultiColorized.DYE_COLORS.length];
		for (int i = 0, l = RANGE.length; i < l; i++) {
			RANGE[i] = String.valueOf(i);
		}

		// BEDROCK GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(
				GolemNames.BEDROCK_GOLEM, BedrockGolem.class, BedrockGolem::new)
				.setHealth(999.0D).setAttack(32.0D).setKnockback(1.0D).basicTexture()
				.addDesc(new GolemDescription(new TranslationTextComponent("entitytip.indestructible")
								.applyTextStyles(TextFormatting.WHITE, TextFormatting.BOLD)),
						new GolemDescription(new TranslationTextComponent("tooltip.creative_only_item")
								.applyTextStyle(TextFormatting.DARK_RED)))
				.build());
		// BONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BONE_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(40.0D).setAttack(9.5D).setSpeed(0.30D).addBlocks(Blocks.BONE_BLOCK)
				.setTexture(makeTexture(GolemNames.BONE_GOLEM + "_skeleton")).enableFallDamage().build());
		// BOOKSHELF GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.BOOKSHELF_GOLEM, BookshelfGolem.class, BookshelfGolem::new)
				.setHealth(28.0D).setAttack(1.5D).setSpeed(0.29D).addBlocks(Blocks.BOOKSHELF).basicTexture()
				.addSpecial(BookshelfGolem.ALLOW_SPECIAL, true, "Whether this golem can give itself potion effects",
						new TranslationTextComponent("entitytip.grants_self_potion_effects").applyTextStyle(TextFormatting.LIGHT_PURPLE))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_WOOD_STEP).build());
		// CLAY GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CLAY_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(20.0D).setAttack(2.0D).addBlocks(Blocks.CLAY).basicTexture().setSound(SoundEvents.BLOCK_GRAVEL_STEP).build());
		// COAL GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.COAL_GOLEM, CoalGolem.class, CoalGolem::new)
				.setHealth(24.0D).setAttack(2.5D).setSpeed(0.28D).setKnockback(0.2D).addBlocks(Blocks.COAL_BLOCK).basicTexture()
				.addSpecial(CoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness",
						new TranslationTextComponent("entitytip.blinds_creatures").applyTextStyle(TextFormatting.GRAY))
				.build());
		// CONCRETE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CONCRETE_GOLEM, ConcreteGolem.class, ConcreteGolem::new)
				.setHealth(38.0D).setAttack(5.0D).setSpeed(0.26D).addBlocks(BlockTagUtil.TAG_CONCRETE)
				.addSpecial(ConcreteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes",
						new TranslationTextComponent("effect.minecraft.resistance").applyTextStyle(TextFormatting.DARK_GRAY))
				.build());
		// CORAL GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CORAL_GOLEM, CoralGolem.class, CoralGolem::new)
				.setHealth(38.0D).setAttack(6.0D).setSpeed(0.29D).setSwimMode(SwimMode.SWIM)
				.addDesc(SWIMS).setSound(SoundEvents.BLOCK_CORAL_BLOCK_STEP)
				.addSpecial(CoralGolem.ALLOW_HEALING, true, "Whether this golem can occasionally heal when wet", HEALS)
				.addSpecial(CoralGolem.DRY_TIMER, 425, "Number of ticks golem can stay out of water before drying out")
				.addBlocks(BlockTags.CORAL_BLOCKS).addBlocks(BlockTagUtil.TAG_DEAD_CORAL_BLOCKS).build());
		// CRAFTING_GOLEM GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.CRAFTING_GOLEM, CraftingGolem.class, CraftingGolem::new)
				.setHealth(24.0D).setAttack(2.0D).setSpeed(0.29D).addBlocks(Blocks.CRAFTING_TABLE).basicTexture()
				.addSpecial(CraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid",
						new TranslationTextComponent("entitytip.click_open_crafting").applyTextStyle(TextFormatting.BLUE))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_WOOD_STEP).build());
		// DIAMOND GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.DIAMOND_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(220.0D).setAttack(20.0D).setKnockback(0.8D).addBlocks(Blocks.DIAMOND_BLOCK).basicTexture().build());
		// DISPENSER GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.DISPENSER_GOLEM, DispenserGolem.class, DispenserGolem::new)
				// TODO attributes
				
				.addSpecial(DispenserGolem.ALLOW_SPECIAL, true, "Whether the golem can shoot arrows",
						new TranslationTextComponent("entitytip.shoots_arrows").applyTextStyle(TextFormatting.LIGHT_PURPLE))
				.addSpecial(DispenserGolem.ARROW_DAMAGE, Double.valueOf(4.25D), "Amount of damage dealt per arrow")
				.addDesc(new GolemDescription(new TranslationTextComponent("entitytip.click_refill")
						.applyTextStyle(TextFormatting.GRAY), DispenserGolem.ALLOW_SPECIAL))
				.addBlocks(Blocks.DISPENSER)
				.build());
		// EMERALD GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.EMERALD_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(190.0D).setAttack(18.0D).setKnockback(0.8D).addBlocks(Blocks.EMERALD_BLOCK).basicTexture().build());
		// ENDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.ENDSTONE_GOLEM, EndstoneGolem.class, EndstoneGolem::new)
				.setHealth(50.0D).setAttack(8.0D).setSpeed(0.26D).setKnockback(0.3D).addBlocks(Blocks.END_STONE, Blocks.END_STONE_BRICKS)
				.addSpecial(EndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water")
				.addSpecial(EndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport",
						new TranslationTextComponent("entitytip.can_teleport").applyTextStyle(TextFormatting.DARK_AQUA))
				.basicTexture().build());
		// FURNACE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.FURNACE_GOLEM, FurnaceGolem.class, FurnaceGolem::new)
				.setHealth(88.0D).setAttack(6.5D).setSpeed(0.24D).setKnockback(1.0D).addBlocks(Blocks.FURNACE)
				.addDesc(new GolemDescription(new TranslationTextComponent("entitytip.use_fuel").applyTextStyle(TextFormatting.GRAY)))
				.addSpecial(FurnaceGolem.FUEL_FACTOR, 10, "Higher numbers cause fuel to last longer")
				.immuneToFire().build());
		// GLASS GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GLASS_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(8.0D).setAttack(13.0D).setSpeed(0.30D).addBlocks(Blocks.GLASS).basicTexture().enableFallDamage()
				.setSound(SoundEvents.BLOCK_GLASS_STEP).build());
		// GLOWSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GLOWSTONE_GOLEM, GlowstoneGolem.class, GlowstoneGolem::new)
				.setHealth(8.0D).setAttack(12.0D).setSpeed(0.26D).enableFallDamage().addBlocks(Blocks.GLOWSTONE)
				.addSpecial(GlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can glow",
						new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.RED))
				.immuneToFire().addDesc(FIREPROOF).setSound(SoundEvents.BLOCK_GLASS_STEP).basicTexture()
				.setSwimMode(SwimMode.FLOAT).build());
		// GOLD GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.GOLD_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(80.0D).setAttack(8.0D).setSpeed(0.21D).setKnockback(1.0D).addBlocks(Blocks.GOLD_BLOCK).basicTexture().build());
		// ICE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.ICE_GOLEM, IceGolem.class, IceGolem::new)
				.setHealth(18.0D).setAttack(6.0D).setSpeed(0.27D).addBlocks(BlockTags.ICE).basicTexture()
				.addSpecial(IceGolem.AOE, Integer.valueOf(3), "Radial distance at which this golem can freeze / cool liquids (0=disable)")
				.addSpecial(IceGolem.FROST, false, "When true, this golem places frosted (temporary) ice")
				.addDesc(new GolemDescription(new TranslationTextComponent("entitytip.freezes_blocks")
					.applyTextStyle(TextFormatting.AQUA), IceGolem.AOE, c -> (Integer)c.get() > 0))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_GLASS_STEP).build());
		// LAPIS GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.LAPIS_GOLEM, LapisGolem.class, LapisGolem::new)
				.setHealth(50.0D).setAttack(1.5D).setSpeed(0.285D).basicTexture().addBlocks(Blocks.LAPIS_BLOCK)
				.addSpecial(LapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects",
						new TranslationTextComponent("entitytip.attacks_use_potion_effects")
								.applyTextStyle(TextFormatting.LIGHT_PURPLE))
				.build());
		// LEAF GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.LEAF_GOLEM, LeafGolem.class, LeafGolem::new)
				.setHealth(6.0D).setAttack(0.5D).setSpeed(0.31D).setKnockback(0.0D).addBlocks(BlockTags.LEAVES)
				.addSpecial(LeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself",
						new TranslationTextComponent("effect.minecraft.regeneration").applyTextStyle(TextFormatting.DARK_GREEN)
								.appendText(" ").appendSibling(new TranslationTextComponent("enchantment.level.1")))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_GRASS_STEP).build());
		// MAGMA GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MAGMA_GOLEM, MagmaGolem.class, MagmaGolem::new)
				.setHealth(46.0D).setAttack(4.5D).setSpeed(0.28D).addBlocks(Blocks.MAGMA_BLOCK)
				.addSpecial(MagmaGolem.MELT_DELAY, Integer.valueOf(240), "Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
				.addSpecial(MagmaGolem.ALLOW_WATER_DAMAGE, true, "When true, water will hurt this golem")
				.addSpecial(MagmaGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death",
						new TranslationTextComponent("entitytip.splits_upon_death").applyTextStyle(TextFormatting.RED))
				.addSpecial(MagmaGolem.ALLOW_LAVA_SPECIAL, false, "Whether this golem can slowly melt cobblestone",
						new TranslationTextComponent("entitytip.slowly_melts", 
								new TranslationTextComponent("block.minecraft.cobblestone")).applyTextStyle(TextFormatting.RED))
				.addSpecial(MagmaGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
						new TranslationTextComponent("entitytip.lights_mobs_on_fire").applyTextStyle(TextFormatting.GOLD))
				.immuneToFire().addDesc(FIREPROOF).build());
		// MELON GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MELON_GOLEM, MelonGolem.class, MelonGolem::new)
				.setHealth(18.0D).setAttack(1.5D).setSpeed(0.265D).basicTexture().addBlocks(Blocks.MELON)
				.addSpecial(MelonGolem.ALLOW_HEALING, true, "Whether this golem can occasionally heal", HEALS)
				.addSpecial(MelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly",
						new TranslationTextComponent("entitytip.plants_flowers", 
								new TranslationTextComponent("tile.flower1.name")).applyTextStyle(TextFormatting.GREEN))
				.addSpecial(MelonGolem.FREQUENCY, Integer.valueOf(240), "Average number of ticks between planting flowers")
				.setSwimMode(SwimMode.FLOAT).build());
		// MUSHROOM GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.MUSHROOM_GOLEM, MushroomGolem.class, MushroomGolem::new)
				.setHealth(30.0D).setAttack(3.0D).setSpeed(0.30D).addBlocks(Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK)
				.addSpecial(MushroomGolem.FREQUENCY, Integer.valueOf(420), "Average number of ticks between planting mushrooms")
				.addSpecial(MushroomGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)", HEALS)
				.addSpecial(MushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly",
						new TranslationTextComponent("entitytip.plants_shrooms").applyTextStyle(TextFormatting.DARK_GREEN))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_GRASS_STEP).build());
		// NETHER BRICK GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.NETHERBRICK_GOLEM, NetherBrickGolem.class, NetherBrickGolem::new)
				.setHealth(25.0D).setAttack(6.5D).setSpeed(0.28D).setKnockback(0.2D).addBlocks(Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS)
				.addSpecial(NetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire",
						new TranslationTextComponent("entitytip.lights_mobs_on_fire").applyTextStyle(TextFormatting.RED))
				.immuneToFire().addDesc(FIREPROOF).basicTexture().build());
		// NETHER WART GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.NETHERWART_GOLEM, NetherWartGolem.class, NetherWartGolem::new)
				.setHealth(22.0D).setAttack(1.5D).setSpeed(0.26D).basicTexture().addBlocks(Blocks.NETHER_WART_BLOCK)
				.addSpecial(NetherWartGolem.FREQUENCY, Integer.valueOf(880), "Average number of ticks between planting nether wart (if enabled)")
				.addSpecial(NetherWartGolem.ALLOW_HEALING, true, "Whether this golem can randomly heal (at night)", HEALS)
				.addSpecial(NetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly",
						new TranslationTextComponent("entitytip.plants_warts").applyTextStyle(TextFormatting.RED))
				.immuneToFire().addDesc(FIREPROOF).setSound(SoundEvents.BLOCK_WOOD_STEP)
				.setSwimMode(SwimMode.FLOAT).build());
		// OBSIDIAN GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.OBSIDIAN_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(120.0D).setAttack(18.0D).setSpeed(0.23D).setKnockback(0.8D).addBlocks(Blocks.OBSIDIAN)
				.basicTexture().immuneToFire().addDesc(FIREPROOF).build());
		// PRISMARINE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.PRISMARINE_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(34.0D).setAttack(8.0D).setKnockback(0.7D).addBlocks(BlockTagUtil.TAG_PRISMARINE)
				.basicTexture().addDesc(SWIMS).setSwimMode(SwimMode.SWIM).build());
		// QUARTZ GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.QUARTZ_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(85.0D).setAttack(8.5D).setSpeed(0.28D).setKnockback(0.6D).addBlocks(BlockTagUtil.TAG_QUARTZ)
				.basicTexture().setSound(SoundEvents.BLOCK_GLASS_STEP).build());
		// RED SANDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSANDSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).setKnockback(0.6D).addBlocks(BlockTagUtil.TAG_RED_SANDSTONE)
				.basicTexture().build());
		// REDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSTONE_GOLEM, RedstoneGolem.class, RedstoneGolem::new)
				.setHealth(18.0D).setAttack(2.0D).setSpeed(0.26D).setKnockback(0.1D).addBlocks(Blocks.REDSTONE_BLOCK)
				.addSpecial(RedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power",
						new TranslationTextComponent("entitytip.emits_redstone_signal").applyTextStyle(TextFormatting.RED))
				.basicTexture().build());
		// REDSTONE LAMP GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.REDSTONELAMP_GOLEM, RedstoneLampGolem.class, RedstoneLampGolem::new)
				.setHealth(28.0D).setAttack(6.0D).setSpeed(0.26D).addBlocks(Blocks.REDSTONE_LAMP)
				.addSpecial(RedstoneLampGolem.ALLOW_SPECIAL, true, "Whether this golem can light up the area", 
						new TranslationTextComponent("entitytip.lights_area_toggle").applyTextStyle(TextFormatting.GOLD))
				.enableFallDamage().setSound(SoundEvents.BLOCK_GLASS_STEP).build());
		// SANDSTONE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SANDSTONE_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).setKnockback(0.6D).addBlocks(BlockTagUtil.TAG_SANDSTONE)
				.basicTexture().build());
		// SEA LANTERN GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SEALANTERN_GOLEM, SeaLanternGolem.class, SeaLanternGolem::new)
				.setHealth(34.0D).setAttack(6.0D).setSpeed(0.26D).setKnockback(0.9D).addBlocks(Blocks.SEA_LANTERN).basicTexture()
				.addSpecial(SeaLanternGolem.ALLOW_SPECIAL, true, "Whether this golem lights up the area",
						new TranslationTextComponent("entitytip.lights_area").applyTextStyle(TextFormatting.GOLD))
				.addDesc(SWIMS).setSound(SoundEvents.BLOCK_GLASS_STEP).setSwimMode(SwimMode.SWIM).build());
		// SLIME GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SLIME_GOLEM, SlimeGolem.class, SlimeGolem::new)
				.setHealth(58.0D).setAttack(2.5D).setSpeed(0.288D).setKnockback(0.35D).addBlocks(Blocks.SLIME_BLOCK).basicTexture()
				.addSpecial(SlimeGolem.ALLOW_SPLITTING, true, "When true, this golem will split into 2 mini-golems upon death",
						new TranslationTextComponent("entitytip.splits_upon_death").applyTextStyle(TextFormatting.GREEN))
				.addSpecial(SlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking",
						new TranslationTextComponent("entitytip.has_knockback").applyTextStyle(TextFormatting.GREEN))
				.addSpecial(SlimeGolem.KNOCKBACK, Double.valueOf(1.0412D), "Slime Golem knockback power (Higher Value = Further Knockback)")
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.ENTITY_SLIME_SQUISH).build());
		// SPONGE GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.SPONGE_GOLEM, SpongeGolem.class, SpongeGolem::new)
				.setHealth(20.0D).setAttack(1.5D).basicTexture().addBlocks(Blocks.SPONGE, Blocks.WET_SPONGE)
				.addSpecial(SpongeGolem.RANGE, Integer.valueOf(5), "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
				.addSpecial(SpongeGolem.INTERVAL, Integer.valueOf(10), "Number of ticks between each water-check; increase to reduce lag")
				.addSpecial(SpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water",
						new TranslationTextComponent("entitytip.absorbs_water").applyTextStyle(TextFormatting.GOLD))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_WOOL_STEP).build());
		// STAINED GLASS GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STAINEDGLASS_GOLEM, StainedGlassGolem.class, StainedGlassGolem::new)
				.setHealth(9.0D).setAttack(12.0D).setSpeed(0.29D).addBlocks(Tags.Blocks.STAINED_GLASS)
				.enableFallDamage().setSound(SoundEvents.BLOCK_GLASS_STEP).build());
		// STAINED TERRACOTTA GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STAINEDTERRACOTTA_GOLEM, StainedTerracottaGolem.class, StainedTerracottaGolem::new)
				.setHealth(42.0D).setAttack(4.0D).setSpeed(0.22D).setKnockback(0.6D).addBlocks(BlockTagUtil.TAG_TERRACOTTA).build());
		// STRAW GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.STRAW_GOLEM, StrawGolem.class, StrawGolem::new)
				.setHealth(10.0D).setAttack(1.0D).setSpeed(0.32D).setKnockback(0.0D).addBlocks(Blocks.HAY_BLOCK).basicTexture()
				.addSpecial(StrawGolem.SPECIAL_FREQ, Integer.valueOf(460), "Minimum number of ticks between crop-boosts")
				.addSpecial(StrawGolem.ALLOW_SPECIAL, true, "Whether this golem can speed up crop growth",
						new TranslationTextComponent("entitytip.grows_crops").applyTextStyle(TextFormatting.GREEN))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_GRASS_STEP).build());
		// TERRACOTTA GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.TERRACOTTA_GOLEM, GenericGolem.class, GenericGolem::new)
				.setHealth(42.0D).setAttack(4.0D).setSpeed(0.208D).setKnockback(0.6D).addBlocks(Blocks.TERRACOTTA)
				.basicTexture().build());
		// TNT GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.TNT_GOLEM, TNTGolem.class, TNTGolem::new)
				.setHealth(14.0D).setAttack(2.5D).setSpeed(0.26D).basicTexture().addBlocks(Blocks.TNT)
				.addSpecial(TNTGolem.ALLOW_SPECIAL, true, "Whether this golem can explode when fighting or dying",
						new TranslationTextComponent("entitytip.explodes").applyTextStyle(TextFormatting.RED))
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_GRAVEL_STEP).build());
		// WOODEN GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.WOODEN_GOLEM, WoodenGolem.class, WoodenGolem::new)
				.setHealth(20.0D).setAttack(3.0D).setSpeed(0.298D).setKnockback(0.2D).addBlocks(BlockTags.LOGS)
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_WOOD_STEP).build());
		// WOOL GOLEM
		GolemRegistrar.registerGolem(new GolemContainer.Builder(GolemNames.WOOL_GOLEM, WoolGolem.class, WoolGolem::new)
				.setHealth(10.0D).setAttack(1.0D).setSpeed(0.295D).setKnockback(0.2D).addBlocks(BlockTags.WOOL)
				.setSwimMode(SwimMode.FLOAT).setSound(SoundEvents.BLOCK_WOOL_STEP).build());

		// fire GolemRegistrar event for any listening child mods (addons)
		MinecraftForge.EVENT_BUS.post(new GolemRegistrarEvent());
	}
	
	/**
	 * Calls {@link #makeTexture(String, String)} on the assumption that MODID is 'golems'.
	 * Texture should be at 'assets/golems/textures/entity/[TEXTURE].png'
	 **/
	public static ResourceLocation makeTexture(final String TEXTURE) {
		return makeTexture(ExtraGolems.MODID, TEXTURE);
	}

	/**
	 * Makes a ResourceLocation using the passed mod id and part of the texture name. Texture should
	 * be at 'assets/[MODID]/textures/entity/[TEXTURE].png'
	 * @see #makeTexture(String)
	 **/
	public static ResourceLocation makeTexture(final String MODID, final String TEXTURE) {
		return new ResourceLocation(MODID + ":textures/entity/" + TEXTURE + ".png");
	}
}
