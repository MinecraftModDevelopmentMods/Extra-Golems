package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to follow players that are holding
 * specific items.
 **/
@Immutable
public class TemptBehavior extends Behavior {

	public static final Codec<TemptBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(DeferredHolderSet.codec(ForgeRegistries.ITEMS.getRegistryKey()).fieldOf("item").forGetter(TemptBehavior::getItems))
			.and(Codec.STRING.optionalFieldOf("display_name", "").forGetter(TemptBehavior::getDisplayNameKey))
			.apply(instance, TemptBehavior::new));

	/** The tempt items **/
	private final DeferredHolderSet<Item> items;
	private final String displayNameKey;

	public TemptBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, DeferredHolderSet<Item> items, String displayNameKey) {
		super(variant, tooltipPredicate);
		this.items = items;
		this.displayNameKey = displayNameKey;
	}

	//// GETTERS ////

	public DeferredHolderSet<Item> getItems() {
		return items;
	}

	public String getDisplayNameKey() {
		return displayNameKey;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.TEMPT.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final IExtraGolem entity) {
		// TODO adjust tempt goal to account for entity variant
		// resolve holder set and convert to ingredient
		final HolderSet<Item> holderSet = items.get(BuiltInRegistries.ITEM);
		Ingredient ingredient = holderSet.unwrap().map(Ingredient::of, list -> Ingredient.of(list.stream().map(Holder::get).toArray(Item[]::new)));
		entity.asMob().goalSelector.addGoal(1, new TemptGoal(entity.asMob(), 0.75D, ingredient, false));
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		// create item description
		final Component name;
		if(displayNameKey.isEmpty()) {
			HolderSet<Item> holderSet = this.items.get(BuiltInRegistries.ITEM);
			Either<TagKey<Item>, List<Holder<Item>>> value = holderSet.unwrap();
			if(value.left().isPresent()) {
				// create name from tag key
				name = Component.literal("#" + value.left().get().location().toString()).withStyle(ChatFormatting.DARK_GRAY);
			} else if(value.right().isPresent()) {
				// create name from first item
				Item randomItem = value.right().get().get(0).get();
				name = randomItem.getDescription().copy().withStyle(ChatFormatting.LIGHT_PURPLE);
			} else {
				// create empty name
				name = ItemStack.EMPTY.getHoverName();
			}
		} else {
			// create name using the provided display name key
			name = Component.translatable(displayNameKey);
		}

		return ImmutableList.of(Component.translatable(PREFIX + "tempt", name));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TemptBehavior)) return false;
		if (!super.equals(o)) return false;
		TemptBehavior that = (TemptBehavior) o;
		return Objects.equals(items, that.items) && Objects.equals(displayNameKey, that.displayNameKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), items, displayNameKey);
	}
}
