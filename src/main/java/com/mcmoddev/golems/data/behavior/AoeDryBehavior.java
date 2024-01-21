package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.AoeShape;
import com.mcmoddev.golems.util.AoeMapper;
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

/**
 * This behavior allows an entity to remove water or
 * waterlogged blocks in an area
 **/
@Immutable
public class AoeDryBehavior extends AoeBehavior {

	public static final Codec<AoeDryBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStartAoe(instance)
			.apply(instance, AoeDryBehavior::new));

	public AoeDryBehavior(MinMaxBounds.Ints variant, int radius, int interval, AoeShape shape) {
		super(variant, radius, interval, shape);
	}

	//// AOE BEHAVIOR ////

	@Override
	public AoeMapper getMapper() {
		return AoeDryMapper.INSTANCE;
	}

	//// GETTERS ////

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.AOE_DRY.get();
	}

	//// METHODS ////

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.aoe_dry").withStyle(ChatFormatting.GOLD));
	}

	//// CLASSES ////

	@Immutable
	private static class AoeDryMapper implements AoeMapper {

		private static final AoeDryMapper INSTANCE = new AoeDryMapper();

		private AoeDryMapper() { }

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
