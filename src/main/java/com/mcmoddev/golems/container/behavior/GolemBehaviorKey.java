package com.mcmoddev.golems.container.behavior;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class GolemBehaviorKey<T extends GolemBehavior> {

	private final ResourceLocation id;
	private final Class<T> clazz;

	public GolemBehaviorKey(ResourceLocation id, Class<T> clazz) {
		this.id = id;
		this.clazz = clazz;
	}

	public ResourceLocation getId() {
		return id;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GolemBehaviorKey)) return false;
		GolemBehaviorKey<?> that = (GolemBehaviorKey<?>) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
