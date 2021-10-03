package com.mcmoddev.golems.golem_stats.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.AoeBlocksGoal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * This behavior allows an entity to grow crops in an area
 **/
@Immutable
public class AoeGrowBehavior extends GolemBehavior {
  
  /** The radius for which the behavior will apply **/
  protected final int range;
  /** The average number of ticks between application of this behavior **/
  protected final int interval;
  /** The percent chance [0,1] to affect each block **/
  protected final double chance;
  
  public AoeGrowBehavior(CompoundNBT tag) {
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
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.aoe_grow").mergeStyle(TextFormatting.GOLD));
  }
}
