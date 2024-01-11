package com.mcmoddev.golems.data;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.Behaviors;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.Model;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public class GolemContainer {

	private final ResourceLocation id;
	private final Golem golem;

	public GolemContainer(final RegistryAccess registryAccess, final ResourceLocation id) {
		this.id = id;
		// load golem
		final Golem wrapped = registryAccess.registryOrThrow(EGRegistry.Keys.GOLEMS).getOptional(id).orElseThrow();
		// initialize builder
		final Golem.Builder builder = Golem.Builder.from(wrapped);
		final Model.Builder modelBuilder = new Model.Builder(builder.getModel().get().getLayers());
		final Behaviors.Builder behaviorsBuilder = new Behaviors.Builder(builder.getBehaviors().get().getBehaviors());
		// load model modifiers
		// TODO
		// apply model modifiers to model builder
		// TODO
		// load behavior modifiers
		// TODO
		// apply behavior modifiers to behavior builder
		// TODO
	}

	//// GETTERS ////

	/**
	 * @return the ID of the golem object
	 */
	public ResourceLocation getId() {
		return id;
	}

	public Golem getGolem() {
		return golem;
	}

	public Model getModel() {
		return golem.getModel().get();
	}

	public Behaviors getBehaviors() {
		return golem.getBehaviors().get();
	}
}
