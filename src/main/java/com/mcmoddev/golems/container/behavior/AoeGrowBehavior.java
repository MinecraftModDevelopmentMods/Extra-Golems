package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.AoeBlocksGoal;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

@Immutable
public class AoeGrowBehavior extends GolemBehavior {
  
  protected final int range;
  protected final int interval;
  protected final double chance;
  
  public AoeGrowBehavior(CompoundTag tag) {
    super(tag);
    range = tag.getInt("range");
    interval = tag.getInt("interval");
    chance = tag.getDouble("chance");
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, range, interval, false, 
        new AoeBlocksGoal.GrowFunction((float) chance)));
  }
  
  @Override
  public void onAddDescriptions(List<Component> list) {
    list.add(new TranslatableComponent("entitytip.aoe_grow").withStyle(ChatFormatting.GOLD));
  }
}
