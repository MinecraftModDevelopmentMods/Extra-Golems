package com.mcmoddev.golems.util.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IRegistryDelegate;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
@SuppressWarnings("rawtypes")
public final class GolemContainer {

  private final Class<? extends GolemBase> entityClass;
  private final List<IRegistryDelegate<Block>> validBuildingBlocks;
  private final List<ResourceLocation> validBuildingBlockTags;
  private final EntityType<? extends GolemBase> entityType;
  private final String name;
  private final ResourceLocation basicTexture;
  private final SoundEvent basicSound;
  private final boolean fallDamage;
  private final SwimMode swimMode;
  private final boolean hasCustomRender;

  private double health;
  private double attack;
  private final double speed;
  private final double knockbackResist;
  private boolean enabled = true;

  private final ImmutableMap<String, GolemSpecialContainer> specialContainers;
  private final ImmutableList<GolemDescription> descContainers;
  private final ImmutableMap<IRegistryDelegate<Item>, Double> healItemMap;

  /**
   * Constructor for GolemContainer (use the Builder!)
   *
   * @param lEntityType             a constructed EntityType for the golem
   * @param lEntityClass            the class that will handle the golem behavior
   * @param lPath                   the golem name
   * @param lValidBuildingBlocks    a List of block delegates to build the golem
   * @param lValidBuildingBlockTags a List of Block Tags to build the golem
   * @param lHealth                 base health value
   * @param lAttack                 base attack value
   * @param lSpeed                  base speed value
   * @param lKnockbackResist        base knockback resistance
   * @param lFallDamage             whether or not the golem can take fall damage
   * @param lSwimMode               whether or not the golem floats in water
   * @param lSpecialContainers      any golem specials as a Map
   * @param lDesc                   any special descriptions for the golem
   * @param lHealItemMap            a map of items and their corresponding heal amounts
   * @param lTexture                a ResourceLocation for a single default texture
   * @param lBasicSound             a default SoundEvent to use for the golem
   * @param lCustomRender           whether or not the golem will use the default renderer
   **/
  private GolemContainer(final EntityType<? extends GolemBase> lEntityType, final Class<? extends GolemBase> lEntityClass,
      final String lPath, final List<IRegistryDelegate<Block>> lValidBuildingBlocks,
      final List<ResourceLocation> lValidBuildingBlockTags, final double lHealth, final double lAttack, final double lSpeed,
      final double lKnockbackResist, final boolean lFallDamage, final SwimMode lSwimMode,
      final HashMap<String, GolemSpecialContainer> lSpecialContainers, final List<GolemDescription> lDesc,
      final Map<IRegistryDelegate<Item>, Double> lHealItemMap, final ResourceLocation lTexture, final SoundEvent lBasicSound,
      final boolean lCustomRender) {
    this.entityType = lEntityType;
    this.entityClass = lEntityClass;
    this.validBuildingBlocks = lValidBuildingBlocks;
    this.validBuildingBlockTags = lValidBuildingBlockTags;
    this.name = lPath;
    this.health = lHealth;
    this.attack = lAttack;
    this.speed = lSpeed;
    this.knockbackResist = lKnockbackResist;
    this.fallDamage = lFallDamage;
    this.swimMode = lSwimMode;
    this.specialContainers = ImmutableMap.copyOf(lSpecialContainers);
    this.descContainers = ImmutableList.copyOf(lDesc);
    this.healItemMap = ImmutableMap.copyOf(lHealItemMap);
    this.basicTexture = lTexture;
    this.basicSound = lBasicSound;
    this.hasCustomRender = lCustomRender;
  }

  /**
   * Called by various in-game info tools, such as the Golem Book and WAILA / HWYLA. Adds this golem's description(s) to the given
   * List as specified by {@link GolemDescription#addDescription(List, GolemContainer)}.
   *
   * @param list a List that may or may not contain other descriptions already.
   **/
  public void addDescription(final List<ITextComponent> list) {
    for (final GolemDescription cont : descContainers) {
      cont.addDescription(list, this);
    }
  }

  /**
   * @return True if there is at least one valid Block or Block Tag which can be used to build this golem
   **/
  public boolean hasBuildingBlock() {
    return !this.validBuildingBlocks.isEmpty() || !this.validBuildingBlockTags.isEmpty();
  }

