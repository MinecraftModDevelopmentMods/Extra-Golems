package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to burn in sunlight
 * and seek shelter from the sun during the day
 **/
@Immutable
public class BurnInSunBehavior extends Behavior {

	public static final Codec<BurnInSunBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 0.25D).forGetter(BurnInSunBehavior::getChance))
			.apply(instance, BurnInSunBehavior::new));

	/** The percent chance [0,1] to apply each tick **/
	private final double chance;

	public BurnInSunBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, double chance) {
		super(variant, tooltipPredicate);
		this.chance = chance;
	}

	//// GETTERS ////

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.BURN_IN_SUN.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final IExtraGolem entity) {
		final PathfinderMob mob = entity.asMob();
		// TODO adjust goals to use variant
		mob.goalSelector.addGoal(1, new RestrictSunGoal(mob));
		mob.goalSelector.addGoal(2, new FleeSunGoal(mob, 1.1D));
	}

	@Override
	public void onTick(IExtraGolem entity) {
		final Mob mob = entity.asMob();
		// set on fire
		if(entity.isSunBurnTickAccessor() && mob.getRandom().nextFloat() < chance && mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
			mob.setSecondsOnFire(3);
		}
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.burn_in_sun").withStyle(ChatFormatting.DARK_RED));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BurnInSunBehavior)) return false;
		if (!super.equals(o)) return false;
		BurnInSunBehavior that = (BurnInSunBehavior) o;
		return Double.compare(that.chance, chance) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), chance);
	}
}
