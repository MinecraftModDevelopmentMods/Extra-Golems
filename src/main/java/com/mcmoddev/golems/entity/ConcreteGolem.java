package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class ConcreteGolem extends GolemMultiColorized {

	public static final String ALLOW_RESIST = "Allow Special: Resistance";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.CONCRETE_GOLEM + "_base");
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeTexture(ExtraGolems.MODID, GolemNames.CONCRETE_GOLEM + "_grayscale");

	public ConcreteGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, TEXTURE_BASE, TEXTURE_OVERLAY, DYE_COLORS);
	}

	@Override
	protected void damageEntity(DamageSource source, float amount) {
		if (this.getConfigBool(ALLOW_RESIST)) {
			amount *= 0.6F;
			if (source.isFireDamage()) {
				// additional fire resistance
				amount *= 0.85F;
			}
		}
		super.damageEntity(source, amount);
	}

	@Override
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		byte textureNum = GolemTextureBytes.getByBlock(GolemTextureBytes.CONCRETE, body.getBlock());
		this.setTextureNum(textureNum);
	}
	
	@Override
	public ItemStack getCreativeReturn(final RayTraceResult target) {
		return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.CONCRETE, (byte)this.getTextureNum()));
	}
}
