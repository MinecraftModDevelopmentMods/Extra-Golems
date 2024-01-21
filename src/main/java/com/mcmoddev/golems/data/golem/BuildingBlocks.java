package com.mcmoddev.golems.data.golem;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Immutable
public class BuildingBlocks implements Supplier<Collection<Block>> {

	public static final BuildingBlocks EMPTY = new BuildingBlocks(ImmutableList.of());

	public static final Codec<BuildingBlocks> CODEC = EGCodecUtils.listOrElementCodec(ResourcePair.CODEC)
			.xmap(BuildingBlocks::new, BuildingBlocks::getList);

	private final List<ResourcePair> list;

	private final List<TagKey<Block>> tagList;
	private final List<ResourceLocation> blockList;

	public BuildingBlocks(List<ResourcePair> list) {
		this.list = ImmutableList.copyOf(list);
		// parse list
		final ImmutableList.Builder<TagKey<Block>> tagMapBuilder = ImmutableList.builder();
		final ImmutableList.Builder<ResourceLocation> blockMapBuilder = ImmutableList.builder();
		for(ResourcePair entry : list) {
			if(entry.flag()) {
				tagMapBuilder.add(ForgeRegistries.BLOCKS.tags().createTagKey(entry.resource()));
			} else {
				blockMapBuilder.add(entry.resource());
			}
		}
		this.tagList = tagMapBuilder.build();
		this.blockList = blockMapBuilder.build();
	}

	//// SUPPLIER ////

	@Override
	public Collection<Block> get() {
		final Set<Block> set = new HashSet<>();
		for(final )
	}

	//// GETTERS ////

	public List<ResourcePair> getList() {
		return list;
	}

	public List<TagKey<Block>> getTagList() {
		return tagList;
	}

	public List<ResourceLocation> getBlockList() {
		return blockList;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BuildingBlocks)) return false;
		BuildingBlocks that = (BuildingBlocks) o;
		return list.equals(that.list);
	}

	@Override
	public int hashCode() {
		return Objects.hash(list);
	}


	//// CLASSES ////

	public static class Builder {
		private List<ResourcePair> list;

		public Builder() {
			this.list = new ArrayList<>();
		}

		public Builder(List<ResourcePair> list) {
			this.list = new ArrayList<>(list);
		}

		/**
		 * @param list the list of resource pairs
		 * @return the builder instance
		 */
		public Builder addAll(final List<ResourcePair> list) {
			this.list.addAll(list);
			return this;
		}

		/**
		 * @param resource the resource pair
		 * @return the builder instance
		 */
		public Builder add(final ResourcePair resource) {
			this.list.add(resource);
			return this;
		}

		/**
		 * @param predicate a predicate for entries to remove
		 * @return the builder instance
		 */
		public Builder remove(final Predicate<ResourcePair> predicate) {
			this.list.removeIf(predicate);
			return this;
		}

		/**
		 * @return the builder instance
		 */
		public Builder clear() {
			this.list.clear();
			return this;
		}

		public BuildingBlocks build() {
			return new BuildingBlocks(list);
		}
	}
}
