package com.mcmoddev.golems.util;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.GolemContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides safe and re-usable attribute maps for golems with different materials.
 */
public final class GolemAttributeManager {

	/** Caches attribute suppliers based on material **/
	private static final Map<ResourceLocation, AttributeSupplier> materialToAttributeMap = new HashMap<>();

	/**
	 * @param access the registry access
	 * @param id the material
	 * @return a new AttributeMap
	 */
	public static AttributeMap getAttributes(final RegistryAccess access, final ResourceLocation id) {
		if (!materialToAttributeMap.containsKey(id)) {
			final GolemContainer container = GolemContainer.getOrCreate(access, id);
			AttributeSupplier supplier = buildAttributes(container.getAttributeSupplier().get());
			materialToAttributeMap.put(id, supplier);
		}
		return new AttributeMap(materialToAttributeMap.get(id));
	}

	/**
	 * Creates an attribute supplier using the registered attributes and material attributes
	 *
	 * @param materialAttributes the attributes defined in the GolemContainer
	 * @return the AttributeSupplier with all registered attributes
	 */
	private static AttributeSupplier buildAttributes(final AttributeSupplier.Builder materialAttributes) {
		// Merges the registered attributes with the material attributes
		// Required to allow attribute modification event to modify golem attributes (#109)
		final AttributeSupplier master = DefaultAttributes.getSupplier(EGRegistry.EntityReg.GOLEM.get());
		AttributeSupplier.Builder builder = new AttributeSupplier.Builder(master);
		builder.combine(materialAttributes);
		return builder.build();
	}

	/**
	 * Resets the attribute supplier cache (unused)
	 */
	public static void clear() {
		materialToAttributeMap.clear();
	}
}
