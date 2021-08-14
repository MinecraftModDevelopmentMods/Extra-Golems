package com.mcmoddev.golems.entity.base;

import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

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
   * @return an array with loot tables corresponding to each texture
   **/
  ResourceLocation[] getLootTableArray();
  
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
  ItemStack getCreativeReturn(final HitResult target);

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
  
  default ResourceLocation getCurrentLootTable() {
    final ResourceLocation[] array = getLootTableArray();
    return array[getTextureNum() % array.length];
  }

  /**
   * Selects a random texture to apply. Parameters are given in case the golem
   * randomizes texture based on location.
   * 
   * @param world the World
   * @param pos   an approximate position for the golem
   **/
  default void randomizeTexture(final Level world, final BlockPos pos) {
    final byte texture = (byte) world.getRandom().nextInt(Math.max(1, getNumTextures()));
    setTextureNum(texture);
  }
  
  default InteractionResult handlePlayerInteract(final Player player, final InteractionHand hand) {
    // change texture when player clicks
    final int incremented = (this.getTextureNum() + 1) % this.getNumTextures();
    this.setTextureNum((byte) incremented);
    player.swing(hand);
    return InteractionResult.SUCCESS;
  }
}
