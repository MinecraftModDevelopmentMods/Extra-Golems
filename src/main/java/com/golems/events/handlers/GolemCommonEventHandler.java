package com.golems.events.handlers;

import com.golems.entity.GolemBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles events added specifically from this mod.
 **/
public class GolemCommonEventHandler {

	@SubscribeEvent
	public void onLivingSpawned(final EntityJoinWorldEvent event) {
		// add custom 'attack golem' AI to zombies. They already have this for regular iron golems
		if (event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityPigZombie)) {
			final EntityZombie zombie = (EntityZombie) event.getEntity();
			for (final EntityAITasks.EntityAITaskEntry entry : zombie.targetTasks.taskEntries) {
				if (entry.action instanceof EntityAIAttackGolem) {
					return;
				}
			}
			zombie.targetTasks.addTask(3, new EntityAIAttackGolem(zombie));
		}
	}

	private static final class EntityAIAttackGolem extends EntityAINearestAttackableTarget<GolemBase> {
		private EntityAIAttackGolem(final EntityCreature creature) {
			super(creature, GolemBase.class, true);
		}
	}
}
