package com.mcmoddev.golems.entity;

public interface IFuelConsumer {

	/**
	 * @param fuel the new amount of fuel
	 **/
	void setFuel(final int fuel);

	/**
	 * @return the amount of fuel remaining
	 **/
	int getFuel();

	/**
	 * @return the maximum amount of fuel
	 **/
	int getMaxFuel();

	/**
	 * @return true if the fuel level is above zero
	 **/
	default boolean hasFuel() {
		return getFuel() > 0;
	}

	/**
	 * @return a number between 0.0 and 1.0 to indicate fuel level
	 **/
	default float getFuelPercentage() {
		return (float) getFuel() / (float) getMaxFuel();
	}

	/**
	 * @param toAdd the amount of fuel to add or subtract
	 **/
	default void addFuel(final int toAdd) {
		if (toAdd != 0) {
			setFuel(getFuel() + toAdd);
		}
	}
}
