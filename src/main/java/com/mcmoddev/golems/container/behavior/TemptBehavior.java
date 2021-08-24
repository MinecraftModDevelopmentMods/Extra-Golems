package com.mcmoddev.golems.container.behavior;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.ResourcePair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

@Immutable
public class TemptBehavior extends GolemBehavior {
  
  private final Optional<ResourcePair> item;

  public TemptBehavior(CompoundTag tag) {
    super(tag);
    item = ResourcePair.read(tag.getString("item")).resultOrPartial(s -> ExtraGolems.LOGGER.error("Failed to parse '" + s + "' in TemptBehavior"));
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    if(item.isPresent()) {
      Ingredient ing;
      if(item.get().flag()) {
        ing = Ingredient.of(ItemTags.getAllTags().getTag(item.get().resource()));
      } else {
        ing = Ingredient.of(ForgeRegistries.ITEMS.getValue(item.get().resource()));
      }
      entity.goalSelector.addGoal(1, new TemptGoal(entity, 0.75D, ing, false));
    }
  }
}
