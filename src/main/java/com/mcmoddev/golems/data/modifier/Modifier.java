package com.mcmoddev.golems.data.modifier;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;

import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

@Immutable
public abstract class Modifier {

	public static final Codec<Modifier> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> EGRegistry.GOLEM_MODIFIER_SERIALIZER_SUPPLIER.get().getCodec())
			.dispatch(Modifier::getCodec, Function.identity());

	public abstract void apply(final Golem.Builder builder);

	public abstract Codec<? extends Modifier> getCodec();
}
