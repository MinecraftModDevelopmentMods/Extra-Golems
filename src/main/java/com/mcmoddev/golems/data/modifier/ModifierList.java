package com.mcmoddev.golems.data.modifier;

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
public class ModifierList {

	public static final Codec<ModifierList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("target").forGetter(ModifierList::getTarget),
			Priority.CODEC.optionalFieldOf("priority", Priority.NORMAL).forGetter(ModifierList::getPriority),
			EGCodecUtils.listOrElementCodec(Modifier.DIRECT_CODEC).fieldOf("modifiers").forGetter(ModifierList::getModifiers)
	).apply(instance, ModifierList::new));
	public static final Codec<Holder<ModifierList>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.MODIFIER_LIST, CODEC, true);

	private final ResourceLocation target;
	private final Priority priority;
	private final List<Modifier> modifiers;

	public ModifierList(ResourceLocation target, Priority priority, List<Modifier> modifiers) {
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

	public List<Modifier> getModifiers() {
		return modifiers;
	}
}
