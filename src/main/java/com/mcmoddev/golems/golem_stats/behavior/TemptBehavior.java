package com.mcmoddev.golems.golem_stats.behavior;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.ResourcePair;

import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This behavior allows an entity to follow players that are holding
 * specific items.
 **/
@Immutable
public class TemptBehavior extends GolemBehavior {
  
  /** An optional containing either an item ID or item tag, if any **/
  private final Optional<ResourcePair> item;

  public TemptBehavior(CompoundNBT tag) {
    super(tag);
    item = ResourcePair.read(tag.getString("item")).resultOrPartial(s -> ExtraGolems.LOGGER.error("Failed to parse '" + s + "' in TemptBehavior"));
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    if(item.isPresent()) {
      Ingredient ing;
      if(item.get().flag()) {
        ing = Ingredient.fromTag(ItemTags.getCollection().get(item.get().resource()));
      } else {
        ing = Ingredient.fromItems(ForgeRegistries.ITEMS.getValue(item.get().resource()));
      }
      entity.goalSelector.addGoal(1, new TemptGoal(entity, 0.75D, ing, false));
    }
  }
}
