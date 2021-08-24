package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * This is the base class for NBT-based behaviors.
 * Each GolemContainer has a set of behaviors that is
 * read from a CompoundTag. Each behavior can access
 * the entity at key points such as goal registration,
 * mob interaction, entity death, and entity NBT read/write.
 * The GolemBehavior does not change after loading its
 * initial parameters.
 * The GolemBehavior may make use of a BehaviorParameter
 * to easily load complicated settings from NBT.
 */
@Immutable
public abstract class GolemBehavior {
  
  protected static final MutableComponent FIRE_DESC = new TranslatableComponent("entitytip.lights_mobs_on_fire").withStyle(ChatFormatting.GOLD);
  protected static final MutableComponent EFFECTS_SELF_DESC = new TranslatableComponent("entitytip.potion_effects_self").withStyle(ChatFormatting.LIGHT_PURPLE);
  protected static final MutableComponent EFFECTS_ENEMY_DESC = new TranslatableComponent("entitytip.potion_effects_enemy").withStyle(ChatFormatting.LIGHT_PURPLE);
  
  protected final CompoundTag tag;
  
  public GolemBehavior(final CompoundTag tag) {
    this.tag = tag;
  }

  /**
   * Called when the Golem registers goals
   * @param entity the Golem
   */
  public void onRegisterGoals(final GolemBase entity) {
    
  }
  
  /**
   * Called when the Golem hurts an entity
   * @param entity the Golem
   * @param target the entity that was hurt
   */
  public void onHurtTarget(final GolemBase entity, final Entity target) {
    
  }
  
  /**
   * Called when the Golem is hurt
   * @param entity the Golem
   * @param source the source of the damage
   * @param amount the amount of damage
   */
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    
  }
  
  /**
   * Called when a player interacts and the item was not a banner or heal item
   * @param entity the Golem
   * @param player the Player
   * @param hand the Player's hand
   */
  public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
    
  }
  
  /**
   * Called when the Golem dies, before it is marked as removed
   * @param entity the Golem
   * @param source the DamageSource that killed the Golem
   */
  public void onDie(final GolemBase entity, final DamageSource source) {
    
  }
  
  /**
   * Called after reading additional data from NBT
   * @param entity the Golem
   * @param tag the Golem NBT tag
   */
  public void onWriteData(final GolemBase entity, final CompoundTag tag) {
    
  }
  
  /**
   * Called after writing additional data to NBT
   * @param entity the Golem
   * @param tag the Golem NBT tag
   */
  public void onReadData(final GolemBase entity, final CompoundTag tag) {
    
  }

  /**
   * Called when building the Golem Info Book to add descriptions
   * @param list the current description list
   */
  public void onAddDescriptions(List<Component> list) {
    
  }
}
