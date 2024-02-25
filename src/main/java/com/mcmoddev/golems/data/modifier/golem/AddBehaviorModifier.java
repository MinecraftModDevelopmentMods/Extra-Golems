package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.behavior.Behavior;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;

/**
 * Adds all of the given behaviors to the {@link BehaviorList.Builder}
 */
@SuppressWarnings("rawtypes")
@Immutable
public class AddBehaviorModifier extends Modifier {

	private static final Codec<Either<ResourceLocation, List<Behavior>>> EITHER_CODEC = Codec.either(ResourceLocation.CODEC, EGCodecUtils.listOrElementCodec(Behavior.DIRECT_CODEC));

	public static final Codec<AddBehaviorModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EITHER_CODEC.fieldOf("behavior").forGetter(AddBehaviorModifier::getBehaviors),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(AddBehaviorModifier::replace)
	).apply(instance, AddBehaviorModifier::new));

	private final Either<ResourceLocation, List<Behavior>> behaviors;
	private final boolean replace;

	public AddBehaviorModifier(Either<ResourceLocation, List<Behavior>> behaviors, boolean replace) {
		this.behaviors = behaviors;
		this.replace = replace;
	}

	//// GETTERS ////

	public Either<ResourceLocation, List<Behavior>> getBehaviors() {
		return behaviors;
	}

	public boolean replace() {
		return replace;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.behaviors(b -> {
			if(replace()) {
				b.clear();
			}
			// add elements from
			getBehaviors().ifLeft(id -> {
				final Registry<BehaviorList> registry = builder.getRegistryAccess().registryOrThrow(EGRegistry.Keys.BEHAVIOR_LIST);
				registry.getOptional(id).ifPresentOrElse(
						behaviorList -> b.addAll(behaviorList.getBehaviors()),
						() -> ExtraGolems.LOGGER.error("Failed to apply AddBehaviorModifier; missing BehaviorList with ID " + id));
			});
			getBehaviors().ifRight(b::addAll);
		});
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_BEHAVIOR.get();
	}
}
