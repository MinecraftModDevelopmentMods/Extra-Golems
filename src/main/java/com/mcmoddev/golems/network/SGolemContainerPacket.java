package com.mcmoddev.golems.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mojang.serialization.DataResult;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a single ResourceLocation ID
 * and the corresponding Golem Container as it was read from JSON.
 **/
public class SGolemContainerPacket {

  protected GolemContainer golemContainer;

  /**
   * @param golemContainerIn the Golem Container
   **/
  public SGolemContainerPacket(final GolemContainer golemContainerIn) {
    this.golemContainer = golemContainerIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the FriendlyByteBuf
   * @return a new instance of a SGolemContainerPacket based on the FriendlyByteBuf
   */
  public static SGolemContainerPacket fromBytes(final FriendlyByteBuf buf) {
    final CompoundTag sNBT = buf.readNbt();
    final Optional<GolemContainer> sCont = ExtraGolems.PROXY.GOLEM_CONTAINERS.readObject(sNBT).resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to read GolemContainer from NBT for packet\n" + error));
    return new SGolemContainerPacket(sCont.orElse(GolemContainer.EMPTY));
  }
  
  /**
   * Writes the raw packet data to the data stream.
   * @param msg the SGolemContainerPacket
   * @param buf the FriendlyByteBuf
   */
  public static void toBytes(final SGolemContainerPacket msg, final FriendlyByteBuf buf) {
    DataResult<Tag> nbtResult = ExtraGolems.PROXY.GOLEM_CONTAINERS.writeObject(msg.golemContainer);
    Tag tag = nbtResult.resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to write GolemContainer to NBT for packet\n" + error)).get();
    buf.writeNbt((CompoundTag)tag);
  }

  /**
   * Handles the packet when it is received.
   * @param message the SGolemContainerPacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final SGolemContainerPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        ExtraGolems.PROXY.GOLEM_CONTAINERS.put(message.golemContainer.getMaterial(), message.golemContainer);
      });
    }
    context.setPacketHandled(true);
  }
}
