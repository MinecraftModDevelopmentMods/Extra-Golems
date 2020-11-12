package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.entity.MushroomGolem;
import com.mcmoddev.golems.entity.WoolGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.gui.GuiDispenserGolem;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.renders.GolemRenderType;
import com.mcmoddev.golems.renders.GolemRenderer;
import com.mcmoddev.golems.renders.model.SimpleTextureLayer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems_thermal.ThermalGolemNames;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public final class ProxyClient extends ProxyCommon {

  @Override
  public void registerListeners() {
    // add a listener to refresh golem textures
    IResourceManager manager = Minecraft.getInstance().getResourceManager();
    if (manager instanceof IReloadableResourceManager) {
      ((IReloadableResourceManager)manager).addReloadListener(new ReloadListener<ModelBakery>() {
        @Override
        protected void apply(ModelBakery arg0, IResourceManager arg1, IProfiler arg2) {
          GolemRenderType.reloadDynamicTextureMap();
        }

        @Override
        protected ModelBakery prepare(IResourceManager arg0, IProfiler arg1) {
          return null;
        }
        
      });
    }
  }

  @Override
  public void registerContainerRenders() {
    ScreenManager.registerFactory(GolemItems.DISPENSER_GOLEM, GuiDispenserGolem::new);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void registerEntityRenders() {
    // Simple renders
    GolemRegistrar.getContainers().forEach(container -> {
      if (!container.getRenderSettings().hasCustomRender()) {
        RenderingRegistry.registerEntityRenderingHandler(container.getEntityType(), m -> (new GolemRenderer<GolemBase>(m).withAllLayers()));
      }
    });
    // Custom renders
    // Lapis Golem
    registerWithSimpleLayers(
        GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.LAPIS_GOLEM)).getEntityType(),
        new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/gold_edging.png"));
    // Blackstone Golem
    registerWithSimpleLayers(
        GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.BLACKSTONE_GOLEM)).getEntityType(),
        new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/gold_nuggets.png"));
    // Wool Golem
    registerWoolGolemRenders();
    // Mushroom Golem
    registerMushroomGolemRenders();
    // Thermal Series custom renders
    if(AddonLoader.isThermalLoaded()) {
      // Rockwool Golem
      registerRockwoolGolemRenders();
    }
  }
  
  private void registerWithSimpleLayers(final EntityType<? extends GolemBase> entityType, final ResourceLocation... layers) {
    RenderingRegistry.registerEntityRenderingHandler(entityType, 
      m -> {
        GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
        for(final ResourceLocation l : layers) {
          r = r.withLayer(new SimpleTextureLayer<>(r, g -> l, g -> 0xFFFFFF, g -> false, 1.0F));
        }
        return r.withAllLayers();
      });
  }
  
  private void registerWoolGolemRenders() {
    RenderingRegistry.registerEntityRenderingHandler(
      GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.WOOL_GOLEM)).getEntityType(), 
      m -> {
        GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
        return r.withLayer(new SimpleTextureLayer<GolemBase>(r, g -> ((WoolGolem)g).getTexture(), g -> 0xFFFFFF, g -> false, 1.0F) {
          @Override
          protected RenderType getRenderType(final ResourceLocation texture) { return GolemRenderType.getGolemCutout(texture, GolemRenderType.WOOL_TEMPLATE, true); }
        }).withAllLayers();
      });
  }
  
  private void registerMushroomGolemRenders() {
    RenderingRegistry.registerEntityRenderingHandler(
      GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.MUSHROOM_GOLEM)).getEntityType(), 
      m -> {
        GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
        return r.withLayer(new SimpleTextureLayer<GolemBase>(r, g -> ((MushroomGolem)g).getTexture(), g -> 0xFFFFFF, g -> false, 1.0F) {
          @Override
          protected RenderType getRenderType(final ResourceLocation texture) { return GolemRenderType.getGolemCutout(texture, GolemRenderType.MUSHROOM_TEMPLATE, true); }
        }).withAllLayers();
      });
  }
  
  private void registerRockwoolGolemRenders() {
    RenderingRegistry.registerEntityRenderingHandler(
      GolemRegistrar.getContainer(new ResourceLocation(AddonLoader.THERMAL_GOLEMS_MODID, ThermalGolemNames.ROCKWOOL_GOLEM)).getEntityType(), 
      m -> {
        GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
        return r.withLayer(new SimpleTextureLayer<GolemBase>(r, g -> ((com.mcmoddev.golems_thermal.entity.RockwoolGolem)g).getTexture(), g -> 0xFFFFFF, g -> false, 1.0F) {
          @Override
          protected RenderType getRenderType(final ResourceLocation texture) { return GolemRenderType.getGolemCutout(texture, GolemRenderType.WOOL_TEMPLATE, true); }
        }).withAllLayers();
      });
  }

}
