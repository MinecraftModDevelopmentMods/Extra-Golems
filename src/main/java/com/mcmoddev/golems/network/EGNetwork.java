package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class EGNetwork {

	private static final String PROTOCOL_VERSION = "5";
	
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(ExtraGolems.MODID).versioned(PROTOCOL_VERSION);
		registrar.playToServer(ServerBoundSpawnGolemPacket.TYPE, ServerBoundSpawnGolemPacket.STREAM_CODEC, ServerBoundSpawnGolemPacket::handlePacket);
		registrar.playToClient(ClientBoundGolemContainerPacket.TYPE, ClientBoundGolemContainerPacket.STREAM_CODEC, ClientBoundGolemContainerPacket::handlePacket);
	}
}
