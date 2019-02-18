package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityBoneGolem extends GolemBase {

	public EntityBoneGolem(final World world) {
		super(GolemEntityTypes.BONE, world);
		this.setCanTakeFallDamage(true);
		this.setLootTableLoc(GolemNames.BONE_GOLEM);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	protected ResourceLocation applyTexture() {
		//return makeGolemTexture("bone");
		return makeTexture(ExtraGolems.MODID, GolemNames.BONE_GOLEM + "_skeleton");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
