package com.mcmoddev.golems.data.golem;

import com.mcmoddev.golems.util.ResourcePair;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Immutable
public class RepairItems {

	private final Map<ResourcePair, Double> map;

	public RepairItems(Map<ResourcePair, Double> map) {
		this.map = map;
	}

	public static class Builder {
		private Map<ResourcePair, Double> map;

		public Builder() {
			this.map = new HashMap<>();
		}

		public Builder(Map<ResourcePair, Double> map) {
			this.map = new HashMap<>(map);
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

		//// METHODS ////

		/**
		 * @return a new {@link RepairItems} instance
		 */
		public RepairItems build() {
			return new RepairItems(this.map);
		}
	}
}
