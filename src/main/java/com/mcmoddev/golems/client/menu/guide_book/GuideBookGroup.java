package com.mcmoddev.golems.client.menu.guide_book;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.client.menu.guide_book.book.ITableOfContentsEntry;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.golem.Golem;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Holds one or more {@link GuideBookEntry} objects with an optional group ID
 **/
public class GuideBookGroup implements ITableOfContentsEntry {
	public static final Comparator<GuideBookGroup> SORT_BY_ATTACK = Comparator.comparingDouble(GuideBookGroup::getAttack);
	public static final Comparator<GuideBookGroup> SORT_BY_HEALTH = Comparator.comparingDouble(GuideBookGroup::getHealth);
	public static final Comparator<GuideBookGroup> SORT_BY_NAME = Comparator.comparing(group -> (group.getTitle() != null ? group.getTitle() : group.getEntry(0).getTitle()).getString());

	private final List<GuideBookEntry> list;
	private final List<ItemStack> items;
	private final @Nullable ResourceLocation group;
	private final @Nullable Component title;
	private final double averageAttack;
	private final double averageHealth;

	//// CONSTRUCTOR ////

	public GuideBookGroup(GuideBookEntry entry) {
		this(ImmutableList.of(entry), entry.getContainer().getGolem().getGroup());
	}

	public GuideBookGroup(final List<GuideBookEntry> list, final @Nullable ResourceLocation group) {
		// validate list size
		if(list.isEmpty()) {
			throw new IllegalArgumentException("GuideBookGroup requires at least one Entry");
		}
		this.list = ImmutableList.sortedCopyOf(GuideBookEntry.SORT_BY_NAME, list);
		this.group = group;
		if(group != null) {
			this.title = Component.translatable("entity." + group.getNamespace() + ".golem.group." + group.getPath());
		} else {
			this.title = null;
		}
		// calculate average health and attack, and collect blocks and items
		double health = 0;
		double attack = 0;
		final ImmutableList.Builder<ItemStack> itemBuilder = ImmutableList.builder();
		for(GuideBookEntry entry : list) {
			health += entry.getHealth();
			attack += entry.getAttack();
			itemBuilder.addAll(entry.getItems());
		}
		this.averageHealth = health / list.size();
		this.averageAttack = attack / list.size();
		this.items = itemBuilder.build();
	}

	/**
	 * @param registryAccess the registry access
	 * @return an unsorted mutable list of {@link GuideBookGroup} objects, not including hidden Golem Containers.
	 */
	public static List<GuideBookGroup> buildGroups(final RegistryAccess registryAccess) {
		// load registry
		final Registry<Golem> registry = registryAccess.registryOrThrow(EGRegistry.Keys.GOLEM);
		// prepare data structures for groups and entries
		final Map<ResourceLocation, List<GuideBookEntry>> groups = new HashMap<>();
		final List<GuideBookGroup> list = new ArrayList<>();
		// collect groups
		for(ResourceLocation id : registry.keySet()) {
			// load golem container
			GolemContainer container = GolemContainer.getOrCreate(registryAccess, id);
			// verify not hidden
			if(container.getGolem().isHidden()) {
				continue;
			}
			// create entry
			GuideBookEntry entry = new GuideBookEntry(registryAccess, container);
			// add to map or directly to list
			if(container.getGolem().getGroup() != null) {
				groups.computeIfAbsent(container.getGolem().getGroup(), e -> new ArrayList<>()).add(entry);
			} else {
				list.add(new GuideBookGroup(entry));
			}
		}
		// convert group entry lists to groups
		for(Map.Entry<ResourceLocation, List<GuideBookEntry>> entry : groups.entrySet()) {
			list.add(new GuideBookGroup(entry.getValue(), entry.getKey()));
		}
		// build groups
		return list;
	}

	//// TABLE OF CONTENTS ENTRY ////

	@Override
	public List<ItemStack> getItems() {
		return this.items;
	}

	@Override
	public Component getMessage(int index) {
		return this.title != null ? this.title : this.getEntry(index / this.list.size()).getTitle();
	}

	//// GETTERS ////

	/** @return The entry list **/
	public List<GuideBookEntry> getList() {
		return list;
	}

	/**
	 * @param index the index
	 * @return the {@link GuideBookEntry} at {@code [index % arrayLen]}
	 */
	public GuideBookEntry getEntry(final int index) {
		return this.list.get(index % this.list.size());
	}

	/** @return the group ID, if any **/
	@Nullable
	public ResourceLocation getGroup() {
		return group;
	}

	/** @return the title of the group, if any **/
	@Nullable
	public Component getTitle() {
		return title;
	}

	/** @return the average health of all entries **/
	public double getHealth() {
		return averageHealth;
	}

	/** @return the average attack of all entries **/
	public double getAttack() {
		return averageAttack;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GuideBookGroup)) return false;
		GuideBookGroup that = (GuideBookGroup) o;
		return list.equals(that.list) && group.equals(that.group);
	}

	@Override
	public int hashCode() {
		return Objects.hash(list, group);
	}
}
