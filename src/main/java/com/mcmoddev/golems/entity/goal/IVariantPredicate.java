package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IVariantProvider;
import net.minecraft.advancements.critereon.MinMaxBounds;

// TODO make all goals implement this interface
public interface IVariantPredicate {

	/**
	 * @return the range of variant IDs required for this behavior
	 */
	MinMaxBounds.Ints getVariantBounds();

	/**
	 * @param entity the entity
	 * @return true if the variant is in the range defined by {@link #getVariantBounds()}
	 * @see #isVariantInBounds(int)
	 */
	default <T extends IVariantProvider> boolean isVariantInBounds(final T entity) {
		return this.getVariantBounds().matches(entity.getVariant());
	}

	/**
	 * @param variant the variant
	 * @return true if the variant is in the range defined by {@link #getVariantBounds()}
	 */
	default boolean isVariantInBounds(final int variant) {
		return this.getVariantBounds().matches(variant);
	}

}
