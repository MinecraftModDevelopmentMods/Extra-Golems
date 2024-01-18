package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.UpdateTarget;
import com.mcmoddev.golems.data.behavior.util.UpdatePredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * This behavior allows an entity to change its golem or variant when it ticks
 **/
@Immutable
public class TickUpdateGolemBehavior extends Behavior<GolemBase> {

	public static final Codec<TickUpdateGolemBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(UpdateTarget.CODEC.fieldOf("apply").forGetter(TickUpdateGolemBehavior::getApply))
			.and(EGCodecUtils.listOrElementCodec(UpdatePredicate.CODEC).fieldOf("predicate").forGetter(TickUpdateGolemBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(TickUpdateGolemBehavior::getChance))
			.apply(instance, TickUpdateGolemBehavior::new));

	/** The golem and variant **/
	private final UpdateTarget apply;
	/** The conditions to update the golem and variant **/
	private final List<UpdatePredicate> predicates;
	/** The conditions to update the golem and variant as a single predicate **/
	private final Predicate<GolemBase> predicate;
	/** The percent chance **/
	private final double chance;

	public TickUpdateGolemBehavior(MinMaxBounds.Ints variant, UpdateTarget apply, List<UpdatePredicate> predicates, double chance) {
		super(variant);
		this.apply = apply;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.chance = chance;	}

	//// GETTERS ////

	public UpdateTarget getApply() {
		return apply;
	}

	public List<UpdatePredicate> getPredicates() {
		return predicates;
	}

	public Predicate<GolemBase> getPredicate() {
		return predicate;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.TICK_UPDATE_GOLEM.get();
	}

	//// METHODS ////

	@Override
	public void onTick(GolemBase entity) {
		if(getPredicate().test(entity) && entity.getRandom().nextDouble() < getChance()) {
			getApply().apply(entity);
		}
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TickUpdateGolemBehavior)) return false;
		if (!super.equals(o)) return false;
		TickUpdateGolemBehavior that = (TickUpdateGolemBehavior) o;
		return Double.compare(that.chance, chance) == 0 && apply.equals(that.apply) && predicates.equals(that.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), apply, predicates, chance);
	}
}
