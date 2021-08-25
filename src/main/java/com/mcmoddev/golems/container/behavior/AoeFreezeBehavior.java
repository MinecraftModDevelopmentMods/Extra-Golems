package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.AoeBlocksGoal;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * This behavior allows an entity to change water and lava
 * blocks in an area
 **/
@Immutable
public class AoeFreezeBehavior extends GolemBehavior {
  
  /** The radius for which the behavior will apply **/
  protected final int range;
  /** The average number of ticks between application of this behavior **/
  protected final int interval;
  /** True to affect a spherical area, false to affect a disc area **/
  protected final boolean sphere;
  /** True to use frosted ice, false to use regular/packed ice **/
  protected final boolean frosted;
  
  public AoeFreezeBehavior(CompoundTag tag) {
    super(tag);
    range = tag.getInt("range");
    interval = tag.getInt("interval");
    sphere = tag.getBoolean("sphere");
    frosted = tag.getBoolean("frosted");
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, range, interval, sphere, 
        new AoeBlocksGoal.FreezeFunction(frosted)));
  }
  
  @Override
  public void onAddDescriptions(List<Component> list) {
    list.add(new TranslatableComponent("entitytip.aoe_freeze").withStyle(ChatFormatting.AQUA));
  }
}
