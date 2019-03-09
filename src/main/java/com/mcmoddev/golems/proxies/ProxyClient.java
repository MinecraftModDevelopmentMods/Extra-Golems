package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.entity.*;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.renders.RenderColoredGolem;
import com.mcmoddev.golems.renders.RenderGolem;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ExtraGolems.MODID)
public final class ProxyClient extends ProxyCommon {

	public static final IRenderFactory<GolemBase> FACTORY_TEXTURED_GOLEM = RenderGolem::new;

	public static final IRenderFactory<GolemColorized> FACTORY_COLORED_GOLEM = RenderColoredGolem::new;

	@SubscribeEvent
	public static void registerModels(final ModelRegistryEvent event) {
		// itemblocks
		registerRender(Item.getItemFromBlock(GolemItems.golemHead),
			Blocks.PUMPKIN.getRegistryName().toString());
		// items
		registerRender(GolemItems.golemPaper);
		registerRender(GolemItems.spawnBedrockGolem);
		registerRender(GolemItems.infoBook);
	
		// register entity renders by calling a helper function
		registerEntityRender(EntityBedrockGolem.class);
		registerEntityRender(EntityBoneGolem.class);
		registerEntityRender(EntityBookshelfGolem.class);
		registerEntityRender(EntityClayGolem.class);
		registerEntityRender(EntityCoalGolem.class);
		////registerEntityRender(EntityConcreteGolem.class);
		registerEntityRender(EntityCraftingGolem.class);
		registerEntityRender(EntityDiamondGolem.class);
		registerEntityRender(EntityEmeraldGolem.class);
		registerEntityRender(EntityEndstoneGolem.class);
		registerEntityRender(EntityGlassGolem.class);
		registerEntityRender(EntityGlowstoneGolem.class);
		registerEntityRender(EntityGoldGolem.class);
		////registerEntityRender(EntityHardenedClayGolem.class);
		registerEntityRender(EntityIceGolem.class);
		registerEntityRender(EntityLapisGolem.class);
		////registerEntityRender(EntityLeafGolem.class);
		registerEntityRender(EntityMagmaGolem.class);
		registerEntityRender(EntityMelonGolem.class);
		registerEntityRender(EntityMushroomGolem.class);
		registerEntityRender(EntityNetherBrickGolem.class);
		registerEntityRender(EntityNetherWartGolem.class);
		registerEntityRender(EntityObsidianGolem.class);
		registerEntityRender(EntityPrismarineGolem.class);
		registerEntityRender(EntityQuartzGolem.class);
		registerEntityRender(EntityRedSandstoneGolem.class);
		registerEntityRender(EntityRedstoneGolem.class);
		registerEntityRender(EntitySandstoneGolem.class);
		registerEntityRender(EntitySeaLanternGolem.class);
		registerEntityRender(EntitySlimeGolem.class);
		registerEntityRender(EntitySpongeGolem.class);
		////registerEntityRender(EntityStainedClayGolem.class);
		////registerEntityRender(EntityStainedGlassGolem.class);
		registerEntityRender(EntityStrawGolem.class);
		registerEntityRender(EntityTNTGolem.class);
		////registerEntityRender(EntityWoodenGolem.class);
		////registerEntityRender(EntityWoolGolem.class);
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
