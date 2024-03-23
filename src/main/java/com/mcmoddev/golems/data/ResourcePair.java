package com.mcmoddev.golems.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Contains a ResourceLocation and a boolean that is FALSE when the ResourceLocation is a dynamic texture
 */
public final class ResourcePair {

	public static final ResourcePair EMPTY = new ResourcePair(new ResourceLocation("empty"), false);

	public static final Codec<ResourcePair> CODEC = Codec.STRING.comapFlatMap(ResourcePair::read, ResourcePair::toString).stable();

	private final ResourceLocation resource;
	private final boolean flag;

	/**
	 * Contains a ResourceLocation and a boolean.
	 *
	 * @param resource the resource location
	 * @param flag     TRUE if the resource location string begins with '#'
	 */
	public ResourcePair(ResourceLocation resource, boolean flag) {
		this.resource = resource;
		this.flag = flag;
	}

	/**
	 * @return the resource location
	 **/
	public ResourceLocation resource() {
		return resource;
	}

	/**
	 * @return TRUE if the resource location string started with '#'
	 **/
	public boolean flag() {
		return flag;
	}

	@Override
	public int hashCode() {
		return Objects.hash(resource, flag);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ResourcePair)) return false;
		ResourcePair other = (ResourcePair) o;
		return flag == other.flag && Objects.equals(resource, other.resource);
	}

	public static DataResult<ResourcePair> read(String string) {
		try {
			ResourceLocation res;
			boolean dyn;
			if (string.length() > 0 && string.charAt(0) == '#') {
				res = new ResourceLocation(string.substring(1));
				dyn = true;
			} else {
				res = new ResourceLocation(string);
				dyn = false;
			}
			return DataResult.success(new ResourcePair(res, dyn));
		} catch (ResourceLocationException resourcelocationexception) {
			return DataResult.error(() -> "Not a valid resource location: " + string + " " + resourcelocationexception.getMessage());
		}
	}

	@Override
	public String toString() {
		return flag() ? "#".concat(resource().toString()) : resource().toString();
	}
}
