package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.data.behavior.data.ExplodeBehaviorData;
import com.mcmoddev.golems.data.behavior.data.ShootBehaviorData;
import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.function.Predicate;

public enum GolemPredicate implements StringRepresentable, Predicate<IExtraGolem> {
	ALWAYS("always", e -> true),
	NEVER("never", e -> false),
	DAY("day", e -> e.asMob().level().isDay()),
	NIGHT("night", e -> e.asMob().level().isNight()),
	CLEAR("clear", e -> !e.asMob().level().isRainingAt(e.asMob().blockPosition().above())),
	RAIN("rain", e -> e.asMob().level().isRainingAt(e.asMob().blockPosition().above())),
	THUNDER("thunder", e -> e.asMob().level().isThundering() && e.asMob().level().isRainingAt(e.asMob().blockPosition().above())),
	WET("wet", e -> e.asMob().isInWaterRainOrBubble()),
	DRY("dry", e -> !e.asMob().isInWaterRainOrBubble()),
	FUEL("fuel", e -> e.getBehaviorData(UseFuelBehaviorData.class).map(o -> o.hasFuel()).orElse(false)),
	FUEL_EMPTY("fuel_empty", e -> e.getBehaviorData(UseFuelBehaviorData.class).map(o -> !o.hasFuel()).orElse(false)),
	ARROWS("ammo", e -> e.getBehaviorData(ShootBehaviorData.class).map(o -> o.getAmmo() > 0).orElse(false)),
	ARROWS_EMPTY("ammo_empty", e -> e.getBehaviorData(ShootBehaviorData.class).map(o -> o.getAmmo() <= 0).orElse(false)),
	FUSE_LIT("fuse_lit", e -> e.getBehaviorData(ExplodeBehaviorData.class).map(o -> o.isFuseLit()).orElse(false)),
	FUSE_UNLIT("fuse_unlit", e -> e.getBehaviorData(ExplodeBehaviorData.class).map(o -> !o.isFuseLit()).orElse(false)),
	BABY("baby", e -> e.asMob().isBaby()),
	ADULT("adult", e -> !e.asMob().isBaby());;

	public static final Codec<GolemPredicate> CODEC = StringRepresentable.fromEnum(GolemPredicate::values);

	private final String name;
	private final String descriptionId;
	private final Predicate<IExtraGolem> predicate;

	GolemPredicate(String name, Predicate<IExtraGolem> predicate) {
		this.name = name;
		this.descriptionId = "golem.description.golem_predicate." + name;
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
