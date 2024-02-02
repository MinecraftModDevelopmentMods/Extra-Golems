package com.mcmoddev.golems.data.behavior.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum TriggerType implements StringRepresentable {
	HURT("hurt"),
	ATTACK("attack"),
	TICK("tick"),
	LIGHTNING("lightning");

	public static final Codec<TriggerType> CODEC = StringRepresentable.fromEnum(TriggerType::values);

	private final String name;
	private final String descriptionId;

	TriggerType(final String nameIn) {
		name = nameIn;
		this.descriptionId = "golem.description.trigger." + name;
	}

	public String getDescriptionId() {
		return descriptionId;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
