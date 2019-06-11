package com.mcmoddev.golems.items;

import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarvedPumpkin;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemGolemSpell extends Item {
	
	/*
	 * This behavior enables a Dispenser to convert a Carved Pumpkin into a Golem Head. That's all.
	 *
	public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {
		@Override
		protected ItemStack dispenseStack(final IBlockSource source, final ItemStack stack) {
			World world = source.getWorld();
			BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(BlockDispenser.FACING));
			if (ExtraGolemsConfig.enableUseSpellItem() && world.getBlockState(blockpos).getBlock() == Blocks.CARVED_PUMPKIN) {
				if (!world.isRemote) {
					final EnumFacing facing = world.getBlockState(blockpos).get(BlockHorizontal.HORIZONTAL_FACING);
					world.setBlockState(blockpos, GolemItems.GOLEM_HEAD.getDefaultState().with(BlockHorizontal.HORIZONTAL_FACING, facing), 3);
				}
				stack.shrink(1);
			} else {
				return super.dispenseStack(source, stack);
			}
			return stack;
		}
	};
	*/
	public ItemGolemSpell() {
		super(new Item.Properties().maxStackSize(64).group(ItemGroup.MISC));
		// dispenser behavior TODO: NOT WORKING
		// BlockDispenser.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext cxt) {
		if(ExtraGolemsConfig.enableUseSpellItem() && cxt.getPos() != null && cxt.getItem() != null && !cxt.getItem().isEmpty()) {
			final Block b = cxt.getWorld().getBlockState(cxt.getPos()).getBlock();
			if(b == Blocks.CARVED_PUMPKIN || (b == Blocks.PUMPKIN && ExtraGolemsConfig.pumpkinBuildsGolems())) {
				if(!cxt.getWorld().isRemote) {
					final EnumFacing facing = cxt.getWorld().getBlockState(cxt.getPos()).get(BlockHorizontal.HORIZONTAL_FACING); 
					cxt.getWorld().setBlockState(cxt.getPos(), GolemItems.GOLEM_HEAD.getDefaultState().with(BlockHorizontal.HORIZONTAL_FACING, facing), 3);
				}
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
