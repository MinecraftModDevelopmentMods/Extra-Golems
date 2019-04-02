package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.blocks.BlockUtilityPower;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EntityRedstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Redstone Power";

	public EntityRedstoneGolem(final World world) {
		super(EntityRedstoneGolem.class, world);
		final IBlockState state = GolemItems.blockPowerSource.getDefaultState().with(BlockUtilityPower.POWER_LEVEL, 15);
		final int freq = BlockUtilityPower.UPDATE_TICKS;
		final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
		this.tasks.addTask(9, new EntityAIPlaceSingleBlock(this, state, freq, allow));
		this.setLootTableLoc(GolemNames.REDSTONE_GOLEM);
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
		if (this.getConfigBool(ALLOW_SPECIAL)) {
			list.add(TextFormatting.RED + trans("entitytip.emits_redstone_signal"));
		}
		return list;
	}
}
