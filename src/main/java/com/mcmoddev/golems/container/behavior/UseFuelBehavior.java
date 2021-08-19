package com.mcmoddev.golems.container.behavior;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.InertGoal;
import com.mcmoddev.golems.entity.goal.LookAtWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.LookRandomlyWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.UseFuelGoal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

@Immutable
public class UseFuelBehavior extends GolemBehavior {
  
  protected final int maxFuel;
  protected final int interval;

  public UseFuelBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.USE_FUEL);
    maxFuel = tag.getInt("max_fuel");
    interval = tag.getInt("burn_interval");
  }
  
  public int getMaxFuel() { return maxFuel; }
  
  public int getInterval() { return interval; }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    removeGoal(entity, LookAtPlayerGoal.class);
    removeGoal(entity, RandomLookAroundGoal.class);
    entity.goalSelector.addGoal(0, new InertGoal<>(entity));
    entity.goalSelector.addGoal(1, new UseFuelGoal<>(entity, interval));
    entity.goalSelector.addGoal(1, new TemptGoal(entity, 0.7D, Ingredient.of(ItemTags.COALS), false));
    entity.goalSelector.addGoal(7, new LookAtWhenActiveGoal<>(entity, Player.class, 6.0F));
    entity.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal<>(entity));
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
    if(!player.isCrouching() && !player.getItemInHand(hand).isEmpty()) {
      entity.consumeFuel(player, hand);
    }
  }
  
  @Override
  public void onWriteData(final GolemBase entity, final CompoundTag tag) {
    entity.saveFuel(tag);
  }
  
  @Override
  public void onReadData(final GolemBase entity, final CompoundTag tag) {
    entity.loadFuel(tag);
  }
  
  protected static boolean removeGoal(final GolemBase entity, final Class<? extends Goal> goalToRemove) {
    final List<Goal> goalsToRemove = new ArrayList<>();
    entity.goalSelector.availableGoals.forEach(g -> {
      if(g.getGoal().getClass() == goalToRemove) {
        goalsToRemove.add(g.getGoal());
      }
    });
    // remove the matching goals
    goalsToRemove.forEach(g -> entity.goalSelector.removeGoal(g) );    
    return !goalsToRemove.isEmpty();
  }
}
