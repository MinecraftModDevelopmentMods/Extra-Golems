package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.menu.PortableCraftingMenu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This behavior allows an entity to open a crafting menu
 * when the player interacts with it
 **/
@Immutable
public class CraftingMenuBehavior extends GolemBehavior {

  public CraftingMenuBehavior(CompoundNBT tag) {
    super(tag);
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final PlayerEntity player, final Hand hand) {
    if (!player.isCrouching() && player instanceof ServerPlayerEntity) {
      // display crafting grid by sending request to server
      NetworkHooks.openGui((ServerPlayerEntity) player, new PortableCraftingMenu.Provider());
      player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
      player.swingArm(hand);
    }
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.crafting_menu").mergeStyle(TextFormatting.BLUE));
  }
}
