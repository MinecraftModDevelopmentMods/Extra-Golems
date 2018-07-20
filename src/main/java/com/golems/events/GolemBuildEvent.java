package com.golems.events;

import com.golems.entity.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GolemBuildEvent extends Event {

	/** The world in which theGolem was built **/
	public final World worldObj;
	/** The X,Y,Z coordinates of the Golem Head block **/
	public final BlockPos headPos;
	/** The Block type being used to build the golem **/
	public final Block blockBelow;
	/** The metadata of blockBelow **/
	public final IBlockState blockState;
	/** Whether the golem's arms are aligned on the x-axis **/
	public final boolean isGolemXAligned;
	/** Whether all 4 construction blocks are identical metadata **/
	public final boolean areBlocksSameMeta;

	/** The GolemBase to spawn if possible **/
	private GolemBase theGolem;
	/** Whether theGolem is not allowed to be spawned **/
	private boolean isGolemBanned;

	public GolemBuildEvent(World world, final BlockPos pos, final boolean isXAligned) {
		this.worldObj = world;
		this.headPos = pos;
		this.blockBelow = world.getBlockState(pos.down(1)).getBlock();
		this.blockState = world.getBlockState(pos.down(1));
		this.isGolemXAligned = isXAligned;
		this.areBlocksSameMeta = this.getAreGolemBlocksSameMeta();
		this.theGolem = (GolemBase) null;
		this.isGolemBanned = false;
	}

	/** Assign this event a new GolemBase to spawn and its spawn permission **/
	public void setGolem(GolemBase golem, boolean isAllowedByConfig) {
		this.theGolem = golem;
		this.isGolemBanned = !isAllowedByConfig;
	}

	/** Assign this event a new GolemBase to spawn **/
	public void setGolem(GolemBase golem) {
		this.setGolem(golem, true);
	}

	public void setIsGolemBanned(boolean toSet) {
		this.isGolemBanned = toSet;
	}

	/** @return the GolemBase to spawn (may be null) **/
	public GolemBase getGolem() {
		return this.theGolem;
	}

	/** @return true if theGolem has not been initialized **/
	public boolean isGolemNull() {
		return this.theGolem == (GolemBase) null;
	}

	/**
	 * @return true if theGolem should not be spawned (even if it is not null).
	 **/
	public boolean isGolemBanned() {
		return this.isGolemBanned;
	}

	/** @return true if all 4 construction blocks have the same metadata **/
	protected boolean getAreGolemBlocksSameMeta() {
		// SOUTH=z++; WEST=x--; NORTH=z--; EAST=x++
		BlockPos[] armsX = { this.headPos.down(1).west(1), this.headPos.down(1).east(1) };
		BlockPos[] armsZ = { this.headPos.down(1).north(1), this.headPos.down(1).south(1) };
		int metaBelow1 = this.blockBelow.getMetaFromState(this.blockState);
		IBlockState state;
		state = this.worldObj.getBlockState(this.headPos.down(2));
		int metaBelow2 = this.blockBelow.getMetaFromState(state);
		state = this.isGolemXAligned ? this.worldObj.getBlockState(armsX[0])
				: this.worldObj.getBlockState(armsZ[0]);
		int metaArm1 = this.blockBelow.getMetaFromState(state);
		state = this.isGolemXAligned ? this.worldObj.getBlockState(armsX[1])
				: this.worldObj.getBlockState(armsZ[1]);
		int metaArm2 = this.blockBelow.getMetaFromState(state);

		return metaBelow1 == metaBelow2 && metaBelow2 == metaArm1 && metaArm1 == metaArm2;
	}
}
