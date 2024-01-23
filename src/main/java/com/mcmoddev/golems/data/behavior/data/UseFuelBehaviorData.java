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
	private final EntityDataAccessor<Integer> fuel;
	private int maxFuel;
	private int burnTime;

	public UseFuelBehaviorData(final IExtraGolem entity, final EntityDataAccessor<Integer> fuel) {
		this.entity = entity;
		this.fuel = fuel;
		processBehaviors();
	}

	//// METHODS ////

	/** Loads the relevant behaviors from the IExtraGolem to initialize fields **/
	private void processBehaviors() {
		final Optional<GolemContainer> oContainer = entity.getContainer(entity.asMob().level().registryAccess());
		if(oContainer.isEmpty()) {
			return;
		}
		final List<UseFuelBehavior> behaviors = oContainer.get().getBehaviors().getBehaviors(UseFuelBehavior.class);
		if(behaviors.isEmpty()) {
			return;
		}
		this.maxFuel = behaviors.get(0).getMaxFuel();
		this.burnTime = behaviors.get(0).getBurnTime();
	}

	/** @param fuel the new amount of fuel **/
	public void setFuel(final int fuel) {
		entity.asMob().getEntityData().set(this.fuel, fuel);
	}

	/** @return the amount of fuel remaining **/
	public int getFuel() {
		return entity.asMob().getEntityData().get(fuel);
	}

	/** @return the maximum amount of fuel **/
	public int getMaxFuel() {
		return maxFuel;
	}

	/** @return true if the fuel level is above zero */
	public boolean hasFuel() {
		return getFuel() > 0;
	}

	public int getBurnTime() {
		return burnTime;
	}

	/** @return a number between 0.0 and 1.0 to indicate fuel level **/
	public float getFuelPercentage() {
		return (float) getFuel() / (float) maxFuel;
	}

	/** @param amount the amount of fuel to add or subtract **/
	public void addFuel(final int amount) {
		if (amount != 0) {
			setFuel(getFuel() + amount);
		}
	}

	//// NBT ////

	private static final String KEY_FUEL = "Fuel";

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag tag = new CompoundTag();
		tag.putInt(KEY_FUEL, getFuel());
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		setFuel(tag.getInt(KEY_FUEL));
	}
}
