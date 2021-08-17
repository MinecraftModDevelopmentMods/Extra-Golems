package com.mcmoddev.golems.util.behavior;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.menu.PortableCraftingMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

@Immutable
public class CraftingMenuBehavior extends GolemBehavior {

  public CraftingMenuBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.CRAFTING_MENU);
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
    if (!player.isCrouching() && player instanceof ServerPlayer) {
      // display crafting grid by sending request to server
      NetworkHooks.openGui((ServerPlayer) player, new PortableCraftingMenu.Provider());
      player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
      player.swing(hand);
    }
  }
}
