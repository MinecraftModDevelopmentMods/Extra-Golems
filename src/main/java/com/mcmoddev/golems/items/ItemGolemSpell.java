package com.mcmoddev.golems.items;

import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class ItemGolemSpell extends Item {

	public ItemGolemSpell() {
		super(new Item.Properties().maxStackSize(64).group(ItemGroup.MISC));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext cxt) {
		if(ExtraGolemsConfig.enableUseSpellItem() && cxt.getPos() != null && cxt.getItem() != null && !cxt.getItem().isEmpty()) {
			final Block b = cxt.getWorld().getBlockState(cxt.getPos()).getBlock();
			if(b == Blocks.CARVED_PUMPKIN || (b == Blocks.PUMPKIN && ExtraGolemsConfig.pumpkinBuildsGolems())) {
				cxt.getWorld().setBlockState(cxt.getPos(), GolemItems.golemHead.getDefaultState(), 3);
				cxt.getItem().shrink(1);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return super.getDisplayName(stack).applyTextStyle(TextFormatting.RED);
	}
}
