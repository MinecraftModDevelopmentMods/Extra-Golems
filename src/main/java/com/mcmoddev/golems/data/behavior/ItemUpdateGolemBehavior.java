package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.behavior.util.UpdateTarget;
import com.mcmoddev.golems.data.behavior.util.UpdatePredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
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
 * This behavior allows an entity to change its golem or variant
 * when an item is used on the entity
 **/
@Immutable
public class ItemUpdateGolemBehavior extends Behavior {

	public static final Codec<ItemUpdateGolemBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(UpdateTarget.CODEC.fieldOf("apply").forGetter(ItemUpdateGolemBehavior::getApply))
			.and(DeferredHolderSet.codec(BuiltInRegistries.ITEM.key()).optionalFieldOf("item", DeferredHolderSet.empty()).forGetter(ItemUpdateGolemBehavior::getItems))
			.and(EGCodecUtils.listOrElementCodec(UpdatePredicate.CODEC).fieldOf("predicate").forGetter(ItemUpdateGolemBehavior::getPredicates))
			.and(Codec.BOOL.optionalFieldOf("consume", false).forGetter(ItemUpdateGolemBehavior::consume))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(ItemUpdateGolemBehavior::getChance))
			.apply(instance, ItemUpdateGolemBehavior::new));

	/** The golem and variant **/
	private final UpdateTarget apply;
	/** The items associated with the update **/
	private final DeferredHolderSet<Item> items;
	/** The conditions to update the golem and variant **/
	private final List<UpdatePredicate> predicates;
	/** The conditions to update the golem and variant as a single predicate **/
	private final Predicate<IExtraGolem> predicate;
	/** True to consume the item, if any **/
	private final boolean consume;
	/** The percent chance **/
	private final double chance;

	public ItemUpdateGolemBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, UpdateTarget apply, DeferredHolderSet<Item> items, List<UpdatePredicate> predicates, boolean consume, double chance) {
		super(variant, tooltipPredicate);
		this.apply = apply;
		this.items = items;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.consume = consume;
		this.chance = chance;
	}

	//// GETTERS ////

	public UpdateTarget getApply() {
		return apply;
	}

	public List<UpdatePredicate> getPredicates() {
		return predicates;
	}

	public DeferredHolderSet<Item> getItems() {
		return items;
	}

	public boolean consume() {
		return consume;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.ITEM_UPDATE_GOLEM.get();
	}

	//// METHODS ////

	@Override
	public void onMobInteract(IExtraGolem entity, Player player, InteractionHand hand) {
		// TODO verify this does not fire for both hands
		// determine held item
		ItemStack item = player.getItemInHand(hand);
		// validate and use item
		if((getItems().isEmpty() || getItems().get(BuiltInRegistries.ITEM).contains(item.getItemHolder()))
				&& this.predicate.test(entity)
				&& entity.asMob().getRandom().nextDouble() < getChance()
				&& getApply().apply(entity)) {
			// swing hand
			player.swing(hand);
			// consume item
			if(consume() && !player.getAbilities().instabuild) {
				item.shrink(1);
			}
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
