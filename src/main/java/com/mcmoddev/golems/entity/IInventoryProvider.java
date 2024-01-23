package com.mcmoddev.golems.entity;

import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface IInventoryProvider extends InventoryCarrier {

	/** @param player the player who has opened a menu **/
	void setPlayerInMenu(@Nullable final Player player);

	/** @return the player who has opened a menu, if any **/
	@Nullable Player getPlayerInMenu();

	/**
	 * @param distance the maximum distance from the player to this entity
	 * @return true if the player with an open menu exists and is within the given distance
	 **/
	boolean isPlayerInRangeForMenu(final double distance);

	/** @return true if the inventory changed since the last tick **/
	boolean isInventoryChanged();

	/** Sets the inventory changed flag to false **/
	void resetInventoryChanged();
}
