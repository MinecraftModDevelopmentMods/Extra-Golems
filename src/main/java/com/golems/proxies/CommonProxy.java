package com.golems.proxies;

import com.golems.blocks.BlockGolemHead;
import com.golems.blocks.BlockUtilityGlow;
import com.golems.blocks.BlockUtilityGlowWater;
import com.golems.blocks.BlockUtilityPower;
import com.golems.entity.*;
import com.golems.events.handlers.GolemCommonEventHandler;
import com.golems.items.ItemBedrockGolem;
import com.golems.items.ItemInfoBook;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.ConsumerLootTables;
import com.golems.util.GolemLookup;
import com.golems.util.GolemNames;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ExtraGolems.MODID)
public class CommonProxy {

	/**
	 * A List containing default instances of each Golem.
	 * They do not exist in the world, the list is simply a 
	 * reference for things like Golem Info Book
	 **/
	public static final List<GolemBase> DUMMY_GOLEMS = new LinkedList();
	
	protected static int golemEntityCount;

	public void preInitRenders() {
		// Unused
	}

	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new GolemCommonEventHandler());
	}	
	
	private static EntityEntry build(final Class<? extends GolemBase> entityClass, final String name, Block... blocks) {
		// register block(s) with GolemLookup
		if(blocks != null && blocks.length > 0) {
			GolemLookup.addGolem(entityClass, blocks);
		}
		// build an EntityEntry to return
		EntityEntryBuilder builder = EntityEntryBuilder.<GolemBase>create();
		builder.entity(entityClass);
		builder.name(ExtraGolems.MODID + "." + name);
		builder.id(new ResourceLocation(ExtraGolems.MODID, name), ++golemEntityCount);
		builder.tracker(48, 3, true);
		return builder.build();
	}
	
	/**
	 * THIS IS 100% THE MOST IMPORTANT EVENT HANDLER IN THE ENTIRE MOD.
	 * This method 1) registers all golems 2) registers their loot tables
	 * and 3) registers which block to use for which golem.
	 * @param event The EntityEntry registration event
	 */
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityEntry> event) {
		golemEntityCount = 0;
		// Register Golem EntityEntries as well as building blocks
		event.getRegistry().registerAll(
				build(EntityBedrockGolem.class, GolemNames.BEDROCK_GOLEM, (Block)null),
				build(EntityBoneGolem.class, GolemNames.BONE_GOLEM, Blocks.BONE_BLOCK),
				build(EntityBookshelfGolem.class, GolemNames.BOOKSHELF_GOLEM, Blocks.BOOKSHELF),
				build(EntityClayGolem.class, GolemNames.CLAY_GOLEM, Blocks.CLAY),
				build(EntityCoalGolem.class, GolemNames.COAL_GOLEM, Blocks.COAL_BLOCK),
				build(EntityConcreteGolem.class, GolemNames.CONCRETE_GOLEM, Blocks.CONCRETE),
				build(EntityCraftingGolem.class, GolemNames.CRAFTING_GOLEM, Blocks.CRAFTING_TABLE),
				build(EntityDiamondGolem.class, GolemNames.DIAMOND_GOLEM, Blocks.DIAMOND_BLOCK),
				build(EntityEmeraldGolem.class, GolemNames.EMERALD_GOLEM, Blocks.EMERALD_BLOCK),
				build(EntityEndstoneGolem.class, GolemNames.ENDSTONE_GOLEM, Blocks.END_STONE),
				build(EntityGlassGolem.class, GolemNames.GLASS_GOLEM, Blocks.GLASS),
				build(EntityGlowstoneGolem.class, GolemNames.GLOWSTONE_GOLEM, Blocks.GLOWSTONE),
				build(EntityGoldGolem.class, GolemNames.GOLD_GOLEM, Blocks.GOLD_BLOCK),
				build(EntityHardenedClayGolem.class, GolemNames.TERRACOTTA_GOLEM, Blocks.HARDENED_CLAY),
				build(EntityIceGolem.class, GolemNames.ICE_GOLEM, Blocks.PACKED_ICE, Blocks.ICE),
				build(EntityLapisGolem.class, GolemNames.LAPIS_GOLEM, Blocks.LAPIS_BLOCK),
				build(EntityLeafGolem.class, GolemNames.LEAF_GOLEM, Blocks.LEAVES, Blocks.LEAVES2),
				build(EntityMagmaGolem.class, GolemNames.MAGMA_GOLEM, Blocks.MAGMA),
				build(EntityMelonGolem.class, GolemNames.MELON_GOLEM, Blocks.MELON_BLOCK),
				build(EntityMushroomGolem.class, GolemNames.MUSHROOM_GOLEM, 
						Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK),
				build(EntityNetherBrickGolem.class, GolemNames.NETHERBRICK_GOLEM, 
						Blocks.NETHER_BRICK, Blocks.RED_NETHER_BRICK),
				build(EntityNetherWartGolem.class, GolemNames.NETHERWART_GOLEM, Blocks.NETHER_WART_BLOCK),
				build(EntityObsidianGolem.class, GolemNames.OBSIDIAN_GOLEM, Blocks.OBSIDIAN),
				build(EntityPrismarineGolem.class, GolemNames.PRISMARINE_GOLEM, Blocks.PRISMARINE),
				build(EntityQuartzGolem.class, GolemNames.QUARTZ_GOLEM, Blocks.QUARTZ_BLOCK),
				build(EntityRedSandstoneGolem.class, GolemNames.REDSANDSTONE_GOLEM, Blocks.RED_SANDSTONE),
				build(EntityRedstoneGolem.class, GolemNames.REDSTONE_GOLEM, Blocks.REDSTONE_BLOCK),
				build(EntityRedstoneLampGolem.class, GolemNames.REDSTONELAMP_GOLEM, Blocks.REDSTONE_LAMP, Blocks.LIT_REDSTONE_LAMP),
				build(EntitySandstoneGolem.class, GolemNames.SANDSTONE_GOLEM, Blocks.SANDSTONE),
				build(EntitySeaLanternGolem.class, GolemNames.SEALANTERN_GOLEM, Blocks.SEA_LANTERN),
				build(EntitySlimeGolem.class, GolemNames.SLIME_GOLEM, Blocks.SLIME_BLOCK),
				build(EntitySpongeGolem.class, GolemNames.SPONGE_GOLEM, Blocks.SPONGE),
				build(EntityStainedClayGolem.class, GolemNames.STAINEDTERRACOTTA_GOLEM, Blocks.STAINED_HARDENED_CLAY),
				build(EntityStainedGlassGolem.class, GolemNames.STAINEDGLASS_GOLEM, Blocks.STAINED_GLASS),
				build(EntityStrawGolem.class, GolemNames.STRAW_GOLEM, Blocks.HAY_BLOCK),
				build(EntityTNTGolem.class, GolemNames.TNT_GOLEM, Blocks.TNT),
				build(EntityWoodenGolem.class, GolemNames.WOODEN_GOLEM, Blocks.LOG, Blocks.LOG2),
				build(EntityWoolGolem.class, GolemNames.WOOL_GOLEM, Blocks.WOOL)
			);
		
		// Also register Golem Loot Tables
		LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/_golem_base"));
		GolemNames.forEach(new ConsumerLootTables());
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(GolemItems.golemHead) {

			@Override
			@SideOnly(Side.CLIENT)
			public boolean hasEffect(final ItemStack stack) {
				return true;
			}
		}.setRegistryName(GolemItems.golemHead.getRegistryName()));

		event.getRegistry()
			.register(new ItemBedrockGolem().setTranslationKey("spawn_bedrock_golem")
				.setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"));

		event.getRegistry().register(new Item().setTranslationKey("golem_paper")
			.setRegistryName(ExtraGolems.MODID, "golem_paper").setCreativeTab(CreativeTabs.MISC));
		
		event.getRegistry().register(new ItemInfoBook().setTranslationKey("info_book")
			.setRegistryName(ExtraGolems.MODID, "info_book"));
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		final int GLOWSTONE_FREQ = GolemLookup.getConfig(EntityGlowstoneGolem.class).getInt(EntityGlowstoneGolem.FREQUENCY);
		final int SEALANTERN_FREQ = GolemLookup.getConfig(EntitySeaLanternGolem.class).getInt(EntitySeaLanternGolem.FREQUENCY);
		event.getRegistry().registerAll(
			new BlockGolemHead().setTranslationKey("golem_head").setRegistryName(ExtraGolems.MODID, "golem_head"),
			new BlockUtilityGlow(Material.GLASS, 1.0F, GLOWSTONE_FREQ, Blocks.AIR.getDefaultState())
				.setTranslationKey("light_provider_full").setRegistryName(ExtraGolems.MODID, "light_provider_full"),
			new BlockUtilityGlowWater(Material.WATER, 1.0F, SEALANTERN_FREQ, Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, 0))
				.setTranslationKey("water_light_provider_full").setRegistryName(ExtraGolems.MODID, "water_light_provider_full"),
			new BlockUtilityPower(15, EntityRedstoneGolem.DEF_FREQ).setTranslationKey("power_provider_all").setRegistryName(ExtraGolems.MODID, "power_provider_all"));
	}

}
