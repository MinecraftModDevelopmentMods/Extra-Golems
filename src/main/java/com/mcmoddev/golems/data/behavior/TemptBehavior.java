package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * This behavior allows an entity to follow players that are holding
 * specific items.
 **/
@Immutable
public class TemptBehavior extends Behavior<GolemBase> {

	public static final Codec<TemptBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(DeferredHolderSet.codec(ForgeRegistries.ITEMS.getRegistryKey()).fieldOf("item").forGetter(TemptBehavior::getItems))
			.apply(instance, TemptBehavior::new));

	/** The tempt items **/
	private final DeferredHolderSet<Item> items;

	public TemptBehavior(MinMaxBounds.Ints variant, DeferredHolderSet<Item> items) {
		super(variant);
		this.items = items;
		// TODO tempt behavior description
	}

	//// GETTERS ////

	public DeferredHolderSet<Item> getItems() {
		return items;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.TEMPT.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// TODO adjust tempt goal to account for entity variant
		final HolderSet<Item> holderSet = items.get(BuiltInRegistries.ITEM);
		Ingredient ingredient = holderSet.unwrap().map(Ingredient::of, list -> Ingredient.of(list.stream().map(Holder::get).toArray(Item[]::new)));
		entity.goalSelector.addGoal(1, new TemptGoal(entity, 0.75D, ingredient, false));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TemptBehavior)) return false;
		if (!super.equals(o)) return false;
		TemptBehavior that = (TemptBehavior) o;
		return Objects.equals(items, that.items);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), items);
	}
}
