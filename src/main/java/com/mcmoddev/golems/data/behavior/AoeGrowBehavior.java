package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.AoeShape;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.util.AoeMapper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
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
 * This behavior allows an entity to grow crops in an area
 **/
@Immutable
public class AoeGrowBehavior extends AoeBehavior {

	private static final IntProvider DEFAULT_AMOUNT = UniformInt.of(2, 5);

	public static final Codec<AoeGrowBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStartAoe(instance)
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 0.05D).forGetter(AoeGrowBehavior::getChance))
			.and(IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("amount", DEFAULT_AMOUNT).forGetter(AoeGrowBehavior::getAmount))
			.apply(instance, AoeGrowBehavior::new));

	/** The chance to apply per block **/
	private final double chance;
	/** The number of grow stages to add to each block **/
	private final IntProvider amount;
	/** The AoeMapper instance **/
	private final AoeMapper mapper;

	public AoeGrowBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, int radius, int interval, AoeShape shape, double chance, IntProvider amount) {
		super(variant, tooltipPredicate, radius, interval, shape);
		this.chance = chance;
		this.amount = amount;
		this.mapper = new AoeGrowMapper(chance, amount);
	}

	//// AOE BEHAVIOR ////

	@Override
	public AoeMapper getMapper() {
		return this.mapper;
	}

	//// GETTERS ////

	public double getChance() {
		return chance;
	}

	public IntProvider getAmount() {
		return amount;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.AOE_GROW.get();
	}

	//// METHODS ////

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		return ImmutableList.of(Component.translatable(PREFIX + "aoe_grow").withStyle(ChatFormatting.GOLD));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AoeGrowBehavior)) return false;
		if (!super.equals(o)) return false;
		AoeGrowBehavior that = (AoeGrowBehavior) o;
		return Double.compare(that.chance, chance) == 0 && amount.equals(that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), chance, amount);
	}

	//// CLASSES ////

	@Immutable
	private static class AoeGrowMapper implements AoeMapper {

		/** The percent chance [0,1] to grow an individual block **/
		private final double chance;
		/** An int provider for the number of grow stages to add to a block **/
		private final IntProvider growStages;

		private AoeGrowMapper(double chance, IntProvider growStages) {
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
