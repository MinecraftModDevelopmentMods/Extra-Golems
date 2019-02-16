package com.golems.entity;

import java.util.List;

import com.golems.main.ExtraGolems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityConcreteGolem extends GolemColorizedMultiTextured {

	public static final String ALLOW_RESIST = "Allow Special: Resistance";
	public static final String PREFIX = "concrete";
	public static final int[] COLOR_ARRAY = ItemDye.DYE_COLORS;
	
	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeTexture(ExtraGolems.MODID, "golem_" + PREFIX + "_base");
		private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeTexture(ExtraGolems.MODID, "golem_" + PREFIX + "_grayscale");
	
	public EntityConcreteGolem(final World world) {
		super(world, TEXTURE_BASE, TEXTURE_OVERLAY, COLOR_ARRAY);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	@Override
	protected void damageEntity(DamageSource source, float amount) {
		if(getConfig(this).getBoolean(ALLOW_RESIST)) {
			amount *= 3.0F / 5.0F;
		}
		super.damageEntity(source, amount);
	}
	
	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block metadata to give this golem the right texture (defaults to last item in color array)
		final int meta = body.getBlock().getMetaFromState(body)
			% this.getColorArray().length;
		this.setTextureNum((byte) (this.getColorArray().length - meta - 1));
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) { 
		if(getConfig(this).getBoolean(ALLOW_RESIST)) {
			String sResist = TextFormatting.DARK_GRAY + trans("effect.resistance");
			list.add(sResist);
		}
		return list;
	}
}
