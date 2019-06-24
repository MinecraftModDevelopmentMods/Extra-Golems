package com.mcmoddev.golems.util.config;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
@SuppressWarnings("rawtypes")
public class GolemContainer {

	private final Class<? extends GolemBase> entityClass;
	private final List<Block> validBuildingBlocks;
	private final List<ResourceLocation> validBuildingBlockTags;
	private final EntityType<? extends GolemBase> entityType;
	private final String name;
	private final ResourceLocation lootTable;
	private double health;
	private double attack;
	private double speed;
	private boolean enabled = true;

	public final Map<String, GolemSpecialContainer> specialContainers;
	public final List<GolemDescription> descContainers;

	/**
	 * Constructor for GolemContainer (use the Builder!)
	 *
	 * @param lEntityType             a constructed EntityType for the golem
	 * @param lPath                   the golem name
	 * @param lValidBuildingBlocks    a List of blocks to build the golem
	 * @param lValidBuildingBlockTags a List of Block Tags to build the golem
	 * @param lHealth                 base health value
	 * @param lAttack                 base attack value
	 * @param lSpeed                  base speed value
	 * @param lSpecialContainers      any golem specials as a Map
	 * @param lDesc                   any special descriptions for the golem
	 * @param lLootTable              a ResourceLocation for the on-death loot table, may be null
	 **/
	private GolemContainer(final EntityType<? extends GolemBase> lEntityType,
						   final Class<? extends GolemBase> lEntityClass, final String lPath,
						   final List<Block> lValidBuildingBlocks, final List<ResourceLocation> lValidBuildingBlockTags,
						   final double lHealth, final double lAttack, final double lSpeed,
						   final HashMap<String, GolemSpecialContainer> lSpecialContainers,
						   final List<GolemDescription> lDesc, final ResourceLocation lLootTable) {
		this.entityType = lEntityType;
		this.entityClass = lEntityClass;
		this.validBuildingBlocks = lValidBuildingBlocks;
		this.validBuildingBlockTags = lValidBuildingBlockTags;
		this.name = lPath;
		this.health = lHealth;
		this.attack = lAttack;
		this.speed = lSpeed;
		this.specialContainers = lSpecialContainers;
		this.descContainers = lDesc;
		this.lootTable = lLootTable;
	}

	/**
	 * Called by various in-game info tools, such as the Golem Book
	 * and WAILA / HWYLA. Adds this golem's description(s) to the given
	 * List as specified by
	 * {@link GolemDescription#addDescription(List, GolemContainer)}.
	 *
	 * @param list a List that may or may not contain other descriptions already.
	 **/
	public void addDescription(final List<ITextComponent> list) {
		for (final GolemDescription cont : descContainers) {
			cont.addDescription(list, this);
		}
	}

	/**
	 * @return True if there is at least one valid
	 * Block or Block Tag which can be used to build this golem
	 **/
	public boolean hasBuildingBlock() {
		return !this.validBuildingBlocks.isEmpty() || !this.validBuildingBlockTags.isEmpty();
	}

	/**
	 * @return a Set of all possible Blocks that can be used
	 * to build the golem. Does not contain duplicates but may be empty.
	 * @see #hasBuildingBlock()
	 **/
	public Set<Block> getBuildingBlocks() {
		// make set of all blocks including tags (run-time only)
		Set<Block> blocks = new HashSet<>();
		blocks.addAll(validBuildingBlocks);
		for (final Tag<Block> tag : loadTags(validBuildingBlockTags)) {
			blocks.addAll(tag.getAllElements());
		}
		return blocks;
	}

	/**
	 * @deprecated use {@link #areBuildingBlocks(Block, Block, Block, Block)}
	 **/
	public boolean isBuildingBlock(final Block b) {
		return areBuildingBlocks(b, b, b, b);
	}

	/**
	 * Checks if this golem's building block set includes all of the given blocks.
	 *
	 * @param body the Block immediately below the head
	 * @param legs the Block immediately below the body Block
	 * @param arm1 first arm Block (could be North-South or East-West)
	 * @param arm2 second arm Block (could be North-South or East-West)
	 * @return true if all blocks are valid building blocks
	 * @see #getBuildingBlocks()
	 **/
	public boolean areBuildingBlocks(final Block body, final Block legs, final Block arm1, final Block arm2) {
		final Set<Block> blocks = getBuildingBlocks();
		return blocks.contains(body) && blocks.contains(legs) && blocks.contains(arm1) && blocks.contains(arm2);
	}

	/**
	 * Returns a single Block that can be used for this golem.
	 * It is not guaranteed that there is a Block or that it is the
	 * most easily obtainable by the player, it's simply the first
	 * element in the set of building blocks.
	 *
	 * @return a Block to build this golem, or null if none are found
	 * @see #getBuildingBlocks()
	 **/
	@Nullable
	public Block getPrimaryBuildingBlock() {
		if (hasBuildingBlock()) {
			final Block[] blocks = this.getBuildingBlocks().toArray(new Block[0]);
			if (blocks != null && blocks.length > 0) {
				return blocks[0];
			}
		}
		return null;
	}

