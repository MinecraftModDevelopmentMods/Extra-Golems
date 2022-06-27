package com.mcmoddev.golems.container.behavior.parameter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ChangeMaterialBehaviorParameter extends BehaviorParameter {

	private final double chance;
	private final ResourceLocation material;

	public ChangeMaterialBehaviorParameter(final CompoundTag tag) {
		super();
		this.chance = tag.getDouble("chance");
		this.material = new ResourceLocation(tag.getString("material"));
	}

	/**
	 * @return the chance for this conditional texture to apply
	 **/
	public double getChance() {
		return chance;
	}

	/**
	 * @return the material ID
	 **/
	public ResourceLocation getMaterial() {
		return material;
	}


	@Override
	public String toString() {
		return "ChangeMaterialBehaviorParameter: chance[" + chance + "] material[" + material.toString() + "]";
	}
}
