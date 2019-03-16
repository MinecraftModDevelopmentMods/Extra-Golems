package com.mcmoddev.golems.util.config;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemContainer {

	private final List<Block> validBuildingBlocks;
	//Avoid making setters/getters for non-final fields for now
	public final EntityType<GolemBase> entityType;
	public String name;
	public double health;
	public double attack;
	public boolean enabled = true;

	public Map<String, GolemSpecialContainer> specialContainers;

	private GolemContainer(final EntityType<GolemBase> lEntityType, final String lPath, 
			final List<Block> lValidBuildingBlocks, final double lHealth, 
			final double lAttack, final HashMap<String, GolemSpecialContainer> lSpecialContainers) {
		this.entityType = lEntityType;
		this.validBuildingBlocks = lValidBuildingBlocks;
		this.name = lPath;
		this.health = lHealth;
		this.attack = lAttack;
		this.specialContainers = lSpecialContainers;
	}


	public boolean hasBuildingBlock() {
		return !this.validBuildingBlocks.isEmpty();
	}

	public Block[] getBuildingBlocks() {
		return this.validBuildingBlocks.toArray(new Block[0]);
	}
	
	public boolean isBuildingBlock(final Block b) {
		// TODO:  check Block Tags either here or when the blocks are added in Builder
		return this.validBuildingBlocks.contains(b);
	}
	public Block getPrimaryBuildingBlock() {
		return this.validBuildingBlocks.get(0);
	}

	/**
	 * This class is my own work
	 * @author Glitch
	 */
	public static final class Builder {
		private final String modid;
		private final String golemName;
		private final EntityType.Builder<GolemBase> entityTypeBuilder;
		private double health = 100.0D;
		private double attack = 14.0D; //Average iron golem attack in Normal mode
		//This is a list to allow determining the "priority" of golem blocks. This could be used to our
		//advantage in golem building logic for conflicts in the future.
		private List<Block> validBuildingBlocks = new ArrayList<>();
		private List<GolemSpecialContainer> containers = new ArrayList<>();

		/**
		 * Creates the builder
		 * @param golemName the name of the golem
		 * @param entityClazz the class of the golem (e.g. EntityFooGolem.class)
		 * @param entityFunction the constructor function of the class (e.g. EntityFooGolem::new)
		 */
		public Builder(final String modid, final String golemName, final Class<? extends GolemBase> entityClazz,
				final Function<? super World, ? extends GolemBase> entityFunction) {
			this.modid = modid;
			this.golemName = golemName;
			this.entityTypeBuilder = EntityType.Builder.<GolemBase>create(entityClazz, entityFunction)
				.tracker(48, 3, true);
		}

		/**
		 * Sets the max health of a golem
		 * @param lHealth The max health (in half hearts) of the golem. <b>Defaults to 100</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setHealth(final double lHealth) {
			health = lHealth;
			return this;
		}

		/**
		 * Sets the attack strength of a golem
		 * @param lAttack The attack strength (in half hearts) of the golem. <b>Defaults to 14</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setAttack(final double lAttack) {
			attack = lAttack;
			return this;
		}

		/**
		 * Adds building blocks that may be used for creating the golem
		 * @param additionalBlocks blocks that may be used for building
		 * @return instance to allow chaining of methods
		 */
		public Builder addValidBlocks(final Block... additionalBlocks) {
			if(additionalBlocks != null && additionalBlocks.length > 0) {
				this.validBuildingBlocks.addAll(Arrays.asList(additionalBlocks));
			}
			return this;
		}

		/**
		 * Adds any GolemSpecialContainers to be used by the golem
		 * @param specialContainers specials to be added
		 * @return instance to allow chaining of methods
		 */
		public Builder addSpecials(final GolemSpecialContainer... specialContainers) {
			containers.addAll(Arrays.asList(specialContainers));
			return this;
		}
		/**
		 * Builds the container
		 * @return a copy of the newly constructed GolemContainer
		 */
		public GolemContainer build() {
			EntityType<GolemBase> entityType = entityTypeBuilder.build(golemName);
			entityType.setRegistryName(modid, golemName);
			HashMap<String, GolemSpecialContainer> containerMap = new HashMap<>();
			for(GolemSpecialContainer c : containers) {
				containerMap.put(c.name, c);
			}
			return new GolemContainer(entityType, golemName, validBuildingBlocks, health, attack, containerMap);
		}
	}

}
