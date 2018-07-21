package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityWoolGolem extends GolemMultiTextured {

	private static final String WOOL_PREFIX = "wool";
	private static final String[] coloredWoolTypes = { "black", "orange", "magenta", "light_blue",
			"yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green",
			"red", "white" };

	public EntityWoolGolem(World world) {
		super(world, Config.WOOL.getBaseAttack(), new ItemStack(Blocks.WOOL), WOOL_PREFIX,
				coloredWoolTypes);
		this.setCanSwim(true);
	}

	@Override
	public ItemStack getCreativeReturn() {
		ItemStack woolStack = super.getCreativeReturn();
		woolStack.setItemDamage(this.getTextureNum() % (coloredWoolTypes.length + 1));
		return woolStack;
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.WOOL.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	@Override
	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
		// final int size = 1 + this.rand.nextInt(3) + lootingLevel;
		final int meta = this.getTextureNum() % coloredWoolTypes.length;
		this.addDrop(dropList, new ItemStack(Blocks.WOOL, 1 + rand.nextInt(2), 0), 100);
		this.addDrop(dropList, Blocks.WOOL, meta, 1, 2, 60 + lootingLevel * 10);
		this.addDrop(dropList, Items.STRING, 0, 1, 2, 5 + lootingLevel * 10);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_CLOTH_STEP;
	}

	@Override
	public void setTextureNum(byte toSet, final boolean updateInstantly) {
		toSet %= (byte) (coloredWoolTypes.length - 1);
		super.setTextureNum(toSet, updateInstantly);
	}
}
