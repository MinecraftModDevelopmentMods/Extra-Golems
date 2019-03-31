package com.mcmoddev.golems.util.config;

import com.google.common.collect.Lists;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.*;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemContainer {

	private final List<Block> validBuildingBlocks;
	private final List<Tag<Block>> validBuildingBlockTags;
	//Avoid making setters/getters for non-final fields for now
	public final EntityType<GolemBase> entityType;
	private String name;
	private double health;
	private double attack;
	private double speed;
	private boolean enabled = true;

	public Map<String, GolemSpecialContainer> specialContainers;

	private GolemContainer(final EntityType<GolemBase> lEntityType, final String lPath, 
			final List<Block> lValidBuildingBlocks, final List<Tag<Block>> lValidBuildingBlockTags,
			final double lHealth, final double lAttack, final double lSpeed,
			final HashMap<String, GolemSpecialContainer> lSpecialContainers) {
		this.entityType = lEntityType;
		this.validBuildingBlocks = lValidBuildingBlocks;
		this.validBuildingBlockTags = lValidBuildingBlockTags;
		this.name = lPath;
		this.health = lHealth;
		this.attack = lAttack;
		this.speed = lSpeed;
		this.specialContainers = lSpecialContainers;
	}


	public boolean hasBuildingBlock() {
		return !(this.validBuildingBlocks.isEmpty() && this.validBuildingBlocks.isEmpty());
	}

	public Block[] getBuildingBlocks() {
		// make set of all blocks including tags (run-time only)
		Set<Block> blocks = new HashSet<>();
		blocks.addAll(validBuildingBlocks);
		for(Tag<Block> tag : this.validBuildingBlockTags) {
			blocks.addAll(tag.getAllElements());
		}
		return blocks.toArray(new Block[0]);
	}
	
	public boolean isBuildingBlock(final Block b) {
		if(null == b) return false;
		// make set of all blocks including tags (run-time only)
		Set<Block> blocks = new HashSet<>();
		blocks.addAll(validBuildingBlocks);
		for(Tag<Block> tag : this.validBuildingBlockTags) {
			blocks.addAll(tag.getAllElements());
		}
 		return this.validBuildingBlocks.contains(b);
	}
	
	public boolean areBuildingBlocks(final Block b1, final Block b2, final Block b3, final Block b4) {
		return isBuildingBlock(b1) && isBuildingBlock(b2) && isBuildingBlock(b3) && isBuildingBlock(b4);
	}
	
	@Nullable
	public Block getPrimaryBuildingBlock() {
		if(hasBuildingBlock()) {
			Block[] allBlocks = getBuildingBlocks();
			return allBlocks != null && allBlocks.length > 0 ? allBlocks[0] : null;
		}
		return null;
	}
	
	////////// SETTERS //////////
	public void setHealth(final double pHealth) { this.health = pHealth; }
	public void setAttack(final double pAttack) { this.attack = pAttack; }
	public void setSpeed(final double pSpeed) { this.speed = pSpeed; }
	public void setEnabled(final boolean pEnabled) { this.enabled = pEnabled; }
	
	////////// GETTERS //////////
	public EntityType<GolemBase> getEntityType() { return this.entityType; }
	public String getName() { return this.name; }
	public double getHealth() { return this.health; }
	public double getAttack() { return this.attack; }
	public double getSpeed() { return this.speed; }
	public boolean isEnabled() { return this.enabled; }

	/**
	 * This class is my own work
	 * @author Glitch
	 */
	public static final class Builder {
		private final String golemName;
		private final EntityType.Builder<GolemBase> entityTypeBuilder;
		private String modid = ExtraGolems.MODID;
		private double health = 100.0D;
		private double attack = 14.0D; //Average iron golem attack in Normal mode
		private double speed = 0.25D;
		//This is a list to allow determining the "priority" of golem blocks. This could be used to our
		//advantage in golem building logic for conflicts in the future.
		private List<Block> validBuildingBlocks = new ArrayList<>();
		private List<Tag<Block>> validBuildingBlockTags = new ArrayList<>();
		private List<GolemSpecialContainer> containers = new ArrayList<>();

		/**
		 * Creates the builder
		 * @param golemName the name of the golem
		 * @param entityClazz the class of the golem (e.g. EntityFooGolem.class)
		 * @param entityFunction the constructor function of the class (e.g. EntityFooGolem::new)
		 */
		public Builder(final String golemName, final Class<? extends GolemBase> entityClazz,
				final Function<? super World, ? extends GolemBase> entityFunction) {
			this.golemName = golemName;
			this.entityTypeBuilder = EntityType.Builder.<GolemBase>create(entityClazz, entityFunction)
				.tracker(48, 3, true);
		}
		
		/**
		 * Sets the Mod ID of the golem for registry name
		 * @param lModId the MODID to use to register the golem. <b>Defaults to "golems"</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setModId(final String lModId) {
			this.modid = lModId;
			return this;
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
		 * Sets the movement speed of a golem
		 * @param lMoveSpeed The move speed of the golem. <b>Defaults to 0.25D</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setSpeed(final double lMoveSpeed) {
			speed = lMoveSpeed;
			return this;
		}

		/**
		 * Adds building blocks that may be used for creating the golem
		 * @param additionalBlocks blocks that may be used for building
		 * @return instance to allow chaining of methods
		 */
		public Builder addBlocks(final Block... additionalBlocks) {
			if(additionalBlocks != null && additionalBlocks.length > 0) {
				this.validBuildingBlocks.addAll(Arrays.asList(additionalBlocks));
			}
			return this;
		}
		
		/**
		 * Adds building blocks that may be used for creating the golem
		 * in the form of a Block Tag
		 * @param blockTag the {@code Tag<Block>} to use
		 * @return instance to allow chaining of methods
		 */
		public Builder addBlocks(final Tag<Block> blockTag) {
			this.validBuildingBlockTags.add(blockTag);
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
		 * Adds any GolemSpecialContainers to be used by the golem
		 * @param specialContainers specials to be added
		 * @return instance to allow chaining of methods
		 */
		public Builder addSpecial(final String name, final Object value, final String comment) {
			containers.add(new GolemSpecialContainer.Builder(name, value, comment).build());
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
			return new GolemContainer(entityType, golemName, validBuildingBlocks, 
					validBuildingBlockTags, health, attack, speed, containerMap);
		}
	}

}
