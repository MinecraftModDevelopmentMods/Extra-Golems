package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.GolemVariant;
import com.mcmoddev.golems.data.behavior.util.UpdatePredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * This behavior allows an entity to change its container
 * when an item is used on the entity or when it ticks based on several conditions
 **/
@Immutable
public class ItemUpdateGolemBehavior extends Behavior<GolemBase> {

	public static final Codec<ItemUpdateGolemBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(GolemVariant.EITHER_CODEC.fieldOf("apply").forGetter(ItemUpdateGolemBehavior::getApply))
			.and(DeferredHolderSet.codec(BuiltInRegistries.ITEM.key()).optionalFieldOf("item", DeferredHolderSet.empty()).forGetter(ItemUpdateGolemBehavior::getItems))
			.and(EGCodecUtils.listOrElementCodec(UpdatePredicate.CODEC).fieldOf("predicate").forGetter(ItemUpdateGolemBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(ItemUpdateGolemBehavior::getChance))
			.apply(instance, ItemUpdateGolemBehavior::new));

	/** The golem and variant **/
	private final GolemVariant apply;
	/** The items associated with the update **/
	private final DeferredHolderSet<Item> items;
	/** The conditions to update the golem and variant **/
	private final List<UpdatePredicate> predicates;
	/** The conditions to update the golem and variant as a single predicate **/
	private final Predicate<GolemBase> predicate;
	/** The percent chance **/
	private final double chance;

	public ItemUpdateGolemBehavior(MinMaxBounds.Ints variant, GolemVariant apply, DeferredHolderSet<Item> items, List<UpdatePredicate> predicates, double chance) {
		super(variant);
		this.apply = apply;
		this.items = items;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.chance = chance;
	}

	//// GETTERS ////

	public GolemVariant getApply() {
		return apply;
	}

	public List<UpdatePredicate> getPredicates() {
		return predicates;
	}

	public Predicate<GolemBase> getPredicate() {
		return predicate;
	}

	public DeferredHolderSet<Item> getItems() {
		return items;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.ITEM_UPDATE_GOLEM.get();
	}

	//// METHODS ////

	@Override
	public void onMobInteract(GolemBase entity, Player player, InteractionHand hand) {
		// TODO verify this does not fire for both hands
		// determine held item
		ItemStack item = player.getItemInHand(hand);
		// validate and use item
		if((getItems().isEmpty() || getItems().get(BuiltInRegistries.ITEM).contains(item.getItemHolder()))
				&& entity.getRandom().nextDouble() < getChance()
				&& getApply().apply(entity)) {
			player.swing(hand);
		}
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemUpdateGolemBehavior)) return false;
		if (!super.equals(o)) return false;
		ItemUpdateGolemBehavior that = (ItemUpdateGolemBehavior) o;
		return Double.compare(that.chance, chance) == 0 && apply.equals(that.apply) && items.equals(that.items) && predicates.equals(that.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), apply, items, predicates, chance);
	}
}
