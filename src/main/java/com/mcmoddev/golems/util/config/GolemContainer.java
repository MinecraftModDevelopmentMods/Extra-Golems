package com.mcmoddev.golems.util.config;

import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemContainer {

	public EntityType entityType;
	public List<Block> validBuildingBlocks;
	public String name;
	public double health;
	public double attack;
	public boolean canUseSpecial;
	public boolean enabled = true;

	private GolemContainer(EntityType lEntityType,
	String lPath, List<Block> lValidBuildingBlocks, double lHealth, double lAttack, boolean lCanUseSpecial) {
		this.entityType = lEntityType;
		this.validBuildingBlocks = lValidBuildingBlocks;
		this.name = lPath;
		this.health = lHealth;
		this.attack = lAttack;
		this.canUseSpecial = lCanUseSpecial;
	}


	/**
	 * This class is my own work
	 * @author Glitch
	 */
	@SuppressWarnings("unused")
	public static final class Builder {
		private final String golemName;
		private final EntityType.Builder entityTypeBuilder;
		private double health = 100.0D;
		private double attack = 14.0D; //Average iron golem attack in Normal mode
		private boolean allowSpecial = true;
		//This is a list to allow determining the "priority" of golem blocks. This could be used to our
		//advantage in golem building logic for conflicts in the future.
		private List<Block> validBuildingBlocks = new ArrayList<>();

		/**
		 * Creates the builder
		 * @param golemName the name of the golem
		 * @param entityClazz the class of the golem (e.g. EntityFooGolem.class)
		 * @param entityFunction the constructor function of the class (e.g. EntityFooGolem::new)
		 */
		public Builder(String golemName, Block primaryBlock, Class<? extends GolemBase> entityClazz,
			       Function<? super World, ? extends GolemBase> entityFunction) {
			this(golemName, primaryBlock, EntityType.Builder.create(entityClazz, entityFunction));
		}

		/**
		 * Creates the builder.
		 * @param golemName the name of the golem
		 * @param builder the builder of the golem, with any needed options already set
		 */
		public Builder(String golemName, Block primaryBlock, EntityType.Builder builder) {
			this.entityTypeBuilder = builder;
			this.golemName = golemName;
			this.validBuildingBlocks.add(primaryBlock);
		}
		/**
		 * Sets the max health of a golem
		 * @param lHealth The max health (in half hearts) of the golem. <b>Defaults to 100</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setHealth(double lHealth) {
			health = lHealth;
			return this;
		}

		/**
		 * Sets the attack strength of a golem
		 * @param lAttack The attack strength (in half hearts) of the golem. <b>Defaults to 14</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setAttack(double lAttack) {
			attack = lAttack;
			return this;
		}

		/**
		 * Adds building blocks that may be used for creating the golem
		 * @param additionalBlocks blocks
		 * @return instance to allow chaining of methods
		 */
		public Builder addValidBlocks(Block... additionalBlocks) {
			this.validBuildingBlocks.addAll(Arrays.asList(additionalBlocks));
			return this;
		}

		/**
		 * Builds the container
		 * @return a copy of the newly constructed GolemContainer
		 */
		public GolemContainer build() {
			return new GolemContainer(
				entityTypeBuilder.build(golemName), golemName, validBuildingBlocks, health, attack,
				allowSpecial);
		}
	}
}
