package com.mcmoddev.golems.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;

/**
 * Contains a ResourceLocation and a boolean that is FALSE when the ResourceLocation is a dynamic texture
 */
public final class ResourcePair {
  
  public static final Codec<ResourcePair> CODEC = Codec.STRING.comapFlatMap(ResourcePair::read, ResourcePair::toString).stable();

  private final ResourceLocation resource;
  private final boolean flag;
  
  /**
   * Contains a ResourceLocation and a boolean.
   * @param resource the resource location
   * @param flag TRUE if the resource location string begins with '#'
   */
  public ResourcePair(ResourceLocation resource, boolean flag) {
    this.resource = resource;
    this.flag = flag;
  }
  
  /** @return the resource location **/
  public ResourceLocation resource() { return resource; }
  
  /** @return TRUE if the resource location string started with '#' **/
  public boolean flag() { return flag; }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (flag ? 1231 : 1237);
    result = prime * result + ((resource == null) ? 0 : resource.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ResourcePair other = (ResourcePair) obj;
    if (flag != other.flag)
      return false;
    if (resource == null) {
      if (other.resource != null)
        return false;
    } else if (!resource.equals(other.resource))
      return false;
    return true;
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
      return DataResult.error("Not a valid resource location: " + string + " " + resourcelocationexception.getMessage());
    }
  }

  @Override
  public String toString() {
    return flag() ? "#".concat(resource().toString()) : resource().toString();
  }
}
