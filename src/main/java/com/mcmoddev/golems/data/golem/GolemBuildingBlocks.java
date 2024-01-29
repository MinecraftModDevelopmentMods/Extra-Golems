package com.mcmoddev.golems.data.golem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Immutable
public class GolemBuildingBlocks implements Supplier<Collection<Block>> {

	public static final GolemBuildingBlocks EMPTY = new GolemBuildingBlocks();

	public static final Codec<GolemBuildingBlocks> CODEC = Codec.unboundedMap(GolemPart.CODEC, BuildingBlocks.CODEC)
			.xmap(o -> {
				BuildingBlocks all = o.get(GolemPart.ALL);
				if(all != null) {
					return new GolemBuildingBlocks(all);
				}
				BuildingBlocks arms = o.get(GolemPart.ARMS);
				if(arms != null) {
					return new GolemBuildingBlocks(o.get(GolemPart.BODY), o.get(GolemPart.LEGS), arms, arms);
				}
				return new GolemBuildingBlocks(o.get(GolemPart.BODY), o.get(GolemPart.LEGS), o.get(GolemPart.LEFT_ARM), o.get(GolemPart.RIGHT_ARM));
			}, GolemBuildingBlocks::getBlocks);

	private final Map<GolemPart, BuildingBlocks> blocks;
	private final Set<Block> cachedBlocks;

	//// CONSTRUCTOR ////

	private GolemBuildingBlocks() {
		this.blocks = ImmutableMap.of();
		this.cachedBlocks = ImmutableSet.of();
	}

	public GolemBuildingBlocks(BuildingBlocks all) {
		this.blocks = new EnumMap<>(GolemPart.class);
		this.blocks.put(GolemPart.ALL, all);
		this.cachedBlocks = new HashSet<>();
	}

	public GolemBuildingBlocks(BuildingBlocks body, BuildingBlocks legs, BuildingBlocks leftArm, BuildingBlocks rightArm) {
		this.blocks = new EnumMap<>(GolemPart.class);
		this.blocks.put(GolemPart.BODY, body);
		this.blocks.put(GolemPart.LEGS, legs);
		this.blocks.put(GolemPart.LEFT_ARM, leftArm);
		this.blocks.put(GolemPart.RIGHT_ARM, rightArm);
		this.cachedBlocks = new HashSet<>();
	}

	//// GETTERS ////

	/**
	 * @return an unmodifiable view of the blocks map.
	 * Note, this will NEVER contain {@link GolemPart#ARMS}, arm entries are always stored separately
	 **/
	public Map<GolemPart, BuildingBlocks> getBlocks() {
		return ImmutableMap.copyOf(this.blocks);
	}

	//// SUPPLIER ////

	@Override
	public Collection<Block> get() {
		if(this.cachedBlocks.isEmpty() && !this.blocks.isEmpty()) {
			// add blocks when ALL is present
			final BuildingBlocks all = blocks.get(GolemPart.ALL);
			if (all != null) {
				this.cachedBlocks.addAll(all.get());
				return this.cachedBlocks;
			}
			// add blocks for BODY, LEGS, LEFT_ARM, and RIGHT_ARM
			this.cachedBlocks.addAll(blocks.get(GolemPart.BODY).get());
			this.cachedBlocks.addAll(blocks.get(GolemPart.LEGS).get());
			this.cachedBlocks.addAll(blocks.get(GolemPart.LEFT_ARM).get());
			this.cachedBlocks.addAll(blocks.get(GolemPart.RIGHT_ARM).get());
		}
		return this.cachedBlocks;
	}

	//// METHODS ////

