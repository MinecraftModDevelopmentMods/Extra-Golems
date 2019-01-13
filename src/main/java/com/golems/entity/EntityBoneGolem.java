package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityBoneGolem extends GolemBase {

	public EntityBoneGolem(final World world) {
		super(world, Config.BONE.getBaseAttack(), Blocks.BONE_BLOCK);
		this.setCanTakeFallDamage(true);
	}

	protected ResourceLocation applyTexture() {
		//return makeGolemTexture("bone");
		return makeGolemTexture("bone_skeleton");
	}
	
	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.BONE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		this.addDrop(dropList, Items.BONE, 0, lootingLevel + rand.nextInt(4), 24 + rand.nextInt(6), 100);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
