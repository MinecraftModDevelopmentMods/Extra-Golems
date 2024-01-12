package com.mcmoddev.golems.data.modifier.model;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.Layer;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public class AddLayersGolemModifier extends GolemModifier {

	public static final Codec<AddLayersGolemModifier> CODEC = EGCodecUtils.listOrElementCodec(Layer.CODEC)
			.xmap(AddLayersGolemModifier::new, AddLayersGolemModifier::getLayers)
			.fieldOf("layers").codec();

	private final List<Layer> layers;

	public AddLayersGolemModifier(List<Layer> layers) {
		this.layers = layers;
	}

	//// GETTERS ////

	public List<Layer> getLayers() {
		return layers;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.model(b -> b.addAll(this.getLayers()));
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_LAYERS.get();
	}
}
