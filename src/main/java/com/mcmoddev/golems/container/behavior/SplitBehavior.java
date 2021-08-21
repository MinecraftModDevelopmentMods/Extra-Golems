package com.mcmoddev.golems.container.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;

@Immutable
public class SplitBehavior extends GolemBehavior {
  
  protected final int children;

  public SplitBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.SPLIT_ON_DEATH);
    children = tag.getInt("children");
  }
  
  public int getChildren() { return children; }
  
  @Override
  public void onDie(final GolemBase entity, final DamageSource source) {
    attemptAddChildren(entity, children);
  }
  
  /**
   * Attempts to spawn the given number of "mini" golems
   * @param entity the parent Golem
   * @param count the number of children to spawn
   * @return a collection containing the entities that were spawned
   **/
  protected Collection<GolemBase> attemptAddChildren(final GolemBase entity, final int count) {
    final List<GolemBase> children = new ArrayList<>();
    if(!entity.level.isClientSide() && !entity.isBaby() && count > 0) {
      for(int i = 0; i < count; i++) {
        GolemBase child = GolemBase.create(entity.level, entity.getMaterial());
        child.setBaby(true);
        if (entity.getTarget() != null) {
          child.setTarget(entity.getTarget());
        }
        // set location
        child.copyPosition(entity);
        // spawn the entity
        entity.level.addFreshEntity(child);
        // add to the list
        children.add(child);
      }
    }
    return children;
  }
}
