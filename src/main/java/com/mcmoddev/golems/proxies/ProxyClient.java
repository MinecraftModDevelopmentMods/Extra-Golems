package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.gui.GuiDispenserGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.renders.GolemRenderType;
import com.mcmoddev.golems.renders.GolemRenderer;
import com.mcmoddev.golems.renders.model.SimpleTextureLayer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
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
    // TODO this was supposed to help with block tags...
//		final IResourceManager irr = Minecraft.getInstance().getResourceManager();
//		if(irr instanceof IReloadableResourceManager) {
//			((IReloadableResourceManager) irr).addReloadListener(l -> BlockTagUtil.loadTags());
//		}
    // TODO add a listener to refresh golem textures
    IResourceManager manager = Minecraft.getInstance().getResourceManager();
    if (manager instanceof IReloadableResourceManager) {
      ((IReloadableResourceManager)manager).addReloadListener(new ReloadListener<ModelBakery>() {

        @Override
        protected void apply(ModelBakery arg0, IResourceManager arg1, IProfiler arg2) {
          GolemRenderType.clearDynamicTextureMap();
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
    // ScreenManager.registerFactory(p_216911_0_, p_216911_1_);
    ScreenManager.registerFactory(GolemItems.DISPENSER_GOLEM, GuiDispenserGolem::new);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void registerEntityRenders() {
    GolemRegistrar.getContainers().forEach(container -> {
      if (!container.getRenderSettings().hasCustomRender()) {
        RenderingRegistry.registerEntityRenderingHandler(container.getEntityType(), m -> (new GolemRenderer<GolemBase>(m).withAllLayers()));
      }
    });
    // Custom renders
    registerLapisGolemRenders();
  }
  
  private void registerLapisGolemRenders() {
    final ResourceLocation GOLD = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/gold_edging.png");
    RenderingRegistry.registerEntityRenderingHandler(
        GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.LAPIS_GOLEM)).getEntityType(), 
        m -> {
          GolemRenderer<GolemBase> r = new GolemRenderer<>(m);
          return r.withLayer(new SimpleTextureLayer<>(r, g -> GOLD, g -> 0xFFFFFF, g -> false, 1.0F)).withAllLayers();
        });
  }

}
