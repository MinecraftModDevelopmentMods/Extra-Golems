package com.golems.proxies;

import java.util.stream.IntStream;

import com.golems.blocks.*;
import com.golems.entity.*;
import com.golems.events.handlers.GolemCommonEventHandler;
import com.golems.items.ItemBedrockGolem;
import com.golems.items.ItemInfoBook;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;

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
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

@Mod.EventBusSubscriber(modid = ExtraGolems.MODID)
public class CommonProxy {

	// private static Map<String, Block> blocks = new ConcurrentHashMap<>();
	// private static Map<String, Item> items = new ConcurrentHashMap<>();

	protected static int golemEntityCount;

	public void preInitRenders() {
		// Unused
	}

	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new GolemCommonEventHandler());
	}

	public void registerEntities() {
		CommonProxy.golemEntityCount = 0;
		registerLootTable("_golem_base");
		register(EntityBedrockGolem.class, "golem_bedrock", false);
		register(EntityBoneGolem.class, "golem_bone");
		register(EntityBookshelfGolem.class, "golem_bookshelf");
		register(EntityClayGolem.class, "golem_clay");
		register(EntityCoalGolem.class, "golem_coal");
		register(EntityCraftingGolem.class, "golem_crafting");
		register(EntityDiamondGolem.class, "golem_diamond");
		register(EntityEmeraldGolem.class, "golem_emerald");
		register(EntityEndstoneGolem.class, "golem_end_stone");
		register(EntityGlassGolem.class, "golem_glass");
		register(EntityGlowstoneGolem.class, "golem_glowstone");
		register(EntityGoldGolem.class, "golem_gold");
		register(EntityHardenedClayGolem.class, "golem_hardened_clay");
		register(EntityIceGolem.class, "golem_ice");
		register(EntityLapisGolem.class, "golem_lapis");
		register(EntityLeafGolem.class, "golem_leaves");
		register(EntityMagmaGolem.class, "golem_magma");
		register(EntityMelonGolem.class, "golem_melon");
		register(EntityMushroomGolem.class, "golem_shroom", false);
		register(EntityNetherBrickGolem.class, "golem_nether_brick");
		register(EntityNetherWartGolem.class, "golem_nether_wart");
		register(EntityObsidianGolem.class, "golem_obsidian");
		register(EntityPrismarineGolem.class, "golem_prismarine");
		register(EntityQuartzGolem.class, "golem_quartz");
		register(EntityRedSandstoneGolem.class, "golem_red_sandstone");
		register(EntityRedstoneGolem.class, "golem_redstone");
		register(EntitySandstoneGolem.class, "golem_sandstone");
		register(EntitySeaLanternGolem.class, "golem_sea_lantern");
		register(EntitySlimeGolem.class, "golem_slime");
		register(EntitySpongeGolem.class, "golem_sponge");
		register(EntityStainedClayGolem.class, "golem_stained_clay", false);
		register(EntityStainedGlassGolem.class, "golem_stained_glass", false);
		register(EntityStrawGolem.class, "golem_straw");
		register(EntityTNTGolem.class, "golem_tnt");
		register(EntityWoodenGolem.class, "golem_wooden", false);
		register(EntityWoolGolem.class, "golem_wool", false);
		
		// register GolemMultiTextured loot tables
		registerLootTables(EntityWoolGolem.WOOL_PREFIX, EntityWoolGolem.coloredWoolTypes);
		registerLootTables(EntityWoodenGolem.WOOD_PREFIX, EntityWoodenGolem.woodTypes);
		registerLootTables(EntityMushroomGolem.SHROOM_PREFIX, EntityMushroomGolem.SHROOM_TYPES);
		
		// prepare and register loot tables for GolemColorizedMultiTextured
		String[] stainedGlass = new String[EntityStainedGlassGolem.COLORS.length];
		for(int i = 0, l = stainedGlass.length; i < l; i++) {
			stainedGlass[i] = Integer.toString(i);
		}
		String[] stainedClay = new String[EntityStainedClayGolem.COLORS.length];
		for(int i = 0, l = stainedGlass.length; i < l; i++) {
			stainedClay[i] = Integer.toString(i);
		}
		registerLootTables(EntityStainedGlassGolem.PREFIX, stainedGlass);
		registerLootTables(EntityStainedClayGolem.PREFIX, stainedClay);
		
		
	}

	/** registers the entity with an optional loot table. **/
	protected static void register(final Class<? extends GolemBase> entityClass, final String name, final boolean registerLootTable) {

		EntityRegistry.registerModEntity(
				new ResourceLocation(ExtraGolems.MODID, name), entityClass, 
				ExtraGolems.MODID + "." + name, ++golemEntityCount,
				ExtraGolems.instance, 16 * 4, 3, true);
		if(registerLootTable) {
			registerLootTable(name);
		}
	}
	
	/** registers the entity with a loot table. **/
	protected static void register(final Class<? extends GolemBase> entityClass, final String name) {
		register(entityClass, name, true);
	}
	
	protected static void registerLootTable(final String name) {
		/*ResourceLocation rl =*/ LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/" + name));
		//System.out.println("registered loot table at " + rl);
	}
	
	protected static void registerLootTables(final String prefix, final String[] names) {
		for(String s : names) {
			registerLootTable("golem_" + prefix + "/" + s);
		}
	}

	// TODO: Reimplement old version
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(GolemItems.golemHead) {

			@Override
			@SideOnly(Side.CLIENT)
			public boolean hasEffect(final ItemStack stack) {
				return Config.itemGolemHeadHasGlint;
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

	// TODO: Reimplement old version
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		// TODO: Clean up
		final int GLOWSTONE_FREQ = Config.GLOWSTONE.getInt(EntityGlowstoneGolem.FREQUENCY);
		final int SEALANTERN_FREQ = Config.SEA_LANTERN.getInt(EntitySeaLanternGolem.FREQUENCY);
		event.getRegistry().registerAll(
				new BlockGolemHead().setTranslationKey("golem_head").setRegistryName(ExtraGolems.MODID, "golem_head"),
				new BlockUtilityGlow(Material.GLASS, 1.0F, GLOWSTONE_FREQ, Blocks.AIR.getDefaultState())
					.setTranslationKey("light_provider_full").setRegistryName(ExtraGolems.MODID, "light_provider_full"),
				new BlockUtilityGlowWater(Material.WATER, 1.0F, SEALANTERN_FREQ, Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, 0))
					.setTranslationKey("water_light_provider_full").setRegistryName(ExtraGolems.MODID, "water_light_provider_full"),
				new BlockUtilityPower(15, EntityRedstoneGolem.FREQUENCY).setTranslationKey("power_provider_all").setRegistryName(ExtraGolems.MODID, "power_provider_all"));
	}

}
