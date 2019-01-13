package com.golems.proxies;

import com.golems.entity.*;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.renders.RenderColoredGolem;
import com.golems.renders.RenderGolem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ExtraGolems.MODID)
public final class ClientProxy extends CommonProxy {

	public static final IRenderFactory<GolemBase> FACTORY_TEXTURED_GOLEM = RenderGolem::new;

	public static final IRenderFactory<GolemColorized> FACTORY_COLORED_GOLEM = RenderColoredGolem::new;

	@Override
	public void registerEvents() {
		super.registerEvents();
	}


	@SubscribeEvent
	public static void registerModels(final ModelRegistryEvent event) {
		ExtraGolems.proxy.preInitRenders();
	}

	@Override
	public void preInitRenders() {

		// itemblocks
		registerRender(Item.getItemFromBlock(GolemItems.golemHead),
				Blocks.PUMPKIN.getRegistryName().toString());
		// items
		registerRender(GolemItems.golemPaper);
		registerRender(GolemItems.spawnBedrockGolem);
		registerRender(GolemItems.infoBook);
	}

	@Override
	public void registerEntities() {
		super.registerEntities();
		// register entity renders by calling a helper function
		registerTextured(EntityBedrockGolem.class);
		registerTextured(EntityBoneGolem.class);
		registerTextured(EntityBookshelfGolem.class);
		registerTextured(EntityClayGolem.class);
		registerTextured(EntityCoalGolem.class);
		registerTextured(EntityCraftingGolem.class);
		registerTextured(EntityDiamondGolem.class);
		registerTextured(EntityEmeraldGolem.class);
		registerTextured(EntityEndstoneGolem.class);
		registerTextured(EntityGlassGolem.class);
		registerTextured(EntityGlowstoneGolem.class);
		registerTextured(EntityGoldGolem.class);
		registerTextured(EntityHardenedClayGolem.class);
		registerTextured(EntityIceGolem.class);
		registerTextured(EntityLapisGolem.class);
		registerColorized(EntityLeafGolem.class);
		registerTextured(EntityMagmaGolem.class);
		registerTextured(EntityMelonGolem.class);
		registerTextured(EntityMushroomGolem.class);
		registerTextured(EntityNetherBrickGolem.class);
		registerTextured(EntityNetherWartGolem.class);
		registerTextured(EntityObsidianGolem.class);
		registerTextured(EntityPrismarineGolem.class);
		registerTextured(EntityQuartzGolem.class);
		registerTextured(EntityRedSandstoneGolem.class);
		registerTextured(EntityRedstoneGolem.class);
		registerTextured(EntitySandstoneGolem.class);
		registerTextured(EntitySeaLanternGolem.class);
		registerTextured(EntitySlimeGolem.class);
		registerTextured(EntitySpongeGolem.class);
		registerColorized(EntityStainedClayGolem.class);
		registerColorized(EntityStainedGlassGolem.class);
		registerTextured(EntityStrawGolem.class);
		registerTextured(EntityTNTGolem.class);
		registerTextured(EntityWoodenGolem.class);
		registerTextured(EntityWoolGolem.class);
	}

	/** Registers an entity with the RenderGolem rendering class. */
	public static void registerTextured(final Class<? extends GolemBase> golem) {
		RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_TEXTURED_GOLEM);
	}

	public static void registerColorized(final Class<? extends GolemColorized> golem) {
		RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_COLORED_GOLEM);
	}

	private void registerRender(final Item i, final String name, int... meta) {
		if (meta.length < 1) {
			meta = new int[] { 0 };
		}
		final ModelResourceLocation mrl = new ModelResourceLocation(name, "inventory");
		for (final int m : meta) {
			ModelLoader.setCustomModelResourceLocation(i, m, mrl);
		}
	}

	private void registerRender(final Item i, final int... meta) {
		registerRender(i, i.getRegistryName().toString(), meta);
	}
}
