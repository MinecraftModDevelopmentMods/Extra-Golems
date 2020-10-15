package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemColorized;
import com.mcmoddev.golems.gui.GuiDispenserGolem;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.renders.ColoredGolemRenderer;
import com.mcmoddev.golems.renders.GolemRenderer;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public final class ProxyClient extends ProxyCommon {

  public static final IRenderFactory<GolemBase> FACTORY_TEXTURED_GOLEM = GolemRenderer::new;

  public static final IRenderFactory<GolemColorized> FACTORY_COLORED_GOLEM = ColoredGolemRenderer::new;

  @Override
  public void registerListeners() {
    // TODO this was supposed to help with block tags...
//		final IResourceManager irr = Minecraft.getInstance().getResourceManager();
//		if(irr instanceof IReloadableResourceManager) {
//			((IReloadableResourceManager) irr).addReloadListener(l -> BlockTagUtil.loadTags());
//		}
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
      if (container.useDefaultRender()) {
        if (GolemColorized.class.isAssignableFrom(container.getEntityClass())) {
          registerColorized((EntityType<? extends GolemColorized>) container.getEntityType());
        } else {
          registerTextured(container.getEntityType());
        }

      }
    });
  }

  /**
   * Registers an entity with the GolemRenderer rendering class.
   * 
   * @param type the EntityType. Must be of type {@code EntityType<GolemBase>}
   */
  public static void registerTextured(final EntityType<? extends GolemBase> type) {
    RenderingRegistry.registerEntityRenderingHandler(type, FACTORY_TEXTURED_GOLEM);
  }

  /**
   * Registers an entity with the ColoredGolemRenderer class
   * 
   * @param type the EntityType. Must be of type
   *             {@code EntityType<GolemColorized>}
   **/
  public static void registerColorized(final EntityType<? extends GolemColorized> type) {
    RenderingRegistry.registerEntityRenderingHandler(type, FACTORY_COLORED_GOLEM);
  }
}
