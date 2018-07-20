package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.util.WeightedItem;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityWoodenGolem extends GolemMultiTextured {

	public static final String woodPrefix = "wooden";
	public static final String[] woodTypes = { "oak", "spruce", "birch", "jungle", "acacia",
			"big_oak" };

	public EntityWoodenGolem(World world) {
		super(world, Config.WOOD.getBaseAttack(), new ItemStack(Blocks.LOG), woodPrefix, woodTypes);
		this.setCanSwim(true);
	}

	@Override
	public ItemStack getCreativeReturn() {
		// try to return the same block of this golem's texture
		Block block = Blocks.LOG;
		int damage = this.getTextureNum() % woodTypes.length;
		if (this.getTextureNum() > 3) {
			block = Blocks.LOG2;
			damage %= 2;
		}
		return new ItemStack(block, 1, damage);
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.WOOD.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel) {
		int size = 6 + this.rand.nextInt(4 + lootingLevel * 4);
		int meta = this.getTextureNum() % woodTypes.length;
		this.addDrop(dropList, new ItemStack(Blocks.PLANKS, size > 16 ? 16 : size, meta), 100);
		this.addDrop(dropList, Items.STICK, 0, 1, 4, 10 + lootingLevel * 4);
		this.addDrop(dropList, Blocks.SAPLING, 0, 1, 2, 4 + lootingLevel * 4);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
