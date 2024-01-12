package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.data.modifier.model.RemoveLayersGolemModifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class RemoveRepairItemsGolemModifier extends GolemModifier {

	public static final Codec<RemoveRepairItemsGolemModifier> CODEC = RemovePredicate.CODEC
			.xmap(RemoveRepairItemsGolemModifier::new, RemoveRepairItemsGolemModifier::getPredicate)
			.fieldOf("predicate").codec();

	private final RemovePredicate predicate;

	public RemoveRepairItemsGolemModifier(RemovePredicate predicate) {
		this.predicate = predicate;
	}

	//// GETTERS ////

	public RemovePredicate getPredicate() {
		return predicate;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.repairItems(b -> b.remove(getPredicate()));
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
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
