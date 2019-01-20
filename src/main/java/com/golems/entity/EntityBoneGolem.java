package com.golems.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityBoneGolem extends GolemBase {

	public EntityBoneGolem(final World world) {
		super(world);
		this.setCanTakeFallDamage(true);
		this.setLootTableLoc("golem_bone");
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	protected ResourceLocation applyTexture() {
		//return makeGolemTexture("bone");
		return makeGolemTexture("bone_skeleton");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
