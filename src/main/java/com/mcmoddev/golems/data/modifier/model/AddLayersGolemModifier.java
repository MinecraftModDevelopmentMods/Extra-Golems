package com.mcmoddev.golems.data.modifier.model;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.Layer;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Adds the given layers or model references to the {@link LayerList.Builder}
 */
@Immutable
public class AddLayersGolemModifier extends GolemModifier {

	public static final Codec<AddLayersGolemModifier> CODEC = EGCodecUtils.listOrElementCodec(LayerList.EITHER_CODEC)
			.xmap(AddLayersGolemModifier::new, AddLayersGolemModifier::getLayers)
			.fieldOf("layers").codec();

	private final List<Either<Layer, Holder<LayerList>>> layers;

	public AddLayersGolemModifier(List<Either<Layer, Holder<LayerList>>> layers) {
		this.layers = layers;
	}

	//// GETTERS ////

	public List<Either<Layer, Holder<LayerList>>> getLayers() {
		return layers;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.layers(b -> b.addAll(this.getLayers()));
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_LAYERS.get();
	}
}
