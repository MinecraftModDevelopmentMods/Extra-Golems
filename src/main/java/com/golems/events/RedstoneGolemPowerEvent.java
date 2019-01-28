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
@Deprecated
@Event.HasResult
@Cancelable
// UNUSED, REMOVE IN NEXT RELEASE
public final class RedstoneGolemPowerEvent extends Event {

	public final GolemBase golem;
	public final BlockPos posToAffect;

	protected int powerLevel;
	public int updateFlag = 3;

	public RedstoneGolemPowerEvent(final GolemBase golemBase, final BlockPos toAffect, final int defPower) {
		this.setResult(Result.ALLOW);
		this.golem = golemBase;
		this.posToAffect = toAffect;
		this.powerLevel = defPower;
	}

	public void setPowerLevel(final int toSet) {
		if (toSet > 15) {
			this.powerLevel = 15;
		} else if(toSet < 0) {
			this.powerLevel = 0;
		} else {
			this.powerLevel = toSet;
		}
	}

	public int getPowerLevel() {
		return this.powerLevel;
	}
}
