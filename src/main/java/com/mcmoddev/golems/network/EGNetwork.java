package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class EGNetwork {

	private static final String PROTOCOL_VERSION = "5";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(ExtraGolems.MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void register() {
		int messageId = 0;
		CHANNEL.registerMessage(messageId++, ServerBoundSpawnGolemPacket.class, ServerBoundSpawnGolemPacket::toBytes, ServerBoundSpawnGolemPacket::fromBytes, ServerBoundSpawnGolemPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(messageId++, ClientBoundGolemContainerPacket.class, ClientBoundGolemContainerPacket::toBytes, ClientBoundGolemContainerPacket::fromBytes, ClientBoundGolemContainerPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
}
