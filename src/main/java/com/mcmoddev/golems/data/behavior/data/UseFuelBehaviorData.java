package com.mcmoddev.golems.data.behavior.data;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.UseFuelBehavior;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractGolem;

import java.util.List;
import java.util.Optional;

/**
 * Used by {@link UseFuelBehavior} to manage fuel
 */
public class UseFuelBehaviorData implements IBehaviorData {

	// DATA //
	private final IExtraGolem entity;
	private final int maxFuel;
	private final int burnTime;

	public UseFuelBehaviorData(final IExtraGolem entity, final UseFuelBehavior behavior) {
		this.entity = entity;
		this.maxFuel = behavior.getMaxFuel();
		this.burnTime = behavior.getBurnTime();
	}

	//// METHODS ////

	/** @return the maximum amount of fuel **/
	public int getMaxFuel() {
		return maxFuel;
	}

	/** @return true if the fuel level is above zero */
	public boolean hasFuel() {
		return entity.getFuel() > 0;
	}

	public int getBurnTime() {
		return burnTime;
	}

	/** @return a number between 0.0 and 1.0 to indicate fuel level **/
	public float getFuelPercentage() {
		return (float) entity.getFuel() / (float) maxFuel;
	}

	/** @param amount the amount of fuel to add or subtract **/
	public void addFuel(final int amount) {
		if (amount != 0) {
			entity.setFuel(entity.getFuel() + amount);
		}
	}

	/** @param fuel the fuel amount **/
	public void setFuel(final int fuel) {
		entity.setFuel(fuel);
	}

	/** @return the fuel amount **/
	public int getFuel() {
		return entity.getFuel();
	}

	//// NBT ////

	private static final String KEY_FUEL = "Fuel";

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag tag = new CompoundTag();
		tag.putInt(KEY_FUEL, entity.getFuel());
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		entity.setFuel(tag.getInt(KEY_FUEL));
	}
}
