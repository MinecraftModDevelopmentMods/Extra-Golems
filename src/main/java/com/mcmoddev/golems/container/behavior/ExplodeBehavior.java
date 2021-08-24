package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.ExplodeGoal;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

@Immutable
public class ExplodeBehavior extends GolemBehavior {
  
  protected final double range;
  protected final int fuse;
  protected final double chanceOnHurt;
  protected final double chanceOnAttack;

  public ExplodeBehavior(CompoundTag tag) {
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
    if(target.isOnFire() || entity.getRandom().nextFloat() < chanceOnAttack) {
      entity.lightFuse();
    }
  }
  
  @Override
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    if(source.isFire() || entity.getRandom().nextFloat() < chanceOnHurt) {
      entity.lightFuse();
    }
  }
  
  @Override
  public void onDie(final GolemBase entity, final DamageSource source) {
    entity.explode((float) range);
  }
  
  @Override
  public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
    final ItemStack itemstack = player.getItemInHand(hand);
    if (!itemstack.isEmpty() && itemstack.getItem() == Items.FLINT_AND_STEEL) {
      // play sound and swing hand
      final Vec3 pos = entity.position();
      entity.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.FLINTANDSTEEL_USE, entity.getSoundSource(), 1.0F,
          entity.getRandom().nextFloat() * 0.4F + 0.8F);
      player.swing(hand);

      entity.setSecondsOnFire(Math.floorDiv(getFuseLen(), 20));
      entity.lightFuse();
      itemstack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
    }
  }
  
  @Override
  public void onWriteData(final GolemBase entity, final CompoundTag tag) {
    entity.saveFuse(tag);
  }
  
  @Override
  public void onReadData(final GolemBase entity, final CompoundTag tag) {
    entity.loadFuse(tag);
  }
  
  @Override
  public void onAddDescriptions(List<Component> list) {
    list.add(new TranslatableComponent("entitytip.explode").withStyle(ChatFormatting.RED));
  }
}
