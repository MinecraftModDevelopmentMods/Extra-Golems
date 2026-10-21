package com.mcmoddev.golems.client;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.client.entity.GolemModel;
import com.mcmoddev.golems.client.entity.GolemRenderType;
import com.mcmoddev.golems.client.entity.GolemRenderer;
import com.mcmoddev.golems.client.menu.GolemInventoryScreen;
import com.mcmoddev.golems.client.menu.GuideBookScreen;
import com.mcmoddev.golems.data.GolemContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class EGClientEvents {

	public static void register(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(EGClientEvents.ForgeHandler.class);
		modEventBus.register(EGClientEvents.ModHandler.class);
		ModHandler.addResources();
	}

	public static class ModHandler {

		@SubscribeEvent
		public static void setupClient(final FMLClientSetupEvent event) {
			// Screen registration moved to RegisterMenuScreensEvent
		}

		@SubscribeEvent
		public static void registerScreens(final RegisterMenuScreensEvent event) {
			event.register(EGRegistry.MenuReg.GOLEM_INVENTORY.get(), GolemInventoryScreen::new);
		}

		@SubscribeEvent
		public static void registerEntityLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(GolemRenderer.GOLEM_MODEL_RESOURCE, GolemModel::createBodyLayer);
		}

		@SubscribeEvent
		public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(EGRegistry.EntityReg.GOLEM.get(), GolemRenderer::new);
		}

		public static void addResources() {
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			if (resourceManager instanceof ReloadableResourceManager) {
				// reload dynamic texture map
				((ReloadableResourceManager) resourceManager).registerReloadListener(new SimplePreparableReloadListener<ModelBakery>() {
					@Override
					protected ModelBakery prepare(ResourceManager arg0, ProfilerFiller arg1) {
						return null;
					}

					@Override
					protected void apply(ModelBakery arg0, ResourceManager arg1, ProfilerFiller arg2) {
						GolemRenderType.reloadDynamicTextureMap();
					}

					@Override
					public String getName() {
						return "Extra Golems textures";
					}
				});
			}
		}
	}

	public static final class ForgeHandler {

		@SubscribeEvent
		public static void onPlayerLoggedOut(final ClientPlayerNetworkEvent.LoggingOut event) {
			GolemContainer.reset();
		}

		public static void loadBookGui(final Player playerIn, final ItemStack itemstack) {
			// only load client-side, of course
			if (!playerIn.level().isClientSide()) {
				return;
			}
			// open the gui
			Minecraft.getInstance().setScreen(new GuideBookScreen(playerIn, itemstack));
		}
	}
}
