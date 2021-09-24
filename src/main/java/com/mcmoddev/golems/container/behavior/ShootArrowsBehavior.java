package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.MoveToItemGoal;
import com.mcmoddev.golems.menu.PortableDispenserMenu;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This behavior allows an entity to pick up and shoot arrows,
 * as well as load/save the arrow inventory
 **/
@Immutable
public class ShootArrowsBehavior extends GolemBehavior {
  
  /** The amount of damage dealt by arrows **/
  protected final double damage;
  /** The follow range modifier to allow the entity to detect enemies from a distance **/
  protected final AttributeModifier rangeModifier = new AttributeModifier("Ranged attack bonus", 2.0F, AttributeModifier.Operation.MULTIPLY_TOTAL);
  
  public ShootArrowsBehavior(CompoundNBT tag) {
    super(tag);
    damage = tag.getDouble("damage");
  }
  
  /** @return The amount of damage dealt by arrows **/
  public double getDamage() { return damage; }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    // modify follow range
    entity.getAttribute(Attributes.FOLLOW_RANGE).applyPersistentModifier(rangeModifier);
    entity.goalSelector.addGoal(4, new MoveToItemGoal(entity, 10.0D, 30, 1.0D));
  }
  
  @Override
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    // if it's an arrow or other projectile, set the attacker as revenge target
    if (source instanceof IndirectEntityDamageSource && source.getTrueSource() instanceof LivingEntity) {
      entity.setAttackTarget((LivingEntity) source.getTrueSource());
    }
    final boolean forceMelee = (entity.getAttackTarget() != null && entity.getAttackTarget().getDistanceSq(entity) < 8.0D);
    entity.updateCombatTask(forceMelee);
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final PlayerEntity player, final Hand hand) {
    if (!player.isCrouching() && player instanceof ServerPlayerEntity) {
      // open dispenser GUI by sending request to server
      NetworkHooks.openGui((ServerPlayerEntity) player, new PortableDispenserMenu.Provider(entity.getArrowInventory()));
      player.swingArm(hand);
    }
  }
  
  @Override
  public void onWriteData(final GolemBase entity, final CompoundNBT tag) {
    entity.saveArrowInventory(tag);
  }
  
  @Override
  public void onReadData(final GolemBase entity, final CompoundNBT tag) {
    entity.loadArrowInventory(tag);
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.shoot_arrows").mergeStyle(TextFormatting.LIGHT_PURPLE));
    list.add(new TranslationTextComponent("entitytip.click_refill").mergeStyle(TextFormatting.GRAY));
  }
}
