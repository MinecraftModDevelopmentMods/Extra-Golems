package com.mcmoddev.golems.util;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Map;

/**
 * Provides methods to update and manage attribute values for different golems
 */
public final class EGAttributeUtils {

	/**
	 * Modifies the attributes in the {@link AttributeMap} to use the base values in the given map
	 * @param attributeMap the attribute map instance to modify
	 * @param baseValues a map of attributes and base values to assign
	 **/
	public static void setBaseValues(final AttributeMap attributeMap, final Map<Attribute, Double> baseValues) {
		// iterate map entries
		for(Map.Entry<Attribute, Double> entry : baseValues.entrySet()) {
			// verify attribute exists
			if(!attributeMap.hasAttribute(entry.getKey())) {
				continue;
			}
			// set base value of the attribute
			attributeMap.getInstance(entry.getKey()).setBaseValue(entry.getValue());
		}
	}

	/**
	 * Adds a permanent modifier to an {@link AttributeInstance} after verifying the modifier is not already present
	 * @param instance the attribute instance to apply the modifier
	 * @param modifier the attribute modifier to apply
	 */
	public static void safeAddModifier(final AttributeInstance instance, final AttributeModifier modifier) {
		if(instance.hasModifier(modifier)) {
			return;
		}
		instance.addPermanentModifier(modifier);
	}

	/**
	 * Adds a modifier from an {@link AttributeInstance} after verifying the modifier is present
	 * @param instance the attribute instance to apply the modifier
	 * @param modifier the attribute modifier to apply
	 */
	public static void safeRemoveModifier(final AttributeInstance instance, final AttributeModifier modifier) {
		if(!instance.hasModifier(modifier)) {
			return;
		}
		instance.removeModifier(modifier);
	}

}
