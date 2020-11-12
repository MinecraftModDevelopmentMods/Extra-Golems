package com.mcmoddev.golems.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.blocks.BlockUtilityPower;
import com.mcmoddev.golems.events.GolemRegistrarEvent;
import com.mcmoddev.golems.events.handlers.GolemCommonEventHandler;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.items.ItemGolemSpell;
import com.mcmoddev.golems.items.ItemInfoBook;
import com.mcmoddev.golems.proxies.ProxyClient;
import com.mcmoddev.golems.proxies.ProxyCommon;
import com.mcmoddev.golems.proxies.ProxyServer;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExtraGolems.MODID)
@Mod.EventBusSubscriber(modid = ExtraGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExtraGolems {

  public static final String MODID = "golems";

  @SuppressWarnings("Convert2MethodRef")
  // DO NOT USE METHOD REFERENCES. THESE ARE BAD! (according to gigaherz)
  public static final ProxyCommon PROXY = DistExecutor.runForDist(() -> () -> new ProxyClient(), () -> () -> new ProxyServer());

  public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);

  public ExtraGolems() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    MinecraftForge.EVENT_BUS.register(new GolemCommonEventHandler());
    ExtraGolems.PROXY.registerListeners();
    ExtraGolemsEntities.initEntityTypes();
    AddonLoader.initEntityTypes();
    // fire GolemRegistrar event for any listening child mods (addons)
    MinecraftForge.EVENT_BUS.post(new GolemRegistrarEvent());
    // set up config file
    ExtraGolemsConfig.setupConfig();
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExtraGolemsConfig.COMMON_CONFIG);
  }

  private void setup(final FMLCommonSetupEvent event) {
    AddonLoader.setupEvent(event);
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    AddonLoader.interModEnqueueEvent(event);
  }

  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    ExtraGolems.LOGGER.info("registerEntities");
    // Register EntityTypes by iterating through each registered GolemContainer
    GolemRegistrar.getContainers().forEach(container -> {
      event.getRegistry().register(container.getEntityType());
    });
    ExtraGolems.PROXY.registerEntityRenders();
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    ExtraGolems.LOGGER.info("registerItems");
    event.getRegistry().registerAll(new BlockItem(GolemItems.GOLEM_HEAD, new Item.Properties().group(ItemGroup.MISC)) {
      @Override
      @OnlyIn(Dist.CLIENT)
      public boolean hasEffect(final ItemStack stack) {
        return true;
      }
    }.setRegistryName(GolemItems.GOLEM_HEAD.getRegistryName()), new ItemBedrockGolem().setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"),
        new ItemGolemSpell().setRegistryName(ExtraGolems.MODID, "golem_paper"), new ItemInfoBook().setRegistryName(ExtraGolems.MODID, "info_book"));
  }

  @SubscribeEvent
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    ExtraGolems.LOGGER.info("registerBlocks");
    event.getRegistry().registerAll(new BlockGolemHead().setRegistryName(ExtraGolems.MODID, "golem_head"),
        new BlockUtilityGlow(Material.GLASS, 1.0F).setRegistryName(ExtraGolems.MODID, "light_provider"),
        new BlockUtilityPower(15).setRegistryName(ExtraGolems.MODID, "power_provider"));
  }

  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    ExtraGolems.LOGGER.info("registerContainers");
    event.getRegistry().register(GolemItems.CRAFTING_GOLEM.setRegistryName(ExtraGolems.MODID, "crafting_portable"));
    event.getRegistry().register(GolemItems.DISPENSER_GOLEM.setRegistryName(ExtraGolems.MODID, "dispenser_portable"));
    ExtraGolems.PROXY.registerContainerRenders();
  }
}
