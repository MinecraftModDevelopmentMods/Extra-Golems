package com.mcmoddev.golems.util.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class GolemBehaviors {
  
  public static final ImmutableMap<ResourceLocation, Function<CompoundTag, GolemBehavior>> BEHAVIORS;

  public static final ResourceLocation AOE_DRY = new ResourceLocation("aoe_dry");
  public static final ResourceLocation AOE_FREEZE = new ResourceLocation("aoe_freeze");
  public static final ResourceLocation ARROWS = new ResourceLocation("arrows");
  public static final ResourceLocation EXPLODE = new ResourceLocation("explode");
  public static final ResourceLocation ON_ACTUALLY_HURT = new ResourceLocation("hurt");
  public static final ResourceLocation ON_HURT_TARGET = new ResourceLocation("attack");
  public static final ResourceLocation PLACE_BLOCKS = new ResourceLocation("place_blocks");
  public static final ResourceLocation PASSIVE_EFFECT = new ResourceLocation("passive_effect");
  public static final ResourceLocation SPLIT_ON_DEATH = new ResourceLocation("split");
  public static final ResourceLocation TELEPORT = new ResourceLocation("teleport");
  public static final ResourceLocation USE_FUEL = new ResourceLocation("use_fuel");
  
  static {
    Map<ResourceLocation, Function<CompoundTag, GolemBehavior>> goals = new HashMap<>();
    goals.put(ON_ACTUALLY_HURT, tag -> new OnActuallyHurtBehavior(tag));
    goals.put(ON_HURT_TARGET, tag -> new OnHurtTargetBehavior(tag));
    goals.put(PLACE_BLOCKS, tag -> new PlaceBlocksBehavior(tag));
    goals.put(PASSIVE_EFFECT, tag -> new PassiveEffectBehavior(tag));
    goals.put(SPLIT_ON_DEATH, tag -> new SplitBehavior(tag));
    
    goals.put(USE_FUEL, tag -> new UseFuelBehavior(tag));
    BEHAVIORS = ImmutableMap.copyOf(goals);
  }
  
  private GolemBehaviors() { }
  
  public static GolemBehavior create(final ResourceLocation name, final CompoundTag tag) {
    if(BEHAVIORS.containsKey(name)) {
      return BEHAVIORS.get(name).apply(tag);
    }
    return null;
  }

}
