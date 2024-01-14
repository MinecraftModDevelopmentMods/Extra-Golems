package com.mcmoddev.golems.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public class EGCodecUtils {
	/** Codec to map between items and item stacks with a single item and no tag **/
	public static final Codec<ItemStack> ITEM_OR_STACK_CODEC = Codec.either(ForgeRegistries.ITEMS.getCodec(), ItemStack.CODEC)
			.xmap(either -> either.map(ItemStack::new, Function.identity()),
					stack -> stack.getCount() == 1 && !stack.hasTag()
							? Either.left(stack.getItem())
							: Either.right(stack));

	/** Codec to validate and read an integer as a hex string **/
	public static final Codec<Integer> HEX_CODEC = hexIntCodec();
	/** Codec to accept either a hex string or a raw integer **/
	public static final Codec<Integer> HEX_OR_INT_CODEC = Codec.either(HEX_CODEC, Codec.intRange(0, 0xFFFFFF))
			.xmap(either -> either.map(Function.identity(), Function.identity()), Either::right);

	/** {@link MinMaxBounds.Ints} codec **/
	public static final Codec<MinMaxBounds.Ints> MIN_MAX_INTS_DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("min").forGetter(o -> Optional.ofNullable(o.getMin())),
			Codec.INT.optionalFieldOf("max").forGetter(o -> Optional.ofNullable(o.getMax()))
	).apply(instance, (p1, p2) -> {
		if(p1.isPresent() && p2.isEmpty()) return MinMaxBounds.Ints.atLeast(p1.get());
		if(p1.isEmpty() && p2.isPresent()) return MinMaxBounds.Ints.atMost(p2.get());
		if(p1.isEmpty() && p2.isEmpty()) return MinMaxBounds.Ints.ANY;
		return MinMaxBounds.Ints.between(p1.get(), p2.get());
	}));
	/** {@link MinMaxBounds.Ints} or {@link Codec#INT} codec **/
	public static final Codec<MinMaxBounds.Ints> MIN_MAX_INTS_CODEC = Codec.either(Codec.INT, MIN_MAX_INTS_DIRECT_CODEC)
			.xmap(either -> either.map(MinMaxBounds.Ints::exactly, Function.identity()),
					o -> (o.getMin() != null && o.getMax() != null && o.getMin().equals(o.getMax())) ? Either.left(o.getMin()) : Either.right(o));

	/** {@link MinMaxBounds.Ints} codec **/
	public static final Codec<MinMaxBounds.Doubles> MIN_MAX_DOUBLES_DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.optionalFieldOf("min").forGetter(o -> Optional.ofNullable(o.getMin())),
			Codec.DOUBLE.optionalFieldOf("max").forGetter(o -> Optional.ofNullable(o.getMax()))
	).apply(instance, (p1, p2) -> {
		if(p1.isPresent() && p2.isEmpty()) return MinMaxBounds.Doubles.atLeast(p1.get());
		if(p1.isEmpty() && p2.isPresent()) return MinMaxBounds.Doubles.atMost(p2.get());
		if(p1.isEmpty() && p2.isEmpty()) return MinMaxBounds.Doubles.ANY;
		return MinMaxBounds.Doubles.between(p1.get(), p2.get());
	}));
	/** {@link MinMaxBounds.Doubles} or {@link Codec#DOUBLE} codec **/
	public static final Codec<MinMaxBounds.Doubles> MIN_MAX_DOUBLES_CODEC = Codec.either(Codec.DOUBLE, MIN_MAX_DOUBLES_DIRECT_CODEC)
			.xmap(either -> either.map(MinMaxBounds.Doubles::exactly, Function.identity()),
					o -> (o.getMin() != null && o.getMax() != null && o.getMin().equals(o.getMax())) ? Either.left(o.getMin()) : Either.right(o));

	/** {@link MobEffectInstance} codec **/
	public static final Codec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
			Codec.INT.optionalFieldOf("duration", 0).forGetter(MobEffectInstance::getDuration),
			Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance::getAmplifier),
			Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
			Codec.BOOL.optionalFieldOf("visible", true).forGetter(MobEffectInstance::isVisible),
			Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(MobEffectInstance::showIcon)
	).apply(instance, MobEffectInstance::new));

	public static final Codec<String> AS_STRING_CODEC = Codec.STRING.comapFlatMap(DataResult::success, Object::toString);

	public static final Pattern RESOURCE_LOCATION_PATTERN = Pattern.compile("(?:[a-z0-9_.]+:)?[a-z0-9_./-]+");
	public static final Pattern HEX_PATTERN = Pattern.compile("[0-9a-fA-F]+");

	/**
	 * @param codec an element codec
	 * @param <T> the element type
	 * @return a codec that allows either a single element or a list of elements
	 */
	public static <T> Codec<List<T>> listOrElementCodec(final Codec<T> codec) {
		return Codec.either(codec, codec.listOf())
				.xmap(either -> either.map(ImmutableList::of, Function.identity()),
						list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list));
	}

	/**
	 * @param codec an element codec
	 * @param <T> the element type
	 * @return a codec that allows either a single element or a list of elements
	 */
	public static <T> Codec<Set<T>> setOrElementCodec(final Codec<T> codec) {
		return Codec.either(codec, codec.listOf().xmap(o -> (Set<T>) ImmutableSet.copyOf(o), ImmutableList::copyOf))
				.xmap(either -> either.map(ImmutableSet::of, Function.identity()),
						set -> set.size() == 1 ? Either.left(set.iterator().next()) : Either.right(set));
	}

	/**
	 * @return a codec that converts between hex formatted strings and packed decimal integers
	 */
	private static Codec<Integer> hexIntCodec() {
		Function<String, DataResult<String>> function = (s) -> {
			if(s.isEmpty()) {
				return DataResult.error(() -> "Failed to parse hex int from empty string");
			}
			if(!HEX_PATTERN.matcher(s).matches()) {
				return DataResult.error(() -> "Invalid hex int " + s);
			}
			return DataResult.success(s);
		};
		return Codec.STRING.flatXmap(function, function).xmap(s -> Integer.valueOf(s, 16), Integer::toHexString);
	}

	/**
	 * @param min the minimum value, inclusive
	 * @param max the maximum value, inclusive
	 * @return a codec that fails when the min or max of the int is outside the given range
	 */
	public static Codec<MinMaxBounds.Ints> boundedIntCodec(final int min, final int max) {
		Function<MinMaxBounds.Ints, DataResult<MinMaxBounds.Ints>> function = (instance) -> {
			if (instance.getMin() != null && instance.getMin() < min) {
				return DataResult.error(() -> "Value too low. minimum " + min + "; provided [" + instance + "]");
			} else if(instance.getMax() != null && instance.getMax() > max) {
				return DataResult.error(() -> "Value too high. maximum " + max + "; provided [" + instance + "]");
			} else {
				return DataResult.success(instance);
			}
		};
		return MIN_MAX_INTS_CODEC.flatXmap(function, function);
	}
}