	/**
	 * Allows additional blocks to be registered as "valid"
	 * in order to build this golem. Useful especially for
	 * add-ons. If you're using this in your mod to change your
	 * own golems, please use {@link GolemContainer.Builder#addBlocks(Block...)}
	 *
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
	 *
	 * @param additional Block Tag to register as "valid"
	 * @return if the Block Tag was added successfully
	 **/
	public boolean addBlocks(@Nonnull final Tag<Block> additional) {
		return this.validBuildingBlockTags.add(additional.getId());
	}

	/**
	 * Required for correctly loading tags - they must be called as needed and
	 * can not be stored or queried before they are properly loaded and reloaded.
	 *
	 * @param rls a Collection of ResourceLocation IDs that represent Block Tags.
	 * @return
	 **/
	private static Collection<Tag<Block>> loadTags(final Collection<ResourceLocation> rls) {
		final Collection<Tag<Block>> tags = new HashSet<>();
		for (final ResourceLocation rl : rls) {
			if (BlockTags.getCollection().get(rl) != null) {
				tags.add(BlockTags.getCollection().get(rl));
			}
		}
		return tags;
	}

	////////// SETTERS //////////
	public void setHealth(final double pHealth) {
		this.health = pHealth;
	}

	public void setAttack(final double pAttack) {
		this.attack = pAttack;
	}

	public void setSpeed(final double pSpeed) {
		this.speed = pSpeed;
	}

	public void setEnabled(final boolean pEnabled) {
		this.enabled = pEnabled;
	}

	////////// GETTERS //////////
	public Class<? extends GolemBase> getEntityClass() {
		return this.entityClass;
	}

	public EntityType<? extends GolemBase> getEntityType() {
		return this.entityType;
	}

	public ResourceLocation getRegistryName() {
		return this.entityType.getRegistryName();
	}

	public String getName() {
		return this.name;
	}

	public double getHealth() {
		return this.health;
	}

	public double getAttack() {
		return this.attack;
	}

