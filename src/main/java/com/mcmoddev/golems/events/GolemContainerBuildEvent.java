package com.mcmoddev.golems.events;

import java.util.ArrayList;
import java.util.List;

import com.mcmoddev.golems.util.GolemContainer;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.IRegistryDelegate;

/**
 * This event is fired when a {@link GolemContainer}
 * is about to be built using settings from a {@link GolemContainer.Builder}<br>
 * This event is NOT cancelable. <br>
 * This event does NOT have a result.
 * <p>
 * It is also possible to use this event to modify existing GolemContainers
 * before they are fully built.
 *
 * @see GolemContainer
 * @see GolemContainer.Builder#build()
 **/
public class GolemContainerBuildEvent extends Event {
  private final ResourceLocation registryName;
  private final List<IRegistryDelegate<Block>> validBuildingBlocks;
  private final List<ResourceLocation> validBuildingBlockTags;
  
  private GolemContainer.Builder builder;
  
  public GolemContainerBuildEvent(final ResourceLocation name, final GolemContainer.Builder builderIn,
      final List<IRegistryDelegate<Block>> buildingBlocks, final List<ResourceLocation> buildingBlockTags) {
    registryName = name;
    builder = builderIn;
    validBuildingBlocks = buildingBlocks;
    validBuildingBlockTags = buildingBlockTags;
  }
  
  /** @return a ResourceLocation containing the modid and golemName of the container to be built **/
  public ResourceLocation getName() {
    return registryName;
  }
  
  /** @return the builder to allow changes to be made before the container is built **/
  public GolemContainer.Builder builder() {
    return builder;
  }

  /** @return the list of valid building blocks **/
  public List<IRegistryDelegate<Block>> getBlocks() {
    return validBuildingBlocks;
  }

  /** @return the list of building block tags **/
  public List<ResourceLocation> getBlockTags() {
    return validBuildingBlockTags;
  }
}
