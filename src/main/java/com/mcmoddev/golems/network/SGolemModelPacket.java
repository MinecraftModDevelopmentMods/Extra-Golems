package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a map of
 * ResourceLocation IDs and Golem Containers
 **/
@Deprecated
public class SGolemModelPacket {

	protected static final Codec<Map<ResourceLocation, GolemRenderSettings>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, GolemRenderSettings.CODEC);

	protected Map<ResourceLocation, GolemRenderSettings> data;

	/**
	 * @param data the data map
	 **/
	public SGolemModelPacket(final Map<ResourceLocation, GolemRenderSettings> data) {
		this.data = data;
		if (FMLEnvironment.dist != Dist.CLIENT) {
			// update server-side map
			ExtraGolems.GOLEM_MODEL_MAP.clear();
			ExtraGolems.GOLEM_MODEL_MAP.putAll(data);
		}
	}

	/**
	 * Reads the raw packet data from the data stream.
	 *
	 * @param buf the PacketBuffer
	 * @return a new instance of a SGolemModelPacket based on the PacketBuffer
	 */
	public static SGolemModelPacket fromBytes(final FriendlyByteBuf buf) {
		final Map<ResourceLocation, GolemRenderSettings> data = buf.readWithCodec(CODEC);
		return new SGolemModelPacket(data);
	}

	/**
	 * Writes the raw packet data to the data stream.
	 *
	 * @param msg the SGolemModelPacket
	 * @param buf the PacketBuffer
	 */
	public static void toBytes(final SGolemModelPacket msg, final FriendlyByteBuf buf) {
		buf.writeWithCodec(CODEC, msg.data);
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
				// finalize each entry before adding it to the client-side map
				message.data.values().forEach(GolemRenderSettings::load);
				ExtraGolems.GOLEM_MODEL_MAP.clear();
				ExtraGolems.GOLEM_MODEL_MAP.putAll(message.data);
			});
		}
		context.setPacketHandled(true);
	}
}
