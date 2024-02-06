package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Removes all repair item entries from the {@link com.mcmoddev.golems.data.golem.RepairItems.Builder}
 * that pass any of the given {@link RemovePredicate}s
 */
@Immutable
public class RemoveRepairItemsModifier extends Modifier {

	public static final Codec<RemoveRepairItemsModifier> CODEC = EGCodecUtils.listOrElementCodec(RemovePredicate.CODEC)
			.xmap(RemoveRepairItemsModifier::new, RemoveRepairItemsModifier::getPredicates)
			.fieldOf("predicate").codec();

	private final List<RemovePredicate> predicates;
	private final Predicate<Map.Entry<ResourcePair, Double>> predicate;

	public RemoveRepairItemsModifier(List<RemovePredicate> predicates) {
		this.predicates = predicates;
		this.predicate = PredicateUtils.or(predicates);
	}

	//// GETTERS ////

	public List<RemovePredicate> getPredicates() {
		return predicates;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.repairItems(b -> b.remove(this.predicate));
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.REMOVE_REPAIR_ITEMS.get();
	}

	//// CLASSES ////

	public static class RemovePredicate implements Predicate<Map.Entry<ResourcePair, Double>> {

		public static final Codec<RemovePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourcePair.CODEC.optionalFieldOf("item").forGetter(o -> Optional.ofNullable(o.resourcePair)),
				EGCodecUtils.MIN_MAX_DOUBLES_CODEC.optionalFieldOf("amount").forGetter(o -> Optional.ofNullable(o.values))
		).apply(instance, RemovePredicate::new));

		private final @Nullable ResourcePair resourcePair;
		private final @Nullable MinMaxBounds.Doubles values;

		public RemovePredicate(Optional<ResourcePair> resourcePair, Optional<MinMaxBounds.Doubles> values) {
			this.resourcePair = resourcePair.orElse(null);
			this.values = values.orElse(null);
		}

		@Override
		public boolean test(Map.Entry<ResourcePair, Double> entry) {
			if(this.resourcePair != null && !this.resourcePair.equals(entry.getKey())) {
				return false;
			}
			if(this.values != null && !this.values.matches(entry.getValue())) {
				return false;
			}
			// all checks passed
			return true;
		}
	}
}
