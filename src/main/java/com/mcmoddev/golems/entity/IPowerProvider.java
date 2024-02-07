package com.mcmoddev.golems.entity;

public interface IPowerProvider {

	/** @return the power level to provide, from 0 to 15 **/
	int getPowerLevel();

	/** @return true if {@link #getPowerLevel()} is more than zero **/
	default boolean isProvidingPower() {
		return getPowerLevel() > 0;
	}
}
