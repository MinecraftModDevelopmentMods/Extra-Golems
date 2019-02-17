package com.golems.proxies;

import com.golems.blocks.BlockGolemHead;
import com.golems.blocks.BlockUtilityGlow;
import com.golems.blocks.BlockUtilityGlowWater;
import com.golems.blocks.BlockUtilityPower;
import com.golems.entity.*;
import com.golems.items.ItemBedrockGolem;
import com.golems.items.ItemInfoBook;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.ConsumerLootTables;
import com.golems.util.GolemLookup;
import com.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ExtraGolems.MODID)
public class ProxyCommon {

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

	public static EntityType build(final Class<? extends GolemBase> entityClass, Function<? super World, ? extends GolemBase> factoryIn, final String name, Block... blocks) {
		// register block(s) with GolemLookup
		if(blocks != null && blocks.length > 0) {
			GolemLookup.addGolem(entityClass, blocks);
		}
		EntityType.Builder<GolemBase> builder = EntityType.Builder.create(entityClass, factoryIn);
//		// build an EntityType to return
		builder.tracker(48, 3, true);
		return builder.build(name).setRegistryName(ExtraGolems.MODID, name);
	}
	
	/**
	 * THIS IS 100% THE MOST IMPORTANT EVENT HANDLER IN THE ENTIRE MOD.
	 * This method 1) registers all golems 2) registers their loot tables
	 * and 3) registers which block to use for which golem.
	 * @param event The EntityEntry registration event
	 */
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
		golemEntityCount = 0;
		// Register Golem EntityEntries as well as building blocks
		event.getRegistry().registerAll(
			GolemEntityTypes.BEDROCK, GolemEntityTypes.BONE, GolemEntityTypes.BOOKSHELF,
			GolemEntityTypes.CLAY, GolemEntityTypes.COAL,
			//GolemEntityTypes.CONCRETE,
			GolemEntityTypes.CRAFTING, GolemEntityTypes.DIAMOND, GolemEntityTypes.EMERALD,
			GolemEntityTypes.ENDSTONE, GolemEntityTypes.GLASS, GolemEntityTypes.GLOWSTONE,
			GolemEntityTypes.GOLD,
			//GolemEntityTypes.HARDENED_CLAY,
			GolemEntityTypes.ICE, GolemEntityTypes.LAPIS,
			//GolemEntityTypes.LEAF,
			GolemEntityTypes.MAGMA, GolemEntityTypes.MELON, GolemEntityTypes.MUSHROOM,
			GolemEntityTypes.NETHER_BRICK, GolemEntityTypes.NETHER_WART, GolemEntityTypes.OBSIDIAN,
			GolemEntityTypes.PRISMARINE, GolemEntityTypes.QUARTZ, GolemEntityTypes.RED_SANDSTONE,
			GolemEntityTypes.REDSTONE, GolemEntityTypes.SANDSTONE, GolemEntityTypes.SEA_LANTERN,
			GolemEntityTypes.SLIME, GolemEntityTypes.SPONGE,
			//GolemEntityTypes.STAINED_CLAY,
			//GolemEntityTypes.STAINED_GLASS,
			GolemEntityTypes.STRAW,
			GolemEntityTypes.TNT//,
			//GolemEntityTypes.WOOD,
			//GolemEntityTypes.WOOL
			);
		
		// Also register Golem Loot Tables
		LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/_golem_base"));
		GolemNames.forEach(new ConsumerLootTables());
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(GolemItems.golemHead, new Item.Properties()) {

			@Override
			@OnlyIn(Dist.CLIENT)
			public boolean hasEffect(final ItemStack stack) {
				return Config.golemHeadHasGlint();
			}
		}.setRegistryName(GolemItems.golemHead.getRegistryName()));

		event.getRegistry()
			.register(new ItemBedrockGolem()
				.setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"));

		event.getRegistry().register(new Item(new Item.Properties())
			.setRegistryName(ExtraGolems.MODID, "golem_paper"));

		event.getRegistry().register(new ItemInfoBook()
			.setRegistryName(ExtraGolems.MODID, "info_book"));
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		final int GLOWSTONE_FREQ = GolemLookup.getConfig(EntityGlowstoneGolem.class).getInt(EntityGlowstoneGolem.FREQUENCY);
		final int SEALANTERN_FREQ = GolemLookup.getConfig(EntitySeaLanternGolem.class).getInt(EntitySeaLanternGolem.FREQUENCY);
		event.getRegistry().registerAll(
			new BlockGolemHead().setRegistryName(ExtraGolems.MODID, "golem_head"),
			new BlockUtilityGlow(Material.GLASS, 1.0F, GLOWSTONE_FREQ, Blocks.AIR.getDefaultState())
				.setRegistryName(ExtraGolems.MODID, "light_provider_full"),
			new BlockUtilityGlowWater(Material.WATER, 1.0F, SEALANTERN_FREQ, Blocks.WATER.getDefaultState().with(BlockFlowingFluid.LEVEL, 0))
				.setRegistryName(ExtraGolems.MODID, "water_light_provider_full"),
			new BlockUtilityPower(15, EntityRedstoneGolem.DEF_FREQ).setRegistryName(ExtraGolems.MODID, "power_provider_all"));
	}

}
