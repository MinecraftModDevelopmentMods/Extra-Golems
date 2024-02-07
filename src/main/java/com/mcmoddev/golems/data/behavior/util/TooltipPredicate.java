package com.mcmoddev.golems.data.behavior.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Predicate;

public enum TooltipPredicate implements StringRepresentable, Predicate<TooltipFlag> {
	HIDDEN("hidden", tooltipFlag -> false),
	NORMAL("normal", tooltipFlag -> true),
	EXTENDED("extended", tooltipFlag -> tooltipFlag.isAdvanced());

	public static final Codec<TooltipPredicate> CODEC = StringRepresentable.fromEnum(TooltipPredicate::values);

	private final String name;
	private final Predicate<TooltipFlag> predicate;

	TooltipPredicate(String name, Predicate<TooltipFlag> predicate) {
		this.name = name;
		this.predicate = predicate;
	}

	@Override
	public boolean test(TooltipFlag tooltipFlag) {
		return this.predicate.test(tooltipFlag);
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
