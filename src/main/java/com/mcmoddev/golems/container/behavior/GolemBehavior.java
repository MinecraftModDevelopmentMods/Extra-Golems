package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * This is the base class for NBT-based behaviors.
 * Each GolemContainer has a set of behaviors that is
 * read from a CompoundNBT. Each behavior can access
 * the entity at key points such as goal registration,
 * mob interaction, entity death, and entity NBT read/write.
 * The GolemBehavior does not change after loading its
 * initial parameters.
 * The GolemBehavior may make use of a BehaviorParameter
 * to easily load complicated settings from NBT.
 */
@Immutable
public abstract class GolemBehavior {
  
  /** The description text for an entity that lights enemies on fire **/
  protected static final IFormattableTextComponent FIRE_DESC = new TranslationTextComponent("entitytip.lights_mobs_on_fire").mergeStyle(TextFormatting.GOLD);
  /** The description text for an entity that applies potion effects to itself **/
  protected static final IFormattableTextComponent EFFECTS_SELF_DESC = new TranslationTextComponent("entitytip.potion_effects_self").mergeStyle(TextFormatting.LIGHT_PURPLE);
  /** The description text for an entity that applies potion effects its enemy **/
  protected static final IFormattableTextComponent EFFECTS_ENEMY_DESC = new TranslationTextComponent("entitytip.potion_effects_enemy").mergeStyle(TextFormatting.LIGHT_PURPLE);
  
  protected final CompoundNBT tag;
  
  public GolemBehavior(final CompoundNBT tag) {
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
  public void onMobInteract(final GolemBase entity, final PlayerEntity player, final Hand hand) {
    
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
  public void onWriteData(final GolemBase entity, final CompoundNBT tag) {
    
  }
  
  /**
   * Called after writing additional data to NBT
   * @param entity the Golem
   * @param tag the Golem NBT tag
   */
  public void onReadData(final GolemBase entity, final CompoundNBT tag) {
    
  }

  /**
   * Called when building the Golem Info Book to add descriptions
   * @param list the current description list
   */
  public void onAddDescriptions(List<ITextComponent> list) {
    
  }
}
