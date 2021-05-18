package com.mcmoddev.golems.events;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when an Endstone Golem (or child class of such) tries to teleport. The
 * destination can be modified, or the event can be canceled entirely.
 *
 * @see EnderTeleportEvent
 **/
@Cancelable
public class EndGolemTeleportEvent extends EntityTeleportEvent {

  public final GolemBase entityGolem;

  public EndGolemTeleportEvent(final GolemBase entity, final double targetX, final double targetY, final double targetZ,
      final float attackDamage) {
    super(entity, targetX, targetY, targetZ);
    this.entityGolem = entity;
  }
}
