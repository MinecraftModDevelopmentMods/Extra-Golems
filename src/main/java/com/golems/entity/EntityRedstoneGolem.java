package com.golems.entity;

import com.golems.main.Config;
import com.golems.main.GolemItems;
import com.golems.util.WeightedItem;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.List;

public final class EntityRedstoneGolem extends GolemBase {

//	public static final String ALLOW_SPECIAL = "Allow Special: Electrical Spark";
//	public static final String FREQUENCY = "Chance of smiting foes";

	/**
	 * If you want to change power after constructor, add a {@link DataParameter}.
	 **/


	/**
	 * Default constructor for Redstone Golem.
	 **/
	public EntityRedstoneGolem(final World world) {
		this(world, Config.REDSTONE.getBaseAttack(), Blocks.REDSTONE_BLOCK);
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 * 
	 * @param world
	 * @param attack
	 * @param pick

	 */
	public EntityRedstoneGolem(final World world, final float attack, final Block pick) {
		super(world, attack, pick);
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 * 
	 * @param world
	 * @param attack
	 */
	public EntityRedstoneGolem(final World world, final float attack) {
		this(world, attack, GolemItems.golemHead);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("redstone");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.REDSTONE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}



	@Override
	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
		final int size = 8 + rand.nextInt(14 + lootingLevel * 4);
		this.addDrop(dropList, new ItemStack(Items.REDSTONE, size > 36 ? 36 : size), 100);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
