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
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

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
	
	private static EntityEntry build(final Class<? extends GolemBase> entityClass, final String name) {
		EntityEntryBuilder builder = EntityEntryBuilder.<GolemBase>create();
		builder.entity(entityClass);
		builder.name(name);
		builder.id(new ResourceLocation(ExtraGolems.MODID, name), ++golemEntityCount);
		builder.tracker(48, 3, true);
		return builder.build();
	}
	
	/**
	 * THIS IS 100% THE MOST IMPORTANT EVENT HANDLER IN THE ENTIRE MOD.
	 * This method 1) registers all golems 2) registers their loot tables
	 * and 3) registers which block to use for which golem.
	 * @param event
	 */
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityEntry> event) {
		golemEntityCount = 0;
		event.getRegistry().registerAll(
				build(EntityBedrockGolem.class, GolemNames.BEDROCK_GOLEM),
				build(EntityBoneGolem.class, GolemNames.BONE_GOLEM),
				build(EntityBookshelfGolem.class, GolemNames.BOOKSHELF_GOLEM),
				build(EntityClayGolem.class, GolemNames.CLAY_GOLEM),
				build(EntityCoalGolem.class, GolemNames.COAL_GOLEM),
				build(EntityConcreteGolem.class, GolemNames.CONCRETE_GOLEM),
				build(EntityCraftingGolem.class, GolemNames.CRAFTING_GOLEM),
				build(EntityDiamondGolem.class, GolemNames.DIAMOND_GOLEM),
				build(EntityEmeraldGolem.class, GolemNames.EMERALD_GOLEM),
				build(EntityEndstoneGolem.class, GolemNames.ENDSTONE_GOLEM),
				build(EntityGlassGolem.class, GolemNames.GLASS_GOLEM),
				build(EntityGlowstoneGolem.class, GolemNames.GLOWSTONE_GOLEM),
				build(EntityGoldGolem.class, GolemNames.GOLD_GOLEM),
				build(EntityHardenedClayGolem.class, GolemNames.TERRACOTTA_GOLEM),
				build(EntityIceGolem.class, GolemNames.ICE_GOLEM),
				build(EntityLapisGolem.class, GolemNames.LAPIS_GOLEM),
				build(EntityLeafGolem.class, GolemNames.LEAF_GOLEM),
				build(EntityMagmaGolem.class, GolemNames.MAGMA_GOLEM),
				build(EntityMelonGolem.class, GolemNames.MELON_GOLEM),
				build(EntityMushroomGolem.class, GolemNames.MUSHROOM_GOLEM),
				build(EntityNetherBrickGolem.class, GolemNames.NETHERBRICK_GOLEM),
				build(EntityNetherWartGolem.class, GolemNames.NETHERWART_GOLEM),
				build(EntityObsidianGolem.class, GolemNames.OBSIDIAN_GOLEM),
				build(EntityPrismarineGolem.class, GolemNames.PRISMARINE_GOLEM),
				build(EntityQuartzGolem.class, GolemNames.QUARTZ_GOLEM),
				build(EntityRedSandstoneGolem.class, GolemNames.REDSANDSTONE_GOLEM),
				build(EntityRedstoneGolem.class, GolemNames.REDSTONE_GOLEM),
				build(EntitySandstoneGolem.class, GolemNames.SANDSTONE_GOLEM),
				build(EntitySeaLanternGolem.class, GolemNames.SEALANTERN_GOLEM),
				build(EntitySlimeGolem.class, GolemNames.SLIME_GOLEM),
				build(EntitySpongeGolem.class, GolemNames.SPONGE_GOLEM),
				build(EntityStainedClayGolem.class, GolemNames.STAINEDTERRACOTTA_GOLEM),
				build(EntityStainedGlassGolem.class, GolemNames.STAINEDGLASS_GOLEM),
				build(EntityStrawGolem.class, GolemNames.STRAW_GOLEM),
				build(EntityTNTGolem.class, GolemNames.TNT_GOLEM),
				build(EntityWoodenGolem.class, GolemNames.WOODEN_GOLEM),
				build(EntityWoolGolem.class, GolemNames.WOOL_GOLEM)
			);
		
		// Also register Golem Loot Tables and Golem Building Blocks
		// Loot tables:
		LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/_golem_base"));
		GolemNames.forEach(new ConsumerLootTables());
		// Golem Lookup - Building Blocks
		GolemLookup.addGolem(EntityBedrockGolem.class, (Block)null);
		GolemLookup.addGolem(EntityBoneGolem.class, Blocks.BONE_BLOCK);
		GolemLookup.addGolem(EntityBookshelfGolem.class, Blocks.BOOKSHELF);
		GolemLookup.addGolem(EntityClayGolem.class, Blocks.CLAY);
		GolemLookup.addGolem(EntityCoalGolem.class, Blocks.COAL_BLOCK);
		GolemLookup.addGolem(EntityConcreteGolem.class, Blocks.CONCRETE);
		GolemLookup.addGolem(EntityCraftingGolem.class, Blocks.CRAFTING_TABLE);
		GolemLookup.addGolem(EntityDiamondGolem.class, Blocks.DIAMOND_BLOCK);
		GolemLookup.addGolem(EntityEmeraldGolem.class, Blocks.EMERALD_BLOCK);
		GolemLookup.addGolem(EntityEndstoneGolem.class, Blocks.END_STONE);
		GolemLookup.addGolem(EntityGlassGolem.class, Blocks.GLASS);
		GolemLookup.addGolem(EntityGlowstoneGolem.class, Blocks.GLOWSTONE);
		GolemLookup.addGolem(EntityGoldGolem.class, Blocks.GOLD_BLOCK);
		GolemLookup.addGolem(EntityHardenedClayGolem.class, Blocks.HARDENED_CLAY);
		GolemLookup.addGolem(EntityIceGolem.class, new Block[] { Blocks.PACKED_ICE, Blocks.ICE });
		GolemLookup.addGolem(EntityLapisGolem.class, Blocks.LAPIS_BLOCK);
		GolemLookup.addGolem(EntityLeafGolem.class, Blocks.LEAVES);
		GolemLookup.addGolem(EntityMagmaGolem.class, Blocks.MAGMA);
		GolemLookup.addGolem(EntityMelonGolem.class, Blocks.MELON_BLOCK);
		GolemLookup.addGolem(EntityMushroomGolem.class, 
				new Block[] { Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK });
		GolemLookup.addGolem(EntityNetherBrickGolem.class, 
				new Block[] { Blocks.NETHER_BRICK, Blocks.RED_NETHER_BRICK });
		GolemLookup.addGolem(EntityNetherWartGolem.class, Blocks.NETHER_WART_BLOCK);
		GolemLookup.addGolem(EntityObsidianGolem.class, Blocks.OBSIDIAN);
		GolemLookup.addGolem(EntityPrismarineGolem.class, Blocks.PRISMARINE);
		GolemLookup.addGolem(EntityQuartzGolem.class, Blocks.QUARTZ_BLOCK);
		GolemLookup.addGolem(EntityRedSandstoneGolem.class, Blocks.RED_SANDSTONE);
		GolemLookup.addGolem(EntityRedstoneGolem.class, Blocks.REDSTONE_BLOCK);
		GolemLookup.addGolem(EntitySandstoneGolem.class, Blocks.SANDSTONE);
		GolemLookup.addGolem(EntitySeaLanternGolem.class, Blocks.SEA_LANTERN);
		GolemLookup.addGolem(EntitySlimeGolem.class, Blocks.SLIME_BLOCK);
		GolemLookup.addGolem(EntitySpongeGolem.class, Blocks.SPONGE);
		GolemLookup.addGolem(EntityStainedClayGolem.class, Blocks.STAINED_HARDENED_CLAY);
		GolemLookup.addGolem(EntityStainedGlassGolem.class, Blocks.STAINED_GLASS);
		GolemLookup.addGolem(EntityStrawGolem.class, Blocks.HAY_BLOCK);
		GolemLookup.addGolem(EntityTNTGolem.class, Blocks.TNT);
		GolemLookup.addGolem(EntityWoodenGolem.class, Blocks.LOG);
		GolemLookup.addGolem(EntityWoolGolem.class, Blocks.WOOL);
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
