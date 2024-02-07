/**
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package com.mcmoddev.golems.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Block States are poorly serialized, this can be used as a replacement
 */
public class DeferredBlockState implements Supplier<BlockState> {

	public static final Codec<DeferredBlockState> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("Name").forGetter(DeferredBlockState::getBlock),
			Codec.unboundedMap(Codec.STRING, EGCodecUtils.AS_STRING_CODEC).optionalFieldOf("properties", ImmutableMap.of()).forGetter(DeferredBlockState::getProperties)
	).apply(instance, DeferredBlockState::new));

	public static final Codec<DeferredBlockState> CODEC = Codec.either(ResourceLocation.CODEC, DIRECT_CODEC)
			.xmap(either -> either.map(DeferredBlockState::new, Function.identity()),
					o -> o.getProperties().isEmpty() ? Either.left(o.getBlock()) : Either.right(o));

	private final ResourceLocation block;
	private final Map<String, String> properties;

	private BlockState blockState;

	public DeferredBlockState(ResourceLocation block) {
		this(block, ImmutableMap.of());
	}

	public DeferredBlockState(ResourceLocation block, Map<String, String> properties) {
		this.block = block;
		this.properties = ImmutableMap.copyOf(properties);
	}

	//// GETTERS ////

	public ResourceLocation getBlock() {
		return block;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	//// METHODS ////

	@Override
	public BlockState get() {
		if(null == blockState) {
			// resolve block state
			final Block block = ForgeRegistries.BLOCKS.getValue(this.block);
			if(null == block) {
				throw new RuntimeException("[ExtraGolems.DeferredBlockState] Unknown block with ID '" + this.block.toString() + "'");
			}
			BlockState state = block.defaultBlockState();
			if(!state.getProperties().isEmpty()) {
				// resolve properties
				for(Map.Entry<String, String> entry : properties.entrySet()) {
					// load property key
					Property<?> property = block.getStateDefinition().getProperty(entry.getKey());
					if(null == property) continue;
					// assign property value
					state = setProperty(state, property, entry.getValue());
				}
			}
			this.blockState = state;
		}
		return blockState;
	}

	private <T extends Comparable<T>> BlockState setProperty(final BlockState blockState, final Property<T> property, final String valueKey) {
		if(property.getValue(valueKey).isEmpty()) {
			return blockState;
		}
		return blockState.setValue(property, property.getValue(valueKey).get());
	}
}
