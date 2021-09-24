package com.mcmoddev.golems.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
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
 * and the corresponding Golem Render Settings as it was read from JSON.
 **/
public class SGolemModelPacket {

  protected ResourceLocation key;
  protected GolemRenderSettings golemModel;

  /**
   * @param key the ResourceLocation ID of the Golem Render Settings
   * @param golemModelIn the Golem Render Settings
   **/
  public SGolemModelPacket(final ResourceLocation key, final GolemRenderSettings golemModelIn) {
    this.key = key;
    this.golemModel = golemModelIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a SGolemModelPacket based on the PacketBuffer
   */
  public static SGolemModelPacket fromBytes(final PacketBuffer buf) {
    final ResourceLocation sKey = buf.readResourceLocation();
    final CompoundNBT sNBT = buf.readCompoundTag();
    final Optional<GolemRenderSettings> sCont = ExtraGolems.GOLEM_RENDER_SETTINGS.readObject(sNBT).resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to read GolemRenderSettings from NBT for packet\n" + error));
    return new SGolemModelPacket(sKey, sCont.orElse(GolemRenderSettings.EMPTY));
  }
  
  /**
   * Writes the raw packet data to the data stream.
   * @param msg the SGolemModelPacket
   * @param buf the PacketBuffer
   */
  public static void toBytes(final SGolemModelPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = ExtraGolems.GOLEM_RENDER_SETTINGS.writeObject(msg.golemModel);
    INBT tag = nbtResult.resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to write GolemRenderSettings to NBT for packet\n" + error)).get();
    buf.writeResourceLocation(msg.key);
    buf.writeCompoundTag((CompoundNBT)tag);
  }

  /**
   * Handles the packet when it is received.
   * @param message the SGolemModelPacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final SGolemModelPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        ExtraGolems.GOLEM_RENDER_SETTINGS.put(message.key, message.golemModel);
		message.golemModel.load();
      });
    }
    context.setPacketHandled(true);
  }
}
