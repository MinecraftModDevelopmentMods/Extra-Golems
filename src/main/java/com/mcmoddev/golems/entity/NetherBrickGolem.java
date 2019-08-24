package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public final class NetherBrickGolem extends GolemBase {

	public static final String ALLOW_FIRE_SPECIAL = "Allow Special: Burn Enemies";

	public NetherBrickGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
	}

	/**
	 * Attack by lighting on fire as well.
	 */
	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			if (this.getConfigBool(ALLOW_FIRE_SPECIAL)) {
				entity.setFire(2 + rand.nextInt(5));
			}
			return true;
		}
		return false;
	}
}
