package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to place power blocks
 **/
@Immutable
public class PowerBehavior extends Behavior {

	private static final TagKey<Block> CANNOT_SUPPORT = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation(ExtraGolems.MODID, "cannot_support_utility_blocks"));

	public static final Codec<PowerBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("interval", 4).forGetter(PowerBehavior::getInterval))
			.and(Codec.intRange(0, 15).optionalFieldOf("power", 15).forGetter(PowerBehavior::getPowerLevel))
			.apply(instance, PowerBehavior::new));

	/** The interval to place blocks **/
	private final int interval;
	/** The power level **/
	private final int powerLevel;

	public PowerBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, int interval, int powerLevel) {
		super(variant, tooltipPredicate);
		this.interval = interval;
		this.powerLevel = powerLevel;
	}

	//// GETTERS ////

	public int getInterval() {
		return interval;
	}

	public int getPowerLevel() {
		return powerLevel;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.POWER.get();
	}

	//// METHODS ////

	@Override
	public void onTick(IExtraGolem entity) {
		final Mob mob = entity.asMob();
		if(mob.tickCount % interval == 0) {
			placeBlockAt(mob.level(), mob.blockPosition());
		}
	}


	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		return ImmutableList.of(Component.translatable(PREFIX + "power").withStyle(ChatFormatting.RED));
	}

	/**
	 * @param level the level
	 * @param pos the block position
	 * @return True if the block position was replaceable and at least one block was successfully placed
	 */
	protected boolean placeBlockAt(final Level level, final BlockPos pos) {
		// determine the block to place
		final BlockState blockState = EGRegistry.BlockReg.POWER_PROVIDER.get().defaultBlockState().setValue(PowerBlock.POWER_LEVEL, powerLevel);
		// iterate positions in this column
		final BlockPos.MutableBlockPos blockPos = pos.mutable().move(0, -1, 0);
		int count = 0;
		for(int i = 0; i < 4; i++) {
			BlockState replace = level.getBlockState(blockPos);
			// verify block below is not blacklisted
			if(level.getBlockState(blockPos.below()).is(CANNOT_SUPPORT)) {
				continue;
			}
			// attempt to replace air
			if(replace.isAir()) {
				level.setBlock(blockPos, blockState, Block.UPDATE_ALL);
				count++;
			}
			// attempt to replace water
			if(replace.is(Blocks.WATER) && replace.getFluidState().isSource()) {
				level.setBlock(blockPos, blockState.setValue(PowerBlock.WATERLOGGED, true), Block.UPDATE_ALL);
				count++;
			}
			// update block pos
			blockPos.move(0, 1, 0);
		}
		// no blocks were placed
		return count > 0;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PowerBehavior)) return false;
		if (!super.equals(o)) return false;
		PowerBehavior that = (PowerBehavior) o;
		return interval == that.interval && powerLevel == that.powerLevel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), interval, powerLevel);
	}
}
