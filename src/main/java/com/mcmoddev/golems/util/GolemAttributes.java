package com.mcmoddev.golems.util;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides safe and re-usable attribute maps for golems with different materials.
 */
public final class GolemAttributes {

	/** Caches attribute suppliers based on material **/
	private static final Map<ResourceLocation, AttributeSupplier> materialToAttributeMap = new HashMap<>();

	/**
	 * @param access the registry access
	 * @param material the material
	 * @return a new AttributeMap
	 */
	public static AttributeMap getAttributes(final RegistryAccess access, final ResourceLocation material) {
		if (!materialToAttributeMap.containsKey(material)) {
			final Registry<GolemContainer> registry = access.registry(ExtraGolems.Keys.GOLEM_CONTAINERS).orElseThrow();
			GolemContainer container = registry.getOptional(material).orElse(GolemContainer.EMPTY);
			AttributeSupplier supplier = buildAttributes(container.getAttributeSupplier().get());
			materialToAttributeMap.put(material, supplier);
		}
		return new AttributeMap(materialToAttributeMap.get(material));
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
		final AttributeSupplier master = DefaultAttributes.getSupplier(EGRegistry.GOLEM.get());
		AttributeSupplier.Builder builder = new AttributeSupplier.Builder(master);
		builder.combine(materialAttributes);
		return builder.build();
	}

	/**
	 * Used to reset the attribute cache when datapacks are (re)loaded
	 */
	public static void clear() {
		materialToAttributeMap.clear();
	}
}
