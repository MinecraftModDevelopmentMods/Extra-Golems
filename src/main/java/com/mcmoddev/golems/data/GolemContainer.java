package com.mcmoddev.golems.data;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.golem.Attributes;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.Model;
import com.mcmoddev.golems.data.modifier.GolemModifierList;
import com.mcmoddev.golems.data.modifier.Priority;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Contains the ID and values of a {@link Golem} after applying {@link GolemModifierList}s
 * for the given ID
 */
public class GolemContainer {

	private final ResourceLocation id;
	private final Holder<Golem> holder;
	private final Golem golem;

	public GolemContainer(final RegistryAccess registryAccess, final ResourceLocation id) {
		this.id = id;
		// load golem
		final Registry<Golem> golemRegistry = registryAccess.registryOrThrow(EGRegistry.Keys.GOLEMS);
		final Golem wrapped = golemRegistry.getOptional(id).orElseThrow();
		this.holder = golemRegistry.wrapAsHolder(wrapped);
		// load modifiers
		final Map<Priority, Collection<GolemModifierList>> modifiers = new EnumMap<>(Priority.class);
		registryAccess.registryOrThrow(EGRegistry.Keys.GOLEM_MODIFIER_LISTS).entrySet()
				.stream()
				.filter(entry -> id.equals(entry.getValue().getTarget()))
				.map(Map.Entry::getValue)
				.forEach(list -> modifiers.computeIfAbsent(list.getPriority(), p -> new ArrayList<>()).add(list));
		// initialize builders
		final Golem.Builder builder = Golem.Builder.from(wrapped);
		// apply each modifier in order of priority
		for(Priority priority : Priority.values()) {
			for(GolemModifierList modifierList : modifiers.getOrDefault(priority, ImmutableList.of())) {
				modifierList.getModifiers().forEach(m -> m.apply(builder));
			}
		}
		// create golem
		this.golem = builder.build();
	}

	//// GETTERS ////

	/** @return the ID of the golem object **/
	public ResourceLocation getId() {
		return id;
	}

	/** @return the reconstructed {@link Golem} instance **/
	public Golem getGolem() {
		return golem;
	}

	/** @return the holder for the original {@link Golem} instance **/
	public Holder<Golem> getHolder() {
		return holder;
	}

	/** @return the reconstructed {@link Attributes} instance **/
	public Attributes getAttributes() {
		return golem.getAttributes();
	}

	/** @return the reconstructed {@link Model} instance **/
	public Model getModel() {
		return golem.getModel().get();
	}

	/** @return the reconstructed {@link BehaviorList} instance **/
	public BehaviorList getBehaviors() {
		return golem.getBehaviors().get();
	}
}