	public double getSpeed() {
		return this.speed;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean hasLootTable() {
		return this.lootTable != null;
	}

	@Nullable
	public ResourceLocation getLootTable() {
		return this.lootTable;
	}

	//////////////////////////////////////////////////////////////
	/////////////////// END OF GOLEM CONTAINER ///////////////////
	//////////////////////////////////////////////////////////////

	/**
	 * This class is my own work
	 *
	 * @author Glitch
	 */
	public static final class Builder {
		private final String golemName;
		private final Class<? extends GolemBase> entityClass;
		private EntityType.Builder<? extends GolemBase> entityTypeBuilder;
		private ResourceLocation lootTable = null;
		private String modid = ExtraGolems.MODID;
		private double health = 100.0D;
		private double attack = 7.0D;
		private double speed = 0.25D;
		//This is a list to allow determining the "priority" of golem blocks. This could be used to our
		//advantage in golem building logic for conflicts in the future.
		private List<Block> validBuildingBlocks = new ArrayList<>();
		private List<ResourceLocation> validBuildingBlockTags = new ArrayList<>();
		private List<GolemSpecialContainer> specials = new ArrayList<>();
		private List<GolemDescription> descriptions = new ArrayList<>();

		/**
		 * Creates the builder
		 *
		 * @param golemName     the name of the golem
		 * @param entityClazz   the class of the golem (e.g. EntityFooGolem.class)
		 * @param entityFactory the constructor function of the class (e.g. EntityFooGolem::new)
		 */
		public Builder(final String golemName, final Class<? extends GolemBase> entityClazz,
					   final EntityType.IFactory<? extends GolemBase> entityFactory) {
			this.golemName = golemName;
			this.entityClass = entityClazz;
			this.entityTypeBuilder = EntityType.Builder.create(entityFactory, EntityClassification.MISC)
				.setTrackingRange(48).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
				.size(1.4F, 2.9F);
		}

		/**
		 * Sets the Mod ID of the golem for registry name
		 *
		 * @param lModId the MODID to use to register the golem. <b>Defaults to "golems"</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setModId(final String lModId) {
			this.modid = lModId;
			return this;
		}

		/**
		 * Sets the max health of a golem
		 *
		 * @param lHealth The max health (in half hearts) of the golem. <b>Defaults to 100</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setHealth(final double lHealth) {
			health = lHealth;
			return this;
		}

		/**
		 * Sets the attack strength of a golem
		 *
		 * @param lAttack The attack strength (in half hearts) of the golem. <b>Defaults to 7</b>
		 * @return instance to allow chaining of methods
		 */
		public Builder setAttack(final double lAttack) {
			attack = lAttack;
			return this;
		}

		/**
		 * Sets the movement speed of a golem
		 *
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
		 *
		 * @param additionalBlocks blocks that may be used for building
		 * @return instance to allow chaining of methods
		 * @see #addBlocks(Tag)
		 */
		public Builder addBlocks(final Block... additionalBlocks) {
			if (additionalBlocks != null && additionalBlocks.length > 0) {
				this.validBuildingBlocks.addAll(Arrays.asList(additionalBlocks));
			}
			return this;
		}

		/**
		 * Adds building blocks that may be used for creating the golem
		 * in the form of a Block Tag. If no blocks are added via
		 * this method or the Block[] version, this golem cannot
		 * be built in-world.
		 *
		 * @param blockTag the {@code Tag<Block>} to use
		 * @return instance to allow chaining of methods
		 * @see #addBlocks(Block[])
		 */
		public Builder addBlocks(final Tag<Block> blockTag) {
			this.validBuildingBlockTags.add(blockTag.getId());
			return this;
		}

		/**
		 * Adds any {@link GolemSpecialContainer}s to be used by the golem
		 *
		 * @param specialContainers specials to be added
		 * @return instance to allow chaining of methods
		 * @see #addSpecial(String, Object, String)
		 */
		public Builder addSpecials(final GolemSpecialContainer... specialContainers) {
			specials.addAll(Arrays.asList(specialContainers));
			return this;
		}

		/**
		 * Adds any GolemSpecialContainers to be used by the golem.
		 * If this option should be toggled (ie, a {@code Boolean}) and
		 * you want an in-game description, use
		 * {@link #addSpecial(String, Object, String, ITextComponent)}
		 *
		 * @param name    a name unique to this golem's set of config options
		 * @param value   the initial (default) value for this config option
		 * @param comment a short description for the config file
		 * @return instance to allow chaining of methods
		 * @see #addSpecials(GolemSpecialContainer...)
		 */
		public Builder addSpecial(final String name, final Object value, final String comment) {
			specials.add(new GolemSpecialContainer.Builder(name, value, comment).build());
			return this;
		}

		/**
		 * Adds a {@link GolemSpecialContainer} with the given values along with
		 * a {@link GolemDescription} associated with the Special. Assumes the
		 * Special you are adding is a {@code Boolean} value. If this is not the case,
		 * use {@link #addSpecial(String, Object, String)} to add the config
		 * and use {@link #addDesc(GolemDescription...)} to add a custom description.
		 *
		 * @param name    a name unique to this golem's set of config options
		 * @param value   the initial (default) value for this config option
		 * @param comment a short description for the config file
		 * @param desc    a fancier description to be used in-game
		 * @return instance to allow chaining of methods
		 **/
		public Builder addSpecial(final String name, final Boolean value, final String comment, final ITextComponent desc) {
			addSpecial(name, value, comment);
			addDesc(new GolemDescription(desc, name));
			return this;
		}

		/**
		 * Adds any {@link GolemDescription}s to be used by the golem
		 *
		 * @param desc description to be added
		 * @return instance to allow chaining of methods
		 */
		public Builder addDesc(final GolemDescription... desc) {
			for (final GolemDescription cont : desc) {
				descriptions.add(cont);
			}
			return this;
		}

		/**
		 * Registers a single loot table for entity drops upon death.
		 * This loot table will be automatically added to the golem.
		 * If the golem should have different loot tables based on
		 * state (such as multi-textured golems), use
		 * {@link #addLootTables(String, String, String[])} instead.
		 * <br><br><i>Note: {@link #setModId(String)} must be called
		 * <b>before</b> using this method</i>
		 *
		 * @param modid    the loot table parent MOD ID
		 * @param location the loot table name, including sub-folders
		 * @return instance to allow chaining of methods
		 **/
		public Builder addLootTable(final String location) {
			this.lootTable = new ResourceLocation(this.modid, "entities/".concat(location));
			//LootTableList.register(this.lootTable);
			return this;
		}

		/**
		 * Registers a set of loot tables. It is up to the golem
		 * class to handle and return the correct loot table
		 * upon entity death.
		 * <br><br><i>Note: {@link #setModId(String)} must be called
		 * <b>before</b> using this method</i>
		 *
		 * @param modid     the loot table parent MOD ID
		 * @param path      a path to prefix the loot table name
		 * @param locations the loot table names
		 * @return instance to allow chaining of methods
		 **/
		public Builder addLootTables(final String path, final String[] locations) {
			for (final String s : locations) {
				//LootTableList.register(new ResourceLocation(this.modid, "entities/".concat(path.concat("/".concat(s)))));
			}
			return this;
		}

		/**
		 * Makes this golem immune to fire damage.
		 *
		 * @return instance to allow chaining of methods
		 **/
		public Builder immuneToFire() {
			this.entityTypeBuilder = this.entityTypeBuilder.immuneToFire();
			return this;
		}

		/**
		 * Builds the container according to values that have
		 * been set inside this Builder
		 *
		 * @return a copy of the newly constructed GolemContainer
		 */
		public GolemContainer build() {
			EntityType<? extends GolemBase> entityType = entityTypeBuilder.build(golemName);
			entityType.setRegistryName(modid, golemName);
			HashMap<String, GolemSpecialContainer> containerMap = new HashMap<>();
			for (GolemSpecialContainer c : specials) {
				containerMap.put(c.name, c);
			}
			return new GolemContainer(entityType, entityClass, golemName,
				validBuildingBlocks, validBuildingBlockTags,
				health, attack, speed, containerMap, descriptions, lootTable);
		}
	}

}
