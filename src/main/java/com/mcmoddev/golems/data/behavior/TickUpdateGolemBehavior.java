package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


/**
 * This behavior allows an entity to change its container
 * when an item is used on the entity or when it ticks based on several conditions
 **/
@Immutable
public class TickUpdateGolemBehavior extends Behavior<GolemBase> {

	public static final Codec<TickUpdateGolemBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(UpdateOnTick.CODEC.fieldOf("target").forGetter(TickUpdateGolemBehavior::getUpdateOnTick))
			.apply(instance, TickUpdateGolemBehavior::new));

	public final UpdateOnTick updateOnTick;

	public TickUpdateGolemBehavior(MinMaxBounds.Ints variant, UpdateOnTick updateOnTick) {
		super(variant);

		this.updateOnTick = updateOnTick;
	}

	//// GETTERS ////


	public UpdateOnTick getUpdateOnTick() {
		return updateOnTick;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.TICK_UPDATE_GOLEM.get();
	}

	//// METHODS ////

	@Override
	public void onTick(GolemBase entity) {
		if(!canApply(entity)) {
			return;
		}
		if(updateOnTick.getPredicate().test(entity) && entity.getRandom().nextDouble() < updateOnTick.getChance()) {
			updateOnTick.update(entity);
		}
	}


	//// CLASSES ////

	public static enum UpdatePredicate implements Predicate<GolemBase>, StringRepresentable {
		TICK("tick", e -> true),
		WET("wet", e -> e.isInWaterRainOrBubble()),
		DRY("dry", e -> !e.isInWaterRainOrBubble()),
		FUELED("fuel", e -> e.hasFuel()),
		FUEL_EMPTY("fuel_empty", e -> !e.hasFuel());

		public static final Codec<UpdatePredicate> CODEC = StringRepresentable.fromEnum(UpdatePredicate::values);

		private final String name;
		private final Predicate<GolemBase> predicate;

		private UpdatePredicate(String name, Predicate<GolemBase> predicate) {
			this.name = name;
			this.predicate = predicate;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		@Override
		public boolean test(GolemBase golemBase) {
			return this.predicate.test(golemBase);
		}
	}

	public static class UpdateOnTick extends GolemVariantCombo {

		public static final Codec<UpdateOnTick> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
				.and(UpdatePredicate.CODEC.fieldOf("predicate").forGetter(UpdateOnTick::getPredicate))
				.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(UpdateOnTick::getChance))
				.apply(instance, UpdateOnTick::new));

		private final UpdatePredicate predicate;
		/** The percent chance **/
		private final double chance;

		public UpdateOnTick(Optional<ResourceLocation> golem, Optional<Integer> variant, UpdatePredicate predicate, double chance) {
			super(golem, variant);
			this.predicate = predicate;
			this.chance = chance;
		}

		public UpdatePredicate getPredicate() {
			return predicate;
		}

		public double getChance() {
			return chance;
		}
	}
}
