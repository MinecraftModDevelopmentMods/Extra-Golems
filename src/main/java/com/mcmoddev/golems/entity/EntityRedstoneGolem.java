package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.blocks.BlockUtilityPower;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemLookup;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public final class EntityRedstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Redstone Power";
	public static final int DEF_FREQ = 2;

	/**
	 * Default constructor for Redstone Golem
	 **/
	public EntityRedstoneGolem(final World world) {
		this(EntityRedstoneGolem.class, world, GolemLookup.getConfig(EntityRedstoneGolem.class).getBoolean(ALLOW_SPECIAL), 15, DEF_FREQ);
		this.setLootTableLoc(GolemNames.REDSTONE_GOLEM);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	/**
	 * Flexible constructor to allow child classes to customize
	 **/
	public EntityRedstoneGolem(final Class<? extends EntityRedstoneGolem> clazz, final World world, 
			boolean allowSpecial, int power, int frequency) {
		super(clazz, world);
		final IBlockState state = GolemItems.blockPowerSource.getDefaultState().with(BlockUtilityPower.POWER_LEVEL, power);
		this.tasks.addTask(9, new EntityAIPlaceSingleBlock(this, state, frequency, allowSpecial));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.REDSTONE_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return super.getBrightnessForRender() + 64;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		// does not fire for child classes
		if (this.getClass() == EntityRedstoneGolem.class && getConfig(this).getBoolean(ALLOW_SPECIAL))
			list.add(TextFormatting.RED + trans("entitytip.emits_redstone_signal"));
		return list;
	}
}
