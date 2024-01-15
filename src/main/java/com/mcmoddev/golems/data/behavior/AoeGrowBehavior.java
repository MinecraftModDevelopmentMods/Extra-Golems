package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.AoeShape;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.AoeBlocksGoal;
import com.mcmoddev.golems.event.AoeFunction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to remove water or
 * waterlogged blocks in an area
 **/
@Immutable
public class AoeGrowBehavior extends Behavior<GolemBase> {

	private static final IntProvider DEFAULT_AMOUNT = UniformInt.of(2, 5);

	public static final Codec<AoeGrowBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(0, 127).optionalFieldOf("radius", 3).forGetter(AoeGrowBehavior::getRadius))
			.and(Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("interval", 4).forGetter(AoeGrowBehavior::getInterval))
			.and(AoeShape.CODEC.optionalFieldOf("shape", AoeShape.SPHERE).forGetter(AoeGrowBehavior::getShape))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 0.05D).forGetter(AoeGrowBehavior::getChance))
			.and(IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("amount", DEFAULT_AMOUNT).forGetter(AoeGrowBehavior::getAmount))
			.apply(instance, AoeGrowBehavior::new));

	/** The radius for which the behavior will apply **/
	private final int radius;
	/** The average number of ticks between application of this behavior **/
	private final int interval;
	/** The shape of the affected area **/
	private final AoeShape shape;
	/** The chance to apply per block **/
	private final double chance;
	/** The number of grow stages to add to each block **/
	private final IntProvider amount;

	public AoeGrowBehavior(MinMaxBounds.Ints variant, int radius, int interval, AoeShape shape, double chance, IntProvider amount) {
		super(variant);
		this.radius = radius;
		this.interval = interval;
		this.shape = shape;
		this.chance = chance;
		this.amount = amount;
	}

	//// GETTERS ////

	public int getRadius() {
		return radius;
	}

	public int getInterval() {
		return interval;
	}

	public AoeShape getShape() {
		return shape;
	}

	public double getChance() {
		return chance;
	}

	public IntProvider getAmount() {
		return amount;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.AOE_GROW.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// TODO adjust goal to use variant
		// TODO adjust goal to use AoeShape
		entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, radius, interval, shape, new AoeGrowFunction(chance, amount)));
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.aoe_grow").withStyle(ChatFormatting.GOLD));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AoeGrowBehavior)) return false;
		AoeGrowBehavior that = (AoeGrowBehavior) o;
		return radius == that.radius && interval == that.interval && Double.compare(that.chance, chance) == 0 && shape == that.shape && amount.equals(that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), radius, interval, shape, chance, amount);
	}

	//// CLASSES ////

	@Immutable
	public static class AoeGrowFunction implements AoeFunction {

		private final double chance;
		private final IntProvider growStages;

		public AoeGrowFunction(double chance, IntProvider growStages) {
			this.chance = chance;
			this.growStages = growStages;
		}

		@Override
		public BlockState map(LivingEntity entity, BlockPos pos, BlockState input) {
			// handle crop blocks that are not fully grown
			if (input.getBlock() instanceof CropBlock crop && !crop.isMaxAge(input) && entity.getRandom().nextDouble() < chance) {
				// determine the next grow stage for the crop
				int growth = growStages.sample(entity.getRandom());
				if(growth <= 0) {
					return input;
				}
				// determine the updated age of the crop
				return crop.getStateForAge(Math.min(crop.getMaxAge(), crop.getAge(input) + growth));
			}
			return input;
		}

	}
}
