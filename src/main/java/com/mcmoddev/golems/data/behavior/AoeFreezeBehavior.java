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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to transform water and lava in an area
 **/
@Immutable
public class AoeFreezeBehavior extends AoeBehavior {

	public static final Codec<AoeFreezeBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStartAoe(instance)
			.and(Codec.BOOL.optionalFieldOf("frosted", false).forGetter(AoeFreezeBehavior::useFrostedIce))
			.apply(instance, AoeFreezeBehavior::new));

	/** True to use frosted ice, false to use regular/packed ice **/
	private final boolean useFrostedIce;
	/** The AoeMapper instance **/
	private final AoeMapper mapper;

	public AoeFreezeBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, int radius, int interval, AoeShape shape, boolean useFrostedIce) {
		super(variant, tooltipPredicate, radius, interval, shape);
		this.useFrostedIce = useFrostedIce;
		this.mapper = new AoeFreezeMapper(useFrostedIce);
	}

	//// AOE BEHAVIOR ////

	@Override
	public AoeMapper getMapper() {
		return mapper;
	}


	//// GETTERS ////

	public boolean useFrostedIce() {
		return useFrostedIce;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.AOE_FREEZE.get();
	}

	//// METHODS ////

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		return ImmutableList.of(Component.translatable(PREFIX + "aoe_freeze").withStyle(ChatFormatting.AQUA));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AoeFreezeBehavior)) return false;
		if (!super.equals(o)) return false;
		AoeFreezeBehavior that = (AoeFreezeBehavior) o;
		return useFrostedIce == that.useFrostedIce;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), useFrostedIce);
	}


	//// CLASSES ////

	@Immutable
	private static class AoeFreezeMapper implements AoeMapper {

		/** This percentage of Packed Ice placed will become regular ice instead. **/
		private final float iceChance = 0.52F;
		/** This percentage of Obsidian placed will become cobblestone instead. **/
		private final float cobbleChance = 0.29F;
		/** When true, all water will turn to Frosted Ice **/
		private final boolean useFrostedIce;

		private AoeFreezeMapper(final boolean useFrost) {
			this.useFrostedIce = useFrost;
		}

		@Override
		public BlockState map(final LivingEntity entity, final BlockPos pos, final BlockState input) {
			final BlockState cobbleState = Blocks.COBBLESTONE.defaultBlockState();
			final BlockState iceState = this.useFrostedIce ? Blocks.FROSTED_ICE.defaultBlockState() : Blocks.ICE.defaultBlockState();
			final Block block = input.getBlock();
			final FluidState fluidState = input.getFluidState();
			// verify liquid source block
			if(!(block instanceof LiquidBlock liquidBlock && fluidState.isSource())) {
				return input;
			}
			// water fluid
			if (fluidState.is(FluidTags.WATER)) {
				final boolean useIce = this.useFrostedIce || entity.getRandom().nextFloat() < this.iceChance;
				return useIce ? iceState : Blocks.PACKED_ICE.defaultBlockState();
			}
			// lava fluid
			if (fluidState.is(FluidTags.LAVA)) {
				final boolean useCobble = entity.getRandom().nextFloat() < this.cobbleChance;
				return useCobble ? cobbleState : Blocks.OBSIDIAN.defaultBlockState();
			}
			// no checks passed
			return input;
		}
	}
}
