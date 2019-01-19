package com.golems.entity;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityHardenedClayGolem extends GolemBase {

	public EntityHardenedClayGolem(final World world) {
		super(world);
		this.setLootTableLoc("golem_hardened_clay");
		this.setBaseMoveSpeed(0.18D);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("hardened_clay");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
