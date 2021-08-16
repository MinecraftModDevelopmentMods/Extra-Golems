package com.mcmoddev.golems;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.blocks.GlowBlock;
import com.mcmoddev.golems.blocks.PowerBlock;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.items.GolemSpellItem;
import com.mcmoddev.golems.items.GuideBookItem;
import com.mcmoddev.golems.items.SpawnGolemItem;
import com.mcmoddev.golems.proxies.ClientProxy;
import com.mcmoddev.golems.proxies.CommonProxy;
import com.mcmoddev.golems.proxies.ServerProxy;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

@Mod(ExtraGolems.MODID)
@Mod.EventBusSubscriber(modid = ExtraGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExtraGolems {

  public static final String MODID = "golems";

  @SuppressWarnings("Convert2MethodRef")
  // DO NOT USE METHOD REFERENCES. THESE ARE BAD! (according to gigaherz)
  public static final CommonProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);
  
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public ExtraGolems() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    MinecraftForge.EVENT_BUS.register(new EGEvents());
    ExtraGolems.PROXY.registerListeners();
    ExtraGolemsEntities.initEntityTypes();
    // set up config file
    ExtraGolemsConfig.setupConfig();
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExtraGolemsConfig.COMMON_CONFIG);
  }
  
  private void setup(final FMLCommonSetupEvent event) {

  }

  private void enqueueIMC(final InterModEnqueueEvent event) {

  }

  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    ExtraGolems.LOGGER.info("registerEntities");
    EntityType.Builder<GolemBase> builder = EntityType.Builder.of(GolemBase::new, MobCategory.MISC)
        .setTrackingRange(48).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).sized(1.4F, 2.9F);
    EntityType<GolemBase> entityType = builder.build("golem");
    event.getRegistry().register(entityType.setRegistryName(MODID, "golem"));
  }
  
  @SubscribeEvent
  public static void registerEntityLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    ExtraGolems.LOGGER.info("registerEntityLayers");
    ExtraGolems.PROXY.registerEntityLayers(event);
  }
  
  @SubscribeEvent
  public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    ExtraGolems.LOGGER.info("registerEntityRenderers");
    ExtraGolems.PROXY.registerEntityRenders(event);
  }
  
  @SubscribeEvent
  public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
    ExtraGolems.LOGGER.info("registerEntityAttributes");
    event.put(EGRegistry.GOLEM, GolemContainer.EMPTY.getAttributeSupplier().get().build());
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    ExtraGolems.LOGGER.info("registerItems");
    event.getRegistry().registerAll(new BlockItem(EGRegistry.GOLEM_HEAD, new Item.Properties().tab(CreativeModeTab.TAB_MISC)) {
      @Override
      @OnlyIn(Dist.CLIENT)
      public boolean isFoil(final ItemStack stack) {
        return true;
      }
    }.setRegistryName(EGRegistry.GOLEM_HEAD.getRegistryName()), new SpawnGolemItem().setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"),
        new GolemSpellItem().setRegistryName(ExtraGolems.MODID, "golem_paper"), new GuideBookItem().setRegistryName(ExtraGolems.MODID, "info_book"));
  }

  @SubscribeEvent
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    ExtraGolems.LOGGER.info("registerBlocks");
    event.getRegistry().registerAll(new BlockGolemHead().setRegistryName(ExtraGolems.MODID, "golem_head"),
        new GlowBlock(Material.GLASS, 1.0F).setRegistryName(ExtraGolems.MODID, "light_provider"),
        new PowerBlock(15).setRegistryName(ExtraGolems.MODID, "power_provider"));
  }

  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
    ExtraGolems.LOGGER.info("registerContainers");
    event.getRegistry().register(EGRegistry.CRAFTING_GOLEM.setRegistryName(ExtraGolems.MODID, "crafting_portable"));
    event.getRegistry().register(EGRegistry.DISPENSER_GOLEM.setRegistryName(ExtraGolems.MODID, "dispenser_portable"));
    ExtraGolems.PROXY.registerContainerRenders();
  }
}
