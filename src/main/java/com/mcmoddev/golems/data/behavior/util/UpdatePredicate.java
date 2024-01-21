package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.function.Predicate;

public enum UpdatePredicate implements Predicate<IExtraGolem>, StringRepresentable {
	TICK("tick", e -> true),
	WET("wet", e -> e.asMob().isInWaterRainOrBubble()),
	DRY("dry", e -> !e.asMob().isInWaterRainOrBubble()),
	FUELED("fuel", e -> e.hasFuel()),
	FUEL_EMPTY("fuel_empty", e -> !e.hasFuel()),
	ARROWS("arrows", e -> e.getArrowsInInventory() > 0),
	ARROWS_EMPTY("arrows_empty", e -> e.getArrowsInInventory() <= 0),
	FUSE_LIT("fuse_lit", e -> e.isFuseLit()),
	FUSE_UNLIT("fuse_unlit", e -> !e.isFuseLit()),
	BABY("baby", e -> e.asMob().isBaby()),
	ADULT("adult", e -> !e.asMob().isBaby());

	public static final Codec<UpdatePredicate> CODEC = StringRepresentable.fromEnum(UpdatePredicate::values);

	private final String name;
	private final Predicate<IExtraGolem> predicate;

	private UpdatePredicate(String name, Predicate<IExtraGolem> predicate) {
		this.name = name;
		this.predicate = predicate;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	@Override
	public boolean test(IExtraGolem entity) {
		return this.predicate.test(entity);
	}
}
