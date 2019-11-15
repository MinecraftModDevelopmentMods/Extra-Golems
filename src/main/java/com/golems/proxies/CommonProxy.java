package com.golems.proxies;

import java.util.LinkedList;
import java.util.List;

import com.golems.blocks.*;
import com.golems.entity.*;
import com.golems.events.handlers.GolemCommonEventHandler;
import com.golems.items.ItemBedrockGolem;
import com.golems.items.ItemGolemSpell;
import com.golems.items.ItemInfoBook;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.GolemLookup;
import com.golems.util.GolemNames;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
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

@Mod.EventBusSubscriber
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
	 * THIS IS 100% THE MOST IMPORTANT EVENT HANDLER IN THE ENTIRE MOD.
	 * This method 1) registers all golems 2) registers their loot tables
	 * and 3) registers which block to use for which golem.
	 * @param event The EntityEntry registration event
	 */
	@SubscribeEvent
	public static void registerEntities() {
		golemEntityCount = 0;
		// Register Golem EntityEntries as well as registering blocks
		register(EntityBedrockGolem.class, GolemNames.BEDROCK_GOLEM, (Block)null);
		register(EntityBoneGolem.class, GolemNames.BONE_GOLEM, Blocks.BONE_BLOCK);
		register(EntityBookshelfGolem.class, GolemNames.BOOKSHELF_GOLEM, Blocks.BOOKSHELF);
		register(EntityClayGolem.class, GolemNames.CLAY_GOLEM, Blocks.CLAY);
		register(EntityCoalGolem.class, GolemNames.COAL_GOLEM, Blocks.COAL_BLOCK);
		register(EntityCraftingGolem.class, GolemNames.CRAFTING_GOLEM, Blocks.CRAFTING_TABLE);
		register(EntityDiamondGolem.class, GolemNames.DIAMOND_GOLEM, Blocks.DIAMOND_BLOCK);
		register(EntityEmeraldGolem.class, GolemNames.EMERALD_GOLEM, Blocks.EMERALD_BLOCK);
		register(EntityEndstoneGolem.class, GolemNames.ENDSTONE_GOLEM, Blocks.END_STONE);
		register(EntityFurnaceGolem.class, GolemNames.FURNACE_GOLEM, Blocks.FURNACE, Blocks.LIT_FURNACE);
		register(EntityGlassGolem.class, GolemNames.GLASS_GOLEM, Blocks.GLASS);
		register(EntityGlowstoneGolem.class, GolemNames.GLOWSTONE_GOLEM, Blocks.GLOWSTONE);
		register(EntityGoldGolem.class, GolemNames.GOLD_GOLEM, Blocks.GOLD_BLOCK);
		register(EntityHardenedClayGolem.class, GolemNames.TERRACOTTA_GOLEM, Blocks.HARDENED_CLAY);
		register(EntityIceGolem.class, GolemNames.ICE_GOLEM, Blocks.PACKED_ICE, Blocks.ICE);
		register(EntityLapisGolem.class, GolemNames.LAPIS_GOLEM, Blocks.LAPIS_BLOCK);
		register(EntityLeafGolem.class, GolemNames.LEAF_GOLEM, Blocks.LEAVES, Blocks.LEAVES2);
		register(EntityMagmaGolem.class, GolemNames.MAGMA_GOLEM, Blocks.MAGMA);
		register(EntityMelonGolem.class, GolemNames.MELON_GOLEM, Blocks.MELON_BLOCK);
		register(EntityMushroomGolem.class, GolemNames.MUSHROOM_GOLEM, 
				Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK);
		register(EntityNetherBrickGolem.class, GolemNames.NETHERBRICK_GOLEM, 
				Blocks.NETHER_BRICK, Blocks.RED_NETHER_BRICK);
		register(EntityNetherWartGolem.class, GolemNames.NETHERWART_GOLEM, Blocks.NETHER_WART_BLOCK);
		register(EntityObsidianGolem.class, GolemNames.OBSIDIAN_GOLEM, Blocks.OBSIDIAN);
		register(EntityPrismarineGolem.class, GolemNames.PRISMARINE_GOLEM, Blocks.PRISMARINE);
		register(EntityQuartzGolem.class, GolemNames.QUARTZ_GOLEM, Blocks.QUARTZ_BLOCK);
		register(EntityRedSandstoneGolem.class, GolemNames.REDSANDSTONE_GOLEM, Blocks.RED_SANDSTONE);
		register(EntityRedstoneGolem.class, GolemNames.REDSTONE_GOLEM, Blocks.REDSTONE_BLOCK);
		register(EntityRedstoneLampGolem.class, GolemNames.REDSTONELAMP_GOLEM, Blocks.REDSTONE_LAMP, Blocks.LIT_REDSTONE_LAMP);
		register(EntitySandstoneGolem.class, GolemNames.SANDSTONE_GOLEM, Blocks.SANDSTONE);
		register(EntitySeaLanternGolem.class, GolemNames.SEALANTERN_GOLEM, Blocks.SEA_LANTERN);
		register(EntitySlimeGolem.class, GolemNames.SLIME_GOLEM, Blocks.SLIME_BLOCK);
		register(EntitySpongeGolem.class, GolemNames.SPONGE_GOLEM, Blocks.SPONGE);
		register(EntityStainedClayGolem.class, GolemNames.STAINEDTERRACOTTA_GOLEM, Blocks.STAINED_HARDENED_CLAY);
		register(EntityStainedGlassGolem.class, GolemNames.STAINEDGLASS_GOLEM, Blocks.STAINED_GLASS);
		register(EntityStrawGolem.class, GolemNames.STRAW_GOLEM, Blocks.HAY_BLOCK);
		register(EntityTNTGolem.class, GolemNames.TNT_GOLEM, Blocks.TNT);
		register(EntityWoodenGolem.class, GolemNames.WOODEN_GOLEM, Blocks.LOG, Blocks.LOG2);
		register(EntityWoolGolem.class, GolemNames.WOOL_GOLEM, Blocks.WOOL);
		
		// Also register Golem Loot Tables
		LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/_golem_base"));
		registerLootTables(ExtraGolems.MODID, GolemNames.WOOL_GOLEM, EntityWoolGolem.coloredWoolTypes);
		registerLootTables(ExtraGolems.MODID, GolemNames.WOODEN_GOLEM, EntityWoodenGolem.woodTypes);
		registerLootTables(ExtraGolems.MODID, GolemNames.MUSHROOM_GOLEM, EntityMushroomGolem.SHROOM_TYPES);
		registerLootTables(ExtraGolems.MODID, GolemNames.REDSTONELAMP_GOLEM, EntityRedstoneLampGolem.VARIANTS);
		registerLootTables(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM, EntityStainedGlassGolem.COLOR_ARRAY.length);
		registerLootTables(ExtraGolems.MODID, GolemNames.STAINEDTERRACOTTA_GOLEM, EntityStainedClayGolem.COLOR_ARRAY.length);
	}

	protected static void register(Class<? extends GolemBase> entityClass, String name, Block... blocks) {
		register(entityClass, name, true, blocks);
	}
	
	protected static void register(final Class<? extends GolemBase> entityClass, final String name,
			final boolean lootTable, final Block... blocks) {
		EntityRegistry.registerModEntity(entityClass, name, ++golemEntityCount, ExtraGolems.instance, 64, 3, true);
		if(blocks != null && blocks.length > 0) {
			GolemLookup.addGolem(entityClass, blocks);
		}
		if(lootTable && !entityClass.isAssignableFrom(GolemColorized.class) 
				&& !entityClass.isAssignableFrom(GolemMultiTextured.class)) {
			LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/" + name));
		}
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
			.register(new ItemBedrockGolem().setUnlocalizedName("spawn_bedrock_golem")
				.setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"));

		event.getRegistry().register(new ItemGolemSpell().setUnlocalizedName("golem_paper")
			.setRegistryName(ExtraGolems.MODID, "golem_paper"));
		
		event.getRegistry().register(new ItemInfoBook().setUnlocalizedName("info_book")
			.setRegistryName(ExtraGolems.MODID, "info_book"));
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		final int GLOWSTONE_FREQ = GolemLookup.getConfig(EntityGlowstoneGolem.class).getInt(EntityGlowstoneGolem.FREQUENCY);
		final int SEALANTERN_FREQ = GolemLookup.getConfig(EntitySeaLanternGolem.class).getInt(EntitySeaLanternGolem.FREQUENCY);
		event.getRegistry().registerAll(
			new BlockGolemHead().setUnlocalizedName("golem_head").setRegistryName(ExtraGolems.MODID, "golem_head"),
			new BlockUtilityGlow(Material.GLASS, 1.0F, GLOWSTONE_FREQ, Blocks.AIR.getDefaultState())
				.setUnlocalizedName("light_provider_full").setRegistryName(ExtraGolems.MODID, "light_provider_full"),
			new BlockUtilityGlowWater(Material.WATER, 1.0F, SEALANTERN_FREQ, Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, 0))
				.setUnlocalizedName("water_light_provider_full").setRegistryName(ExtraGolems.MODID, "water_light_provider_full"),
			new BlockUtilityPower(15, EntityRedstoneGolem.DEF_FREQ).setUnlocalizedName("power_provider_all").setRegistryName(ExtraGolems.MODID, "power_provider_all"));
	}
	
	/**
	 * Registers multiple loot tables for each of the textures specified. They are registered under
	 * the subfile [name] and individually named according to each element in [textures]
	 */
	private static void registerLootTables(final String MODID, final String name, final String[] textures) {
		for(String s : textures) {
			LootTableList.register(new ResourceLocation(MODID, "entities/" + name + "/" + s));
		}
	}
	
	/**
	 * Registers loot tables for GolemColorizedMultiTextured, with loot tables 
	 * registered under the subfile [name] and individually named '0' through '[max-1]'
	 */
	private static void registerLootTables(final String MODID, final String name, final int max) {
		String[] array = new String[max];
		for (int i = 0; i < max; i++) {
			array[i] = Integer.toString(i);
		}
		registerLootTables(MODID, name, array);
	}

}
