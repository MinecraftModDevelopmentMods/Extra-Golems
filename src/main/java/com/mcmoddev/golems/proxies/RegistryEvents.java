package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtraGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryEvents {
	
	private RegistryEvents() { }

	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
		System.out.print("registerEntities\n");
		ExtraGolems.PROXY.registerEntities(event);
		ExtraGolems.PROXY.registerEntityRenders();
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		System.out.print("registerItems\n");
		ExtraGolems.PROXY.registerItems(event);
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		System.out.print("registerBlocks\n");
		ExtraGolems.PROXY.registerBlocks(event);
	}

	@SubscribeEvent
	public static void registerModels(final ModelRegistryEvent event) {
		System.out.print("registerModels\n");
		ExtraGolems.PROXY.registerModels();
	}
}