	/**
	 * @param body the body block
	 * @param legs the legs block
	 * @param arm1 the first arm block
	 * @param arm2 the second arm block
	 * @return true if the golem can be constructed with the given blocks
	 */
	public boolean matches(final Block body, final Block legs, final Block arm1, final Block arm2) {
		final BuildingBlocks all = blocks.get(GolemPart.ALL);
		if(all != null) {
			return all.test(body) && all.test(legs) && all.test(arm1) && all.test(arm2);
		}
		if(!blocks.get(GolemPart.BODY).test(body)) {
			return false;
		}
		if(!blocks.get(GolemPart.LEGS).test(legs)) {
			return false;
		}
		if(!blocks.get(GolemPart.LEFT_ARM).test(arm1)) {
			return false;
		}
		if(!blocks.get(GolemPart.RIGHT_ARM).test(arm2)) {
			return false;
		}
		// all checks passed
		return true;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GolemBuildingBlocks)) return false;
		GolemBuildingBlocks that = (GolemBuildingBlocks) o;
		return blocks.equals(that.blocks);
	}

	@Override
	public int hashCode() {
		return Objects.hash(blocks);
	}

	//// CLASSES ////

	public static class Builder {
		private final Map<GolemPart, BuildingBlocks.Builder> blocks;

		//// CONSTRUCTORS ////

		public Builder() {
			this.blocks = new EnumMap<>(GolemPart.class);
		}

		public Builder(final Map<GolemPart, BuildingBlocks> map) {
			this();
			for(Map.Entry<GolemPart, BuildingBlocks> entry : map.entrySet()) {
				this.blocks.put(entry.getKey(), new BuildingBlocks.Builder(entry.getValue()));
			}
		}

		public Builder(final GolemBuildingBlocks copy) {
			this(copy.getBlocks());
		}

		//// METHODS ////

		public Builder withAll(final BuildingBlocks.Builder builder) {
			this.blocks.put(GolemPart.ALL, builder);
			return this;
		}

		/**
		 * @param part
		 * @param builder the builder to use in place of the given part
		 * @return
		 */
		public Builder withPart(final GolemPart part, final BuildingBlocks.Builder builder) {
			this.blocks.put(part, builder);
			return this;
		}

		/**
		 * @param part the part to modify
		 * @param action an action to perform on the given part
		 * @return the builder instance
		 */
		public Builder apply(final GolemPart part, final Consumer<BuildingBlocks.Builder> action) {
			// delegate to all parts if applicable
			if(part == GolemPart.ALL) {
				return apply(action);
			}
			// apply action to each arm part if applicable
			if(part == GolemPart.ARMS) {
				apply(GolemPart.LEFT_ARM, action).apply(GolemPart.RIGHT_ARM, action);
			}
			// apply action to one part
			if(this.blocks.containsKey(part)) {
				action.accept(this.blocks.get(part));
			}
			return this;
		}

		public Builder apply(final Consumer<BuildingBlocks.Builder> action) {
			// apply action to all parts
			for(BuildingBlocks.Builder builder : this.blocks.values()) {
				action.accept(builder);
			}
			return this;
		}

		/**
		 * @return a newly constructed {@link GolemBuildingBlocks} object.
		 * @throws IllegalStateException when the map is missing {@link GolemPart#BODY} or {@link GolemPart#LEGS}
		 * @throws IllegalStateException when the map is missing {@link GolemPart#ARMS} and one of {@link GolemPart#LEFT_ARM} or {@link GolemPart#RIGHT_ARM}
		 */
		public GolemBuildingBlocks build() {
			// validate empty
			if(this.blocks.isEmpty()) {
				return EMPTY;
			}
			// validate all
			final BuildingBlocks.Builder all = this.blocks.get(GolemPart.ALL);
			if(all != null) {
				return new GolemBuildingBlocks(all.build());
			}
			// validate body and legs
			final BuildingBlocks.Builder body = this.blocks.get(GolemPart.BODY);
			final BuildingBlocks.Builder legs = this.blocks.get(GolemPart.LEGS);
			if(null == body || null == legs) {
				final List<GolemPart> required = Arrays.asList(GolemPart.BODY, GolemPart.LEGS);
				throw new IllegalStateException("Missing arguments in GolemBuildingBlocks.Builder; Required: " + required + " Provided: " + this.blocks.keySet());
			}
			// validate arms
			final BuildingBlocks.Builder arms = this.blocks.get(GolemPart.ARMS);
			final BuildingBlocks.Builder leftArm = this.blocks.get(GolemPart.LEFT_ARM);
			final BuildingBlocks.Builder rightArm = this.blocks.get(GolemPart.RIGHT_ARM);
			if(null == arms && (null == leftArm || null == rightArm)) {
				final List<GolemPart> required = Arrays.asList(GolemPart.LEFT_ARM, GolemPart.RIGHT_ARM);
				throw new IllegalStateException("Missing arguments in GolemBuildingBlocks.Builder; Required: " + required + " Provided: " + this.blocks.keySet());
			}
			// build shared arms and build object
			if(arms != null) {
				final BuildingBlocks armsBlocks = arms.build();
				return new GolemBuildingBlocks(body.build(), legs.build(), armsBlocks, armsBlocks);
			}
			// build separate arms and build object
			return new GolemBuildingBlocks(body.build(), legs.build(), leftArm.build(), rightArm.build());
		}
	}

}
