package com.mcmoddev.golems.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.events.GolemContainerBuildEvent;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemRenderSettings.IColorProvider;
import com.mcmoddev.golems.util.GolemRenderSettings.ILightingProvider;
import com.mcmoddev.golems.util.GolemRenderSettings.ITextureProvider;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.IRegistryDelegate;

/**
 * This class stores characteristics and other vital information
 * about a single golem. These attributes are then referenced from
 * {@link GolemBase} as needed.
 * Must be created using the {@link GolemContainer.Builder}.
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 **/
@SuppressWarnings("rawtypes")
public final class GolemContainer {

  private final Class<? extends GolemBase> entityClass;
  private final Set<IRegistryDelegate<Block>> validBuildingBlocks;
  private final Set<ResourceLocation> validBuildingBlockTags;
  private final EntityType<? extends GolemBase> entityType;
  private final String name;
  private final GolemRenderSettings renderSettings;
  private final SoundEvent basicSound;
  private final boolean fallDamage;
  private final boolean explosionImmunity;
  private final SwimMode swimMode;
  private final boolean canInteractChangeTexture;
  private final boolean noGolemBookEntry;

  private double health;
  private double attack;

  private final double speed;
  private final double knockbackResist;
  private final int lightLevel;
  private final int powerLevel;
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
   * @param lRenderSettings         a GolemRenderSettings instance
   * @param lValidBuildingBlocks    a List of block delegates to build the golem
   * @param lValidBuildingBlockTags a List of Block Tags to build the golem
   * @param lHealth                 base health value
   * @param lAttack                 base attack value
   * @param lSpeed                  base speed value
   * @param lKnockbackResist        base knockback resistance
   * @param lLightLevel             a static light level emitted by the golem, if any
   * @param lFallDamage             whether or not the golem can take fall damage
   * @param lExplosionImmunity      whether or not the golem can take explosion damage
   * @param lSwimMode               whether or not the golem floats in water
   * @param lSpecialContainers      any golem specials as a Map
   * @param lDesc                   any special descriptions for the golem
   * @param lHealItemMap            a map of items and their corresponding heal amounts
   * @param lBasicSound             a default SoundEvent to use for the golem
   **/
  private GolemContainer(final EntityType<? extends GolemBase> lEntityType, final Class<? extends GolemBase> lEntityClass,
      final String lPath, final GolemRenderSettings lRenderSettings, final Set<IRegistryDelegate<Block>> lValidBuildingBlocks,
      final Set<ResourceLocation> lValidBuildingBlockTags, final double lHealth, final double lAttack, final double lSpeed,
      final double lKnockbackResist, final int lLightLevel, final int lPowerLevel, final boolean lFallDamage, 
      final boolean lExplosionImmunity, final SwimMode lSwimMode, final HashMap<String, GolemSpecialContainer> lSpecialContainers, 
      final List<GolemDescription> lDesc, final Map<IRegistryDelegate<Item>, Double> lHealItemMap,
      final SoundEvent lBasicSound, final boolean lNoGolemBookEntry) {
    this.entityType = lEntityType;
    this.entityClass = lEntityClass;
    this.renderSettings = lRenderSettings;
    this.validBuildingBlocks = lValidBuildingBlocks;
    this.validBuildingBlockTags = lValidBuildingBlockTags;
    this.name = lPath;
    this.health = lHealth;
    this.attack = lAttack;
    this.speed = lSpeed;
    this.knockbackResist = lKnockbackResist;
    this.lightLevel = lLightLevel;
    this.powerLevel = lPowerLevel;
    this.fallDamage = lFallDamage;
    this.explosionImmunity = lExplosionImmunity;
    this.swimMode = lSwimMode;
    this.specialContainers = ImmutableMap.copyOf(lSpecialContainers);
    this.descContainers = ImmutableList.copyOf(lDesc);
    this.healItemMap = ImmutableMap.copyOf(lHealItemMap);
    this.basicSound = lBasicSound;
    this.noGolemBookEntry = lNoGolemBookEntry;
    
    this.canInteractChangeTexture = (GolemMultiTextured.class.isAssignableFrom(lEntityClass));
  }

