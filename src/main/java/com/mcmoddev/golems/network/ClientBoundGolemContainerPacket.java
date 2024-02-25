/**
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package com.mcmoddev.golems.network;

import com.mcmoddev.golems.client.ClientUtils;
import com.mcmoddev.golems.data.GolemContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientBoundGolemContainerPacket {

	public static final byte RESET = (byte) 1;
	public static final byte POPULATE = (byte) 2;
	public static final byte RESET_AND_POPULATE = RESET | POPULATE;

	private final byte action;

	public ClientBoundGolemContainerPacket(byte action) {
		this.action = action;
	}


	/**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of the packet based on the PacketBuffer
     */
    public static ClientBoundGolemContainerPacket fromBytes(final FriendlyByteBuf buf) {
		final byte action = buf.readByte();
		return new ClientBoundGolemContainerPacket(action);
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the packet
     * @param buf the PacketBuffer
     */
    public static void toBytes(final ClientBoundGolemContainerPacket msg, final FriendlyByteBuf buf) {
		buf.writeByte(msg.action);
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message the packet
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final ClientBoundGolemContainerPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
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
        context.setPacketHandled(true);
    }
}