  /**
   * @return a Set of all possible Blocks that can be used to build the golem. Does not contain duplicates but may be empty.
   * @see #hasBuildingBlock()
   **/
  public Set<Block> getBuildingBlocks() {
    // make set of all blocks including tags (run-time only)
    Set<Block> blocks = validBuildingBlocks.isEmpty() ? new HashSet<>()
        : validBuildingBlocks.stream().map(d -> d.get()).collect(Collectors.toSet());
    for (final Tag<Block> tag : loadTags(validBuildingBlockTags)) {
      blocks.addAll(tag.getAllElements());
    }
    return blocks;
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
   * Returns a single Block that can be used for this golem. It is not guaranteed that there is a Block or that it is the most
   * easily obtainable by the player, it's simply the first element in the set of building blocks.
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
   * @return a collection of all the GolemSpecialContainers used by this golem
   **/
  public ImmutableCollection<GolemSpecialContainer> getSpecialContainers() {
    return specialContainers.values();
  }
  
  /**
   * @param key a String key used to find the special container
   * @return the GolemSpecialContainer if found, otherwise null
   **/
  public GolemSpecialContainer getSpecialContainer(final String key) {
    return specialContainers.get(key);
  }
  
  /**
   * @param item an item that could potentially heal the golem
   * @return a percentage of health to restore. May be zero.
   **/
  public double getHealAmount(final Item item) {
    // check the map for the value
    final Map<IRegistryDelegate<Item>, Double> map = loadTagsForHealMap(getBuildingBlocks(), healItemMap);
    if(item != null && item != Items.AIR && map.containsKey(item.delegate)) {
      return map.get(item.delegate);
    }
    // default value is zero
    return 0;
  }
  
  /**
   * @return a Set of all items that can be used to heal the golem
   **/
  public Set<Item> getHealItems() {
    return loadTagsForHealMap(getBuildingBlocks(), healItemMap).keySet().stream().map(del -> del.get()).collect(Collectors.toSet());
  }

  /**
   * Allows additional blocks to be registered as "valid" in order to build this golem. Useful especially for add-ons. If you're
   * using this in your mod to change your own golems, please use {@link GolemContainer.Builder#addBlocks(Block...)}
   *
   * @param additional Block objects to register as "valid"
   * @return if the blocks were added successfully
   **/
  public boolean addBlocks(@Nonnull final Block... additional) {
    return additional.length > 0
        && this.validBuildingBlocks.addAll(Arrays.asList(additional).stream().map(d -> d.delegate).collect(Collectors.toList()));
  }

  /**
   * Allows additional Block Tags to be registered as "valid" in order to build this golem. Useful especially for add-ons. If
   * you're using this in your mod to change your own golems, please use {@link GolemContainer.Builder#addBlocks(Tag)} instead
   *
   * @param additional Block Tag to register as "valid"
   * @return if the Block Tag was added successfully
   **/
  public boolean addBlocks(@Nonnull final Tag<Block> additional) {
    return this.validBuildingBlockTags.add(additional.getId());
  }

  /**
   * Required for correctly loading tags - they must be called as needed and can not be stored or queried before they are properly
   * loaded and reloaded.
   *
   * @param rls a Collection of ResourceLocation IDs that represent Block Tags.
   * @return a current Collection of Block Tags
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
  
  private static Map<IRegistryDelegate<Item>, Double> loadTagsForHealMap(final Set<Block> set, final Map<IRegistryDelegate<Item>, Double> healItems) {
    final Map<IRegistryDelegate<Item>, Double> map = new HashMap<>(healItems);
    // add each block in the set to the given map
    for(final Block b : set) {
      Item ib = b.asItem();
      if(ib != Items.AIR && !map.containsKey(ib.delegate)) {
        // building blocks restore 75% of golem health
        map.put(ib.delegate, 0.75D);
      }
    }
    return map;
  }

  ////////// SETTERS //////////

  /**
   * <b>DO NOT CALL</b> unless you are the config!
   * 
   * @param pHealth new 'health' value
   **/
  public void setHealth(final double pHealth) {
    this.health = pHealth;
  }

  /**
   * <b>DO NOT CALL</b> unless you are the config!
   * 
   * @param pAttack new 'attack' value
   **/
  public void setAttack(final double pAttack) {
    this.attack = pAttack;
  }

  /**
   * <b>DO NOT CALL</b> unless you are the config!
   * 
   * @param pEnabled new 'enabled' value
   **/
  public void setEnabled(final boolean pEnabled) {
    this.enabled = pEnabled;
  }

  ////////// GETTERS //////////

  /** @return the base class used by the Golem. Not always unique. **/
  public Class<? extends GolemBase> getEntityClass() {
    return this.entityClass;
  }

  /** @return the EntityType of the Golem. Always unique. **/
  public EntityType<? extends GolemBase> getEntityType() {
    return this.entityType;
  }

  /** @return a unique ResourceLocation ID for the Golem. Always unique. **/
  public ResourceLocation getRegistryName() {
    return this.entityType.getRegistryName();
  }

  /** @return a default texture for the Golem. May be null. **/
  public ResourceLocation getTexture() {
    return this.basicTexture;
  }

  /**
   * @return whether the golem should use the default IRenderFactory for rendering
   **/
  public boolean useDefaultRender() {
    return !this.hasCustomRender;
  }

  /** @return a default SoundEvent to play when the Golem moves or is attacked **/
  public SoundEvent getSound() {
    return this.basicSound;
  }

  /** @return the name of the Golem as specified in the Builder **/
  public String getName() {
    return this.name;
  }

  /** @return the Golem's default health. Mutable. **/
  public double getHealth() {
    return this.health;
  }

  /** @return the Golem's default attack power. Mutable. **/
  public double getAttack() {
    return this.attack;
  }

  /** @return the Golem's default move speed. Immutable. **/
  public double getSpeed() {
    return this.speed;
  }

  /** @return the Golem's default knockback resistance. Immutable. **/
  public double getKnockbackResist() {
    return this.knockbackResist;
  }

  /** @return true if the Golem is enabled by the config settings. Mutable. **/
  public boolean isEnabled() {
    return this.enabled;
  }

  /** @return true if the Golem takes damage upon falling from heights **/
  public boolean takesFallDamage() {
    return this.fallDamage;
  }

  /** @return true if the Golem can swim on top of water **/
  public boolean canSwim() {
    return this.swimMode == SwimMode.FLOAT;
  }

  /** @return the {@link SwimMode} of the Golem **/
  public SwimMode getSwimMode() {
    return this.swimMode;
  }

  //////////////////////////////////////////////////////////////
  /////////////////// END OF GOLEM CONTAINER ///////////////////
  //////////////////////////////////////////////////////////////

  /**
   * This class is my own work
   * <p>
   * Use this class to build GolemContainer objects that can be registered to the {@link GolemRegistrar}
   *
   * @author Glitch
   */
  public static final class Builder {
    private final String golemName;
    private final Class<? extends GolemBase> entityClass;
    private EntityType.Builder<? extends GolemBase> entityTypeBuilder;

    private ResourceLocation basicTexture = null;
    private SoundEvent basicSound = SoundEvents.BLOCK_STONE_STEP;

    private String modid = ExtraGolems.MODID;
    private double health = 100.0D;
    private double attack = 7.0D;
    private double speed = 0.25D;
    private double knockBackResist = 0.4D;
    private boolean fallDamage = false;
    private boolean customRender = false;
    private SwimMode swimMode = SwimMode.SINK;
    // This is a list to allow determining the "priority" of golem blocks. This
    // could be used to our
    // advantage in golem building logic for conflicts in the future.
    private List<IRegistryDelegate<Block>> validBuildingBlocks = new ArrayList<>();
    private List<ResourceLocation> validBuildingBlockTags = new ArrayList<>();
    private List<GolemSpecialContainer> specials = new ArrayList<>();
    private List<GolemDescription> descriptions = new ArrayList<>();
    private final Map<IRegistryDelegate<Item>, Double> healItemMap = new HashMap<>();

    /**
     * Creates the builder
     *
     * @param modId         the mod ID (e.g., "golems_addon")
     * @param golemName     the name of the golem (e.g. "golem_foo")
     * @param entityClazz   the class of the golem (e.g. EntityFooGolem.class)
     * @param entityFactory the constructor function of the class (e.g. EntityFooGolem::new). For golems with no special
     *                      abilities, use {@code GenericGolem.class}
     **/
    public Builder(final String modId, final String golemName, final Class<? extends GolemBase> entityClazz,
        final EntityType.IFactory<? extends GolemBase> entityFactory) {
      this.modid = modId;
      this.golemName = golemName;
      this.entityClass = entityClazz;
      this.entityTypeBuilder = EntityType.Builder.create(entityFactory, EntityClassification.MISC).setTrackingRange(48)
          .setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).size(1.4F, 2.9F);
    }

    /**
     * Creates the builder with the assumption that the mod id is "golems"
     *
     * @param golemName     the name of the golem (e.g. "golem_foo")
     * @param entityClazz   the class of the golem (e.g. EntityFooGolem.class)
     * @param entityFactory the constructor function of the class (e.g. EntityFooGolem::new). For golems with no special
     *                      abilities, use {@code GenericGolem.class}
     **/
    public Builder(final String golemName, final Class<? extends GolemBase> entityClazz,
        final EntityType.IFactory<? extends GolemBase> entityFactory) {
      this(ExtraGolems.MODID, golemName, entityClazz, entityFactory);
    }

    /**
     * Sets the Mod ID of the golem for registry name
     *
     * @param lModId the MODID to use to register the golem. <b>Defaults to "golems"</b>
     * @return instance to allow chaining of methods
     **/
    public Builder setModId(final String lModId) {
      this.modid = lModId;
      return this;
    }

    /**
     * Sets the max health of a golem
     *
     * @param lHealth The max health (in half hearts) of the golem. <b>Defaults to 100</b>
     * @return instance to allow chaining of methods
     **/
    public Builder setHealth(final double lHealth) {
      health = lHealth;
      return this;
    }

    /**
     * Sets the attack strength of a golem
     *
     * @param lAttack The attack strength (in half hearts) of the golem. <b>Defaults to 7</b>
     * @return instance to allow chaining of methods
     **/
    public Builder setAttack(final double lAttack) {
      attack = lAttack;
      return this;
    }

    /**
     * Sets the movement speed of a golem
     *
     * @param lMoveSpeed The move speed of the golem. <b>Defaults to 0.25D</b>
     * @return instance to allow chaining of methods
     **/
    public Builder setSpeed(final double lMoveSpeed) {
      speed = lMoveSpeed;
      return this;
    }

    /**
     * Sets the knockback resistance (heaviness) of a golem
     *
     * @param lKnockbackResist The knockback resistance of the golem. <b>Defaults to 0.4D</b>
     * @return instance to allow chaining of methods
     **/
    public Builder setKnockback(final double lKnockbackResist) {
      knockBackResist = lKnockbackResist;
      return this;
    }

    /**
     * Sets a basic texture location of a golem. If this golem inherits from one of the multi-texture golem classes, then this
     * method of setting textures is ignored. Instead, pass the correct textures in the constructor of that golem class.
     *
     * @param lTexture The texture to apply to the golem
     * @return instance to allow chaining of methods
     * @see #basicTexture()
     **/
    public Builder setTexture(final ResourceLocation lTexture) {
      basicTexture = lTexture;
      return this;
    }

    /**
     * Calls {@link #setTexture(ResourceLocation)} to set a single texture for the golem based on current values of {@code modid}
     * and {@code golemName}. For example, if the modid is {@code "golems"} and the golemName is {@code "golem_clay"} then the
     * texture location is assumed to be {@code assets/golems/textures/entity/golem_clay.png}
     *
     * @return instance to allow chaining of methods
     * @see #setModId(String)
     **/
    public Builder basicTexture() {
      return setTexture(new ResourceLocation(modid + ":textures/entity/" + golemName + ".png"));
    }

    /**
     * Prevents the golem from using the default render factory. If this is called, you must register your own
     * {@code LivingRenderer}
     * 
     * @return instance to allow chaining of methods
     **/
    public Builder hasCustomRender() {
      this.customRender = true;
      return this;
    }

    /**
     * Sets an all-purpose SoundEvent of a golem
     *
     * @param lSound The sound this golem makes when walking, attacked, or killed. Defaults to
     *               {@code SoundEvents.BLOCK_STONE_STEP}
     * @return instance to allow chaining of methods
     **/
    public Builder setSound(final SoundEvent lSound) {
      basicSound = lSound;
      return this;
    }

    /**
     * Sets the Swim Mode of a golem: SINK, FLOAT, or SWIM. <b>Defaults to SwimMode.SINK</b>
     *
     * @param mode the SwimMode to use for this golem
     * @return instance to allow chaining of methods
     * @see GolemContainer.SwimMode
     **/
    public Builder setSwimMode(GolemContainer.SwimMode mode) {
      this.swimMode = mode;
      return this;
    }

    /**
     * Adds building blocks that may be used for creating the golem. If no blocks are added via this method or the Block Tag
     * version, this golem cannot be built in-world.
     *
     * @param additionalBlocks blocks that may be used for building
     * @return instance to allow chaining of methods
     * @see #addBlocks(Tag)
     **/
    public Builder addBlocks(final Block... additionalBlocks) {
      if (additionalBlocks != null && additionalBlocks.length > 0) {
        this.validBuildingBlocks
            .addAll(Arrays.asList(additionalBlocks).stream().map(b -> b.delegate).collect(Collectors.toList()));
      }
      return this;
    }

    /**
     * Adds building blocks that may be used for creating the golem in the form of a Block Tag. If no blocks are added via this
     * method or the Block[] version, this golem cannot be built in-world.
     *
     * @param blockTag the {@code Tag<Block>} to use
     * @return instance to allow chaining of methods
     * @see #addBlocks(Block[])
     **/
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
     **/
    public Builder addSpecials(final GolemSpecialContainer... specialContainers) {
      specials.addAll(Arrays.asList(specialContainers));
      return this;
    }

    /**
     * Adds any GolemSpecialContainers to be used by the golem. If this option should be toggled (ie, a {@code Boolean}) and you
     * want an in-game description, use {@link #addSpecial(String, Boolean, String, ITextComponent)}
     *
     * @param name    a name unique to this golem's set of config options
     * @param value   the initial (default) value for this config option
     * @param comment a short description for the config file
     * @return instance to allow chaining of methods
     * @see #addSpecials(GolemSpecialContainer...)
     **/
    public Builder addSpecial(final String name, final Object value, final String comment) {
      specials.add(new GolemSpecialContainer.Builder(name, value, comment).build());
      return this;
    }

    /**
     * Adds a {@link GolemSpecialContainer} with the given values along with a {@link GolemDescription} associated with the
     * Special. Assumes the Special you are adding is a {@code Boolean} value. If this is not the case, use
     * {@link #addSpecial(String, Object, String)} to add the config and use {@link #addDesc(GolemDescription...)} to add a custom
     * description.
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
     **/
    public Builder addDesc(final GolemDescription... desc) {
      for (final GolemDescription cont : desc) {
        descriptions.add(cont);
      }
      return this;
    }

    /**
     * Associates a specific item with a percentage of health
     * that is restored by using that item on the golem
     * 
     * @param item the item
     * @param amount percentage of health that the item restores 
     * (typically 0.25 or 0.5)
     * @return instance to allow chaining of methods
     **/
    public Builder addHealItem(final Item item, final double amount) {
      healItemMap.put(item.delegate, Double.valueOf(amount));
      return this;
    }

    /**
     * Makes the golem immune to fire damage.
     *
     * @return instance to allow chaining of methods
     **/
    public Builder immuneToFire() {
      this.entityTypeBuilder = this.entityTypeBuilder.immuneToFire();
      return this;
    }

    /**
     * Makes the golem vulnerable to fall damage.
     *
     * @return instance to allow chaining of methods
     **/
    public Builder enableFallDamage() {
      this.fallDamage = true;
      return this;
    }

    /**
     * Builds the container according to values that have been set inside this Builder
     *
     * @return a copy of the newly constructed GolemContainer
     **/
    public GolemContainer build() {
      EntityType<? extends GolemBase> entityType = entityTypeBuilder.build(golemName);
      entityType.setRegistryName(modid, golemName);
      HashMap<String, GolemSpecialContainer> containerMap = new HashMap<>();
      for (GolemSpecialContainer c : specials) {
        containerMap.put(c.name, c);
      }
      return new GolemContainer(entityType, entityClass, golemName, validBuildingBlocks, validBuildingBlockTags, health, attack,
          speed, knockBackResist, fallDamage, swimMode, containerMap, descriptions, healItemMap, basicTexture, basicSound, customRender);
    }
  }

  /**
   * There are three distinct behaviors when a golem is in contact with water: <br>
   * {@code SINK} = the golem does not swim at all <br>
   * {@code FLOAT} = the golem swims on top of water <br>
   * {@code SWIM} = the golem navigates up and down in the water
   **/
  public enum SwimMode {
    SINK, FLOAT, SWIM;
  }
}
