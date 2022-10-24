package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.util.GolemAttributes;
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
public class SGolemContainerPacket {

	protected static final Codec<Map<ResourceLocation, GolemContainer>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, GolemContainer.CODEC);

	protected Map<ResourceLocation, GolemContainer> data;

	/**
	 * @param data the data map
	 **/
	public SGolemContainerPacket(final Map<ResourceLocation, GolemContainer> data) {
		this.data = data;
		if (FMLEnvironment.dist != Dist.CLIENT) {
			// update server-side map
			ExtraGolems.GOLEM_CONTAINER_MAP.clear();
			ExtraGolems.GOLEM_CONTAINER_MAP.putAll(data);
			// update server-side attributes
			GolemAttributes.clear();
		}
	}

	/**
	 * Reads the raw packet data from the data stream.
	 *
	 * @param buf the PacketBuffer
	 * @return a new instance of a SGolemContainerPacket based on the PacketBuffer
	 */
	public static SGolemContainerPacket fromBytes(final FriendlyByteBuf buf) {
		final Map<ResourceLocation, GolemContainer> data = buf.readWithCodec(CODEC);
		return new SGolemContainerPacket(data);
	}

	/**
	 * Writes the raw packet data to the data stream.
	 *
	 * @param msg the SGolemContainerPacket
	 * @param buf the PacketBuffer
	 */
	public static void toBytes(final SGolemContainerPacket msg, final FriendlyByteBuf buf) {
		buf.writeWithCodec(CODEC, msg.data);
	}

	/**
	 * Handles the packet when it is received.
	 *
	 * @param message         the SGolemContainerPacket
	 * @param contextSupplier the NetworkEvent.Context supplier
	 */
	public static void handlePacket(final SGolemContainerPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				// update client-side map
				ExtraGolems.GOLEM_CONTAINER_MAP.clear();
				ExtraGolems.GOLEM_CONTAINER_MAP.putAll(message.data);
				// update client-side attributes (probably redundant)
				GolemAttributes.clear();
			});
		}
		context.setPacketHandled(true);
	}
}
