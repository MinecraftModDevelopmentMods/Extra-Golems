package com.mcmoddev.golems.util.config;

import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class, unlike most of the others in this package, is my own work.
 * This contains most of the logic that would normally be expected to go in GolemConfiguration
 * @author Glitch
 */
public final class GolemRegistrar {

	protected static HashMap<Class<? extends GolemBase>, GolemContainer> golemList = new HashMap<>();

	private GolemRegistrar() {
		//
	}

	public static void registerGolem(GolemContainer container) {
		golemList.put(container.getEntityType().getEntityClass(), container);
	}

	public static GolemContainer getContainer(Class<? extends GolemBase> clazz) {
		return golemList.get(clazz);
	}

	@Nullable
	@Deprecated
	public static GolemBase getGolem(World world, Block buildingBlock) {
		GolemContainer container = null;
		for(GolemContainer c : golemList.values()) {
			if(c.isBuildingBlock(buildingBlock)) {
				container = c;
				break;
			}
		}
		if(container == null) return null;
		else return container.entityType.create(world);
	}
	
	/**
	 * Checks all registered GolemContainers until one is
	 * found that is constructed out of the passed Blocks.
	 * Parameters are the current World and the 4 blocks that will
	 * be used to calculate this Golem (order does not matter).
	 * It is okay to pass {@code null} or Air.
	 * @return the constructed GolemBase instance if there is one
	 * for the passed blocks, otherwise null
	 * @see GolemContainer#areBuildingBlocks(Block, Block, Block, Block)
	 **/
	@Nullable
	public static GolemBase getGolem(World world, Block b1, Block b2, Block b3, Block b4) {
		GolemContainer container = null;
		for(GolemContainer c : golemList.values()) {
			if(c.areBuildingBlocks(b1, b2, b3, b4)) {
				container = c;
				break;
			}
		}
		if(container == null) return null;
		else return container.entityType.create(world);
	}

	public static Collection<GolemContainer> getContainers() {
		return golemList.values();
	}
}
