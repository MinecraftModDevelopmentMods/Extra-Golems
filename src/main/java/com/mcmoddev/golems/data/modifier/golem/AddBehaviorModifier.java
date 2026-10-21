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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Adds all of the given behaviors to the {@link BehaviorList.Builder}
 */
@SuppressWarnings("rawtypes")
@Immutable
public class AddBehaviorModifier extends Modifier {

	private static final Codec<Either<ResourceLocation, List<Behavior>>> EITHER_CODEC = Codec
			.either(ResourceLocation.CODEC, EGCodecUtils.listOrElementCodec(Behavior.DIRECT_CODEC));

	public static final MapCodec<AddBehaviorModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			EITHER_CODEC.fieldOf("behavior").forGetter(AddBehaviorModifier::getBehaviors),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(AddBehaviorModifier::replace))
			.apply(instance, AddBehaviorModifier::new));

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
			if (replace()) {
				b.clear();
			}
			// add elements from
			getBehaviors().ifLeft(id -> {
				final ResourceKey<BehaviorList> key = ResourceKey.create(EGRegistry.Keys.BEHAVIOR_LIST, id);
				builder.getRegistryAccess().lookup(EGRegistry.Keys.BEHAVIOR_LIST)
						.flatMap(registry -> registry.get(key))
						.ifPresentOrElse(
								holder -> b.addAll(holder.value().getBehaviors()),
								() -> ExtraGolems.LOGGER.error(
										"Failed to apply AddBehaviorModifier; missing BehaviorList with ID " + id));
			});
			getBehaviors().ifRight(b::addAll);
		});
	}

	@Override
	public MapCodec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_BEHAVIOR.get();
	}
}
