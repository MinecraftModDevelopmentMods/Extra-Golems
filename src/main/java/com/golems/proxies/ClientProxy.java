package com.golems.proxies;

import com.golems.blocks.BlockLightProvider;
import com.golems.blocks.BlockPowerProvider;
import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.EntityBoneGolem;
import com.golems.entity.EntityBookshelfGolem;
import com.golems.entity.EntityClayGolem;
import com.golems.entity.EntityCoalGolem;
import com.golems.entity.EntityCraftingGolem;
import com.golems.entity.EntityDiamondGolem;
import com.golems.entity.EntityEmeraldGolem;
import com.golems.entity.EntityEndstoneGolem;
import com.golems.entity.EntityGlassGolem;
import com.golems.entity.EntityGlowstoneGolem;
import com.golems.entity.EntityGoldGolem;
import com.golems.entity.EntityHardenedClayGolem;
import com.golems.entity.EntityIceGolem;
import com.golems.entity.EntityLapisGolem;
import com.golems.entity.EntityLeafGolem;
import com.golems.entity.EntityMagmaGolem;
import com.golems.entity.EntityMelonGolem;
import com.golems.entity.EntityMushroomGolem;
import com.golems.entity.EntityNetherBrickGolem;
import com.golems.entity.EntityNetherWartGolem;
import com.golems.entity.EntityObsidianGolem;
import com.golems.entity.EntityPrismarineGolem;
import com.golems.entity.EntityQuartzGolem;
import com.golems.entity.EntityRedSandstoneGolem;
import com.golems.entity.EntityRedstoneGolem;
import com.golems.entity.EntitySandstoneGolem;
import com.golems.entity.EntitySeaLanternGolem;
import com.golems.entity.EntitySlimeGolem;
import com.golems.entity.EntitySpongeGolem;
import com.golems.entity.EntityStainedClayGolem;
import com.golems.entity.EntityStainedGlassGolem;
import com.golems.entity.EntityStrawGolem;
import com.golems.entity.EntityTNTGolem;
import com.golems.entity.EntityWoodenGolem;
import com.golems.entity.EntityWoolGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemColorized;
import com.golems.events.handlers.GolemClientEventHandler;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.renders.RenderColoredGolem;
import com.golems.renders.RenderGolem;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
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
public class ClientProxy extends CommonProxy {

	public static final IRenderFactory<GolemBase> FACTORY_TEXTURED_GOLEM = new IRenderFactory<GolemBase>() {

		@Override
		public Render<? super GolemBase> createRenderFor(RenderManager manager) {
			return new RenderGolem(manager);
		}
	};

	public static final IRenderFactory<GolemColorized> FACTORY_COLORED_GOLEM = new IRenderFactory<GolemColorized>() {

		@Override
		public Render<? super GolemColorized> createRenderFor(RenderManager manager) {
			return new RenderColoredGolem(manager);
		}
	};

	@Override
	public void registerEvents() {
		super.registerEvents();
		MinecraftForge.EVENT_BUS.register(new GolemClientEventHandler());
	}

	// TODO: this looks wrong
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ExtraGolems.proxy.preInitRenders();
	}

	@Override
	public void preInitRenders() {
		// blocks
		ModelLoader.setCustomStateMapper(GolemItems.blockPowerSource,
				new StateMap.Builder().ignore(BlockPowerProvider.POWER).build());
		ModelLoader.setCustomStateMapper(GolemItems.blockLightSource,
				new StateMap.Builder().ignore(BlockLightProvider.LIGHT).build());
		// itemblocks
		registerRender(Item.getItemFromBlock(GolemItems.golemHead),
				Blocks.PUMPKIN.getRegistryName().toString());
		// items
		registerRender(GolemItems.golemPaper);
		registerRender(GolemItems.spawnBedrockGolem);
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

	/** Registers an entity with the RenderGolem rendering class */
	public static void registerTextured(Class<? extends GolemBase> golem) {
		RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_TEXTURED_GOLEM);
	}

	public static void registerColorized(Class<? extends GolemColorized> golem) {
		RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_COLORED_GOLEM);
	}

	private void registerRender(Item i, String name, int... meta) {
		if (meta.length < 1) {
			meta = new int[] { 0 };
		}
		final ModelResourceLocation MRL = new ModelResourceLocation(name, "inventory");
		for (int m : meta) {
			ModelLoader.setCustomModelResourceLocation(i, m, MRL);
		}
	}

	private void registerRender(Item i, int... meta) {
		registerRender(i, i.getRegistryName().toString(), meta);
	}
}
