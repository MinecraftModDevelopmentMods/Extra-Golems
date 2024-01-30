package com.mcmoddev.golems.client.menu.guide_book.book;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public interface ITableOfContentsEntry {

	/** @return a list of {@link ItemStack}s to display **/
	List<ItemStack> getItems();

	/**
	 * @param index the index
	 * @return the item stack to display on the button, or empty if the list is empty.
	 */
	default ItemStack getItem(final int index) {
		if(this.getItems().isEmpty()) {
			return ItemStack.EMPTY;
		}
		return this.getItems().get(index % this.getItems().size());
	}

	/**
	 * @param index the index
	 * @return the message to display on the button
	 */
	Component getMessage(final int index);
}
