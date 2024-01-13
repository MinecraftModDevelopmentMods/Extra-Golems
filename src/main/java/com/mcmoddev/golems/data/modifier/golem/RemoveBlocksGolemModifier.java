package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Removes entries in the {@link com.mcmoddev.golems.data.golem.BuildingBlocks.Builder}
 * that pass any of the given {@link RemovePredicate}s
 */
@Immutable
public class RemoveBlocksGolemModifier extends GolemModifier {

	public static final Codec<RemoveBlocksGolemModifier> CODEC = EGCodecUtils.listOrElementCodec(RemovePredicate.CODEC)
			.xmap(RemoveBlocksGolemModifier::new, RemoveBlocksGolemModifier::getPredicates)
			.fieldOf("predicate").codec();

	private final List<RemovePredicate> predicate;

	public RemoveBlocksGolemModifier(List<RemovePredicate> predicate) {
		this.predicate = predicate;
	}

	//// GETTERS ////

	public List<RemovePredicate> getPredicates() {
		return predicate;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.blocks(b -> getPredicates().forEach(b::remove));
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
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
