package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.Behavior;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Adds all of the given behaviors to the {@link BehaviorList.Builder}
 * that pass any of the given {@link RemovePredicate}s
 */
@SuppressWarnings("rawtypes")
@Immutable
public class RemoveBehaviorGolemModifier extends GolemModifier {

	public static final Codec<RemoveBehaviorGolemModifier> CODEC = EGCodecUtils.listOrElementCodec(RemovePredicate.CODEC)
			.xmap(RemoveBehaviorGolemModifier::new, RemoveBehaviorGolemModifier::getPredicates)
			.fieldOf("predicate").codec();

	private final List<RemovePredicate> predicates;
	private final Predicate<Behavior> predicate;

	public RemoveBehaviorGolemModifier(List<RemovePredicate> predicates) {
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
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.REMOVE_BEHAVIOR.get();
	}

	//// CLASSES ////

	public static class RemovePredicate implements Predicate<Behavior> {

		public static final Codec<RemovePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.optionalFieldOf("type").forGetter(o -> Optional.ofNullable(o.type)),
				EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(o -> o.variant)
		).apply(instance, RemovePredicate::new));

		private final @Nullable ResourceLocation type;
		private final MinMaxBounds.Ints variant;

		public RemovePredicate(Optional<ResourceLocation> type, MinMaxBounds.Ints variant) {
			this.type = type.orElse(null);
			this.variant = variant;
		}

		@Override
		public boolean test(Behavior behavior) {
			if(type != null && !this.type.equals(EGRegistry.BEHAVIOR_SERIALIZERS_SUPPLIER.get().getKey(behavior.getCodec()))) {
				return false;
			}
			return variant.equals(behavior.getVariant());
		}
	}
}
