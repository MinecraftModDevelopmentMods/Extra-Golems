package com.mcmoddev.golems.data.modifier;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;

import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

@Immutable
public abstract class GolemModifier {

	public static final Codec<GolemModifier> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> EGRegistry.GOLEM_MODIFIER_SERIALIZERS_SUPPLIER.get().getCodec())
			.dispatch(GolemModifier::getCodec, Function.identity());

	public abstract void apply(final Golem.Builder builder);

	public abstract Codec<? extends GolemModifier> getCodec();
}
