package com.mcmoddev.golems.data.golem;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Immutable
public class RepairItems {

	public static final RepairItems EMPTY = new RepairItems(ImmutableMap.of());

	public static final Codec<RepairItems> CODEC = Codec.unboundedMap(ResourcePair.CODEC, Codec.doubleRange(0.0D, 1.0D))
			.xmap(RepairItems::new, RepairItems::getMap);

	private final Map<ResourcePair, Double> map;

	private final Map<TagKey<Item>, Double> tagMap;
	private final Map<ResourceLocation, Double> itemMap;

	public RepairItems(Map<ResourcePair, Double> map) {
		this.map = ImmutableMap.copyOf(map);
		// parse maps
		final ImmutableMap.Builder<TagKey<Item>, Double> tagMapBuilder = ImmutableMap.builder();
		final ImmutableMap.Builder<ResourceLocation, Double> itemMapBuilder = ImmutableMap.builder();
		for(Map.Entry<ResourcePair, Double> entry : map.entrySet()) {
			if(entry.getKey().flag()) {
				tagMapBuilder.put(ForgeRegistries.ITEMS.tags().createTagKey(entry.getKey().resource()), entry.getValue());
			} else {
				itemMapBuilder.put(entry.getKey().resource(), entry.getValue());
			}
		}
		this.tagMap = tagMapBuilder.build();
		this.itemMap = itemMapBuilder.build();
	}

	//// GETTERS ////

	public Map<ResourcePair, Double> getMap() {
		return map;
	}

	public Map<TagKey<Item>, Double> getTagMap() {
		return tagMap;
	}

	public Map<ResourceLocation, Double> getItemMap() {
		return itemMap;
	}

	/**
	 * @param itemStack an item stack
	 * @return the repair amount for the given item stack, or 0 if none is found
	 */
	public double getRepairAmount(final ItemStack itemStack) {
		// resolve id
		final ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
		if(this.itemMap.containsKey(itemId)) {
			return this.itemMap.get(itemId);
		}
		// resolve reverse tag
		final Optional<IReverseTag<Item>> reverseTag = ForgeRegistries.ITEMS.tags().getReverseTag(itemStack.getItem());
		if(reverseTag.isPresent()) {
			for(TagKey<Item> tagKey : reverseTag.get().getTagKeys().toList()) {
				if(this.tagMap.containsKey(tagKey)) {
					return this.tagMap.get(tagKey);
				}
			}
		}
		return 0.0D;
	}

	//// BUILDER ////

	public static class Builder {
		private Map<ResourcePair, Double> map;

		public Builder() {
			this.map = new HashMap<>();
		}

		public Builder(Map<ResourcePair, Double> map) {
			this.map = new HashMap<>(map);
		}

		public Builder(RepairItems repairItems) {
			this(repairItems.getMap());
		}

		/**
		 * @param map the map of resource pair and double
		 * @return the builder instance
		 */
		public Builder addAll(final Map<ResourcePair, Double> map) {
			this.map.putAll(map);
			return this;
		}

		/**
		 * @param id the resource pair of the entry
		 * @param amount the amount to heal
		 * @return the builder instance
		 */
		public Builder add(final ResourcePair id, final double amount) {
			this.map.put(id, amount);
			return this;
		}

		/**
		 * @param predicate a predicate for entries to remove
		 * @return the builder instance
		 */
		public Builder remove(final Predicate<Map.Entry<ResourcePair, Double>> predicate) {
			final Set<ResourcePair> keys = new HashSet<>();
			// test each entry
			for(Map.Entry<ResourcePair, Double> entry : this.map.entrySet()) {
				if(predicate.test(entry)) {
					keys.add(entry.getKey());
				}
			}
			// remove entries that passed the predicate
			keys.forEach(this.map::remove);
			return this;
		}

		/**
		 * @return the builder instance
		 */
		public Builder clear() {
			this.map.clear();
			return this;
		}

		/**
		 * @return a new {@link RepairItems} instance
		 */
		public RepairItems build() {
			return new RepairItems(this.map);
		}
	}
}
