package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.golem.RepairItems;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * Adds all of the given repair items to the {@link RepairItems.Builder}, optionally replacing the existing values
 */
@Immutable
public class AddRepairItemsModifier extends Modifier {

	public static final Codec<AddRepairItemsModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(ResourcePair.CODEC, Codec.DOUBLE).fieldOf("repair_items").forGetter(AddRepairItemsModifier::getRepairItems),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(AddRepairItemsModifier::replace)
	).apply(instance, AddRepairItemsModifier::new));

	private final Map<ResourcePair, Double> map;
	private final boolean replace;

	public AddRepairItemsModifier(Map<ResourcePair, Double> map, boolean replace) {
		this.map = map;
		this.replace = replace;
	}

	//// GETTERS ////

	public Map<ResourcePair, Double> getRepairItems() {
		return map;
	}

	public boolean replace() {
		return replace;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.repairItems(b -> {
			if(replace()) {
				b.clear();
			}
			b.addAll(getRepairItems());
		});
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_REPAIR_ITEMS.get();
	}
}
