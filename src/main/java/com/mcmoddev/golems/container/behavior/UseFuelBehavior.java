package com.mcmoddev.golems.container.behavior;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.xml.soap.Text;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.InertGoal;
import com.mcmoddev.golems.entity.goal.LookAtWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.LookRandomlyWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.UseFuelGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;


/**
 * This behavior allows an entity to use fuel, accept fuel items,
 * only move and attack while fueled, and save/load fuel
 **/
@Immutable
public class UseFuelBehavior extends GolemBehavior {
  
  /** The maximum amount of fuel the entity can accept **/
  protected final int maxFuel;
  /** The number of ticks it takes to deplete one unit of fuel **/
  protected final int interval;

  public UseFuelBehavior(CompoundNBT tag) {
    super(tag);
    maxFuel = tag.getInt("max_fuel");
    interval = tag.getInt("burn_interval");
  }
  
  /** @return The maximum amount of fuel the entity can accept **/
  public int getMaxFuel() { return maxFuel; }
  
  /** @return The number of ticks it takes to deplete one unit of fuel **/
  public int getInterval() { return interval; }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    removeGoal(entity, LookAtGoal.class);
    removeGoal(entity, LookRandomlyGoal.class);
    entity.goalSelector.addGoal(0, new InertGoal<>(entity));
    entity.goalSelector.addGoal(1, new UseFuelGoal<>(entity, interval));
    entity.goalSelector.addGoal(7, new LookAtWhenActiveGoal<>(entity, PlayerEntity.class, 6.0F));
    entity.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal<>(entity));
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final PlayerEntity player, final Hand hand) {
    if(!player.isCrouching() && !player.getHeldItem(hand).isEmpty()) {
      entity.consumeFuel(player, hand);
    }
  }
  
  @Override
  public void onWriteData(final GolemBase entity, final CompoundNBT tag) {
    entity.saveFuel(tag);
  }
  
  @Override
  public void onReadData(final GolemBase entity, final CompoundNBT tag) {
    entity.loadFuel(tag);
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.use_fuel").mergeStyle(TextFormatting.GRAY));
  }
  
  protected static boolean removeGoal(final GolemBase entity, final Class<? extends Goal> goalToRemove) {
    final List<Goal> goalsToRemove = new ArrayList<>();
    entity.goalSelector.goals.forEach(g -> {
      if(g.getGoal().getClass() == goalToRemove) {
        goalsToRemove.add(g.getGoal());
      }
    });
    // remove the matching goals
    goalsToRemove.forEach(g -> entity.goalSelector.removeGoal(g) );    
    return !goalsToRemove.isEmpty();
  }
}
