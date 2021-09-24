package com.mcmoddev.golems.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mojang.serialization.DataResult;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a single ResourceLocation ID
 * and the corresponding Golem Container as it was read from JSON.
 **/
public class SGolemContainerPacket {

  protected ResourceLocation key;
  protected GolemContainer golemContainer;

  /**
   * @param key the ResourceLocation ID of the Golem Container
   * @param golemContainerIn the Golem Container
   **/
  public SGolemContainerPacket(final ResourceLocation key, final GolemContainer golemContainerIn) {
    this.key = key;
    this.golemContainer = golemContainerIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a SGolemContainerPacket based on the PacketBuffer
   */
  public static SGolemContainerPacket fromBytes(final PacketBuffer buf) {
    final ResourceLocation sKey = buf.readResourceLocation();
    final CompoundNBT sNBT = buf.readCompoundTag();
    final Optional<GolemContainer> sCont = ExtraGolems.GOLEM_CONTAINERS.readObject(sNBT).resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to read GolemContainer from NBT for packet\n" + error));
    return new SGolemContainerPacket(sKey, sCont.orElse(GolemContainer.EMPTY));
  }
  
  /**
   * Writes the raw packet data to the data stream.
   * @param msg the SGolemContainerPacket
   * @param buf the PacketBuffer
   */
  public static void toBytes(final SGolemContainerPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = ExtraGolems.GOLEM_CONTAINERS.writeObject(msg.golemContainer);
    INBT tag = nbtResult.resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to write GolemContainer to NBT for packet\n" + error)).get();
    buf.writeResourceLocation(msg.key);
    buf.writeCompoundTag((CompoundNBT)tag);
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
        ExtraGolems.GOLEM_CONTAINERS.put(message.key, message.golemContainer);
      });
    }
    context.setPacketHandled(true);
  }
}
