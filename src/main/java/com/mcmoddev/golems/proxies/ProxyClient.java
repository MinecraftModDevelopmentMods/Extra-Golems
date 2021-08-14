package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.GolemItems;
import com.mcmoddev.golems.entity.MushroomGolem;
import com.mcmoddev.golems.entity.WoolGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.gui.GuiDispenserGolem;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.renders.GolemRenderType;
import com.mcmoddev.golems.renders.GolemRenderer;
import com.mcmoddev.golems.renders.model.GolemModel;
import com.mcmoddev.golems.renders.model.SimpleTextureLayer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

public final class ProxyClient extends ProxyCommon {

  public static final ModelLayerLocation GOLEM_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(ExtraGolems.MODID, "golem"), "main");

  @Override
  public void registerListeners() {
    // add a listener to refresh golem textures
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    if (manager instanceof ReloadableResourceManager) {
      ((ReloadableResourceManager)manager).registerReloadListener(new SimplePreparableReloadListener<ModelBakery>() {
        @Override
        protected void apply(ModelBakery arg0, ResourceManager arg1, ProfilerFiller arg2) {
          GolemRenderType.reloadDynamicTextureMap();
        }

        @Override
        protected ModelBakery prepare(ResourceManager arg0, ProfilerFiller arg1) {
          return null;
        }
        
      });
    }
  }

  @Override
  public void registerContainerRenders() {
    MenuScreens.register(GolemItems.DISPENSER_GOLEM, GuiDispenserGolem::new);
  }
  
  @Override
  public void addEntityLayers(EntityRenderersEvent.AddLayers event) {
  }

  @Override
  public void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(GOLEM_MODEL_RESOURCE , GolemModel::createBodyLayer);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
    // Simple renders
    GolemRegistrar.getContainers().forEach(container -> {
      if (!container.getRenderSettings().hasCustomRender()) {
        event.registerEntityRenderer(container.getEntityType(), m -> (new GolemRenderer<GolemBase>(m).withAllLayers()));
      }
    });
    // Custom renders
    // Lapis Golem
    registerWithSimpleLayers(event,
        GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.LAPIS_GOLEM)).getEntityType(),
        new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/gold_edging.png"));
    // Blackstone Golem
    registerWithSimpleLayers(event,
        GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.BLACKSTONE_GOLEM)).getEntityType(),
        new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/gold_nuggets.png"));
    // Wool Golem
    registerWoolGolemRenders(event);
    // Mushroom Golem
    registerMushroomGolemRenders(event);
    // Thermal Series custom renders
    if(AddonLoader.isThermalLoaded()) {
      
    }
  }
  
  private void registerWithSimpleLayers(final EntityRenderersEvent.RegisterRenderers event, final EntityType<? extends GolemBase> entityType, final ResourceLocation... layers) {
    event.registerEntityRenderer(entityType, 
      m -> {
        GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
        for(final ResourceLocation l : layers) {
          r = r.withLayer(new SimpleTextureLayer<>(r, g -> l, g -> 0xFFFFFF, g -> false, 1.0F));
        }
        return r.withAllLayers();
      });
  }
  
  private void registerWoolGolemRenders(final EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(
      GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.WOOL_GOLEM)).getEntityType(), 
      m -> {
        GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
        return r.withLayer(new SimpleTextureLayer<GolemBase>(r, g -> ((WoolGolem)g).getTexture(), g -> 0xFFFFFF, g -> false, 1.0F) {
          @Override
          protected RenderType getRenderType(final ResourceLocation texture) { return GolemRenderType.getGolemCutout(texture, GolemRenderType.WOOL_TEMPLATE, true); }
        }).withAllLayers();
      });
  }
  
  private void registerMushroomGolemRenders(final EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(
      GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.MUSHROOM_GOLEM)).getEntityType(), 
      m -> {
        GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
        return r.withLayer(new SimpleTextureLayer<GolemBase>(r, g -> ((MushroomGolem)g).getTexture(), g -> 0xFFFFFF, g -> false, 1.0F) {
          @Override
          protected RenderType getRenderType(final ResourceLocation texture) { return GolemRenderType.getGolemCutout(texture, GolemRenderType.MUSHROOM_TEMPLATE, true); }
        }).withAllLayers();
      });
  }
  

}
