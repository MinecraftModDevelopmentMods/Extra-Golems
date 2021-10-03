package com.mcmoddev.golems;

import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mcmoddev.golems.integration.TopDescriptionManager;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcmoddev.golems.golem_stats.GolemContainer;
import com.mcmoddev.golems.golem_stats.behavior.GolemBehaviors;
import com.mcmoddev.golems.golem_models.GolemRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.event.EGForgeEvents;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.network.SGolemModelPacket;
import com.mcmoddev.golems.util.GenericJsonReloadListener;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExtraGolems.MODID)
public class ExtraGolems {

  public static final String MODID = "golems";

  public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);
  
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public static final GenericJsonReloadListener<GolemContainer> GOLEM_CONTAINERS = new GenericJsonReloadListener<>("golem_stats", GolemContainer.class, GolemContainer.CODEC, 
      l -> l.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemContainerPacket(e.getKey(), c)))));

  public static final GenericJsonReloadListener<GolemRenderSettings> GOLEM_RENDER_SETTINGS = new GenericJsonReloadListener<>("golem_models", GolemRenderSettings.class, GolemRenderSettings.CODEC, 
      l -> l.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemModelPacket(e.getKey(), c)))));
  
  public ExtraGolems() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    // init helper classes
    GolemBehaviors.init();
    // register event handlers
    this.registerCommonEvents();
    DistExecutor.runForDist(() -> () -> this.registerClientEvents(), () -> () -> this.registerServerEvents());
    // set up config file
    EGConfig.setupConfig();
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EGConfig.COMMON_CONFIG);
    // register messages
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerNetwork");
    int messageId = 0;
    CHANNEL.registerMessage(messageId++, SGolemContainerPacket.class, SGolemContainerPacket::toBytes, SGolemContainerPacket::fromBytes, SGolemContainerPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    CHANNEL.registerMessage(messageId++, SGolemModelPacket.class, SGolemModelPacket::toBytes, SGolemModelPacket::fromBytes, SGolemModelPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
  
  }
  
  private void setup(final FMLCommonSetupEvent event) {

  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    // register TheOneProbe integration
    if(ModList.get().isLoaded("theoneprobe")) {
      ExtraGolems.LOGGER.info("Extra Golems detected TheOneProbe, registering plugin now");
      InterModComms.sendTo(MODID, "theoneprobe", "getTheOneProbe", () -> new TopDescriptionManager.GetTheOneProbe());
    }
  }
  
  private int registerCommonEvents() {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEventHandlers");
    MinecraftForge.EVENT_BUS.register(EGForgeEvents.class);
    FMLJavaModLoadingContext.get().getModEventBus().register(EGRegistry.class);
    return 0;
  }

  private int registerServerEvents() { 
    return 1;
  }
  
  private int registerClientEvents() {
    MinecraftForge.EVENT_BUS.register(com.mcmoddev.golems.event.EGClientEvents.class);
    FMLJavaModLoadingContext.get().getModEventBus().register(com.mcmoddev.golems.event.EGClientModEvents.class);
    com.mcmoddev.golems.event.EGClientEvents.addResources();
    return 2;
  }

  /**
   * Checks all registered GolemContainers until one is found that is constructed
   * out of the passed Blocks. Parameters are the current World and the 4 blocks
   * that will be used to calculate this Golem. It is okay to pass {@code null} or
   * Air.
   *
   * @return an Optional that contains the ResourceLocation of a golem material, or empty if there is none for the passed blocks
   * @see GolemContainer#matches(Block, Block, Block, Block)
   **/
  public static Optional<ResourceLocation> getGolemId(Block below1, Block below2, Block arm1, Block arm2) {
	ResourceLocation id = null;
	for (Entry<ResourceLocation, Optional<GolemContainer>> entry : GOLEM_CONTAINERS.getEntries()) {
	  if (entry.getValue().isPresent() && entry.getValue().get().matches(below1, below2, arm1, arm2)) {
		id = entry.getKey();
		break;
	  }
	}
	return Optional.ofNullable(id);
  }

  /**
   * Checks all registered GolemContainers until one is found that is constructed
   * out of the passed Blocks. Parameters are the current World and the 4 blocks
   * that will be used to calculate this Golem. It is okay to pass {@code null} or
   * Air.
   *
   * @return the constructed GolemBase instance if there is one for the passed blocks, otherwise null
   * @see #getGolemId(Block, Block, Block, Block)
   **/
  @Nullable
  public static GolemBase getGolem(World world, Block below1, Block below2, Block arm1, Block arm2) {
    Optional<ResourceLocation> id = getGolemId(below1, below2, arm1, arm2);
    if (id.isPresent()) {
	  GolemBase entity = GolemBase.create(world, id.get());
	  return entity;
    }
    return null;
  }
}
