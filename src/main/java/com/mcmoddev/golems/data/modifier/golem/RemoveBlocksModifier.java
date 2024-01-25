package com.mcmoddev.golems.data.modifier.golem;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.golem.GolemPart;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Removes entries in the {@link com.mcmoddev.golems.data.golem.BuildingBlocks.Builder}
 * that pass any of the given {@link RemovePredicate}s
 */
@Immutable
public class RemoveBlocksModifier extends Modifier {

	public static final Codec<RemoveBlocksModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(RemovePredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of()).forGetter(RemoveBlocksModifier::getPredicates),
			EGCodecUtils.listOrElementCodec(GolemPart.CODEC).optionalFieldOf("part", ImmutableList.of(GolemPart.ALL)).forGetter(RemoveBlocksModifier::getPart)
			).apply(instance, RemoveBlocksModifier::new));

	private final List<RemovePredicate> predicates;
	private final List<GolemPart> part;
	private final Predicate<ResourcePair> predicate;

	public RemoveBlocksModifier(List<RemovePredicate> predicates, List<GolemPart> part) {
		this.predicates = predicates;
		this.predicate = predicates.isEmpty() ? (o -> true) : PredicateUtils.or(predicates);
		this.part = part;
	}

	//// GETTERS ////

	/** @return The predicates to test ResourcePair entries. If this list is empty, all entries will be removed. **/
	public List<RemovePredicate> getPredicates() {
		return predicates;
	}

	/** @return The Golem Parts to modify. Defaults to {@link GolemPart#ALL} **/
	public List<GolemPart> getPart() {
		return part;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		for(GolemPart p : part) {
			builder.blocks(b -> b.apply(p, o -> o.remove(predicate)));
		}
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.REMOVE_BLOCKS.get();
	}

	//// CLASSES ////

	public static class RemovePredicate implements Predicate<ResourcePair> {

		public static final Codec<RemovePredicate> CODEC = ResourcePair.CODEC.xmap(RemovePredicate::new, o -> o.resource);

		private final ResourcePair resource;

		public RemovePredicate(ResourcePair resource) {
			this.resource = resource;
		}

		@Override
		public boolean test(ResourcePair resourcePair) {
			return resourcePair.equals(this.resource);
		}
	}
}
