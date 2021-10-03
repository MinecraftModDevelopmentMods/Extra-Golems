package com.mcmoddev.golems.item;


import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.block.GolemHeadBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GolemHeadItem extends BlockItem {

  /**
   * This behavior is modified from that of CARVED_PUMPKIN, where the block is
   * placed if a Golem pattern is found.
   **/
  protected static final IDispenseItemBehavior DISPENSER_BEHAVIOR = new OptionalDispenseBehavior() {
	@Override
	protected ItemStack dispenseStack(final IBlockSource source, final ItemStack stack) {
	  World world = source.getWorld();
	  BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
	  if (world.isAirBlock(blockpos) && GolemHeadBlock.canDispenserPlace(world, blockpos)) {
		if (!world.isRemote()) {
		  world.setBlockState(blockpos, EGRegistry.GOLEM_HEAD.getDefaultState(), 3);
		}
		stack.shrink(1);
		this.setSuccessful(true);
	  }
	  return stack;
	}
  };

  public GolemHeadItem(Item.Properties properties) {
    super(EGRegistry.GOLEM_HEAD, properties);
	DispenserBlock.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean hasEffect(final ItemStack stack) {
	return true;
  }
}
