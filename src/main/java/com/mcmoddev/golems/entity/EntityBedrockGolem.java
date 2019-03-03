package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public final class EntityBedrockGolem extends GolemBase {

	public EntityBedrockGolem(final World world) {
		super(EntityBedrockGolem.class, world);
		this.setCreativeReturn(new ItemStack(GolemItems.spawnBedrockGolem));
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.BEDROCK_GOLEM);
	}


	@Override
	public boolean isInvulnerable() {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
		// creative players can "despawn" by using spawnBedrockGolem on this entity
		final ItemStack itemstack = player.getHeldItem(hand);
		if (player.abilities.isCreativeMode && !itemstack.isEmpty() && itemstack.getItem() == GolemItems.spawnBedrockGolem) {
			player.swingArm(hand);
			if (!this.world.isRemote) {
				this.remove();
			} else {
				ItemBedrockGolem.spawnParticles(this.world, this.posX - 0.5D, this.posY + 0.1D,
						this.posZ - 0.5D, 0.1D);
			}
		}

		return super.processInteract(player, hand);
	}

	@Override
	protected void damageEntity(final DamageSource source, final float amount) {
		//
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		list.add(TextFormatting.WHITE + "" + TextFormatting.BOLD + trans("entitytip.indestructible"));
		list.add(TextFormatting.DARK_RED + trans("tooltip.creative_only_item"));
		return list;
	}
}
