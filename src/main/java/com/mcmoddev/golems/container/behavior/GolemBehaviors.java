package com.mcmoddev.golems.container.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mcmoddev.golems.ExtraGolems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class GolemBehaviors {
  
  public static final Map<ResourceLocation, Function<CompoundTag, GolemBehavior>> BEHAVIORS = new HashMap<>();

  public static final ResourceLocation AOE_DRY = register("aoe_dry", AoeDryBehavior::new);
  public static final ResourceLocation AOE_FREEZE = register("aoe_freeze", AoeFreezeBehavior::new);
  public static final ResourceLocation ARROWS = register("arrows", null); // TODO
  public static final ResourceLocation CRAFTING_MENU = register("crafting_menu", CraftingMenuBehavior::new);
  public static final ResourceLocation EXPLODE = register("explode", ExplodeBehavior::new);
  public static final ResourceLocation ON_ACTUALLY_HURT = register("hurt", OnActuallyHurtBehavior::new);
  public static final ResourceLocation ON_HURT_TARGET = register("attack", OnHurtTargetBehavior::new);
  public static final ResourceLocation PLACE_BLOCKS = register("place_blocks", PlaceBlocksBehavior::new);
  public static final ResourceLocation PASSIVE_EFFECT = register("passive_effect", PassiveEffectBehavior::new);
  public static final ResourceLocation SPLIT_ON_DEATH = register("split_on_death", SplitBehavior::new);
  public static final ResourceLocation TELEPORT = register("teleport", TeleportBehavior::new);
  public static final ResourceLocation USE_FUEL = register("use_fuel", UseFuelBehavior::new);
  
  private GolemBehaviors() { }
  
  // Required to classload on both server and client
  public static void init() { }
  
  public static Optional<GolemBehavior> create(final ResourceLocation name, final CompoundTag tag) {
    if(BEHAVIORS.containsKey(name)) {
      return Optional.of(BEHAVIORS.get(name).apply(tag));
    }
    return Optional.empty();
  }
  
  private static ResourceLocation register(final String name, final Function<CompoundTag, GolemBehavior> function) {
    final ResourceLocation id = new ResourceLocation(ExtraGolems.MODID, name);
    BEHAVIORS.put(id, function);    
    return id;
  }

}
