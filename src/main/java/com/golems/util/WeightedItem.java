package com.golems.util;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * This class will produce a randomly sized ItemStack given an Item, metadata, and size bounds in
 * the constructor. It is also weighted and can do percent-chance rolls, for convenience in golem
 * item drop logic. This will likely be removed if Extra Golems starts using loot tables.
 **/
public class WeightedItem {

	public final Item item;
	public final int meta;
	public final int maxAmount;
	public final int minAmount;
	public final int dropChance;

	public WeightedItem(final Item itemIn, final int metadata, final int min, final int max, final int percentChance) {
		this.item = itemIn;
		this.meta = metadata;
		this.minAmount = min;
		this.maxAmount = max;
		this.dropChance = percentChance > 100 ? 100 : percentChance;
	}

	public WeightedItem(final ItemStack stack, final int percentChance) {
		this(stack.getItem(), stack.getMetadata(), stack.getCount(), stack.getCount(),
				percentChance);
	}

	/**
	 * Calculated randomly each time this method is called.
	 **/
	public boolean shouldDrop(final Random rand) {
		return this.item != null && rand.nextInt(100) < this.dropChance;
	}

	/**
	 * Gets a random number between minAmount and maxAmount, inclusive.
	 **/
	public int getRandomSize(final Random rand) {
		return this.maxAmount > this.minAmount
				? this.minAmount + rand.nextInt(this.maxAmount - this.minAmount + 1)
				: this.minAmount;
	}

	/**
	 * Makes an ItemStack of this WeightedItem using {@link #getRandomSize(Random)}.
	 **/
	public ItemStack makeStack(final Random rand) {
		final int size = getRandomSize(rand);
		return new ItemStack(this.item, size, this.meta);
	}
}
