package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.blocks.BlockUtilityGlowWater;
import com.mcmoddev.golems.blocks.BlockUtilityPower;
import com.mcmoddev.golems.entity.EntityRedstoneGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.items.ItemInfoBook;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.ConsumerLootTables;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ExtraGolems.MODID)
public class ProxyCommon {

	/**
	 * A List containing default instances of each Golem.
	 * They do not exist in the world, the list is simply a 
	 * reference for things like Golem Info Book
	 **/
	public final List<GolemBase> DUMMY_GOLEMS = new LinkedList<>();
	
	protected static int golemEntityCount;

	public void preInitRenders() {
		// Unused
	}
	
	/**
	 * 1) register all golems 
	 * 2) register their loot tables
	 * @param event The EntityEntry registration event
	 */
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
		golemEntityCount = 0;
		// Register Golem EntityEntries as well as building blocks
		GolemRegistrar.getContainers().forEach(container -> event.getRegistry().register(container.entityType));
		// Also register Golem Loot Tables
		LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/_golem_base"));
		GolemNames.forEach(new ConsumerLootTables());
	}

	@SubscribeEvent
	public void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(GolemItems.golemHead, new Item.Properties().group(ItemGroup.MISC)) {

			@Override
			@OnlyIn(Dist.CLIENT)
			public boolean hasEffect(final ItemStack stack) {
				return true;
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
	public void registerBlocks(final RegistryEvent.Register<Block> event) {
		// TODO use config once it's working
		final int GLOWSTONE_FREQ = 4;//GolemLookup.getConfig(EntityGlowstoneGolem.class).getInt(EntityGlowstoneGolem.FREQUENCY);
		final int SEALANTERN_FREQ = 4;//GolemLookup.getConfig(EntitySeaLanternGolem.class).getInt(EntitySeaLanternGolem.FREQUENCY);
		event.getRegistry().registerAll(
			new BlockGolemHead().setRegistryName(ExtraGolems.MODID, "golem_head"),
			new BlockUtilityGlow(Material.GLASS, 1.0F, GLOWSTONE_FREQ, Blocks.AIR.getDefaultState())
				.setRegistryName(ExtraGolems.MODID, "light_provider_full"),
			new BlockUtilityGlowWater(Material.WATER, 1.0F, SEALANTERN_FREQ, Blocks.WATER.getDefaultState().with(BlockFlowingFluid.LEVEL, 0))
				.setRegistryName(ExtraGolems.MODID, "water_light_provider_full"),
			new BlockUtilityPower(15, EntityRedstoneGolem.DEF_FREQ).setRegistryName(ExtraGolems.MODID, "power_provider_all"));
	}

}
