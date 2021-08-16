package com.mcmoddev.golems.items;

import com.mcmoddev.golems.gui.EGScreenLoader;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class GuideBookItem extends Item {

  public GuideBookItem() {
    super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
    ItemStack itemstack = playerIn.getItemInHand(handIn);
    if (playerIn.getCommandSenderWorld().isClientSide()) {
      EGScreenLoader.loadBookGui(playerIn, itemstack);
    }
    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
  }
}
