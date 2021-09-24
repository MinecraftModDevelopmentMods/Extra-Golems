package com.mcmoddev.golems.container.behavior;

import java.util.List;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.FollowGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;


/**
 * This behavior allows an entity to detect nearby entities
 * of a specific type and move to follow them
 **/
@Immutable
public class FollowBehavior extends GolemBehavior {
  
  /** The goal priority **/
  private final int priority;
  /** The entity ID of an entity to follow **/
  private final ResourceLocation mobType;
  /** The description component for this behavior **/
  private final ITextComponent description;

  public FollowBehavior(CompoundNBT tag) {
    super(tag);
    priority = tag.getInt("priority");
    mobType = new ResourceLocation(tag.getString("entity"));
    description = new TranslationTextComponent("entitytip.follow_x", new TranslationTextComponent("entity." + mobType.getNamespace() + "." + mobType.getPath())).mergeStyle(TextFormatting.DARK_GREEN);
  }
  
  public int getPriority() { return priority; }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    final Optional<EntityType<?>> oType = EntityType.byKey(mobType.toString());
    if(oType.isPresent()) {
      final EntityType<?> type = oType.get();
      entity.goalSelector.addGoal(priority, new FollowGoal(entity, 1.0D, 4.0F, 8.0F, e -> e.getType().equals(type)));
    }
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(description);
  }
}
