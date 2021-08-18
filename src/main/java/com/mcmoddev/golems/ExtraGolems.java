package com.mcmoddev.golems;

import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.proxy.ClientProxy;
import com.mcmoddev.golems.proxy.CommonProxy;
import com.mcmoddev.golems.proxy.ServerProxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
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
    // register event handlers
    PROXY.registerEventHandlers();
    // set up config file
    EGConfig.setupConfig();
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EGConfig.COMMON_CONFIG);
    // register messages
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerNetwork");
    int messageId = 0;
    CHANNEL.registerMessage(messageId++, SGolemContainerPacket.class, SGolemContainerPacket::toBytes, SGolemContainerPacket::fromBytes, SGolemContainerPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
  }
  
  private void setup(final FMLCommonSetupEvent event) {

  }

  private void enqueueIMC(final InterModEnqueueEvent event) {

  }
  
  /**
   * Checks all registered GolemContainers until one is found that is constructed
   * out of the passed Blocks. Parameters are the current World and the 4 blocks
   * that will be used to calculate this Golem. It is okay to pass {@code null} or
   * Air.
   *
   * @return the constructed GolemBase instance if there is one for the passed blocks, otherwise null
   * @see GolemContainer#matches(Block, Block, Block, Block)
   **/
  @Nullable
  public static GolemBase getGolem(Level world, Block below1, Block below2, Block arm1, Block arm2) {
    GolemContainer container = null;
    for (Optional<GolemContainer> c : ExtraGolems.PROXY.GOLEM_CONTAINERS.getValues()) {
      if (c.isPresent() && c.get().matches(below1, below2, arm1, arm2)) {
        container = c.get();
        break;
      }
    }
    if (container == null) {
      return null;
    }
    return GolemBase.create(world, container.getMaterial());
  }
}
