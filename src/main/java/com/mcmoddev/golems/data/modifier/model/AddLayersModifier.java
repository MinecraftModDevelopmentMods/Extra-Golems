package com.mcmoddev.golems.data.modifier.model;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.Layer;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Adds the given layers or model references to the {@link LayerList.Builder}
 */
@Immutable
public class AddLayersModifier extends Modifier {

	public static final Codec<AddLayersModifier> CODEC = EGCodecUtils.listOrElementCodec(LayerList.LAYER_OR_ID_CODEC)
			.xmap(AddLayersModifier::new, AddLayersModifier::getLayers)
			.fieldOf("layers").codec();

	private final List<Either<Layer, ResourceLocation>> layers;

	public AddLayersModifier(List<Either<Layer, ResourceLocation>> layers) {
		this.layers = layers;
	}

	//// GETTERS ////

	public List<Either<Layer, ResourceLocation>> getLayers() {
		return layers;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.layers(b -> b.addAll(this.getLayers()));
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_LAYERS.get();
	}
}
