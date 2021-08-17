package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.gui.DispenserGolemScreen;
import com.mcmoddev.golems.renders.GolemRenderType;
import com.mcmoddev.golems.renders.GolemRenderer;
import com.mcmoddev.golems.renders.model.GolemModel;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;

public final class ClientProxy extends CommonProxy {

  public static final ModelLayerLocation GOLEM_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(ExtraGolems.MODID, "entity"), "main");

  @Override
  public void addReloadListeners(AddReloadListenerEvent event) {
    // reload render settings
    event.addListener(GOLEM_RENDER_SETTINGS);
    // reload dynamic texture map
    event.addListener(new SimplePreparableReloadListener<ModelBakery>() {
      @Override
      protected ModelBakery prepare(ResourceManager arg0, ProfilerFiller arg1) { return null; }
      @Override
      protected void apply(ModelBakery arg0, ResourceManager arg1, ProfilerFiller arg2) {
        GolemRenderType.reloadDynamicTextureMap();
      }
    });
  }

  @Override
  public void registerContainerRenders() {
    MenuScreens.register(EGRegistry.DISPENSER_GOLEM, DispenserGolemScreen::new);
  }
  
  @Override
  public void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(GOLEM_MODEL_RESOURCE , GolemModel::createBodyLayer);
  }

  @Override
  public void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(EGRegistry.GOLEM, m -> (new GolemRenderer<GolemBase>(m)));
  }
}
