package com.golems.proxies;

import com.golems.blocks.BlockGolemHead;
import com.golems.blocks.BlockUtilityGlow;
import com.golems.blocks.BlockUtilityGlowWater;
import com.golems.blocks.BlockUtilityPower;
import com.golems.entity.*;
import com.golems.events.handlers.GolemCommonEventHandler;
import com.golems.items.ItemBedrockGolem;
import com.golems.items.ItemInfoBook;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.GolemLookup;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	/**
	 * Registers all golems with loot tables, building blocks, and textures
	 **/
	public void registerEntities() {
		CommonProxy.golemEntityCount = 0;
		registerLootTable("_golem_base");
		register(EntityBedrockGolem.class, (Block) null, "golem_bedrock", false);
		register(EntityBoneGolem.class, Blocks.BONE_BLOCK, "golem_bone", true);
		register(EntityBookshelfGolem.class, Blocks.BOOKSHELF, "golem_bookshelf", true);
		register(EntityClayGolem.class, Blocks.CLAY, "golem_clay", true);
		register(EntityCoalGolem.class, Blocks.COAL_BLOCK, "golem_coal", true);
		register(EntityConcreteGolem.class, Blocks.CONCRETE, "golem_concrete", false);
		register(EntityCraftingGolem.class, Blocks.CRAFTING_TABLE, "golem_crafting", true);
		register(EntityDiamondGolem.class, Blocks.DIAMOND_BLOCK, "golem_diamond", true);
		register(EntityEmeraldGolem.class, Blocks.EMERALD_BLOCK, "golem_emerald", true);
		register(EntityEndstoneGolem.class, Blocks.END_STONE, "golem_end_stone", true);
		register(EntityGlassGolem.class, Blocks.GLASS, "golem_glass", true);
		register(EntityGlowstoneGolem.class, Blocks.GLOWSTONE, "golem_glowstone", true);
		register(EntityGoldGolem.class, Blocks.GOLD_BLOCK, "golem_gold", true);
		register(EntityHardenedClayGolem.class, Blocks.HARDENED_CLAY, "golem_hardened_clay", true);
		register(EntityIceGolem.class, new Block[]
			{Blocks.ICE, Blocks.PACKED_ICE}, "golem_ice", true);
		register(EntityLapisGolem.class, Blocks.LAPIS_BLOCK, "golem_lapis", true);
		register(EntityLeafGolem.class, new Block[]
			{Blocks.LEAVES, Blocks.LEAVES2}, "golem_leaves", true);
		register(EntityMagmaGolem.class, Blocks.MAGMA, "golem_magma", true);
		register(EntityMelonGolem.class, Blocks.MELON_BLOCK, "golem_melon", true);
		register(EntityMushroomGolem.class, new Block[]
			{Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK}, "golem_shroom", false);
		register(EntityNetherBrickGolem.class, new Block[]{
			Blocks.NETHER_BRICK, Blocks.RED_NETHER_BRICK}, "golem_nether_brick", true);
		register(EntityNetherWartGolem.class, Blocks.NETHER_WART_BLOCK, "golem_nether_wart", true);
		register(EntityObsidianGolem.class, Blocks.OBSIDIAN, "golem_obsidian", true);
		register(EntityPrismarineGolem.class, Blocks.PRISMARINE, "golem_prismarine", true);
		register(EntityQuartzGolem.class, Blocks.QUARTZ_BLOCK, "golem_quartz", true);
		register(EntityRedSandstoneGolem.class, Blocks.RED_SANDSTONE, "golem_red_sandstone", true);
		register(EntityRedstoneGolem.class, Blocks.REDSTONE_BLOCK, "golem_redstone", true);
		register(EntitySandstoneGolem.class, Blocks.SANDSTONE, "golem_sandstone", true);
		register(EntitySeaLanternGolem.class, Blocks.SEA_LANTERN, "golem_sea_lantern", true);
		register(EntitySlimeGolem.class, Blocks.SLIME_BLOCK, "golem_slime", true);
		register(EntitySpongeGolem.class, Blocks.SPONGE, "golem_sponge", true);
		register(EntityStainedClayGolem.class, Blocks.STAINED_HARDENED_CLAY, "golem_stained_clay", false);
		register(EntityStainedGlassGolem.class, Blocks.STAINED_GLASS, "golem_stained_glass", false);
		register(EntityStrawGolem.class, Blocks.HAY_BLOCK, "golem_straw", true);
		register(EntityTNTGolem.class, Blocks.TNT, "golem_tnt", true);
		register(EntityWoodenGolem.class, new Block[]
			{Blocks.LOG, Blocks.LOG2}, "golem_wooden", false);
		register(EntityWoolGolem.class, Blocks.WOOL, "golem_wool", false);
		
		// register the loot tables for each entity that didn't have one auto-generated
		registerEntityLootTables();
	}
	
	private static void registerEntityLootTables() {
		// register GolemMultiTextured loot tables
		registerLootTables(ExtraGolems.MODID, EntityWoolGolem.WOOL_PREFIX, EntityWoolGolem.coloredWoolTypes);
		registerLootTables(ExtraGolems.MODID, EntityWoodenGolem.WOOD_PREFIX, EntityWoodenGolem.woodTypes);
		registerLootTables(ExtraGolems.MODID, EntityMushroomGolem.SHROOM_PREFIX, EntityMushroomGolem.SHROOM_TYPES);
		
		// prepare and register loot tables for GolemColorizedMultiTextured
		String[] stainedGlass = new String[EntityStainedGlassGolem.COLOR_ARRAY.length];
		for (int i = 0, l = stainedGlass.length; i < l; i++) {
			stainedGlass[i] = Integer.toString(i);
		}
		String[] stainedClay = new String[EntityStainedClayGolem.COLOR_ARRAY.length];
		for (int i = 0, l = stainedGlass.length; i < l; i++) {
			stainedClay[i] = Integer.toString(i);
		}
		String[] concrete = new String[EntityConcreteGolem.COLOR_ARRAY.length];
		for (int i = 0, l = concrete.length; i < l; i++) {
			concrete[i] = Integer.toString(i);
		}
		registerLootTables(ExtraGolems.MODID, EntityStainedGlassGolem.PREFIX, stainedGlass);
		registerLootTables(ExtraGolems.MODID, EntityStainedClayGolem.PREFIX, stainedClay);
		registerLootTables(ExtraGolems.MODID, EntityConcreteGolem.PREFIX, concrete);
	}

	/** registers the entity with an optional loot table. **/
	protected static void register(final Class<? extends GolemBase> entityClass, @Nullable final Block buildingBlock, final String name, final boolean registerLootTable) {
		register(entityClass, new Block[]{buildingBlock}, name, registerLootTable);
	}

	/** registers the entity with an optional loot table. **/
	protected static void register(final Class<? extends GolemBase> entityClass, @Nonnull final Block[] buildingBlock, final String name, final boolean registerLootTable) {
		// register the entity with Forge
		EntityRegistry.registerModEntity(
			new ResourceLocation(ExtraGolems.MODID, name), entityClass,
			ExtraGolems.MODID + "." + name, ++golemEntityCount,
			ExtraGolems.instance, 16 * 4, 3, true);
		// register building block
		GolemLookup.addGolem(entityClass, buildingBlock);
		// register loot table
		if (registerLootTable) {
			registerLootTable(name);
		}
	}
	
	protected static void registerLootTable(final String name) {
		/*ResourceLocation rl =*/
		LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/" + name));
		//System.out.println("registered loot table at " + rl);
	}
	
	protected static void registerLootTables(final String MODID, final String prefix, final String[] names) {
		for(String s : names) {
			LootTableList.register(new ResourceLocation(MODID, "entities/golem_" + prefix + "/" + s));
		}
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(GolemItems.golemHead) {

			@Override
			@SideOnly(Side.CLIENT)
			public boolean hasEffect(final ItemStack stack) {
				return Config.golemHeadHasGlint();
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
