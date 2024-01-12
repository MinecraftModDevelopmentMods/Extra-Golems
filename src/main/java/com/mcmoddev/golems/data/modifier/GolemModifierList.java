package com.mcmoddev.golems.data.modifier;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public class GolemModifierList {

	public static final Codec<GolemModifierList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("target").forGetter(GolemModifierList::getTarget),
			Priority.CODEC.optionalFieldOf("priority", Priority.NORMAL).forGetter(GolemModifierList::getPriority),
			EGCodecUtils.listOrElementCodec(GolemModifier.DIRECT_CODEC).fieldOf("modifiers").forGetter(GolemModifierList::getModifiers)
	).apply(instance, GolemModifierList::new));
	public static final Codec<Holder<GolemModifierList>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.GOLEM_MODIFIER_LISTS, CODEC, true);

	private final ResourceLocation target;
	private final Priority priority;
	private final List<GolemModifier> modifiers;

	public GolemModifierList(ResourceLocation target, Priority priority, List<GolemModifier> modifiers) {
		this.target = target;
		this.priority = priority;
		this.modifiers = modifiers;
	}

	//// GETTERS ////

	public ResourceLocation getTarget() {
		return target;
	}

	public Priority getPriority() {
		return priority;
	}

	public List<GolemModifier> getModifiers() {
		return modifiers;
	}
}
