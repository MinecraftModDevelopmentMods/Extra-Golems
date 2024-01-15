package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to place light blocks
 **/
@Immutable
public class LightBehavior extends Behavior<GolemBase> {

	public static final Codec<LightBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("interval", 4).forGetter(LightBehavior::getInterval))
			.and(Codec.intRange(0, 15).optionalFieldOf("light", 15).forGetter(LightBehavior::getLightLevel))
			.apply(instance, LightBehavior::new));

	/** The interval to place blocks **/
	private final int interval;
	/** The light level **/
	private final int lightLevel;

	public LightBehavior(MinMaxBounds.Ints variant, int interval, int lightLevel) {
		super(variant);
		this.interval = interval;
		this.lightLevel = lightLevel;
	}

	//// GETTERS ////

	public int getInterval() {
		return interval;
	}

	public int getLightLevel() {
		return lightLevel;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.LIGHT.get();
	}

	//// METHODS ////

	@Override
	public void onTick(GolemBase entity) {
		if(entity.tickCount % interval == 0) {
			placeBlockAt(entity.level(), entity.blockPosition());
		}
	}


	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.provides_light").withStyle(ChatFormatting.RED));
	}

	/**
	 * @param level the level
	 * @param pos the block position
	 * @return True if the block position was replacable and the block was succesfully placed
	 */
	protected boolean placeBlockAt(final Level level, final BlockPos pos) {
		// determine the block to place
		final BlockState blockState = EGRegistry.BlockReg.LIGHT_PROVIDER.get().defaultBlockState().setValue(GlowBlock.LIGHT_LEVEL, lightLevel);
		// iterate positions in this column
		final BlockPos.MutableBlockPos blockPos = pos.mutable().move(0, -1, 0);
		for(int i = 0; i < 4; i++) {
			BlockState replace = level.getBlockState(blockPos);
			// attempt to replace air
			if(replace.isAir()) {
				return level.setBlock(pos, blockState, Block.UPDATE_ALL);
			}
			// attempt to replace water
			if(replace.is(Blocks.WATER) && replace.getFluidState().isSource()) {
				return level.setBlock(pos, blockState.setValue(GlowBlock.WATERLOGGED, true), Block.UPDATE_ALL);
			}
			// update block pos
			blockPos.move(0, 1, 0);
		}
		// no blocks were placed
		return false;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LightBehavior)) return false;
		if (!super.equals(o)) return false;
		LightBehavior that = (LightBehavior) o;
		return interval == that.interval && lightLevel == that.lightLevel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), interval, lightLevel);
	}
}
