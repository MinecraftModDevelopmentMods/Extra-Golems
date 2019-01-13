package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntitySandstoneGolem extends GolemBase {

	public EntitySandstoneGolem(final World world) {
		super(world, Config.SANDSTONE.getBaseAttack(), Blocks.SANDSTONE);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("sandstone");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.SANDSTONE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 4 + this.rand.nextInt(8 + lootingLevel * 2);
//		this.addDrop(dropList, new ItemStack(Blocks.SAND, size), 100);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
