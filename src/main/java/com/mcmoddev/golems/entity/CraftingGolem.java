package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.container.ContainerPortableWorkbench;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

public final class CraftingGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Crafting";

  public CraftingGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  @Override
  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    if (!player.isCrouching() && player instanceof ServerPlayer) {
      // display crafting grid by sending request to server
      NetworkHooks.openGui((ServerPlayer) player, new ContainerPortableWorkbench.Provider());
      player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
      player.swing(hand);
      return InteractionResult.SUCCESS;
    }
    return super.mobInteract(player, hand);
  }
}
