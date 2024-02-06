package com.mcmoddev.golems.client.menu.guide_book;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.golem.GolemBuildingBlocks;
import com.mcmoddev.golems.data.golem.GolemPart;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Holds information about golems, building blocks, and descriptions
 * to display in the Guide Book
 **/
public class GuideBookEntry implements ITableOfContentsEntry {

	public static final Comparator<GuideBookEntry> SORT_BY_NAME = Comparator.comparing(o -> o.getTitle().getString());
	public static final Comparator<GuideBookEntry> SORT_BY_NAMESPACE = Comparator.comparing(o -> o.getId().getNamespace());

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
		this.image = resolveImage(this.id).orElse(null);
		this.health = container.getAttributes().getHealth();
		this.attack = container.getAttributes().getAttack();
		this.title = container.getTypeName();
		// collect blocks
		final GolemBuildingBlocks buildingBlocks = container.getGolem().getBlocks();
		final Collection<Block> blocks = new HashSet<>();
		if(container.getGolem().getGroup() != null && !buildingBlocks.getBlocks().containsKey(GolemPart.ALL) && buildingBlocks.getBlocks().containsKey(GolemPart.BODY)) {
			// only show body blocks if there are any.
			// this ensures that golems in groups do not appear to have identical blocks
			// when the only difference is the body block, which is often the case.
			blocks.addAll(buildingBlocks.getBlocks().get(GolemPart.BODY).get());
		} else {
			// otherwise, show all blocks
			blocks.addAll(buildingBlocks.get());
		}
		// create item stacks
		final ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		for(Block b : blocks) {
			builder.add(new ItemStack(b));
		}
		this.items = builder.build();
		// collect descriptions
		this.descriptionList = ImmutableList.copyOf(container.createDescriptions(registryAccess, TooltipFlag.ADVANCED));
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
		ResourceLocation img = id.withPrefix("textures/gui/guide_book/").withSuffix(".png");
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
	public Component getAdvancedMessage(int index) {
		return Component.literal(getId().toString()).withStyle(ChatFormatting.GRAY);
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

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GuideBookEntry)) return false;
		GuideBookEntry that = (GuideBookEntry) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
