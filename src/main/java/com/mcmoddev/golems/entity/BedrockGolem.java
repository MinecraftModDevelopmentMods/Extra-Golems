package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class BedrockGolem extends GolemBase {

  public BedrockGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    this.setInvulnerable(true);
  }

  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return true;
  }
  
  @OnlyIn(Dist.CLIENT)
  @Override
  public boolean canRenderOnFire() {
    return false;
  }

  @Override
  protected boolean processInteract(final PlayerEntity player, final Hand hand) {
    // creative players can "despawn" by using spawnBedrockGolem on this entity
    final ItemStack itemstack = player.getHeldItem(hand);
    if ((player.abilities.isCreativeMode || !ExtraGolemsConfig.bedrockGolemCreativeOnly()) && !itemstack.isEmpty()
        && itemstack.getItem() == GolemItems.SPAWN_BEDROCK_GOLEM) {
      player.swingArm(hand);
      if (!this.world.isRemote) {
        this.remove();
      } else {
        final Vec3d pos = this.getPositionVec().add(0, 0.2D, 0);
        ItemBedrockGolem.spawnParticles(this.world, pos.x, pos.y, pos.z, 0.12D);
      }
    }

    return super.processInteract(player, hand);
  }

  @Override
  protected void damageEntity(final DamageSource source, final float amount) {
    //
  }

  @Override
  public ItemStack getPickedResult(final RayTraceResult target) {
    return new ItemStack(GolemItems.SPAWN_BEDROCK_GOLEM);
  }
}
