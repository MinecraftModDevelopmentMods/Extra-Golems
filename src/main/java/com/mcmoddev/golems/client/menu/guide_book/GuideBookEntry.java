package com.mcmoddev.golems.client.menu.guide_book;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.data.GolemContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Holds information about golems, building blocks, and descriptions
 * to display in the Guide Book
 **/
public class GuideBookEntry implements ITableOfContentsEntry {

	/*
	 * Size of the supplemental image for each entry, if one is present.
	 * Any image with a 2:1 ratio will render with no issues.
	 */
	public static final int IMAGE_WIDTH = 100;
	public static final int IMAGE_HEIGHT = 50;

	private final ResourceLocation id;
	private final GolemContainer container;
	private final List<ItemStack> items;
	private final @Nullable ResourceLocation image;
	private final double health;
	private final double attack;
	private final Component title;
	private final List<Component> descriptionList;
	private final Component description;

	public GuideBookEntry(final RegistryAccess registryAccess, final GolemContainer container) {
		this.id = container.getId();
		this.container = container;
		this.items = container.getGolem().getBlocks().get().stream().map(ItemStack::new).collect(ImmutableList.toImmutableList());
		this.image = resolveImage(this.id).orElse(null);
		this.health = container.getAttributes().getHealth();
		this.attack = container.getAttributes().getAttack();
		this.title = container.getTypeName();
		// collect descriptions
		this.descriptionList = ImmutableList.copyOf(container.createDescriptions(registryAccess));
		// build single component from description list
		final MutableComponent descriptionBuilder = Component.empty();
		for (Component c : this.descriptionList) {
			descriptionBuilder.append(c).append("\n");
		}
		// remove trailing newline
		descriptionBuilder.getSiblings().remove(descriptionBuilder.getSiblings().size() - 1);
		this.description = descriptionBuilder;
	}

	private static Optional<ResourceLocation> resolveImage(final ResourceLocation id) {
		ResourceLocation img = id.withPath("textures/gui/guide_book/").withSuffix(".png");
		Optional<Resource> imageResource = Minecraft.getInstance().getResourceManager().getResource(img);
		if (imageResource.isPresent()) {
			return Optional.of(img);
		}
		return Optional.empty();
	}

	//// TABLE OF CONTENTS ENTRY ////

	@Override
	public Component getMessage(int index) {
		return getTitle();
	}

	@Override
	public List<ItemStack> getItems() {
		return this.items;
	}

	//// GETTERS ////

	public ResourceLocation getId() {
		return id;
	}

	public GolemContainer getContainer() {
		return container;
	}

	@Nullable
	public ResourceLocation getImage() {
		return image;
	}

	public double getHealth() {
		return health;
	}

	public double getAttack() {
		return attack;
	}

	public Component getTitle() {
		return title;
	}

	public List<Component> getDescriptionList() {
		return descriptionList;
	}

	public Component getDescription() {
		return description;
	}
}
