package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.UpdatePredicate;
import com.mcmoddev.golems.data.behavior.util.UpdateTarget;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * This behavior allows an entity to change its golem or variant
 * when a certain {@link TriggerType} is triggered
 **/
@Immutable
public class UpdateGolemBehavior extends Behavior {

	public static final Codec<UpdateGolemBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(UpdateTarget.CODEC.fieldOf("apply").forGetter(UpdateGolemBehavior::getApply))
			.and(TriggerType.CODEC.optionalFieldOf("trigger", TriggerType.TICK).forGetter(UpdateGolemBehavior::getTrigger))
			.and(EGCodecUtils.listOrElementCodec(UpdatePredicate.CODEC).fieldOf("predicate").forGetter(UpdateGolemBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(UpdateGolemBehavior::getChance))
			.apply(instance, UpdateGolemBehavior::new));

	/** The golem and variant **/
	private final UpdateTarget apply;
	/** The trigger type **/
	private final TriggerType trigger;
	/** The conditions to update the golem and variant **/
	private final List<UpdatePredicate> predicates;
	/** The conditions to update the golem and variant as a single predicate **/
	private final Predicate<IExtraGolem> predicate;
	/** The percent chance **/
	private final double chance;

	public UpdateGolemBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, UpdateTarget apply, TriggerType trigger, List<UpdatePredicate> predicates, double chance) {
		super(variant, tooltipPredicate);
		this.apply = apply;
		this.trigger = trigger;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.chance = chance;
	}

	//// GETTERS ////

	public UpdateTarget getApply() {
		return apply;
	}

	public TriggerType getTrigger() {
		return trigger;
	}

	public List<UpdatePredicate> getPredicates() {
		return predicates;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.UPDATE_GOLEM.get();
	}

	//// METHODS ////


	@Override
	public void onTick(IExtraGolem entity) {
		if(trigger == TriggerType.TICK) {
			apply(entity);
		}
	}

	@Override
	public void onAttack(IExtraGolem entity, Entity target) {
		if (trigger == TriggerType.ATTACK) {
			apply(entity);
		}
	}

	@Override
	public void onActuallyHurt(IExtraGolem entity, DamageSource source, float amount) {
		if(trigger == TriggerType.HURT) {
			apply(entity);
		}
	}

	@Override
	public void onStruckByLightning(IExtraGolem entity, LightningBolt lightningBolt) {
		if(trigger == TriggerType.LIGHTNING) {
			apply(entity);
		}
	}

	protected boolean apply(IExtraGolem entity) {
		final Mob mob = entity.asMob();
		// verify world conditions
		if(!this.predicate.test(entity)) {
			return false;
		}
		// verify random chance
		if(mob.getRandom().nextFloat() > chance) {
			return false;
		}
		// apply
		return getApply().apply(entity);
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UpdateGolemBehavior)) return false;
		if (!super.equals(o)) return false;
		UpdateGolemBehavior that = (UpdateGolemBehavior) o;
		return Double.compare(that.chance, chance) == 0 && apply.equals(that.apply) && trigger == that.trigger && predicates.equals(that.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), apply, trigger, predicates, chance);
	}
}
