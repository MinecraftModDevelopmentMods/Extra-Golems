package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemRegistrar;

import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class DeferredContainer {
  protected GolemContainer container;
  protected ResourceLocation[] blocks;
  
  public DeferredContainer(final GolemContainer lContainer, final String modid, final String[] lBlocks) {
    this(lContainer, new ResourceLocation[lBlocks.length]);
    // populate resource locations
    for(int i = 0, l = lBlocks.length; i < l; i++) {
      this.blocks[i] = new ResourceLocation(modid, lBlocks[i]);
    }
  }
  
  public DeferredContainer(final GolemContainer lContainer, final ResourceLocation[] lBlocks) {
    this.container = lContainer;
    this.blocks = lBlocks;
  }
  
  public GolemContainer getContainer() {
    return container;
  }

  public ResourceLocation[] getBlocks() {
    return blocks;
  }
  
  
  /**
   * Finalizes the blocks to be added to the golem container
   **/
  public void addBlocks() {
    GolemContainer cont = GolemRegistrar.getContainer(container.getRegistryName());
    if(null == cont) return;
    // add each block from the list of given names
    for(final ResourceLocation r : blocks) {
      // see if the block exists
      final Block block = ForgeRegistries.BLOCKS.getValue(r);
      // add that block as a building block for the golem
      if(block != null) {
        cont.addBlocks(block);
      }
    }
  }
}
