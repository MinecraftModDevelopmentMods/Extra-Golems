package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.concurrent.Immutable;

/**
 * Sets the group of the golem
 */
@Immutable
public class GroupModifier extends Modifier {

	public static final Codec<GroupModifier> CODEC = ResourceLocation.CODEC
			.xmap(GroupModifier::new, GroupModifier::getGroup)
			.fieldOf("group").codec();

	private final ResourceLocation group;

	public GroupModifier(ResourceLocation group) {
		this.group = group;
	}

	//// GETTERS ////

	public ResourceLocation getGroup() {
		return group;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.group(getGroup());
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.GROUP.get();
	}
}
