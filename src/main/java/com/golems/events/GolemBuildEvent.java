package com.golems.events;

import com.golems.entity.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GolemBuildEvent extends Event {

	/**
	 * The world in which theGolem was built.
	 **/
	public final World worldObj;
	/**
	 * The Block type being used to build the golem.
	 **/
	public final Block blockBelow;
	/**
	 * The metadata of blockBelow.
	 **/
	public final IBlockState blockState;
	/**
	 * Whether the golem's arms are aligned on the x-axis.
	 **/
	public final boolean isGolemXAligned;
	/**
	 * Whether all 4 construction blocks are identical metadata.
	 **/
	public final boolean areBlocksSameMeta;

	/**
	 * The GolemBase to spawn if possible.
	 **/
	private GolemBase theGolem;
	/**
	 * Whether theGolem is not allowed to be spawned.
	 **/
	private boolean isGolemBanned;

	public GolemBuildEvent(final World world, final IBlockState blockBelowState, final boolean sameMeta, final boolean isXAligned) {
		this.worldObj = world;
		this.blockBelow = blockBelowState.getBlock();
		this.blockState = blockBelowState;
		this.isGolemXAligned = isXAligned;
		this.areBlocksSameMeta = sameMeta;
		this.theGolem = (GolemBase) null;
		this.isGolemBanned = false;
	}

	/**
	 * Assign this event a new GolemBase to spawn and its spawn permission.
	 **/
	public void setGolem(final GolemBase golem, final boolean isAllowedByConfig) {
		this.theGolem = golem;
		this.isGolemBanned = !isAllowedByConfig;
	}

	/**
	 * Assign this event a new GolemBase to spawn.
	 **/
	public void setGolem(final GolemBase golem) {
		this.setGolem(golem, true);
	}

	public void setIsGolemBanned(final boolean toSet) {
		this.isGolemBanned = toSet;
	}

	/**
	 * @return the GolemBase to spawn (may be null)
	 **/
	public GolemBase getGolem() {
		return this.theGolem;
	}

	/**
	 * @return true if theGolem has not been initialized
	 **/
	public boolean isGolemNull() {
		return this.theGolem == (GolemBase) null;
	}

	/**
	 * @return true if theGolem should not be spawned (even if it is not null).
	 **/
	public boolean isGolemBanned() {
		return this.isGolemBanned;
	}
}
