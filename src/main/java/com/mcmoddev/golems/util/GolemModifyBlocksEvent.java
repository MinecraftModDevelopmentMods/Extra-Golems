package com.mcmoddev.golems.util;

import com.mcmoddev.golems.data.behavior.util.AoeShape;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This event exists for other mods or addons to handle and modify
 * when the entity modifies a large number of blocks
 */
public final class GolemModifyBlocksEvent extends LivingEvent implements ICancellableEvent {
	
	public enum Result {
		DENY,
		DEFAULT,
		ALLOW
	}

	private Set<BlockPos> blacklist;
	private AoeMapper aoeMapper;

	private final IExtraGolem entity;
	private final BlockPos from;
	private final BlockPos to;
	private final BlockPos center;
	private final int radius;
	private final AoeShape shape;
	private int updateFlag;

	public GolemModifyBlocksEvent(final IExtraGolem golem, final BlockPos center, final int radius, final AoeShape shape, final AoeMapper aoeMapper) {
		super((LivingEntity)golem);
		this.setResult(Result.ALLOW);
		this.blacklist = new HashSet<>();
		this.entity = golem;
		this.center = center;
		this.from = shape.getFromPos(center, radius);
		this.to = shape.getToPos(center, radius);
		this.radius = radius;
		this.shape = shape;
		this.aoeMapper = aoeMapper;
		this.updateFlag = Block.UPDATE_ALL;
	}

	//// GETTERS ////

	public IExtraGolem getGolem() {
		return entity;
	}

	private Result result = Result.DEFAULT;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public AoeMapper getMapper() {
		return this.aoeMapper;
	}

	public BlockPos getCenter() {
		return center;
	}

	public int getRadius() {
		return radius;
	}

	public AoeShape getShape() {
		return shape;
	}

	public int getUpdateFlag() {
		return updateFlag;
	}

	public BlockPos getFrom() {
		return from;
	}

	public BlockPos getTo() {
		return to;
	}

	public Set<BlockPos> getBlacklist() {
		return blacklist;
	}

	//// SETTERS ////

	/**
	 * @param aoeMapper the new {@link AoeMapper}
	 **/
	public void setMapper(final AoeMapper aoeMapper) {
		this.aoeMapper = aoeMapper;
	}

	/**
	 * @param flag the flag to pass to {@link net.minecraft.world.level.Level#setBlock(BlockPos, BlockState, int)}
	 **/
	public void setUpdateFlag(final int flag) {
		this.updateFlag = flag;
	}

	//// METHODS ////

	/**
	 * @param pos a {@link BlockPos} that will not be affected
	 **/
	public void blacklist(final BlockPos pos) {
		this.blacklist.add(pos);
	}

	/**
	 * @param collection a collection of {@link BlockPos} that will not be affected
	 **/
	public void blacklist(final Collection<BlockPos> collection) {
		this.blacklist.addAll(collection);
	}
}