  /**
   * Called by various in-game info tools, such as the Golem Book and WAILA /
   * HWYLA. Adds this golem's description(s) to the given List as specified by
   * {@link GolemDescription#addDescription(List, GolemContainer)}.
   *
   * @param list a List that may or may not contain other descriptions already.
   **/
  public void addDescription(final List<MutableComponent> list) {
    // ADD FIREPROOF TIP
    if (this.entityType.fireImmune()) {
      list.add(new TranslatableComponent("enchantment.minecraft.fire_protection").withStyle(ChatFormatting.GOLD));
    }
    // ADD EXPLOSION-PROOF TIP
    if (this.explosionImmunity) {
      list.add(new TranslatableComponent("enchantment.minecraft.blast_protection").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
    }
    // ADD INTERACT-TEXTURE TIP
    if (ExtraGolemsConfig.enableTextureInteract() && this.canInteractChangeTexture) {
      list.add(new TranslatableComponent("entitytip.click_change_texture").withStyle(ChatFormatting.BLUE));
    }
    // ADD SWIMMING TIP
    if(this.swimMode == SwimMode.SWIM) {
      list.add(new TranslatableComponent("entitytip.advanced_swim").withStyle(ChatFormatting.AQUA));
    }
    // ADD ALL OTHER DESCRIPTIONS
    for (final GolemDescription desc : descContainers) {
      desc.addDescription(list, this);
    }
  }

  /**
   * @return True if there is at least one valid Block or Block Tag which can be
   *         used to build this golem
   **/
  public boolean hasBuildingBlock() {
    return !this.validBuildingBlocks.isEmpty() || !this.validBuildingBlockTags.isEmpty();
  }

  /**
   * @return a Set of all possible Blocks that can be used to build the golem.
   *         Does not contain duplicates but may be empty.
   * @see #hasBuildingBlock()
   **/
  public Set<Block> getBuildingBlocks() {
    // make set of all blocks including tags (run-time only)
    Set<Block> blocks = validBuildingBlocks.isEmpty() ? new HashSet<>() : validBuildingBlocks.stream().map(d -> d.get()).collect(Collectors.toSet());
    for (final Tag<Block> tag : loadTags(validBuildingBlockTags)) {
      blocks.addAll(tag.getValues());
    }
    blocks.remove(Blocks.AIR);
    return blocks;
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
   * Returns a single Block that can be used for this golem. It is not guaranteed
   * that there is a Block or that it is the most easily obtainable by the player,
   * it's simply the first element in the set of building blocks.
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
   * Allows additional blocks to be registered as "valid" in order to build this
   * golem. Useful especially for add-ons. If you're using this in your mod to
   * change your own golems, please use
   * {@link GolemContainer.Builder#addBlocks(Block...)}
   *
   * @param additional Block objects to register as "valid"
   * @return if the blocks were added successfully
   **/
  public boolean addBlocks(@Nonnull final Block... additional) {
    return additional.length > 0
        && this.validBuildingBlocks.addAll(Arrays.asList(additional).stream().map(d -> d.delegate).collect(Collectors.toList()));
  }

  /**
   * Allows additional Block Tags to be registered as "valid" in order to build
   * this golem. Useful especially for add-ons. If you're using this in your mod
   * to change your own golems, please use
   * {@link GolemContainer.Builder#addBlocks(Tag)} instead
   *
   * @param additional Block Tag to register as "valid"
   * @return if the Block Tag was added successfully
   **/
  public boolean addBlocks(@Nonnull final Tag.Named<Block> additional) {
    return this.validBuildingBlockTags.add(additional.getName());
  }

  /**
   * Required for correctly loading tags - they must be called as needed and can
   * not be stored or queried before they are properly loaded and reloaded.
   *
   * @param rls a Collection of ResourceLocation IDs that represent Block Tags.
   * @return a current Collection of Block Tags
   **/
  private static Collection<Tag<Block>> loadTags(final Collection<ResourceLocation> rls) {
    final Collection<Tag<Block>> tags = new HashSet<>();
    for (final ResourceLocation rl : rls) {
      if (BlockTags.getAllTags().getTag(rl) != null) {
        Tag<Block> tag = BlockTags.getAllTags().getTag(rl);
        if(tag != null) {
          tags.add(tag);
        }
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
   * <strong>DO NOT CALL</strong> unless you are the config!
   * 
   * @param pHealth new 'health' value
   **/
  public void setHealth(final double pHealth) {
    this.health = pHealth;
  }

  /**
   * <strong>DO NOT CALL</strong> unless you are the config!
   * 
   * @param pAttack new 'attack' value
   **/
  public void setAttack(final double pAttack) {
    this.attack = pAttack;
  }

  /**
   * <strong>DO NOT CALL</strong> unless you are the config!
   * 
   * @param pEnabled new 'enabled' value
   **/
  public void setEnabled(final boolean pEnabled) {
    this.enabled = pEnabled;
  }
  
  ////////// GETTERS //////////

  /** @return the base class used by the Golem. Not always unique. **/
  public Class<? extends GolemBase> getEntityClass() { return this.entityClass; }

  /** @return the EntityType of the Golem. Always unique. **/
  public EntityType<? extends GolemBase> getEntityType() { return this.entityType; }

  /** @return a unique ResourceLocation ID for the Golem. Always unique. **/
  public ResourceLocation getRegistryName() { return this.entityType.getRegistryName(); }
  
  /** @return the render settings for this golem **/
  public GolemRenderSettings getRenderSettings() { return this.renderSettings; }
 
  /** @return a default SoundEvent to play when the Golem moves or is attacked **/
  public SoundEvent getSound() { return this.basicSound; }

  /** @return the name of the Golem as specified in the Builder **/
  public String getName() { return this.name; }

  /** @return the Golem's default health. Mutable. **/
  public double getHealth() { return this.health; }

  /** @return the Golem's default attack power. Mutable. **/
  public double getAttack() { return this.attack; }

  /** @return the Golem's default move speed. Immutable. **/
  public double getSpeed() { return this.speed; }

  /** @return the Golem's default knockback resistance. Immutable. **/
  public double getKnockbackResist() { return this.knockbackResist; }
  
  /** @return the Golem's light level. Immutable. **/
  public int getLightLevel() { return this.lightLevel; }
  
  /** @return the Golem's redstone power level. Immutable. **/
  public int getPowerLevel() { return this.powerLevel; }

  /** @return true if the Golem is enabled by the config settings. Mutable. **/
  public boolean isEnabled() { return this.enabled; }

  /** @return true if the Golem takes damage upon falling from heights **/
  public boolean takesFallDamage() { return this.fallDamage; }
  
  /** @return true if the Golem takes damage from explosions **/
  public boolean isImmuneToExplosions() { return this.explosionImmunity; }

  /** @return true if the Golem can swim on top of water **/
  public boolean canSwim() { return this.swimMode == SwimMode.FLOAT; }

  /** @return the {@link SwimMode} of the Golem **/
  public SwimMode getSwimMode() { return this.swimMode; }
  
  /** @return true if the Golem should not appear in the golem guide book **/
  public boolean noGolemBookEntry() { return this.noGolemBookEntry; }
  
  /** @return a new attribute map supplier for the Golem **/
  public Supplier<AttributeSupplier.Builder> getAttributeSupplier() {
    return () -> Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, this.health)
         .add(Attributes.MOVEMENT_SPEED, this.speed)
         .add(Attributes.KNOCKBACK_RESISTANCE, this.knockbackResist)
         .add(Attributes.ATTACK_DAMAGE, this.attack);
   }

  //////////////////////////////////////////////////////////////
  /////////////////// END OF GOLEM CONTAINER ///////////////////
  //////////////////////////////////////////////////////////////

  /**
   * This class is my own work
   * <p>
   * Use this class to build GolemContainer objects that can be registered to the
   * {@link GolemRegistrar}
   *
   * @author Glitch
   */
  public static final class Builder {
    private final String golemName;
    private final Class<? extends GolemBase> entityClass;
    private EntityType.Builder<? extends GolemBase> entityTypeBuilder;

    private SoundEvent basicSound = SoundEvents.STONE_STEP;

    private String modid = ExtraGolems.MODID;
    private double health = 100.0D;
    private double attack = 7.0D;
    private double speed = 0.25D;
    private double knockBackResist = 0.4D;
    private int lightLevel = 0;
    private int powerLevel = 0;
    private boolean fallDamage = false;
    private boolean explosionImmunity = false;
    private boolean noGolemBookEntry = false;
    private SwimMode swimMode = SwimMode.SINK;
    private Set<IRegistryDelegate<Block>> validBuildingBlocks = new HashSet<>();
    private Set<ResourceLocation> validBuildingBlockTags = new HashSet<>();
    private List<GolemSpecialContainer> specials = new ArrayList<>();
    private List<GolemDescription> descriptions = new ArrayList<>();
    private final Map<IRegistryDelegate<Item>, Double> healItemMap = new HashMap<>();
    
    // render settings
    private GolemRenderSettings customSettings = null;
    
    private boolean hasCustomRender = false;
    private boolean hasTransparency = false;
    private boolean hasPrefabTexture = false;
    private boolean hasVinesTexture = true;
    private boolean hasColor = false;
    private boolean doVinesGlow = false;
    private boolean doEyesGlow = false;
    
    private ITextureProvider dynamicTextureProvider = g -> GolemRenderSettings.FALLBACK_BLOCK;
    private ITextureProvider prefabTextureProvider = g -> GolemRenderSettings.FALLBACK_PREFAB;
    private ITextureProvider vinesTextureProvider = g -> GolemRenderSettings.FALLBACK_VINES;
    private ITextureProvider eyesTextureProvider = g -> GolemRenderSettings.FALLBACK_EYES;

    private IColorProvider textureColorProvider = g -> 0;
    private IColorProvider vinesColorProvider = g -> GolemRenderSettings.VINES_COLOR;
    
    private ILightingProvider textureGlowProvider = g -> g.isProvidingLight();

    /**
     * Creates the builder
     *
     * @param golemName     the name of the golem (e.g. "golem_foo")
     * @param entityClazz   the class of the golem (e.g. EntityFooGolem.class)
     * @param entityFactory the constructor enabled of the class (e.g.
     *                      EntityFooGolem::new). For golems with no special
     *                      abilities, use {@code GenericGolem.class}
     **/
    public Builder(final String golemName, final Class<? extends GolemBase> entityClazz,
        final EntityType.EntityFactory<? extends GolemBase> entityFactory) {
      this.golemName = golemName;
      this.entityClass = entityClazz;
      this.entityTypeBuilder = EntityType.Builder.of(entityFactory, MobCategory.MISC).setTrackingRange(48).setUpdateInterval(3)
          .setShouldReceiveVelocityUpdates(true).sized(1.4F, 2.9F);
    }

    /**
     * Sets the Mod ID of the golem for registry name
     *
     * @param lModId the MODID to use to register the golem. <strong>Defaults to "golems"</strong>
     * @return instance to allow chaining of methods
     **/
    public Builder setModId(final String lModId) {
      this.modid = lModId;
      return this;
    }

    /**
     * Sets the max health of a golem
     *
     * @param lHealth The max health (in half hearts) of the golem. <strong>Defaults to 100</strong>
     * @return instance to allow chaining of methods
     **/
    public Builder setHealth(final double lHealth) {
      health = lHealth;
      return this;
    }

    /**
     * Sets the attack strength of a golem
     *
     * @param lAttack The attack strength (in half hearts) of the golem. <strong>Defaults to 7</strong>
     * @return instance to allow chaining of methods
     **/
    public Builder setAttack(final double lAttack) {
      attack = lAttack;
      return this;
    }

    /**
     * Sets the movement speed of a golem
     *
     * @param lMoveSpeed The move speed of the golem. <strong>Defaults to 0.25D</strong>
     * @return instance to allow chaining of methods
     **/
    public Builder setSpeed(final double lMoveSpeed) {
      speed = lMoveSpeed;
      return this;
    }

    /**
     * Sets the knockback resistance (heaviness) of a golem
     *
     * @param lKnockbackResist The knockback resistance of the golem. <strong>Defaults to 0.4D</strong>
     * @return instance to allow chaining of methods
     **/
    public Builder setKnockbackResist(final double lKnockbackResist) {
      knockBackResist = lKnockbackResist;
      return this;
    }
    
    /**
     * Sets a static light level to be emitted by a golem
     *
     * @param lLightLevel The light emitted by the golem from 0 to 15. <strong>Defaults to 0</strong>
     * @return instance to allow chaining of methods
     **/
    public Builder setLightLevel(final int lLightLevel) {
      lightLevel = Mth.clamp(lLightLevel, 0, 15);
      if(lightLevel > 0) {
        addSpecial(GolemBase.ALLOW_LIGHT, true, "Whether the golem can emit light");
      }
      return this;
    }
    
   /**
    * Sets a static redstone power level to be emitted by a golem
    *
    * @param lPowerLevel The power emitted by the golem from 0 to 15. <strong>Defaults to 0</strong>
    * @return instance to allow chaining of methods
    **/
   public Builder setPowerLevel(final int lPowerLevel) {
     powerLevel = Mth.clamp(lPowerLevel, 0, 15);
     if(powerLevel > 0) {
       addSpecial(GolemBase.ALLOW_POWER, true, "Whether the golem can emit power");
     }
     return this;
   }

   /**
    * Prevents the golem from using the default render factory. If this is called,
    * you must register your own {@code LivingRenderer}. <strong>Defaults to false</strong>
    * 
    * @return instance to allow chaining of methods
    **/
   public Builder hasCustomRender() {
     this.hasCustomRender = true;
     return this;
   }
   
   /**
    * Sets a pre-made GolemRenderSettings to use. <strong>It is highly recommended to
    * use the helper methods instead</strong>, but this method is here for flexibility.
    *
    * @param renderSettings A pre-built render settings class to use
    * @return instance to allow chaining of methods
    * @see #setDynamicTexture(ITextureProvider)
    * @see #setStaticTexture(ITextureProvider)
    * @see #setTextureColor(IColorProvider)
    * @see #noLighting()
    * @see #setVinesProvider(ITextureProvider)
    * @see #noVinesLighting()
    * @see #setVinesColor(IColorProvider)
    * @see #noVines()
    * @see #setEyesProvider(ITextureProvider)
    * @see #noEyesLighting()
    **/
   public Builder setRenderSettings(final GolemRenderSettings renderSettings) {
     customSettings = renderSettings;
     return this;
   }

    /**
     * Sets a prefabricated texture location for the golem.
     * The vines layer will still render unless it is
     * specifically disabled using {@link #noVines()}.
     * Prefabricated textures can still be colored using
     * {@link #setTextureColor(IColorProvider)}.
     *
     * @param prefab The texture provider to use for the golem
     * @return instance to allow chaining of methods
     **/
    public Builder setStaticTexture(final GolemRenderSettings.ITextureProvider prefab) {
      hasPrefabTexture = true;
      prefabTextureProvider = prefab;
      return this;
    }

    /**
     * Defines a dynamic (block-based) texture location for the golem by
     * wrapping the given modid and block texture name into a ResourceLocation at
     * {@code minecraft:textures/block/[blockTexture].png}
     * @param blockTexture the name of the texture file, not including .png extension
     * @return instance to allow chaining of methods
     * @see #setDynamicTexture(String, String)
     **/
    public Builder setDynamicTexture(final String blockTexture) {
      return setDynamicTexture("minecraft", blockTexture);
    }
    
    /**
     * Defines a dynamic (block-based) texture location for the golem by
     * wrapping the given modid and block texture name into a ResourceLocation at
     * {@code [modid]:textures/block/[blockTexture].png}
     * @param modid the namespace of the texture file
     * @param blockTexture the name of the texture file, not including .png extension
     * @return instance to allow chaining of methods
     * @see #setDynamicTexture(ITextureProvider)
     **/
    public Builder setDynamicTexture(final String modid, final String blockTexture) {
      final ResourceLocation texture = new ResourceLocation(modid, "textures/block/" + blockTexture + ".png");
      return setDynamicTexture(g -> texture);
    }

    /**
     * Defines a dynamic (block-based) texture location for the golem.
     * 
     * @param texture the dynamic texture provider
     * @return instance to allow chaining of methods
     * @see #setDynamicTexture(String)
     * @see #setDynamicTexture(String, String)
     **/
    public Builder setDynamicTexture(final GolemRenderSettings.ITextureProvider texture) {
      hasPrefabTexture = false;
      dynamicTextureProvider = texture;
      return this;
    }
    
    /**
     * Defines a color provider for the golem texture.
     *
     * @param textureColorer The color provider to use
     * @return instance to allow chaining of methods
     **/
    public Builder setTextureColor(final GolemRenderSettings.IColorProvider textureColorer) {
      hasColor = true;
      textureColorProvider = textureColorer;
      return this;
    }
    
    /**
     * Indicates that the texture should be rendered
     * with transparency enabled.
     * @return instance to allow chaining of methods
     **/
    public Builder transparent() {
      hasTransparency = true;
      return this;
    }

    /**
     * Disables the vines layer on the texture.
     * @return instance to allow chaining of methods
     **/
    public Builder noVines() {
      hasVinesTexture = false;
      doVinesGlow = false;
      return this;
    }
    
    /**
     * Defines a texture provider for the vines layer.
     * @param vines the texture provider
     * @return instance to allow chaining of methods
     **/
    public Builder setVinesProvider(final GolemRenderSettings.ITextureProvider vines) {
      hasVinesTexture = true;
      vinesTextureProvider = vines;
      return this;
    }
    
    /**
     * Defines a color provider for the vines layer.
     * Vines are grayscale but use color {@code 0x83a05a} by default.
     * @param vinesColorer the color provider
     * @return instance to allow chaining of methods
     **/
    public Builder setVinesColor(final GolemRenderSettings.IColorProvider vinesColorer) {
      vinesColorProvider = vinesColorer;
      return this;
    }

    /**
     * Defines a texture provider for the eyes layer.
     * @param eyes the texture provider
     * @return instance to allow chaining of methods
     **/
    public Builder setEyesProvider(final GolemRenderSettings.ITextureProvider eyes) {
      eyesTextureProvider = eyes;
      return this;
    }
    
    /**
     * Disables lighting for the vines layer.
     * @return instance to allow chaining of methods
     **/
    public Builder noVinesLighting() {
      doVinesGlow = true;
      return this;
    }
    
    /**
     * Disables lighting on the eyes layer.
     * @return instance to allow chaining of methods
     **/
    public Builder noEyesLighting() {
      doEyesGlow = true;
      return this;
    }
     
    /**
     * Disables lighting for the base texture. This is applied
     * for dynamic as well as prefabricated textures.
     * @return instance to allow chaining of methods
     * @see #noLighting(ILightingProvider)
     **/
    public Builder noLighting() {
      return noLighting(g -> true);
    }
    
    /**
     * Sets the lighting for the base texture. This is applied
     * for dynamic as well as prefabricated textures.
     * @param noLighting the lighting provider to apply
     * @return instance to allow chaining of methods
     **/
    public Builder noLighting(final GolemRenderSettings.ILightingProvider noLighting) {
      textureGlowProvider = noLighting;
      return this;
    }

    /**
     * Sets an all-purpose SoundEvent of a golem
     *
     * @param lSound The sound this golem makes when walking, attacked, or killed.
     *               Defaults to {@code SoundEvents.BLOCK_STONE_STEP}
     * @return instance to allow chaining of methods
     **/
    public Builder setSound(final SoundEvent lSound) {
      basicSound = lSound;
      return this;
    }

    /**
     * Sets the Swim Mode of a golem: SINK, FLOAT, or SWIM. <strong>Defaults to
     * SwimMode.SINK</strong>
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
     * Adds building blocks that may be used for creating the golem. If no blocks
     * are added via this method or the Block Tag version, this golem cannot be
     * built in-world.
     *
     * @param additionalBlocks blocks that may be used for building
     * @return instance to allow chaining of methods
     * @see #addBlocks(net.minecraft.tags.ITag.INamedTag)
     **/
    public Builder addBlocks(final Block... additionalBlocks) {
      if (additionalBlocks != null && additionalBlocks.length > 0) {
        this.validBuildingBlocks.addAll(Arrays.asList(additionalBlocks).stream().map(b -> b.delegate).collect(Collectors.toSet()));
      }
      return this;
    }

    /**
     * Adds building blocks that may be used for creating the golem in the form of a
     * Block Tag. If no blocks are added via this method or the Block[] version,
     * this golem cannot be built in-world.
     *
     * @param blockTag the {@code Tag<Block>} to use
     * @return instance to allow chaining of methods
     * @see #addBlocks(Block...)
     **/
    public Builder addBlocks(final Tag.Named<Block> blockTag) {
      this.validBuildingBlockTags.add(blockTag.getName());
      return this;
    }
    
    /**
     * Adds building blocks that may be used for creating the golem in the form of a
     * Block Tag. If no blocks are added via this method or the Block[] version,
     * this golem cannot be built in-world.
     *
     * @param blockTag the {@code Tag<Block>} to use
     * @return instance to allow chaining of methods
     * @see #addBlocks(Block[])
     **/
    public Builder addBlocks(final ResourceLocation blockTag) {
      this.validBuildingBlockTags.add(blockTag);
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
     * Adds any GolemSpecialContainers to be used by the golem. If this option
     * should be toggled (ie, a {@code Boolean}) and you want an in-game
     * description, use {@link #addSpecial(String, Boolean, String, IFormattableTextComponent)}
     *
     * @param name    a name unique to this golem's set of config options
     * @param value   the initial (default) value for this config option
     * @param comment a short description for the config file
     * @return instance to allow chaining of methods
     * @see #addSpecials(GolemSpecialContainer...)
     **/
    public <T> Builder addSpecial(final String name, final T value, final String comment) {
      specials.add(new GolemSpecialContainer.Builder<T>(name, value, comment).build());
      return this;
    }

    /**
     * Adds a {@link GolemSpecialContainer} with the given values along with a
     * {@link GolemDescription} associated with the Special. Assumes the Special you
     * are adding is a {@code Boolean} value. If this is not the case, use
     * {@link #addSpecial(String, Object, String)} to add the config and use
     * {@link #addDesc(GolemDescription...)} to add a custom description.
     *
     * @param name    a name unique to this golem's set of config options
     * @param value   the initial (default) value for this config option
     * @param comment a short description for the config file
     * @param desc    a fancier description to be used in-game
     * @return instance to allow chaining of methods
     **/
    public Builder addSpecial(final String name, final Boolean value, final String comment, final MutableComponent desc) {
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
      this.entityTypeBuilder = this.entityTypeBuilder.fireImmune();
      return this;
    }
    
    /**
     * Makes the golem immune to explosion damage.
     *
     * @return instance to allow chaining of methods
     **/
    public Builder immuneToExplosions() {
      this.explosionImmunity = true;
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
     * Prevents this golem from contributing an entry
     * to the golem guide book
     *
     * @return instance to allow chaining of methods
     **/
    public Builder noGolemBookEntry() {
      this.noGolemBookEntry = true;
      return this;
    }

    /**
     * Builds the container according to values that have been set inside this
     * Builder
     *
     * @return a copy of the newly constructed GolemContainer
     **/
    public GolemContainer build() {
      // post event in case listeners want to change anything
      final ResourceLocation name = new ResourceLocation(modid, golemName);
      final GolemContainerBuildEvent event = new GolemContainerBuildEvent(name, this, validBuildingBlocks, validBuildingBlockTags);
      MinecraftForge.EVENT_BUS.post(event);
      // build the entity type
      EntityType<? extends GolemBase> entityType = entityTypeBuilder.build(golemName);
      entityType.setRegistryName(name);
      // add specials to the container
      HashMap<String, GolemSpecialContainer> containerMap = new HashMap<>();
      for (GolemSpecialContainer c : specials) {
        containerMap.put(c.name, c);
      }
      // build the render settings
      final GolemRenderSettings renderSettings = customSettings != null ? customSettings : new GolemRenderSettings(hasCustomRender, hasTransparency, textureGlowProvider, dynamicTextureProvider, 
          hasVinesTexture, doVinesGlow, vinesTextureProvider, doEyesGlow, eyesTextureProvider, hasPrefabTexture, prefabTextureProvider, hasColor, textureColorProvider, vinesColorProvider);
      // build the golem container
      return new GolemContainer(entityType, entityClass, golemName, renderSettings, validBuildingBlocks, validBuildingBlockTags, health, attack, speed,
          knockBackResist, lightLevel, powerLevel, fallDamage, explosionImmunity, swimMode, containerMap, descriptions, healItemMap, 
          basicSound, noGolemBookEntry);
    }
  }

  /**
   * There are three distinct behaviors when a golem is in contact with water:
   * <br>
   * {@code SINK} = the golem does not swim at all <br>
   * {@code FLOAT} = the golem swims on top of water <br>
   * {@code SWIM} = the golem navigates up and down in the water
   **/
  public static enum SwimMode {
    SINK, FLOAT, SWIM;
  }
}
