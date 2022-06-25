package com.mcmoddev.golems.event;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when a entity tries to teleport.
 * The destination can be modified, or the event can be canceled entirely.
 *
 * @see EntityTeleportEvent
 **/
@Cancelable
public class GolemTeleportEvent extends EntityTeleportEvent {

	public final GolemBase entityGolem;

	public GolemTeleportEvent(final Mob entity, final double targetX, final double targetY, final double targetZ,
							  final float attackDamage) {
		super(entity, targetX, targetY, targetZ);
		this.entityGolem = entity instanceof GolemBase ? (GolemBase) entity : null;
	}
}
