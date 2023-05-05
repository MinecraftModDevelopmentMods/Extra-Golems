package com.mcmoddev.golems.item;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.GolemHeadBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GolemSpellItem extends Item {

	public GolemSpellItem(final Item.Properties properties) {
		super(properties);
	}

	public static void registerDispenserBehavior() {
		final DispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
				final Level level = blockSource.getLevel();
				final BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
				final BlockState blockState = level.getBlockState(blockPos);
				if (ExtraGolems.CONFIG.enableUseSpellItem() && blockState.is(Blocks.CARVED_PUMPKIN)) {
					final Direction facing = blockState.getValue(CarvedPumpkinBlock.FACING);
					level.setBlock(blockPos, EGRegistry.GOLEM_HEAD.get().defaultBlockState().setValue(GolemHeadBlock.FACING, facing), Block.UPDATE_ALL);
					itemStack.shrink(1);
					return itemStack;
				}
				return super.execute(blockSource, itemStack);
			}
		};
		DispenserBlock.registerBehavior(EGRegistry.GOLEM_SPELL.get(), behavior);
	}

	@Override
	public InteractionResult useOn(UseOnContext cxt) {
		if (ExtraGolems.CONFIG.enableUseSpellItem() && cxt.getClickedPos() != null && cxt.getItemInHand() != null && !cxt.getItemInHand().isEmpty()) {
			final Block b = cxt.getLevel().getBlockState(cxt.getClickedPos()).getBlock();
			if (b == Blocks.CARVED_PUMPKIN || (b == Blocks.PUMPKIN && ExtraGolems.CONFIG.pumpkinBuildsGolems())) {
				if (!cxt.getLevel().isClientSide()) {
					final Direction facing = cxt.getLevel().getBlockState(cxt.getClickedPos()).getValue(HorizontalDirectionalBlock.FACING);
					cxt.getLevel().setBlock(cxt.getClickedPos(), EGRegistry.GOLEM_HEAD.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing), 3);
					GolemHeadBlock.trySpawnGolem(cxt.getPlayer(), cxt.getLevel(), cxt.getClickedPos());
					cxt.getItemInHand().shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.RED);
	}
}
