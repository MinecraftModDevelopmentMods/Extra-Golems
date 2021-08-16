package com.mcmoddev.golems.util.behavior;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Immutable
public abstract class GolemBehavior {
  
  protected final CompoundTag tag;
  protected final ResourceLocation registryName;
  
  public GolemBehavior(final CompoundTag tag, final ResourceLocation id) {
    this.tag = tag;
    this.registryName = id;
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
  
  /** @return a unique ID for a GolemBehavior **/
  public ResourceLocation getRegistryName() {
    return registryName;
  };
}
