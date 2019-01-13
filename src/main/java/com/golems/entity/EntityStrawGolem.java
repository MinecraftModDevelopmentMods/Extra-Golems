package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityStrawGolem extends GolemBase {

	public EntityStrawGolem(final World world) {
		super(world, Config.STRAW.getBaseAttack(), Blocks.HAY_BLOCK);
		this.setCanSwim(true);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("straw");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.STRAW.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 6 + this.rand.nextInt(8 + lootingLevel * 4);
//		this.addDrop(dropList, new ItemStack(Items.WHEAT, size), 100);
//		this.addDrop(dropList, Items.WHEAT_SEEDS, 0, 1, 3 + lootingLevel * 2,
//				10 + lootingLevel * 10);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
}
