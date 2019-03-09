package com.mcmoddev.golems.util.config;

import com.google.common.collect.Lists;
import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemContainer {

	public final EntityType<GolemBase> entityType;
	private Consumer<GolemConfigurationSection> configConsumer;
	private List<Block> validBuildingBlocks;
	public String name;
	public double health;
	public double attack;
	public boolean canUseSpecial;
	public boolean enabled = true;

	private GolemContainer(final EntityType<GolemBase> lEntityType, final String lPath, 
			final List<Block> lValidBuildingBlocks, final double lHealth, 
			final double lAttack, final boolean lCanUseSpecial) {
		this.entityType = lEntityType;
		this.validBuildingBlocks = lValidBuildingBlocks;
		this.name = lPath;
		this.health = lHealth;
		this.attack = lAttack;
		this.canUseSpecial = lCanUseSpecial;
	}

	public boolean hasBuildingBlock() {
		return !this.validBuildingBlocks.isEmpty();
	}
	
	public Block[] getBuildingBlocks() {
		return this.validBuildingBlocks.toArray(new Block[this.validBuildingBlocks.size()]);
	}
	
	public boolean isBuildingBlock(final Block b) {
		// TODO:  check Block Tags either here or when the blocks are added in Builder
		return this.validBuildingBlocks.contains(b);
	}
	
	public GolemConfigurationSection applyConfig(GolemConfigurationSection config) {
		this.configConsumer.accept(config);
		return config;
	}

	/**
	 * This class is my own work
	 * @author Glitch
	 */
	public static final class Builder {
		private final String modId;
		private final String golemName;
		private final EntityType.Builder<GolemBase> entityTypeBuilder;
		private double health = 100.0D;
		private double attack = 14.0D; //Average iron golem attack in Normal mode
		private boolean allowSpecial = true;
		private boolean enabled = true;
		//This is a list to allow determining the "priority" of golem blocks. This could be used to our
		//advantage in golem building logic for conflicts in the future.
		private List<Block> validBuildingBlocks = new ArrayList<>();

		/**
		 * Creates the builder
		 * @param golemName the name of the golem
		 * @param entityClazz the class of the golem (e.g. EntityFooGolem.class)
		 * @param entityFunction the constructor function of the class (e.g. EntityFooGolem::new)
		 */
		public Builder(final String modid, final String golemName, final Class<? extends GolemBase> entityClazz,
				final Function<? super World, ? extends GolemBase> entityFunction) {
			this(modid, golemName, EntityType.Builder.<GolemBase>create(entityClazz, entityFunction).tracker(48, 3, true));
		}

		/**
		 * Creates the builder.
		 * @param golemName the name of the golem
		 * @param builder the builder of the golem, with any needed options already set
		 */
		public Builder(final String modid, final String golemName, final EntityType.Builder<GolemBase> builder) {
			this.modId = modid;
			this.entityTypeBuilder = builder;
			this.golemName = golemName;
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
		 * @param additionalBlocks blocks
		 * @return instance to allow chaining of methods
		 */
		public Builder addValidBlocks(final Block... additionalBlocks) {
			if(additionalBlocks != null && additionalBlocks.length > 0) {
				this.validBuildingBlocks.addAll(Arrays.asList(additionalBlocks));
			}
			return this;
		}

		/**
		 * Builds the container
		 * @return a copy of the newly constructed GolemContainer
		 */
		public GolemContainer build() {
			final EntityType<GolemBase> entityType = entityTypeBuilder.build(golemName);
			entityType.setRegistryName(modId, golemName);
			return new GolemContainer(entityType, golemName, validBuildingBlocks, health, attack, allowSpecial);
		}
	}
}
