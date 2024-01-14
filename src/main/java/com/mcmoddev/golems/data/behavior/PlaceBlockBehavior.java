package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.data.behavior.util.TargetType;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.PlaceBlocksGoal;
import com.mcmoddev.golems.util.DeferredBlockState;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This behavior allows an entity to place blocks at its current location.
 **/
@Immutable
public class PlaceBlockBehavior extends Behavior<GolemBase> {

	public static final Codec<PlaceBlockBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(TriggerType.CODEC.optionalFieldOf("trigger", TriggerType.TICK).forGetter(PlaceBlockBehavior::getTrigger))
			.and(TargetType.CODEC.optionalFieldOf("position", TargetType.SELF).forGetter(PlaceBlockBehavior::getPosition))
			.and(Codec.intRange(0, 255).optionalFieldOf("radius", 0).forGetter(PlaceBlockBehavior::getRadius))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 0.25D).forGetter(PlaceBlockBehavior::getChance))
			.and(EGCodecUtils.listOrElementCodec(DeferredBlockState.CODEC).fieldOf("block").forGetter(PlaceBlockBehavior::getBlocks))
			.and(Codec.STRING.fieldOf("display_name").forGetter(PlaceBlockBehavior::getDisplayNameKey))
			.and(Codec.BOOL.optionalFieldOf("must_survive", true).forGetter(PlaceBlockBehavior::mustSurvive))
			.apply(instance, PlaceBlockBehavior::new));

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
	/** True to call the mustSurvive method of the blocks **/
	private final boolean mustSurvive;

	public PlaceBlockBehavior(MinMaxBounds.Ints variant, TriggerType trigger, TargetType position, int radius, double chance, List<DeferredBlockState> blocks, String displayNameKey, boolean mustSurvive) {
		super(variant);
		this.trigger = trigger;
		this.position = position;
		this.radius = radius;
		this.chance = chance;
		this.blocks = blocks;
		this.displayNameKey = displayNameKey;
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
		if(!(entity.getRandom().nextDouble() < chance)) {
			return false;
		}
		switch (position) {
			case AREA:
				BlockPos pos = entity.blockPosition().offset(
					entity.getRandom().nextInt(radius * 2) - radius,
						entity.getRandom().nextInt(radius * 2) - radius,
						entity.getRandom().nextInt(radius * 2) - radius
				);
				return placeBlockAt(entity.level(), pos, entity.getRandom());
			case SELF:
				return placeBlockAt(entity.level(), entity.blockPosition(), entity.getRandom());
			case ENEMY:
				if(null == entity.getTarget()) {
					return false;
				}
				return placeBlockAt(entity.level(), entity.getTarget().blockPosition(), entity.getRandom());
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
		final BlockState blockState = Util.getRandom(blocks, random).get();
		// validate block can survive
		if(mustSurvive && !blockState.canSurvive(level, pos)) {
			return false;
		}
		// place the block
		return level.setBlock(pos, blockState, Block.UPDATE_ALL);
	}
}
