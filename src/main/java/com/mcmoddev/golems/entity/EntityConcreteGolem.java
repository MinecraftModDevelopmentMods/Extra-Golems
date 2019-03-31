package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityConcreteGolem extends GolemMultiColorized {

	public static final String ALLOW_RESIST = "Allow Special: Resistance";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.CONCRETE_GOLEM + "_base");
		private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.CONCRETE_GOLEM + "_grayscale");

	public EntityConcreteGolem(final World world) {
		super(EntityConcreteGolem.class, world, TEXTURE_BASE, TEXTURE_OVERLAY, dyeColorArray);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2D);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	@Override
	protected void damageEntity(DamageSource source, float amount) {
		if(this.getConfigBool(ALLOW_RESIST)) {
			amount *= 3.0F / 5.0F;
		}
		super.damageEntity(source, amount);
	}

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block metadata to give this golem the right texture (defaults to last item in color array)
		EnumDyeColor color = EnumDyeColor.getColor(new ItemStack(body.getBlock()));
		final int meta = color != null ? color.getId()
			% this.getColorArray().length : 0;
		this.setTextureNum((byte) (this.getColorArray().length - meta - 1));
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(this.getConfigBool(ALLOW_RESIST)) {
			String sResist = TextFormatting.DARK_GRAY + trans("effect.minecraft.resistance");
			list.add(sResist);
		}
		return list;
	}
}
