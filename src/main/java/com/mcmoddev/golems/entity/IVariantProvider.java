package com.mcmoddev.golems.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public interface IVariantProvider {

	String KEY_VARIANT = "Variant";

	/**
	 * @param toSet the index of the variant to apply
	 **/
	void setVariant(final int toSet);

	/**
	 * @return the index of the current variant
	 **/
	int getVariant();

	/**
	 * @return the total number of possible variants
	 **/
	int getVariantCount();

	/**
	 * @param tag the CompoundTag to write to
	 **/
	default void saveVariant(final CompoundTag tag) {
		tag.putByte(KEY_VARIANT, (byte) getVariant());
	}

	/**
	 * @param tag the CompoundTag to read from
	 **/
	default void loadVariant(final CompoundTag tag) {
		if(tag.contains(KEY_VARIANT, Tag.TAG_BYTE)) {
			setVariant(tag.getByte(KEY_VARIANT));
		}
	}

	/**
	 * Selects a random variant to apply. Parameters are given in case the entity
	 * randomizes texture based on location.
	 *
	 * @param level the Level
	 * @param pos an approximate position for the entity
	 * @param random the random source
	 **/
	default void randomizeVariant(final Level level, final BlockPos pos, final RandomSource random) {
		// verify more than one variant exists
		if(!(getVariantCount() > 1)) {
			return;
		}
		// update variant
		final byte texture = (byte) level.getRandom().nextInt(getVariantCount());
		setVariant(texture);
	}

	/**
	 * Updates the variant to the next one, looping once the max variant is reached
	 *
	 * @return true if the variant changed
	 */
	default boolean cycleVariant() {
		// verify more than one variant exists
		if(!(getVariantCount() > 1)) {
			return false;
		}
		// update variant
		final int current = this.getVariant();
		final int incremented = (current + 1) % this.getVariantCount();
		this.setVariant((byte) incremented);
		return current != incremented;
	}
}
