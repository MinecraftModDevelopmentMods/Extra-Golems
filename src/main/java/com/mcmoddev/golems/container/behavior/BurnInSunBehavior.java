package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.BurnInSunGoal;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;


/**
 * This behavior allows an entity to burn in sunlight
 * and seek shelter from the sun during the day
 **/
@Immutable
public class BurnInSunBehavior extends GolemBehavior {
  
  /** The goal priority **/
  private final int priority;
  /** The percent chance [0,1] to apply each tick **/
  private final double chance;

  public BurnInSunBehavior(CompoundNBT tag) {
    super(tag);
    priority = tag.getInt("priority");
    chance = tag.getDouble("chance");
  }
  
  public int getPriority() { return priority; }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    entity.goalSelector.addGoal(priority, new RestrictSunGoal(entity));
    entity.goalSelector.addGoal(priority, new BurnInSunGoal(entity, (float) chance));
    entity.goalSelector.addGoal(priority, new FleeSunGoal(entity, 1.1D));
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.burn_in_sun").mergeStyle(TextFormatting.DARK_PURPLE));
  }
}
