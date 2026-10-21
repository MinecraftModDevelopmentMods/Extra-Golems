package com.mcmoddev.golems.data.modifier;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

@Immutable
public abstract class Modifier {

	public static final Codec<Modifier> DIRECT_CODEC = Codec
			.lazyInitialized(() -> EGRegistry.GOLEM_MODIFIER_SERIALIZER_SUPPLIER.byNameCodec())
			.dispatch("type", Modifier::getCodec, codec -> codec);

	public abstract void apply(final Golem.Builder builder);

	public abstract MapCodec<? extends Modifier> getCodec();
}
