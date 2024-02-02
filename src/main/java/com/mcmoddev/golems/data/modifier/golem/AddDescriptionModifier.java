package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Adds a description translation key
 */
@Immutable
public class AddDescriptionModifier extends Modifier {

	public static final Codec<AddDescriptionModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(Codec.STRING).fieldOf("descriptions").forGetter(AddDescriptionModifier::getDescriptions),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(AddDescriptionModifier::replace)
	).apply(instance, AddDescriptionModifier::new));

	private final List<String> descriptions;
	private final boolean replace;

	public AddDescriptionModifier(List<String> descriptions, boolean replace) {
		this.descriptions = descriptions;
		this.replace = replace;
	}

	//// GETTERS ////

	public List<String> getDescriptions() {
		return descriptions;
	}

	public boolean replace() {
		return replace;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.descriptions(list -> {
			if(replace()) {
				list.clear();
			}
			list.addAll(getDescriptions());
		});
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_DESCRIPTION.get();
	}
}
