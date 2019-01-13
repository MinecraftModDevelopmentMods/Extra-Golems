package com.golems.entity;

import java.util.List;

import com.golems.blocks.BlockUtilityPower;
import com.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.golems.main.Config;
import com.golems.main.GolemItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityRedstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Redstone Power";
	public static final int FREQUENCY = 2;

	/**
	 * If you want to change power after constructor, add a {@link DataParameter}.
	 **/


	/** Default constructor for Redstone Golem **/
	public EntityRedstoneGolem(final World world) {
		this(world, Config.REDSTONE.getBaseAttack(), new ItemStack(Blocks.REDSTONE_BLOCK), Config.REDSTONE.getBoolean(ALLOW_SPECIAL), 15);
	}

	/** Flexible constructor to allow child classes to customize **/
	public EntityRedstoneGolem(final World world, final float attack, final ItemStack pick, boolean allowSpecial, int power) {
		super(world, attack, pick);
		final IBlockState state = GolemItems.blockPowerSource.getDefaultState().withProperty(BlockUtilityPower.POWER_LEVEL, power);
		this.tasks.addTask(9, new EntityAIPlaceSingleBlock(this, state, FREQUENCY, allowSpecial));
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

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 8 + rand.nextInt(14 + lootingLevel * 4);
//		this.addDrop(dropList, new ItemStack(Items.REDSTONE, size > 36 ? 36 : size), 100);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
		return super.getBrightnessForRender() + 64;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		// does not fire for child classes
		if(this.getClass() == EntityRedstoneGolem.class && Config.REDSTONE.getBoolean(ALLOW_SPECIAL))
			list.add(TextFormatting.RED + trans("entitytip.emits_redstone_signal"));
		return list;
	}
}
