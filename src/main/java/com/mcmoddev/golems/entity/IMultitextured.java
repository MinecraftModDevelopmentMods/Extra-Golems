package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public interface IMultitextured {
  
  static final String KEY_TEXTURE = "Texture";

  /** @param toSet the index of the new texture **/
  void setTextureId(final byte toSet);

  /** @return the index of the current texture **/
  int getTextureId();

  /** @return the total number of possible textures **/
  int getTextureCount();
  
  /** @param tag the CompoundNBT to write to **/
  default void saveTextureId(final CompoundNBT tag) {
    tag.putByte(KEY_TEXTURE, (byte) getTextureId());
  }
  
  /** @param tag the CompoundNBT to read from **/
  default void loadTextureId(final CompoundNBT tag) {
    setTextureId(tag.getByte(KEY_TEXTURE));
  }

  /**
   * Selects a random texture to apply. Parameters are given in case the entity
   * randomizes texture based on location.
   * 
   * @param world the World
   * @param pos   an approximate position for the entity
   **/
  default void randomizeTexture(final World world, final BlockPos pos) {
    final byte texture = (byte) world.getRandom().nextInt(Math.max(1, getTextureCount()));
    setTextureId(texture);
  }
  
  /**
   * Updates the texture to the next one in the array
   * @return true if the texture changed
   */
  default boolean cycleTexture() {
    final int current = this.getTextureId();
    final int incremented = (current + 1) % this.getTextureCount();
    this.setTextureId((byte) incremented);
    return current != incremented;
  }
}
