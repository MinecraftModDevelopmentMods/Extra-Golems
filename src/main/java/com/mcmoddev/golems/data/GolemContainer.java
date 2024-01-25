package com.mcmoddev.golems.data;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.behavior.WearBannerBehavior;
import com.mcmoddev.golems.data.golem.Attributes;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.data.modifier.ModifierList;
import com.mcmoddev.golems.data.modifier.Priority;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the ID and values of a {@link Golem} after applying {@link ModifierList}s
 * for the given ID
 */
public class GolemContainer {

	/** The ID of the golem object **/
	private final ResourceLocation id;
	/** The holder for the original {@link Golem} instance **/
	private final Holder<Golem> holder;
	/** The reconstructed {@link Golem} instance **/
	private final Golem golem;
	/** The type description to display **/
	private final Component typeName;
	/** The loot table ID **/
	private final ResourceLocation lootTable;

	private GolemContainer(final RegistryAccess registryAccess, final ResourceLocation id) {
		this.id = id;
		// load golem
		final Registry<Golem> golemRegistry = registryAccess.registryOrThrow(EGRegistry.Keys.GOLEM);
		final Golem wrapped = golemRegistry.getOptional(id).orElseThrow();
		this.holder = golemRegistry.wrapAsHolder(wrapped);
		// load modifiers
		final Map<Priority, Collection<ModifierList>> modifiers = new EnumMap<>(Priority.class);
		registryAccess.registryOrThrow(EGRegistry.Keys.MODIFIER_LIST).entrySet()
				.stream()
				.filter(entry -> id.equals(entry.getValue().getTarget()))
				.map(Map.Entry::getValue)
				.forEach(list -> modifiers.computeIfAbsent(list.getPriority(), p -> new ArrayList<>()).add(list));
		// initialize builders
		final Golem.Builder builder = Golem.Builder.from(registryAccess, wrapped);
		// add default behaviors
		builder.behaviors(b -> b.add(WearBannerBehavior.ANY));
		// apply each modifier in order of priority
		for(Priority priority : Priority.values()) {
			for(ModifierList modifierList : modifiers.getOrDefault(priority, ImmutableList.of())) {
				modifierList.getModifiers().forEach(m -> m.apply(builder));
			}
		}
		// create golem
		this.golem = builder.build();
		// create description
		this.typeName = Component.translatable("entity." + id.getNamespace() + ".golem." + id.getPath());
		// create loot table ID
		this.lootTable = id.withPrefix("entities/golem/");
	}

	//// GETTERS ////

	/** @return The ID of the golem object **/
	public ResourceLocation getId() {
		return id;
	}

	/** @return The reconstructed {@link Golem} instance **/
	public Golem getGolem() {
		return golem;
	}

	/** @return The type description to display **/
	public Component getTypeName() {
		return typeName;
	}

	/** @return The loot table ID **/
	public ResourceLocation getLootTable() {
		return this.lootTable;
	}

	/** @return The holder for the original {@link Golem} instance **/
	public Holder<Golem> getHolder() {
		return holder;
	}

	/** @return The reconstructed {@link Attributes} instance **/
	public Attributes getAttributes() {
		return golem.getAttributes();
	}

	/** @return The reconstructed {@link LayerList} instance **/
	public LayerList getModel() {
		return golem.getLayers().get();
	}

	/** @return The reconstructed {@link BehaviorList} instance **/
	public BehaviorList getBehaviors() {
		return golem.getBehaviors().get();
	}

	public List<Component> createDescriptions(final RegistryAccess registryAccess) {
		final List<Component> list = new ArrayList<>();
		// create attribute descriptions
		getAttributes().onAddDescriptions(this, registryAccess, list);
		// create behavior descriptions
		getBehaviors().forEach(b -> b.onAddDescriptions(list));
		return list;
	}

	//// REGISTRY ////

	private static final Map<ResourceLocation, GolemContainer> REGISTRY = new HashMap<>();

	public static GolemContainer getOrCreate(final RegistryAccess registryAccess, final ResourceLocation id) {
		// get existing entry
		final GolemContainer entry = REGISTRY.get(id);
		if(entry != null) {
			return entry;
		}
		// create new entry
		final GolemContainer container = new GolemContainer(registryAccess, id);
		REGISTRY.put(id, container);
		return container;
	}
}
