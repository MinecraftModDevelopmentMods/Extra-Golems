package com.golems.proxies;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.EntityBoneGolem;
import com.golems.entity.EntityBookshelfGolem;
import com.golems.entity.EntityClayGolem;
import com.golems.entity.EntityCoalGolem;
import com.golems.entity.EntityConcreteGolem;
import com.golems.entity.EntityCraftingGolem;
import com.golems.entity.EntityDiamondGolem;
import com.golems.entity.EntityDispenserGolem;
import com.golems.entity.EntityEmeraldGolem;
import com.golems.entity.EntityEndstoneGolem;
import com.golems.entity.EntityFurnaceGolem;
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
import com.golems.entity.EntityRedstoneLampGolem;
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
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.renders.RenderColoredGolem;
import com.golems.renders.RenderGolem;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ExtraGolems.MODID)
public final class ClientProxy extends CommonProxy {

  public static final IRenderFactory<GolemBase> FACTORY_TEXTURED_GOLEM = RenderGolem::new;

  public static final IRenderFactory<GolemColorized> FACTORY_COLORED_GOLEM = RenderColoredGolem::new;

  @SubscribeEvent
  public static void registerModels(final ModelRegistryEvent event) {
    // itemblocks
    registerRender(Item.getItemFromBlock(GolemItems.golemHead), Blocks.PUMPKIN.getRegistryName().toString());
    // items
    registerRender(GolemItems.golemPaper);
    registerRender(GolemItems.spawnBedrockGolem);
    registerRender(GolemItems.infoBook);

    // register entity renders by calling a helper function
    registerEntityRender(EntityBedrockGolem.class);
    registerEntityRender(EntityBoneGolem.class);
    registerEntityRender(EntityBookshelfGolem.class);
    registerEntityRender(EntityClayGolem.class);
    registerEntityRender(EntityCoalGolem.class);
    registerEntityRender(EntityConcreteGolem.class);
    registerEntityRender(EntityCraftingGolem.class);
    registerEntityRender(EntityDiamondGolem.class);
    registerEntityRender(EntityDispenserGolem.class);
    registerEntityRender(EntityEmeraldGolem.class);
    registerEntityRender(EntityEndstoneGolem.class);
    registerEntityRender(EntityFurnaceGolem.class);
    registerEntityRender(EntityGlassGolem.class);
    registerEntityRender(EntityGlowstoneGolem.class);
    registerEntityRender(EntityGoldGolem.class);
    registerEntityRender(EntityHardenedClayGolem.class);
    registerEntityRender(EntityIceGolem.class);
    registerEntityRender(EntityLapisGolem.class);
    registerEntityRender(EntityLeafGolem.class);
    registerEntityRender(EntityMagmaGolem.class);
    registerEntityRender(EntityMelonGolem.class);
    registerEntityRender(EntityMushroomGolem.class);
    registerEntityRender(EntityNetherBrickGolem.class);
    registerEntityRender(EntityNetherWartGolem.class);
    registerEntityRender(EntityObsidianGolem.class);
    registerEntityRender(EntityPrismarineGolem.class);
    registerEntityRender(EntityQuartzGolem.class);
    registerEntityRender(EntityRedSandstoneGolem.class);
    registerEntityRender(EntityRedstoneGolem.class);
    registerEntityRender(EntityRedstoneLampGolem.class);
    registerEntityRender(EntitySandstoneGolem.class);
    registerEntityRender(EntitySeaLanternGolem.class);
    registerEntityRender(EntitySlimeGolem.class);
    registerEntityRender(EntitySpongeGolem.class);
    registerEntityRender(EntityStainedClayGolem.class);
    registerEntityRender(EntityStainedGlassGolem.class);
    registerEntityRender(EntityStrawGolem.class);
    registerEntityRender(EntityTNTGolem.class);
    registerEntityRender(EntityWoodenGolem.class);
    registerEntityRender(EntityWoolGolem.class);
  }

  /**
   * Helper function for entity rendering registration. If the class inherits from
   * {@code GolemColorized.class}, then it will be register using
   * {@link #registerColorized}. Otherwise, the class will be registered using
   * {@link #registerTextured(Class)} by default.
   */
  public static void registerEntityRender(final Class<? extends GolemBase> clazz) {
    if (GolemColorized.class.isAssignableFrom(clazz)) {
      registerColorized((Class<? extends GolemColorized>) clazz);
    } else {
      registerTextured(clazz);
    }
  }

  /**
   * Registers an entity with the RenderGolem rendering class.
   */
  public static void registerTextured(final Class<? extends GolemBase> golem) {
    RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_TEXTURED_GOLEM);
  }

  public static void registerColorized(final Class<? extends GolemColorized> golem) {
    RenderingRegistry.registerEntityRenderingHandler(golem, FACTORY_COLORED_GOLEM);
  }

  private static void registerRender(final Item i, final String name, int... meta) {
    if (meta.length < 1) {
      meta = new int[] { 0 };
    }
    final ModelResourceLocation mrl = new ModelResourceLocation(name, "inventory");
    for (final int m : meta) {
      ModelLoader.setCustomModelResourceLocation(i, m, mrl);
    }
  }

  private static void registerRender(final Item i, final int... meta) {
    registerRender(i, i.getRegistryName().toString(), meta);
  }
}
