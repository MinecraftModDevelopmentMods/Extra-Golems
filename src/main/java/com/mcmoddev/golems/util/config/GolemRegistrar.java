package com.mcmoddev.golems.util.config;

//TODO: Make this class usable by other mods - it's private right now because it may explode

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

	protected static HashMap<Class<? extends GolemBase>, GolemContainer> golemList;

	private GolemRegistrar() {
		//
	}

	public static void registerGolem(Class<? extends GolemBase> clazz, GolemContainer container) {
		golemList.put(clazz, container);
	}

	public static GolemContainer getContainer(Class<? extends GolemBase> clazz) {
		return golemList.get(clazz);
	}

	@Nullable
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

	public static Collection<GolemContainer> getContainers() {
		return golemList.values();
	}
}
