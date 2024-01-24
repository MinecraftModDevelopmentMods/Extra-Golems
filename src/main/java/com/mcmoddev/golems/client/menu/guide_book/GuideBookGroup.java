package com.mcmoddev.golems.client.menu.guide_book;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.golem.Golem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Holds one or more {@link GuideBookGroup.Entry} objects with an optional group ID
 **/
public class GuideBookGroup {

	private final List<GuideBookGroup.Entry> list;
	private final List<Block> blocks;
	private final @Nullable ResourceLocation group;
	private final @Nullable Component title;
	private final double averageAttack;
	private final double averageHealth;

	//// CONSTRUCTOR ////

	public GuideBookGroup(GuideBookGroup.Entry entry) {
		this(ImmutableList.of(entry), entry.container.getGolem().getGroup());
	}

	public GuideBookGroup(final List<GuideBookGroup.Entry> list, final @Nullable ResourceLocation group) {
		// validate list size
		if(list.isEmpty()) {
			throw new IllegalArgumentException("GuideBookGroup requires at least one Entry");
		}
		this.list = ImmutableList.copyOf(list);
		this.group = group;
		this.title = (group != null) ? Component.translatable(group.toLanguageKey("guide_book.group")) : null;
		// calculate average health and attack, and collect blocks
		double health = 0;
		double attack = 0;
		final ImmutableList.Builder<Block> blockBuilder = ImmutableList.builder();
		for(GuideBookGroup.Entry entry : list) {
			health += entry.getHealth();
			attack += entry.getAttack();
			blockBuilder.add(entry.getBlocks());
		}
		this.averageHealth = health / list.size();
		this.averageAttack = attack / list.size();
		this.blocks = blockBuilder.build();
	}

	/**
	 * @param registryAccess the registry access
	 * @return an unsorted mutable list of {@link GuideBookGroup} objects, not including hidden Golem Containers.
	 */
	public static List<GuideBookGroup> buildGroups(final RegistryAccess registryAccess) {
		// load registry
		final Registry<Golem> registry = registryAccess.registryOrThrow(EGRegistry.Keys.GOLEMS);
		// prepare data structures for groups and entries
		final Map<ResourceLocation, List<GuideBookGroup.Entry>> groups = new HashMap<>();
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
			GuideBookGroup.Entry entry = new GuideBookGroup.Entry(registryAccess, container);
			// add to map or directly to list
			if(container.getGolem().getGroup() != null) {
				groups.computeIfAbsent(container.getGolem().getGroup(), e -> new ArrayList<>()).add(entry);
			} else {
				list.add(new GuideBookGroup(entry));
			}
		}
		// convert group entry lists to groups
		for(Map.Entry<ResourceLocation, List<GuideBookGroup.Entry>> entry : groups.entrySet()) {
			list.add(new GuideBookGroup(entry.getValue(), entry.getKey()));
		}
		// build groups
		return list;
	}

	//// GETTERS ////

	/** @return {@code true} if the list has exactly one element **/
	public boolean isSingleton() {
		return this.list.size() == 1;
	}

	/** @return The entry list **/
	public List<Entry> getList() {
		return list;
	}

	/**
	 * @param index the index
	 * @return the {@link Entry} at {@code [index % arrayLen]}
	 */
	public Entry getEntry(final int index) {
		return this.list.get(index % this.list.size());
	}

	/** @return the group ID, if any **/
	@Nullable
	public ResourceLocation getGroup() {
		return group;
	}

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

	/** @return all blocks for all entries in this group **/
	public List<Block> getBlocks() {
		return blocks;
	}

	/**
	 * @param index the index
	 * @return the Block at {@code [index % arrayLen]} or {@link Blocks#AIR} if the array is empty
	 **/
	public Block getBlock(final int index) {
		if(!this.blocks.isEmpty()) {
			return this.blocks.get(index % this.blocks.size());
		}
		return Blocks.AIR;
	}

	//// CLASSES ////

	/**
	 * Holds information about golems, building blocks, and descriptions
	 * to display in the Guide Book
	 **/
	public static class Entry {

		/*
		 * size of the supplemental image for each entry, if one is present.
		 * Any image with a 2:1 ratio will render with no issues.
		 */
		public static final int IMAGE_WIDTH = 100;
		public static final int IMAGE_HEIGHT = 50;

		private final ResourceLocation id;
		private final GolemContainer container;
		private final Block[] blocks;
		private final @Nullable ResourceLocation image;
		private final double health;
		private final double attack;
		private final Component title;
		private final List<Component> descriptionList;
		private final Component description;

		public Entry(final RegistryAccess registryAccess, final GolemContainer container) {
			this.id = container.getId();
			this.container = container;
			this.blocks = container.getGolem().getBlocks().get().toArray(new Block[0]);
			this.image = resolveImage(this.id).orElse(null);
			this.health = container.getAttributes().getHealth();
			this.attack = container.getAttributes().getAttack();
			this.title = container.getTypeName();
			// collect descriptions
			this.descriptionList = ImmutableList.copyOf(container.createDescriptions(registryAccess));
			// build single component from description list
			final MutableComponent descriptionBuilder = Component.empty();
			for(Component c : this.descriptionList) {
				descriptionBuilder.append(c).append("\n");
			}
			// remove trailing newline
			descriptionBuilder.getSiblings().remove(descriptionBuilder.getSiblings().size() - 1);
			this.description = descriptionBuilder;
		}

		private static Optional<ResourceLocation> resolveImage(final ResourceLocation id) {
			ResourceLocation img = id.withPath("textures/gui/guide_book/").withSuffix(".png");
			Optional<Resource> imageResource = Minecraft.getInstance().getResourceManager().getResource(img);
			if(imageResource.isPresent()) {
				return Optional.of(img);
			}
			return Optional.empty();
		}

		//// GETTERS ////

		public ResourceLocation getId() {
			return id;
		}

		public GolemContainer getContainer() {
			return container;
		}

		public Block[] getBlocks() {
			return blocks;
		}

		/**
		 * @param index the index
		 * @return the Block at {@code [index % arrayLen]} or {@link Blocks#AIR} if the array is empty
		 **/
		public Block getBlock(final int index) {
			if(this.blocks.length > 0) {
				return this.blocks[index % this.blocks.length];
			}
			return Blocks.AIR;
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
}
