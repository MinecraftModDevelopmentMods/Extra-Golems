package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.golem.RepairItems;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;

import java.util.Map;

public class AddRepairItemsGolemModifier extends GolemModifier {

	public static final Codec<AddRepairItemsGolemModifier> CODEC = Codec.unboundedMap(ResourcePair.CODEC, Codec.DOUBLE)
			.xmap(AddRepairItemsGolemModifier::new, AddRepairItemsGolemModifier::getRepairItems)
			.fieldOf("repair_items").codec();

	private final Map<ResourcePair, Double> map;

	public AddRepairItemsGolemModifier(Map<ResourcePair, Double> map) {
		this.map = map;
	}

	//// GETTERS ////

	public Map<ResourcePair, Double> getRepairItems() {
		return map;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.repairItems(b -> b.addAll(getRepairItems()));
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_REPAIR_ITEMS.get();
	}
}
