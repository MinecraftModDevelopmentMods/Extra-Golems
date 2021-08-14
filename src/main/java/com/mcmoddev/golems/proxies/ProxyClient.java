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
import com.mcmoddev.golems.renders.model.SimpleTextureLayer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public final class ProxyClient extends ProxyCommon {

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
  

}
