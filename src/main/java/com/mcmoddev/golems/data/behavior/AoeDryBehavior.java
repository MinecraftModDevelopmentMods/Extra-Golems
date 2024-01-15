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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to remove water or
 * waterlogged blocks in an area
 **/
@Immutable
public class AoeDryBehavior extends Behavior<GolemBase> {

	public static final Codec<AoeDryBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(0, 127).optionalFieldOf("radius", 3).forGetter(AoeDryBehavior::getRadius))
			.and(Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("interval", 4).forGetter(AoeDryBehavior::getInterval))
			.and(AoeShape.CODEC.optionalFieldOf("shape", AoeShape.SPHERE).forGetter(AoeDryBehavior::getShape))
			.apply(instance, AoeDryBehavior::new));

	/** The radius for which the behavior will apply **/
	private final int radius;
	/** The average number of ticks between application of this behavior **/
	private final int interval;
	/** The shape of the affected area **/
	private final AoeShape shape;

	public AoeDryBehavior(MinMaxBounds.Ints variant, int radius, int interval, AoeShape shape) {
		super(variant);
		this.radius = radius;
		this.interval = interval;
		this.shape = shape;
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

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.AOE_DRY.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// TODO adjust goal to use variant
		// TODO adjust goal to use AoeShape
		entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, radius, interval, shape, new AoeDryFunction()));
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.aoe_dry").withStyle(ChatFormatting.GOLD));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AoeDryBehavior)) return false;
		AoeDryBehavior that = (AoeDryBehavior) o;
		return radius == that.radius && interval == that.interval && shape == that.shape;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), radius, interval, shape);
	}

	//// CLASSES ////

	@Immutable
	public static class AoeDryFunction implements AoeFunction {

		public AoeDryFunction() { }

		@Override
		public BlockState map(final LivingEntity entity, final BlockPos pos, final BlockState input) {
			// replace liquid water with air
			if (input.getBlock() instanceof LiquidBlock && input.getFluidState().is(FluidTags.WATER)) {
				return Blocks.AIR.defaultBlockState();
			}
			// break blocks that are always waterlogged (adapted from SpongeBlock)
			if (input.is(Blocks.KELP) || input.is(Blocks.KELP_PLANT) || input.is(Blocks.SEAGRASS) || input.is(Blocks.TALL_SEAGRASS)) {
				BlockEntity blockentity = input.hasBlockEntity() ? entity.level().getBlockEntity(pos) : null;
				Block.dropResources(input, entity.level(), pos, blockentity);
				return Blocks.AIR.defaultBlockState();
			}
			// replace waterlogged blocks with non-waterlogged blocks
			if (input.hasProperty(BlockStateProperties.WATERLOGGED)) {
				return input.setValue(BlockStateProperties.WATERLOGGED, false);
			}
			// no changes
			return input;
		}
	}
}
