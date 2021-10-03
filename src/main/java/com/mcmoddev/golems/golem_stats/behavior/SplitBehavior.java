package com.mcmoddev.golems.golem_stats.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.server.ServerWorld;

/**
 * This behavior allows an entity to create a number of
 * baby entities upon death.
 **/
@Immutable
public class SplitBehavior extends GolemBehavior {
  
  protected final int children;

  public SplitBehavior(CompoundNBT tag) {
    super(tag);
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
    if(entity.world instanceof ServerWorld && !entity.isChild() && count > 0) {
      final DifficultyInstance diff = entity.world.getDifficultyForLocation(entity.getPosition());
      for(int i = 0; i < count; i++) {
        GolemBase child = GolemBase.create(entity.world, entity.getMaterial());
        child.setBaby(true);
		child.setTextureId((byte) entity.getTextureId());
        if (entity.getAttackTarget() != null) {
          child.setAttackTarget(entity.getAttackTarget());
        }
        // set location
        child.copyLocationAndAnglesFrom(entity);
        // spawn the entity
        entity.world.addEntity(child);
        child.onInitialSpawn((ServerWorld) entity.world, diff, SpawnReason.MOB_SUMMONED, null, null);
        // add to the list
        children.add(child);
      }
    }
    return children;
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.split_on_death").mergeStyle(TextFormatting.LIGHT_PURPLE));
  }
}
