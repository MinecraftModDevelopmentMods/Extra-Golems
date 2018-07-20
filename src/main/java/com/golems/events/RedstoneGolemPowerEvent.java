package com.golems.events;

import com.golems.entity.GolemBase;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when an EntityRedstoneGolem is about to place a BlockPowerProvider. This event exists for
 * other mods or addons to handle and modify the Redstone Golem's behavior. It is not handled in
 * Extra Golems.
 */
@Event.HasResult
@Cancelable
public class RedstoneGolemPowerEvent extends Event {

	public final GolemBase golem;
	public final BlockPos posToAffect;

	protected int powerLevel;
	public int updateFlag = 3;

	public RedstoneGolemPowerEvent(GolemBase golemBase, BlockPos toAffect, int defPower) {
		this.setResult(Result.ALLOW);
		this.golem = golemBase;
		this.posToAffect = toAffect;
		this.powerLevel = defPower;
	}

	public void setPowerLevel(int toSet) {
		this.powerLevel = toSet > 15 ? 15 : (toSet < 0 ? 0 : toSet);
	}

	public int getPowerLevel() {
		return this.powerLevel;
	}
}
