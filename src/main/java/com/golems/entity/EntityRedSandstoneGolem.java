package com.golems.entity;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityRedSandstoneGolem extends GolemBase {

	public EntityRedSandstoneGolem(final World world) {
		super(world);
		this.setLootTableLoc("golem_red_sandstone");
		this.setBaseMoveSpeed(0.28D);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("red_sandstone");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
