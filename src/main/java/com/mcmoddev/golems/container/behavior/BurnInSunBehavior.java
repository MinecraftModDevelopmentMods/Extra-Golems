package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.BurnInSunGoal;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;

@Immutable
public class BurnInSunBehavior extends GolemBehavior {
  
  private final int priority;
  private final double chance;

  public BurnInSunBehavior(CompoundTag tag) {
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
  public void onAddDescriptions(List<Component> list) {
    list.add(new TranslatableComponent("entitytip.burn_in_sun").withStyle(ChatFormatting.DARK_PURPLE));
  }
}
