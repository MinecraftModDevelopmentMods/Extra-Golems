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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to remove water or
 * waterlogged blocks in an area
 **/
@Immutable
public class AoeFreezeBehavior extends Behavior<GolemBase> {

	public static final Codec<AoeFreezeBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(0, 127).optionalFieldOf("radius", 3).forGetter(AoeFreezeBehavior::getRadius))
			.and(Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("interval", 4).forGetter(AoeFreezeBehavior::getInterval))
			.and(AoeShape.CODEC.optionalFieldOf("shape", AoeShape.SPHERE).forGetter(AoeFreezeBehavior::getShape))
			.and(Codec.BOOL.optionalFieldOf("frosted", false).forGetter(AoeFreezeBehavior::useFrostedIce))
			.apply(instance, AoeFreezeBehavior::new));

	/** The radius for which the behavior will apply **/
	private final int radius;
	/** The average number of ticks between application of this behavior **/
	private final int interval;
	/** The shape of the affected area **/
	private final AoeShape shape;
	/** True to use frosted ice, false to use regular/packed ice **/
	private final boolean useFrostedIce;

	public AoeFreezeBehavior(MinMaxBounds.Ints variant, int radius, int interval, AoeShape shape, boolean useFrostedIce) {
		super(variant);
		this.radius = radius;
		this.interval = interval;
		this.shape = shape;
		this.useFrostedIce = useFrostedIce;
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

	public boolean useFrostedIce() {
		return useFrostedIce;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.AOE_FREEZE.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// TODO adjust goal to use variant
		// TODO adjust goal to use AoeShape
		entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, radius, interval, shape, new AoeFreezeFunction(useFrostedIce)));
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.aoe_freeze").withStyle(ChatFormatting.AQUA));
	}

	//// CLASSES ////

	@Immutable
	public static class AoeFreezeFunction implements AoeFunction {

		/** This percentage of Packed Ice placed will become regular ice instead. **/
		public final float iceChance = 0.52F;
		/** This percentage of Obsidian placed will become cobblestone instead. **/
		public final float cobbleChance = 0.29F;
		/** When true, all water will turn to Frosted Ice **/
		public final boolean useFrostedIce;

		public AoeFreezeFunction(final boolean useFrost) {
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
