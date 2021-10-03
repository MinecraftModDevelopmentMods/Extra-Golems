package com.mcmoddev.golems.golem_stats.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;


public final class GolemBehaviors {
  
  /** Behavior registry map **/
  public static final Map<ResourceLocation, Function<CompoundNBT, ? extends GolemBehavior>> BEHAVIORS = new HashMap<>();
  /** Behavior registry name and class lookup **/
  public static final Map<ResourceLocation, Class<? extends GolemBehavior>> CLASS_MAP = new HashMap<>();

  /** The AoeDryBehavior key **/
  public static final ResourceLocation AOE_DRY = register("aoe_dry", AoeDryBehavior.class, AoeDryBehavior::new);
  /** The AoeFreezeBehavior key **/
  public static final ResourceLocation AOE_FREEZE = register("aoe_freeze", AoeFreezeBehavior.class, AoeFreezeBehavior::new);
  /** The AoeGrowBehavior key **/
  public static final ResourceLocation AOE_GROW = register("aoe_grow", AoeGrowBehavior.class, AoeGrowBehavior::new);
  /** The BurnInSunBehavior key **/
  public static final ResourceLocation BURN_IN_SUN = register("burn_in_sun", BurnInSunBehavior.class, BurnInSunBehavior::new);
  /** The ChangeTextureBehavior key **/
  public static final ResourceLocation CHANGE_TEXTURE = register("change_texture", ChangeTextureBehavior.class, ChangeTextureBehavior::new);
  /** The CraftingMenuBehavior key **/
  public static final ResourceLocation CRAFTING_MENU = register("crafting_menu", CraftingMenuBehavior.class, CraftingMenuBehavior::new);
  /** The ExplodeBehavior key **/
  public static final ResourceLocation EXPLODE = register("explode", ExplodeBehavior.class, ExplodeBehavior::new);
  /** The FollowBehavior key **/
  public static final ResourceLocation FOLLOW = register("follow", FollowBehavior.class, FollowBehavior::new);
  /** The OnActuallyHurtBehavior key **/
  public static final ResourceLocation ON_ACTUALLY_HURT = register("hurt", OnActuallyHurtBehavior.class, OnActuallyHurtBehavior::new);
  /** The OnHurtTargetBehavior key **/
  public static final ResourceLocation ON_HURT_TARGET = register("attack", OnHurtTargetBehavior.class, OnHurtTargetBehavior::new);
  /** The PlaceBlocksBehavior key **/
  public static final ResourceLocation PLACE_BLOCKS = register("place_blocks", PlaceBlocksBehavior.class, PlaceBlocksBehavior::new);
  /** The PassiveEffectBehavior key **/
  public static final ResourceLocation PASSIVE_EFFECT = register("passive_effect", PassiveEffectBehavior.class, PassiveEffectBehavior::new);
  /** The ShootArrowsBehavior key **/
  public static final ResourceLocation SHOOT_ARROWS = register("shoot_arrows", ShootArrowsBehavior.class, ShootArrowsBehavior::new);
  /** The SplitBehavior key **/
  public static final ResourceLocation SPLIT_ON_DEATH = register("split_on_death", SplitBehavior.class, SplitBehavior::new);
  /** The TeleportBehavior key **/
  public static final ResourceLocation TELEPORT = register("teleport", TeleportBehavior.class, TeleportBehavior::new);
  /** The TemptBehavior key **/
  public static final ResourceLocation TEMPT = register("tempt", TemptBehavior.class, TemptBehavior::new);
  /** The UseFuelBehavior key **/
  public static final ResourceLocation USE_FUEL = register("use_fuel", UseFuelBehavior.class, UseFuelBehavior::new);
  
  private GolemBehaviors() { }
  
  // Required to classload on both server and client
  public static void init() { }
  
  public static Optional<? extends GolemBehavior> create(final ResourceLocation name, final CompoundNBT tag) {
    if(BEHAVIORS.containsKey(name)) {
      return Optional.of(BEHAVIORS.get(name).apply(tag));
    }
    return Optional.empty();
  }
  
  private static <T extends GolemBehavior> ResourceLocation register(final String name, final Class<T> clazz, final Function<CompoundNBT, T> function) {
    final ResourceLocation id = new ResourceLocation(ExtraGolems.MODID, name);
    BEHAVIORS.put(id, function);
    CLASS_MAP.put(id, clazz);
    return id;
  }

}
