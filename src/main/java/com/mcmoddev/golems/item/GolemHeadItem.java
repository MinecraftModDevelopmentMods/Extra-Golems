package com.mcmoddev.golems.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class GolemHeadItem extends BlockItem {
	public GolemHeadItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return super.getEquipmentSlot(stack); // EquipmentSlot.HEAD;
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return true;
	}
}
