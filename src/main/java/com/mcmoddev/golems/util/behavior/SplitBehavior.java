package com.mcmoddev.golems.util.behavior;

import javax.annotation.concurrent.Immutable;

import net.minecraft.nbt.CompoundTag;

@Immutable
public class SplitBehavior extends GolemBehavior {
  
  protected final int children;

  public SplitBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.SPLIT_ON_DEATH);
    children = tag.getInt("children");
  }
  
  public int getChildren() { return children; }
}
