package com.golems.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/** 
 * Fired when an Endstone Golem (or child class of such) tries to teleport.
 * The destination can be modified, or the event can be canceled entirely.
 * @see EnderTeleportEvent 
 **/
@Cancelable
public class EndGolemTeleportEvent extends EnderTeleportEvent 
{
	public EndGolemTeleportEvent(EntityLivingBase entity, double targetX, double targetY, double targetZ, float attackDamage) 
	{
		super(entity, targetX, targetY, targetZ, attackDamage);
	}
}
