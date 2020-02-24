package com.mcmoddev.golems.entity.base;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IMultiTexturedGolem<T> {

  /**
   * Updates the current texture
   * 
   * @param toSet the index of the new texture
   **/
  void setTextureNum(final byte toSet);

  /**
   * @return the index of the current texture
   **/
  int getTextureNum();

  /**
   * @return an array representing possible textures
   **/
  T[] getTextureArray();

  /**
   * @return a Map that relates specific blocks to specific texture indices, to
   *         use when building the golem. Can be empty. Cannot be null.
   * @see com.mcmoddev.golems.util.GolemTextureBytes
   **/
  Map<Block, Byte> getTextureBytes();

  /**
   * Called when the player middle-clicks on a golem to get its "spawn egg" or
   * similar item
   * 
   * @param target the RayTraceResult
   * @return an ItemStack that best represents this golem, or an empty itemstack
   **/
  ItemStack getCreativeReturn(final RayTraceResult target);

  /**
   * @return the total number of possible textures
   **/
  default int getNumTextures() {
    return getTextureArray() != null ? getTextureArray().length : 0;
  }

  /**
   * @return the maximum value that can be passed to {@link #setTextureNum(byte)}
   **/
  default int getMaxTextureNum() {
    return getNumTextures() - 1;
  }

  /**
   * Selects a random texture to apply. Parameters are given in case the golem
   * randomizes texture based on location.
   * 
   * @param world the World
   * @param pos   an approximate position for the golem
   **/
  default void randomizeTexture(final World world, final BlockPos pos) {
    final byte texture = (byte) world.getRandom().nextInt(Math.max(1, getNumTextures()));
    setTextureNum(texture);
  }
}
