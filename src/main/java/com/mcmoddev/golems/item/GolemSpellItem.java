package com.mcmoddev.golems.item;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.EGConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


public class GolemSpellItem extends Item {
  /**
   * This behavior enables a Dispenser to convert a Carved Pumpkin into a Golem Head. That's all.
   **/
  protected static final IDispenseItemBehavior DISPENSER_BEHAVIOR = new OptionalDispenseBehavior() {
	@Override
	protected ItemStack dispenseStack(final IBlockSource source, final ItemStack stack) {
	  World world = source.getWorld();
	  BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
	  if (EGConfig.enableUseSpellItem() && world.getBlockState(blockpos).getBlock() == Blocks.CARVED_PUMPKIN) {
		if (!world.isRemote()) {
		  final Direction facing = world.getBlockState(blockpos).get(HorizontalBlock.HORIZONTAL_FACING);
		  world.setBlockState(blockpos, EGRegistry.GOLEM_HEAD.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, facing), 3);
		}
		stack.shrink(1);
		this.setSuccessful(true);
	  } else {
		return super.dispenseStack(source, stack);
	  }
	  return stack;
	}
  };

  public GolemSpellItem() {
    super(new Item.Properties().maxStackSize(64).group(ItemGroup.MISC));
	DispenserBlock.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext cxt) {
	if (EGConfig.enableUseSpellItem() && cxt.getPos() != null && cxt.getItem() != null && !cxt.getItem().isEmpty()) {
	  final Block b = cxt.getWorld().getBlockState(cxt.getPos()).getBlock();
	  if (b == Blocks.CARVED_PUMPKIN || (b == Blocks.PUMPKIN && EGConfig.pumpkinBuildsGolems())) {
		if (!cxt.getWorld().isRemote()) {
		  final Direction facing = cxt.getWorld().getBlockState(cxt.getPos()).get(HorizontalBlock.HORIZONTAL_FACING);
		  cxt.getWorld().setBlockState(cxt.getPos(), EGRegistry.GOLEM_HEAD.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, facing), 3);
		  GolemHeadBlock.trySpawnGolem(cxt.getPlayer(), cxt.getWorld(), cxt.getPos());
		  cxt.getItem().shrink(1);
		}
		return ActionResultType.SUCCESS;
	  }
	}
	return ActionResultType.PASS;
  }

  @Override
  public ITextComponent getDisplayName(ItemStack stack) {
	return new TranslationTextComponent(this.getTranslationKey(stack)).mergeStyle(TextFormatting.RED);
  }
}
