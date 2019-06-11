package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class EntityEmeraldGolem extends GolemBase {

	public EntityEmeraldGolem(final World world) {
		super(EntityEmeraldGolem.class, world);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.EMERALD_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	/** 
	 * Updates this golem's home position IF there is a nearby village.
	 * @return if the golem found a village home
	 * @see #updateHomeVillageInRange(BlockPos, int)
	 **/
	@Override
	public boolean updateHomeVillage() {
		// EMERALD golem checks a much larger radius than usual
		final int radius = WANDER_DISTANCE * 6;
		return updateHomeVillageInRange(new BlockPos(this), radius);
	}
}
