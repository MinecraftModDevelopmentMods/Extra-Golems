package com.mcmoddev.golems.entity;

public interface ILightProvider {

	/** @return the light level to provide, from 0 to 15 **/
	int getLightLevel();

	/** @return true if {@link #getLightLevel()} is more than zero **/
	default boolean isProvidingLight() {
		return getLightLevel() > 0;
	}
}
