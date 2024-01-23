package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.data.behavior.util.AoeShape;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.GolemModifyBlocksEvent;
import com.mcmoddev.golems.util.AoeMapper;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * This behavior allows an entity to modify blocks in an area
 **/
@Immutable
public abstract class AoeBehavior extends Behavior {

	/** The radius for which the behavior will apply **/
	private final int radius;
	/** The average number of ticks between application of this behavior **/
	private final int interval;
	/** The shape of the affected area **/
	private final AoeShape shape;

	public AoeBehavior(MinMaxBounds.Ints variant, int radius, int interval, AoeShape shape) {
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

	public abstract AoeMapper getMapper();

	//// METHODS ////

	protected static <T extends AoeBehavior> Products.P4<RecordCodecBuilder.Mu<T>, MinMaxBounds.Ints, Integer, Integer, AoeShape> codecStartAoe(RecordCodecBuilder.Instance<T> instance) {
		return Behavior.codecStart(instance)
				.and(Codec.intRange(0, 127).optionalFieldOf("radius", 3).forGetter(AoeBehavior::getRadius))
				.and(Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("interval", 4).forGetter(AoeBehavior::getInterval))
				.and(AoeShape.CODEC.optionalFieldOf("shape", AoeShape.SPHERE).forGetter(AoeBehavior::getShape));
	}

	@Override
	public void onTick(IExtraGolem entity) {
		if(entity.asMob().tickCount % this.interval != 0) {
			return;
		}
		final GolemModifyBlocksEvent event = new GolemModifyBlocksEvent(entity.asMob(), entity.asMob().blockPosition(), getRadius(), getShape(), getMapper());
		// verify the event was not canceled or denied
		if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY) {
			// Apply the mapper to each position in the shape
			for(BlockPos pos : event.getShape().createPositions(event.getCenter(), event.getRadius())) {
				// verify position is not in blacklist
				if(event.getBlacklist().contains(pos)) {
					continue;
				}
				// determine the new block state using the mapper
				BlockState oldState = entity.asMob().level().getBlockState(pos);
				BlockState newState = event.getMapper().map(entity.asMob(), pos, oldState);
				// update the block state
				if(oldState != newState) {
					entity.asMob().level().setBlock(pos, newState, event.getUpdateFlag());
				}
			}
		}
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AoeBehavior)) return false;
		AoeBehavior that = (AoeBehavior) o;
		return radius == that.radius && interval == that.interval && shape == that.shape;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), radius, interval, shape);
	}

}
