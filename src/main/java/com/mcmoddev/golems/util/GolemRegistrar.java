package com.mcmoddev.golems.util;

import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * This class, unlike most of the others in this package, is my own work. This
 * contains most of the logic that would normally be expected to go in
 * GolemConfiguration
 *
 * @author Glitch
 */
public final class GolemRegistrar {

  public static HashMap<ResourceLocation, GolemContainer> golemList = new HashMap<>();

  private GolemRegistrar() {
    //
  }

  /**
   * Register a fully built GolemContainer to the mod.
   * 
   * @param container a unique GolemContainer with a unique ID
   * @see GolemContainer#getRegistryName()
   **/
  public static void registerGolem(final GolemContainer container) {
    if (golemList.containsKey(container.getRegistryName())) {
      return;
    }
    golemList.put(container.getRegistryName(), container);
  }

  /**
   * @param entityType the EntityType of this golem
   * @return the GolemContainer registered under this EntityType, or null if none
   *         is found
   * @see #getContainer(ResourceLocation)
   **/
  @Nullable
  public static GolemContainer getContainer(final EntityType<?> entityType) {
    return entityType != null ? getContainer(entityType.getRegistryName()) : null;
  }

  /**
   * @param name a ResourceLocation key, usually the result of
   *             {@code EntityType#getRegistryName()}
   * @return the GolemContainer registered under this name, or null if none is
   *         found
   **/
  @Nullable
  public static GolemContainer getContainer(final ResourceLocation name) {
    return golemList.get(name);
  }
  
  /**
   * @param name a ResourceLocation key
   * @return if the GolemContainer has been registered under this name
   **/
  public static boolean hasContainer(final ResourceLocation name) {
    return golemList.containsKey(name);
  }

  /**
   * Checks all registered GolemContainers until one is found that is constructed
   * out of the passed Blocks. Parameters are the current World and the 4 blocks
   * that will be used to calculate this Golem. It is okay to pass {@code null} or
   * Air.
   *
   * @return the constructed GolemBase instance if there is one for the passed
   *         blocks, otherwise null
   * @see GolemContainer#areBuildingBlocks(Block, Block, Block, Block)
   **/
  @Nullable
  public static GolemBase getGolem(Level world, Block below1, Block below2, Block arm1, Block arm2) {
    GolemContainer container = null;
    for (GolemContainer c : golemList.values()) {
      if (c.areBuildingBlocks(below1, below2, arm1, arm2)) {
        container = c;
        break;
      }
    }
    if (container == null) {
      return null;
    }
    return container.getEntityType().create(world);
  }

  public static Collection<GolemContainer> getContainers() {
    return golemList.values();
  }
}
