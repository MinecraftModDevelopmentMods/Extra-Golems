package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.function.Predicate;

public enum WorldPredicate implements StringRepresentable, Predicate<IExtraGolem> {
	ALWAYS("always", e -> true),
	NEVER("never", e -> false),
	DAY("day", e -> e.asMob().level().isDay()),
	NIGHT("night", e -> e.asMob().level().isNight()),
	CLEAR("clear", e -> !e.asMob().level().isRainingAt(e.asMob().blockPosition().above())),
	RAIN("rain", e -> e.asMob().level().isRainingAt(e.asMob().blockPosition().above())),
	THUNDER("thunder", e -> e.asMob().level().isThundering() && e.asMob().level().isRainingAt(e.asMob().blockPosition().above()));

	public static final Codec<WorldPredicate> CODEC = StringRepresentable.fromEnum(WorldPredicate::values);

	private final String name;
	private final String descriptionId;
	private final Predicate<IExtraGolem> predicate;

	WorldPredicate(String name, Predicate<IExtraGolem> predicate) {
		this.name = name;
		this.descriptionId = "predicate." + name;
		this.predicate = predicate;
	}

	public String getDescriptionId() {
		return descriptionId;
	}

	@Override
	public boolean test(IExtraGolem entity) {
		return this.predicate.test(entity);
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

}
