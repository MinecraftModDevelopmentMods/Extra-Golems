package com.mcmoddev.golems.util.config;

import com.google.common.base.Predicates;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemContainer {

	private final List<Block> validBuildingBlocks;
	private final List<ResourceLocation> validBuildingBlockTags;
	public final EntityType<GolemBase> entityType;
	private String name;
	private double health;
	private double attack;
	private double speed;
	private boolean enabled = true;

	public Map<String, GolemSpecialContainer> specialContainers;

	private GolemContainer(final EntityType<GolemBase> lEntityType, final String lPath,
						   final List<Block> lValidBuildingBlocks, final List<ResourceLocation> lValidBuildingBlockTags,
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
		return !this.validBuildingBlocks.isEmpty() || !this.validBuildingBlockTags.isEmpty();
	}

	public Block[] getBuildingBlocks() {
		// make set of all blocks including tags (run-time only)
		Set<Block> blocks = new HashSet<>();
		blocks.addAll(validBuildingBlocks);
		for(final Tag<Block> tag : loadTags(validBuildingBlockTags)) {
			blocks.addAll(tag.getAllElements());
		}
		return blocks.toArray(new Block[0]);
	}
	
	public boolean isBuildingBlock(final Block b) {
		if(null == b) return false;
		// if the block has been manually added to block list
		if(this.validBuildingBlocks.contains(b)) {
			return true;
		}
		// if the block is present in any of the tags
		for(final Tag<Block> tag : loadTags(validBuildingBlockTags)) {
			if(b.isIn(tag)) {
				return true;
			}
		}
		// nothing found, result is false
 		return false;
	}
	
	public boolean areBuildingBlocks(final Block b1, final Block b2, final Block b3, final Block b4) {
		return isBuildingBlock(b1) && isBuildingBlock(b2) && isBuildingBlock(b3) && isBuildingBlock(b4);
	}
	
	@Nullable
	public Block getPrimaryBuildingBlock() {
		if(hasBuildingBlock()) {
			if(!this.validBuildingBlocks.isEmpty() && this.validBuildingBlocks.get(0) != null) {
				// get first block in list
				return this.validBuildingBlocks.get(0);
			} else if(!this.validBuildingBlockTags.isEmpty() && this.validBuildingBlockTags.get(0) != null) {
				// get first tag in list and first block mapping in that tag
				Block[] blocks = BlockTags.getCollection().get(this.validBuildingBlockTags.get(0)).getAllElements().toArray(new Block[0]);
				return blocks.length > 0 ? blocks[0] : null;
			}
		}
		return null;
	}
	
	/**
	 * Allows additional blocks to be registered as "valid"
	 * in order to build this golem. Useful especially for
	 * add-ons. If you're using this in your mod to change your
     * own golems, please use {@link GolemContainer.Builder#addBlocks(Block...)}
	 * @param additional Block objects to register as "valid"
	 * @return if the blocks were added successfully
	 **/
	public boolean addBlocks(@Nonnull final Block... additional) {
		return additional.length > 0 && this.validBuildingBlocks.addAll(Arrays.asList(additional));
	}
	
	/**
	 * Allows additional Block Tags to be registered as "valid"
	 * in order to build this golem. Useful especially for
	 * add-ons. If you're using this in your mod to change your
	 * own golems, please use {@link GolemContainer.Builder#addBlocks(Tag)} instead
	 * @param additional Block Tag to register as "valid"
	 * @return if the Block Tag was added successfully
	 **/
	public boolean addBlocks(@Nonnull final Tag<Block> additional) {
		return this.validBuildingBlockTags.add(additional.getId());
	}
	
	private static Collection<Tag<Block>> loadTags(final Collection<ResourceLocation> rls) {
		final Collection<Tag<Block>> tags = new HashSet<>();
		rls.forEach(rl -> tags.add(BlockTags.getCollection().get(rl)));
		return tags;
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
		private List<ResourceLocation> validBuildingBlockTags = new ArrayList<>();
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
		 * Adds building blocks that may be used for creating the golem.
		 * If no blocks are added via this method or the Block Tag version, 
		 * this golem cannot be built in-world.
		 * @param additionalBlocks blocks that may be used for building
		 * @return instance to allow chaining of methods
		 * @see #addBlocks(Tag)
		 */
		public Builder addBlocks(final Block... additionalBlocks) {
			if(additionalBlocks != null && additionalBlocks.length > 0) {
				this.validBuildingBlocks.addAll(Arrays.asList(additionalBlocks));
			}
			return this;
		}
		
		/**
		 * Adds building blocks that may be used for creating the golem
		 * in the form of a Block Tag. If no blocks are added via
		 * this method or the Block[] version, this golem cannot
		 * be built in-world.
		 * @param blockTag the {@code Tag<Block>} to use
		 * @return instance to allow chaining of methods
		 * @see #addBlocks(Block[])
		 */
		public Builder addBlocks(final Tag<Block> blockTag) {
			this.validBuildingBlockTags.add(blockTag.getId());
			return this;
		}

		/**
		 * Adds any GolemSpecialContainers to be used by the golem
		 * @param specialContainers specials to be added
		 * @return instance to allow chaining of methods
		 * @see #addSpecial(String, Object, String)
		 */
		public Builder addSpecials(final GolemSpecialContainer... specialContainers) {
			containers.addAll(Arrays.asList(specialContainers));
			return this;
		}
		
		/**
		 * Adds any GolemSpecialContainers to be used by the golem
		 * @param specialContainers specials to be added
		 * @return instance to allow chaining of methods
		 * @see #addSpecials(GolemSpecialContainer...)
		 */
		public Builder addSpecial(final String name, final Object value, final String comment) {
			containers.add(new GolemSpecialContainer.Builder(name, value, comment).build());
			return this;
		}
		
		/**
		 * Builds the container according to values that have
		 * been set inside this Builder
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
