/**
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.client.ClientUtils;
import com.mcmoddev.golems.data.GolemContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientBoundGolemContainerPacket(byte action) implements CustomPacketPayload {

	public static final byte RESET = (byte) 1;
	public static final byte POPULATE = (byte) 2;
	public static final byte RESET_AND_POPULATE = RESET | POPULATE;

	public static final CustomPacketPayload.Type<ClientBoundGolemContainerPacket> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "golem_container"));

	public static final StreamCodec<ByteBuf, ClientBoundGolemContainerPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BYTE,
		ClientBoundGolemContainerPacket::action,
		ClientBoundGolemContainerPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	/**
	 * Handles the packet when it is received.
	 */
	public static void handlePacket(final ClientBoundGolemContainerPacket message, final IPayloadContext context) {
		context.enqueueWork(() -> {
			// reset golem containers
			if((message.action & RESET) > 0) {
				GolemContainer.reset();
			}
			// populate golem containers
			if((message.action & POPULATE) > 0) {
				ClientUtils.getClientRegistryAccess().ifPresent(GolemContainer::populate);
			}
		});
	}
}
