package com.golems.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityGoldGolem extends GolemBase {

	public EntityGoldGolem(final World world) {
		super(world);
		this.setLootTableLoc("golem_gold");
		this.setBaseMoveSpeed(0.19D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.9D);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("gold");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
