package com.mcmoddev.golems.event;

import com.mcmoddev.golems.data.behavior.util.AoeShape;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.AoeMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This event exists for other mods or addons to handle and modify
 * when the entity modifies a large number of blocks
 */
@Cancelable
public final class GolemModifyBlocksEvent extends LivingEvent {

	private Set<BlockPos> blacklist;
	private AoeMapper aoeMapper;

	private final GolemBase entity;
	private final BlockPos from;
	private final BlockPos to;
	private final BlockPos center;
	private final int radius;
	private final AoeShape shape;
	private int updateFlag;

	public GolemModifyBlocksEvent(final GolemBase golem, final BlockPos center, final int radius, final AoeShape shape, final AoeMapper aoeMapper) {
		super(golem);
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

	@Override
	public GolemBase getEntity() {
		return entity;
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
