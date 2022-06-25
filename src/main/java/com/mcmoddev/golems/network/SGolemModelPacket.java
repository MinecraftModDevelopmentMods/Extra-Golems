package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a single ResourceLocation ID
 * and the corresponding Golem Render Settings as it was read from JSON.
 **/
public class SGolemModelPacket {

	protected ResourceLocation key;
	protected GolemRenderSettings golemModel;

	/**
	 * @param key          the ResourceLocation ID of the Golem Render Settings
	 * @param golemModelIn the Golem Render Settings
	 **/
	public SGolemModelPacket(final ResourceLocation key, final GolemRenderSettings golemModelIn) {
		this.key = key;
		this.golemModel = golemModelIn;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 *
	 * @param buf the FriendlyByteBuf
	 * @return a new instance of a SGolemModelPacket based on the FriendlyByteBuf
	 */
	public static SGolemModelPacket fromBytes(final FriendlyByteBuf buf) {
		final ResourceLocation sKey = buf.readResourceLocation();
		final CompoundTag sNBT = buf.readNbt();
		final Optional<GolemRenderSettings> sCont = ExtraGolems.GOLEM_RENDER_SETTINGS.readObject(sNBT).resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to read GolemRenderSettings from NBT for packet\n" + error));
		return new SGolemModelPacket(sKey, sCont.orElse(GolemRenderSettings.EMPTY));
	}

	/**
	 * Writes the raw packet data to the data stream.
	 *
	 * @param msg the SGolemModelPacket
	 * @param buf the FriendlyByteBuf
	 */
	public static void toBytes(final SGolemModelPacket msg, final FriendlyByteBuf buf) {
		DataResult<Tag> nbtResult = ExtraGolems.GOLEM_RENDER_SETTINGS.writeObject(msg.golemModel);
		Tag tag = nbtResult.resultOrPartial(error -> ExtraGolems.LOGGER.error("Failed to write GolemRenderSettings to NBT for packet\n" + error)).get();
		buf.writeResourceLocation(msg.key);
		buf.writeNbt((CompoundTag) tag);
	}

	/**
	 * Handles the packet when it is received.
	 *
	 * @param message         the SGolemModelPacket
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
