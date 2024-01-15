package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.function.Predicate;

public enum WorldPredicate implements StringRepresentable, Predicate<GolemBase> {
	ALWAYS("always", e -> true),
	NEVER("never", e -> false),
	DAY("day", e -> e.level().isDay()),
	NIGHT("night", e -> e.level().isNight()),
	CLEAR("clear", e -> !e.level().isRainingAt(e.blockPosition().above())),
	RAIN("rain", e -> e.level().isRainingAt(e.blockPosition().above())),
	THUNDER("thunder", e -> e.level().isThundering() && e.level().isRainingAt(e.blockPosition().above()));

	public static final Codec<WorldPredicate> CODEC = StringRepresentable.fromEnum(WorldPredicate::values);

	private final String name;
	private final String descriptionId;
	private final Predicate<GolemBase> predicate;

	WorldPredicate(String name, Predicate<GolemBase> predicate) {
		this.name = name;
		this.descriptionId = "predicate." + name;
		this.predicate = predicate;
	}

	public String getDescriptionId() {
		return descriptionId;
	}

	@Override
	public boolean test(GolemBase entity) {
		return this.predicate.test(entity);
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

}
