package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Adds all of the given blocks to the {@link com.mcmoddev.golems.data.golem.BuildingBlocks.Builder}
 */
@Immutable
public class AddBlocksGolemModifier extends GolemModifier {

	public static final Codec<AddBlocksGolemModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(ResourcePair.CODEC).fieldOf("blocks").forGetter(AddBlocksGolemModifier::getBlocks),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(AddBlocksGolemModifier::replace)
	).apply(instance, AddBlocksGolemModifier::new));

	private final List<ResourcePair> blocks;
	private final boolean replace;

	public AddBlocksGolemModifier(List<ResourcePair> blocks, boolean replace) {
		this.blocks = blocks;
		this.replace = replace;
	}

	//// GETTERS ////

	public List<ResourcePair> getBlocks() {
		return blocks;
	}

	public boolean replace() {
		return replace;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.blocks(b -> {
			if(replace()) {
				b.clear();
			}
			b.addAll(this.getBlocks());
		});
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_BLOCKS.get();
	}
}
