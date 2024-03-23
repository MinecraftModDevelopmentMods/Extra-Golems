package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.BuildingBlocks;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.golem.GolemPart;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.data.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Adds all of the given blocks to the {@link com.mcmoddev.golems.data.golem.BuildingBlocks.Builder}
 */
@Immutable
public class AddBlocksModifier extends Modifier {

	public static final Codec<AddBlocksModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(ResourcePair.CODEC).fieldOf("blocks").forGetter(AddBlocksModifier::getBlocks),
			GolemPart.CODEC.optionalFieldOf("part", GolemPart.ALL).forGetter(AddBlocksModifier::getPart),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(AddBlocksModifier::replace)
	).apply(instance, AddBlocksModifier::new));

	private final List<ResourcePair> blocks;
	private final GolemPart part;
	private final boolean replace;

	public AddBlocksModifier(List<ResourcePair> blocks, GolemPart part, boolean replace) {
		this.blocks = blocks;
		this.part = part;
		this.replace = replace;
	}

	//// GETTERS ////

	public List<ResourcePair> getBlocks() {
		return blocks;
	}

	public GolemPart getPart() {
		return this.part;
	}

	public boolean replace() {
		return replace;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.blocks(b -> {
			if(replace()) {
				b.withPart(getPart(), new BuildingBlocks.Builder(this.getBlocks()));
			}
			b.apply(getPart(), o -> o.addAll(this.getBlocks()));
		});
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_BLOCKS.get();
	}
}
