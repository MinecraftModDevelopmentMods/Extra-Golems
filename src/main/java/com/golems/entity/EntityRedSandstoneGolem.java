package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityRedSandstoneGolem extends GolemBase {

	public EntityRedSandstoneGolem(final World world) {
		super(world, Config.RED_SANDSTONE.getBaseAttack(), Blocks.RED_SANDSTONE);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("red_sandstone");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.RED_SANDSTONE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 4 + this.rand.nextInt(8 + lootingLevel * 2);
//		this.addDrop(dropList, new ItemStack(Blocks.SAND, size, 1), 100);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
