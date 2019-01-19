package com.golems.entity;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityDiamondGolem extends GolemBase {

	public EntityDiamondGolem(final World world) {
		super(world);
		this.setLootTableLoc("golem_diamond");
		this.setBaseMoveSpeed(0.28D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("diamond_block");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
