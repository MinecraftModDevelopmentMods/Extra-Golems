package com.golems.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.golems.items.ItemBedrockGolem;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityBedrockGolem extends GolemBase {

	public EntityBedrockGolem(final World world) {
		super(world);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.BEDROCK_GOLEM);
	}

	@Override
	public boolean isEntityInvulnerable(final DamageSource src) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public EnumActionResult applyPlayerInteraction(final EntityPlayer player, final Vec3d vec, 
    		@Nullable final ItemStack stack, final EnumHand hand) {
		// creative players can "despawn" by using spawnBedrockGolem on this entity
		if (player.capabilities.isCreativeMode && stack != null && stack.getItem() == GolemItems.spawnBedrockGolem) {
			player.swingArm(hand);
			if (!this.worldObj.isRemote) {
				this.setDead();
			} else {
				ItemBedrockGolem.spawnParticles(this.worldObj, this.posX, this.posY + 0.1D, this.posZ, 0.1D);
			}
		}

		return super.applyPlayerInteraction(player, vec, stack, hand);
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
	public ItemStack getPickedResult(final RayTraceResult target) {
		return new ItemStack(GolemItems.spawnBedrockGolem);
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		list.add(TextFormatting.WHITE + "" + TextFormatting.BOLD + trans("entitytip.indestructible"));
		list.add(TextFormatting.DARK_RED + trans("tooltip.creative_only_item"));
		return list;
	}
}
