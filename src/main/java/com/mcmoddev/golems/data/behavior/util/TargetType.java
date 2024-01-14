package com.mcmoddev.golems.data.behavior.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum TargetType implements StringRepresentable {
	SELF("self"),
	ENEMY("enemy"),
	AREA("area");

	public static final Codec<TargetType> CODEC = StringRepresentable.fromEnum(TargetType::values);
	public static final Codec<TargetType> SELF_OR_ENEMY_CODEC = StringRepresentable.fromEnum(() -> new TargetType[] {SELF, ENEMY});

	private final String name;

	TargetType(final String nameIn) {
		name = nameIn;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
