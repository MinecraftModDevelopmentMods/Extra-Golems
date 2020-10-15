package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.gui.GuiDispenserGolem;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.renders.GolemRenderer;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.client.gui.ScreenManager;
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
        RenderingRegistry.registerEntityRenderingHandler(container.getEntityType(), GolemRenderer::new);
      }
    });
  }
}
