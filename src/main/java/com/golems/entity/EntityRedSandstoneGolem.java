package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityRedSandstoneGolem extends GolemBase {

	public EntityRedSandstoneGolem(World world) {
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

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel) {
		int size = 4 + this.rand.nextInt(8 + lootingLevel * 2);
		this.addDrop(dropList, new ItemStack(Blocks.SAND, size, 1), 100);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
