package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.concurrent.Immutable;

/**
 * Sets the group of the golem
 */
@Immutable
public class GroupGolemModifier extends GolemModifier {

	public static final Codec<GroupGolemModifier> CODEC = ResourceLocation.CODEC
			.xmap(GroupGolemModifier::new, GroupGolemModifier::getGroup)
			.fieldOf("group").codec();

	private final ResourceLocation group;

	public GroupGolemModifier(ResourceLocation group) {
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
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.GROUP.get();
	}
}
