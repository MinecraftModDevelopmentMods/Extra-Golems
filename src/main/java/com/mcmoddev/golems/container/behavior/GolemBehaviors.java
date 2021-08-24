package com.mcmoddev.golems.container.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mcmoddev.golems.ExtraGolems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class GolemBehaviors {
  
  public static final Map<ResourceLocation, Function<CompoundTag, ? extends GolemBehavior>> BEHAVIORS = new HashMap<>();
  public static final Map<ResourceLocation, Class<? extends GolemBehavior>> CLASS_MAP = new HashMap<>();

  public static final ResourceLocation AOE_DRY = register("aoe_dry", AoeDryBehavior.class, AoeDryBehavior::new);
  public static final ResourceLocation AOE_FREEZE = register("aoe_freeze", AoeFreezeBehavior.class, AoeFreezeBehavior::new);
  public static final ResourceLocation AOE_GROW = register("aoe_grow", AoeGrowBehavior.class, AoeGrowBehavior::new);
  public static final ResourceLocation BURN_IN_SUN = register("burn_in_sun", BurnInSunBehavior.class, BurnInSunBehavior::new);
  public static final ResourceLocation CHANGE_TEXTURE = register("change_texture", ChangeTextureBehavior.class, ChangeTextureBehavior::new);
  public static final ResourceLocation CRAFTING_MENU = register("crafting_menu", CraftingMenuBehavior.class, CraftingMenuBehavior::new);
  public static final ResourceLocation EXPLODE = register("explode", ExplodeBehavior.class, ExplodeBehavior::new);
  public static final ResourceLocation FOLLOW = register("follow", FollowBehavior.class, FollowBehavior::new);
  public static final ResourceLocation ON_ACTUALLY_HURT = register("hurt", OnActuallyHurtBehavior.class, OnActuallyHurtBehavior::new);
  public static final ResourceLocation ON_HURT_TARGET = register("attack", OnHurtTargetBehavior.class, OnHurtTargetBehavior::new);
  public static final ResourceLocation PLACE_BLOCKS = register("place_blocks", PlaceBlocksBehavior.class, PlaceBlocksBehavior::new);
  public static final ResourceLocation PASSIVE_EFFECT = register("passive_effect", PassiveEffectBehavior.class, PassiveEffectBehavior::new);
  public static final ResourceLocation SHOOT_ARROWS = register("shoot_arrows", ShootArrowsBehavior.class, ShootArrowsBehavior::new);
  public static final ResourceLocation SPLIT_ON_DEATH = register("split_on_death", SplitBehavior.class, SplitBehavior::new);
  public static final ResourceLocation TELEPORT = register("teleport", TeleportBehavior.class, TeleportBehavior::new);
  public static final ResourceLocation TEMPT = register("tempt", TemptBehavior.class, TemptBehavior::new);
  public static final ResourceLocation USE_FUEL = register("use_fuel", UseFuelBehavior.class, UseFuelBehavior::new);
  
  private GolemBehaviors() { }
  
  // Required to classload on both server and client
  public static void init() { }
  
  public static Optional<? extends GolemBehavior> create(final ResourceLocation name, final CompoundTag tag) {
    if(BEHAVIORS.containsKey(name)) {
      return Optional.of(BEHAVIORS.get(name).apply(tag));
    }
    return Optional.empty();
  }
  
  private static <T extends GolemBehavior> ResourceLocation register(final String name, final Class<T> clazz, final Function<CompoundTag, T> function) {
    final ResourceLocation id = new ResourceLocation(ExtraGolems.MODID, name);
    BEHAVIORS.put(id, function);
    CLASS_MAP.put(id, clazz);
    return id;
  }

}
