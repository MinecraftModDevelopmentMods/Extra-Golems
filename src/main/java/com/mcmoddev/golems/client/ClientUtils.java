package com.mcmoddev.golems.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class ClientUtils {

	public static Optional<Level> getClientLevel() {
		return Optional.ofNullable(Minecraft.getInstance().level);
	}

	public static Optional<Player> getClientPlayer() {
		return Optional.ofNullable(Minecraft.getInstance().player);
	}

	public static Optional<RegistryAccess> getClientRegistryAccess() {
		return getClientLevel().map(Level::registryAccess);
	}
}
