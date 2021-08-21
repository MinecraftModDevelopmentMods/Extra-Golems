package com.mcmoddev.golems.container.behavior;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.MoveToArrowsGoal;
import com.mcmoddev.golems.menu.DispenserGolemMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

@Immutable
public class ShootArrowsBehavior extends GolemBehavior {
  
  protected final double damage;
  
  protected final AttributeModifier rangeModifier = new AttributeModifier("Ranged attack bonus", 2.0F, AttributeModifier.Operation.MULTIPLY_TOTAL);
  
  public ShootArrowsBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.SHOOT_ARROWS);
    damage = tag.getDouble("damage");
  }
  
  public double getDamage() { return damage; }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    // modify follow range
    entity.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(rangeModifier);
    entity.goalSelector.addGoal(4, new MoveToArrowsGoal<>(entity, 10.0D, 1.0D));
  }
  
  @Override
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    // if it's an arrow or other projectile, set the attacker as revenge target
    if (source instanceof IndirectEntityDamageSource && source.getEntity() instanceof LivingEntity) {
      entity.setTarget((LivingEntity) source.getEntity());
    }
    final boolean forceMelee = (entity.getTarget() != null && entity.getTarget().distanceToSqr(entity) < 8.0D);
    entity.updateCombatTask(forceMelee);
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
    if (!player.isCrouching() && player instanceof ServerPlayer) {
      // open dispenser GUI by sending request to server
      NetworkHooks.openGui((ServerPlayer) player, new DispenserGolemMenu.Provider(entity.getArrowInventory()));
      player.swing(hand);
    }
  }
  
  @Override
  public void onWriteData(final GolemBase entity, final CompoundTag tag) {
    entity.saveArrowInventory(tag);
  }
  
  @Override
  public void onReadData(final GolemBase entity, final CompoundTag tag) {
    entity.loadArrowInventory(tag);
  }
}
