package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.Behavior;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Adds all of the given behaviors to the {@link BehaviorList.Builder}
 * that pass any of the given {@link RemovePredicate}s
 */
@SuppressWarnings("rawtypes")
@Immutable
public class RemoveBehaviorModifier extends Modifier {

	public static final MapCodec<RemoveBehaviorModifier> CODEC = EGCodecUtils.listOrElementCodec(RemovePredicate.CODEC)
			.xmap(RemoveBehaviorModifier::new, RemoveBehaviorModifier::getPredicates)
			.fieldOf("predicate");

	private final List<RemovePredicate> predicates;
	private final Predicate<Behavior> predicate;

	public RemoveBehaviorModifier(List<RemovePredicate> predicates) {
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
		builder.behaviors(b -> b.remove(predicate));
	}

	@Override
	public MapCodec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.REMOVE_BEHAVIOR.get();
	}

	//// CLASSES ////

	public static class RemovePredicate implements Predicate<Behavior> {

		// Note: This needs to be Codec, not MapCodec, for use in listOrElementCodec
		public static final Codec<RemovePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.optionalFieldOf("type").forGetter(o -> Optional.ofNullable(o.type)),
				EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY)
						.forGetter(o -> o.variant),
				TooltipPredicate.CODEC.optionalFieldOf("tooltip").forGetter(o -> Optional.ofNullable(o.tooltip)))
				.apply(instance, RemovePredicate::new));

		private final @Nullable ResourceLocation type;
		private final MinMaxBounds.Ints variant;
		private final @Nullable TooltipPredicate tooltip;

		public RemovePredicate(Optional<ResourceLocation> type, MinMaxBounds.Ints variant,
				Optional<TooltipPredicate> tooltip) {
			this.type = type.orElse(null);
			this.variant = variant;
			this.tooltip = tooltip.orElse(null);
		}

		@Override
		public boolean test(Behavior behavior) {
			// test type
			if (type != null
					&& !this.type.equals(EGRegistry.BEHAVIOR_SERIALIZER_SUPPLIER.getKey(behavior.getCodec()))) {
				return false;
			}
			// test tooltip
			if (tooltip != null && tooltip != behavior.getTooltipPredicate()) {
				return false;
			}
			// test variant
			final MinMaxBounds.Ints bounds = behavior.getVariantBounds();
			if (!Objects.equals(bounds.min(), variant.min()) || !Objects.equals(bounds.max(), variant.max())) {
				return false;
			}
			// all checks passed
			return true;
		}
	}
}
