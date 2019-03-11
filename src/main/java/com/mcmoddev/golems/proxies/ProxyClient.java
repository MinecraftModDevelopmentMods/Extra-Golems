package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.entity.base.*;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.renders.RenderColoredGolem;
import com.mcmoddev.golems.renders.RenderGolem;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public final class ProxyClient extends ProxyCommon {

	public static final IRenderFactory<GolemBase> FACTORY_TEXTURED_GOLEM = RenderGolem::new;

	public static final IRenderFactory<GolemColorized> FACTORY_COLORED_GOLEM = RenderColoredGolem::new;
	
	@Override
	public void registerEntityRenders() {
		GolemRegistrar.getContainers().forEach(container -> registerEntityRender(container.entityType.getEntityClass()));
	}

	@Override
	public void registerModels() {
		// itemblocks
		registerRender(Item.getItemFromBlock(GolemItems.golemHead),
			Blocks.CARVED_PUMPKIN.getRegistryName().toString());
		// items
//		registerRender(GolemItems.golemPaper);
//		registerRender(GolemItems.spawnBedrockGolem);
//		registerRender(GolemItems.infoBook);

		
	}

	/** 
	 * Helper function for entity rendering registration.
	 * If the class inherits from {@code GolemColorized.class}, 
	 * then it will be register using  {@link #registerColorized}.
	 * Otherwise, the class will be registered using
	 * {@link #registerTextured(Class)} by default.
	 */
	public static void registerEntityRender(final Class<? extends GolemBase> clazz) {
		if(GolemColorized.class.isAssignableFrom(clazz)) {
			registerColorized((Class<? extends GolemColorized>)clazz);
		} else {
			registerTextured(clazz);
		}
	}

	/**
	 * Registers an entity with the RenderGolem rendering class.
	 */
	public static void registerTextured(final Class<? extends GolemBase> golem) {
		RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_TEXTURED_GOLEM);
	}

	public static void registerColorized(final Class<? extends GolemColorized> golem) {
		RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_COLORED_GOLEM);
	}

	private static void registerRender(final Item i, final String name, int... meta) {
		if (meta.length < 1) {
			meta = new int[]{0};
		}
		final ModelResourceLocation mrl = new ModelResourceLocation(name, "inventory");
		for (final int m : meta) {
			//ModelLoader.setCustomModelResourceLocation(i, m, mrl);
		}
	}

	private static void registerRender(final Item i, final int... meta) {
		registerRender(i, i.getRegistryName().toString(), meta);
	}
}
