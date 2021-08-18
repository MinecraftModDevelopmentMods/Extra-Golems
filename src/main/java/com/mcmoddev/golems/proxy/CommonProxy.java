package com.mcmoddev.golems.proxy;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.client.GolemRenderSettings;
import com.mcmoddev.golems.event.EGForgeEvents;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.util.GenericJsonReloadListener;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class CommonProxy {
  
  public final GenericJsonReloadListener<GolemContainer> GOLEM_CONTAINERS = new GenericJsonReloadListener<>("golems", GolemContainer.class, GolemContainer.CODEC, 
      l -> l.getEntries().forEach(e -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemContainerPacket(e.getKey(), e.getValue().get()))));

  public final GenericJsonReloadListener<GolemRenderSettings> GOLEM_RENDER_SETTINGS = new GenericJsonReloadListener<>("golems", GolemRenderSettings.class, GolemRenderSettings.CODEC, 
      l -> {});
  
  public void registerEventHandlers() {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEventHandlers");
    MinecraftForge.EVENT_BUS.register(EGForgeEvents.class);
    FMLJavaModLoadingContext.get().getModEventBus().register(EGRegistry.class);
  }
}
