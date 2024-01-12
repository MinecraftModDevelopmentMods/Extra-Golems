package com.mcmoddev.golems.data.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Priority implements StringRepresentable {
	HIGHEST("highest"),
	HIGH("high"),
	NORMAL("normal"),
	LOW("low"),
	LOWEST("lowest");

	public static final Codec<Priority> CODEC = StringRepresentable.fromEnum(Priority::values);

	private String name;

	Priority(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
