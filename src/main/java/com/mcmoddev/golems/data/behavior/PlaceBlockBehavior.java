package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TargetType;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.WorldPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.DeferredBlockState;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
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
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to place blocks nearby
 **/
@Immutable
public class PlaceBlockBehavior extends Behavior<GolemBase> {

	public static final Codec<PlaceBlockBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Behavior::getVariant),
			TriggerType.CODEC.optionalFieldOf("trigger", TriggerType.TICK).forGetter(PlaceBlockBehavior::getTrigger),
			TargetType.CODEC.optionalFieldOf("position", TargetType.SELF).forGetter(PlaceBlockBehavior::getPosition),
			Codec.intRange(0, 8).optionalFieldOf("radius", 0).forGetter(PlaceBlockBehavior::getRadius),
			Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(PlaceBlockBehavior::getChance),
			EGCodecUtils.listOrElementCodec(DeferredBlockState.CODEC).fieldOf("block").forGetter(PlaceBlockBehavior::getBlocks),
			Codec.STRING.fieldOf("display_name").forGetter(PlaceBlockBehavior::getDisplayNameKey),
			EGCodecUtils.listOrElementCodec(WorldPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(WorldPredicate.ALWAYS)).forGetter(PlaceBlockBehavior::getPredicates),
			Codec.BOOL.optionalFieldOf("must_survive", true).forGetter(PlaceBlockBehavior::mustSurvive)
	).apply(instance, PlaceBlockBehavior::new));

	/** The trigger to place the blocks **/
	private final TriggerType trigger;
	/** The target to place blocks **/
	private final TargetType position;
	/** The radius to place blocks **/
	private final int radius;
	/** The chance to apply **/
	private final double chance;
	/** The blocks to place **/
	private final List<DeferredBlockState> blocks;
	/** The translation key for the display name of the blocks **/
	private final String displayNameKey;
	/** The conditions to place a block **/
	private final List<WorldPredicate> predicates;
	/** The conditions to place a block as a single predicate **/
	private final Predicate<GolemBase> predicate;
	/** True to call the mustSurvive method of the blocks **/
	private final boolean mustSurvive;

	public PlaceBlockBehavior(MinMaxBounds.Ints variant, TriggerType trigger, TargetType position, int radius, double chance, List<DeferredBlockState> blocks, String displayNameKey, List<WorldPredicate> predicates, boolean mustSurvive) {
		super(variant);
		this.trigger = trigger;
		this.position = position;
		this.radius = radius;
		this.chance = chance;
		this.blocks = blocks;
		this.displayNameKey = displayNameKey;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.mustSurvive = mustSurvive;
	}

	//// GETTERS ////

	public TriggerType getTrigger() {
		return trigger;
	}

	public TargetType getPosition() {
		return position;
	}

	public int getRadius() {
		return radius;
	}

	public double getChance() {
		return chance;
	}

	public List<DeferredBlockState> getBlocks() {
		return blocks;
	}

	public String getDisplayNameKey() {
		return displayNameKey;
	}

	public List<WorldPredicate> getPredicates() {
		return predicates;
	}

	public boolean mustSurvive() {
		return mustSurvive;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.PLACE_BLOCK.get();
	}

	//// METHODS ////

	@Override
	public void onTick(GolemBase entity) {
		if(trigger == TriggerType.TICK) {
			placeBlock(entity);
		}
	}

	@Override
	public void onHurtTarget(GolemBase entity, Entity target) {
		if(trigger == TriggerType.ATTACK) {
			placeBlock(entity);
		}
	}

	@Override
	public void onActuallyHurt(GolemBase entity, DamageSource source, float amount) {
		if(trigger == TriggerType.HURT) {
			placeBlock(entity);
		}
	}

	@Override
	public List<Component> createDescriptions() {
		// TODO add block to tooltip
		// TODO add trigger and predicate to tooltip
		return ImmutableList.of(Component.translatable("entitytip.places_blocks", Component.translatable(displayNameKey)).withStyle(ChatFormatting.GREEN));
	}

	/**
	 * Determines where to place the block and attempts to place it
	 * @param entity the entity
	 * @return the return value of {@link #placeBlockAt(Level, BlockPos, RandomSource)}
	 */
	protected boolean placeBlock(final GolemBase entity) {
		if(blocks.isEmpty()) {
			return false;
		}
		if(!predicate.test(entity)) {
			return false;
		}
		if(!(entity.getRandom().nextDouble() < chance)) {
			return false;
		}
		// determine block position
		BlockPos pos = entity.blockPosition();
		if(radius > 0) {
			pos = entity.blockPosition().offset(
					entity.getRandom().nextInt(radius * 2) - radius,
					entity.getRandom().nextInt(radius * 2) - radius,
					entity.getRandom().nextInt(radius * 2) - radius
			);
		}
		// place the block based on target type
		switch (position) {
			case AREA:
			case SELF:
				return placeBlockAt(entity.level(), pos, entity.getRandom());
			case ENEMY:
				if(null == entity.getTarget()) {
					return false;
				}
				pos = entity.getTarget().blockPosition().offset(pos.subtract(entity.blockPosition()));
				return placeBlockAt(entity.level(), pos, entity.getRandom());
		}

		return false;
	}

	/**
	 * @param level the level
	 * @param pos the block position
	 * @param random a random instance
	 * @return True if the block position was replacable, the block could survive, and it was sucessfully placed
	 */
	protected boolean placeBlockAt(final Level level, final BlockPos pos, final RandomSource random) {
		final BlockState replace = level.getBlockState(pos);
		// validate block can be replaced
		if(!replace.canBeReplaced()) {
			return false;
		}
		// determine the block to place
		BlockState blockState = Util.getRandom(blocks, random).get();
		// update waterlogged property if applicable
		if(blockState.hasProperty(BlockStateProperties.WATERLOGGED) && replace.is(Blocks.WATER) && replace.getFluidState().isSource()) {
			blockState = blockState.setValue(BlockStateProperties.WATERLOGGED, true);
		}
		// validate block can survive
		if(mustSurvive && !(blockState.canSurvive(level, pos) && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP, SupportType.FULL))) {
			return false;
		}
		// place the block
		return level.setBlock(pos, blockState, Block.UPDATE_ALL);
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PlaceBlockBehavior)) return false;
		if (!super.equals(o)) return false;
		PlaceBlockBehavior that = (PlaceBlockBehavior) o;
		return radius == that.radius && Double.compare(that.chance, chance) == 0 && mustSurvive == that.mustSurvive && trigger == that.trigger && position == that.position && blocks.equals(that.blocks) && displayNameKey.equals(that.displayNameKey) && predicates.equals(that.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), trigger, position, radius, chance, blocks, displayNameKey, predicates, mustSurvive);
	}
}
