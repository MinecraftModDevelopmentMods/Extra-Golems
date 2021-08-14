package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.GolemItems;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class BedrockGolem extends GolemBase {

  public BedrockGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
    this.setInvulnerable(true);
  }

  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return true;
  }
  
  @OnlyIn(Dist.CLIENT)
  @Override
  public boolean displayFireAnimation() {
    return false;
  }

  @Override
  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    // creative players can "despawn" by using spawnBedrockGolem on this entity
    final ItemStack itemstack = player.getItemInHand(hand);
    if ((player.isCreative() || !ExtraGolemsConfig.bedrockGolemCreativeOnly()) && !itemstack.isEmpty()
        && itemstack.getItem() == GolemItems.SPAWN_BEDROCK_GOLEM) {
      player.swing(hand);
      if (!this.level.isClientSide) {
        this.discard();
      } else {
        final Vec3 pos = this.position().add(0, 0.2D, 0);
        ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y, pos.z, 0.12D);
      }
    }

    return super.mobInteract(player, hand);
  }

  @Override
  protected void actuallyHurt(final DamageSource source, final float amount) {
    //
  }

  @Override
  public ItemStack getPickedResult(final HitResult target) {
    return new ItemStack(GolemItems.SPAWN_BEDROCK_GOLEM);
  }
}
