package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.ExplodeGoal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * This behavior allows an entity to light a fuse, save/load the fuse, 
 * and create an explosion when the fuse reaches 0
 **/
@Immutable
public class ExplodeBehavior extends GolemBehavior {
  
  /** The radius of the explosion **/
  protected final double range;
  /** The minimum length of the fuse **/
  protected final int fuse;
  /** The percent chance [0,1] to apply when the entity is hurt **/
  protected final double chanceOnHurt;
  /** The percent chance [0,1] to apply when the entity attacks **/
  protected final double chanceOnAttack;

  public ExplodeBehavior(CompoundNBT tag) {
    super(tag);
    range = tag.getDouble("range");
    fuse = tag.getInt("fuse");
    chanceOnHurt = tag.getDouble("chance_on_hurt");
    chanceOnAttack = tag.getDouble("chance_on_attack");
  }
  
  public int getFuseLen() { return fuse; }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    entity.goalSelector.addGoal(0, new ExplodeGoal<>(entity, (float)range));
  }
  
  @Override
  public void onHurtTarget(final GolemBase entity, final Entity target) {
    if(target.isBurning() || entity.world.getRandom().nextFloat() < chanceOnAttack) {
      entity.lightFuse();
    }
  }
  
  @Override
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    if(source.isFireDamage() || entity.world.getRandom().nextFloat() < chanceOnHurt) {
      entity.lightFuse();
    }
  }
  
  @Override
  public void onDie(final GolemBase entity, final DamageSource source) {
    entity.explode((float) range);
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final PlayerEntity player, final Hand hand) {
    final ItemStack itemstack = player.getHeldItem(hand);
    if (!itemstack.isEmpty() && itemstack.getItem() == Items.FLINT_AND_STEEL) {
      // play sound and swing hand
      final Vector3d pos = entity.getPositionVec();
      entity.world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.ITEM_FLINTANDSTEEL_USE, entity.getSoundCategory(), 1.0F,
          entity.world.getRandom().nextFloat() * 0.4F + 0.8F);
      player.swingArm(hand);

      entity.setFire(Math.floorDiv(getFuseLen(), 20));
      entity.lightFuse();
      itemstack.damageItem(1, player, c -> c.sendBreakAnimation(hand));
    }
  }
  
  @Override
  public void onWriteData(final GolemBase entity, final CompoundNBT tag) {
    entity.saveFuse(tag);
  }
  
  @Override
  public void onReadData(final GolemBase entity, final CompoundNBT tag) {
    entity.loadFuse(tag);
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.explode").mergeStyle(TextFormatting.RED));
  }
}
