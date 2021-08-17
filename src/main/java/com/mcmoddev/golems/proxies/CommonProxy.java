package com.mcmoddev.golems.proxies;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.util.GenericJsonReloadListener;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemRenderSettings;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class CommonProxy {
  
  public final GenericJsonReloadListener<GolemContainer> GOLEM_CONTAINERS = new GenericJsonReloadListener<>("golems", GolemContainer.class, GolemContainer.CODEC, 
      l -> l.getEntries().forEach(e -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemContainerPacket(e.getKey(), e.getValue().get()))));

  public final GenericJsonReloadListener<GolemRenderSettings> GOLEM_RENDER_SETTINGS = new GenericJsonReloadListener<>("golems", GolemRenderSettings.class, GolemRenderSettings.CODEC, 
      l -> {});
  
  public void addReloadListeners(AddReloadListenerEvent event) { }
  
  public void registerEntityLayers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) { }
  public void registerEntityRenders(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) { }

  public void registerContainerRenders() { }
  
  
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
  public GolemBase getGolem(Level world, Block below1, Block below2, Block arm1, Block arm2) {
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
