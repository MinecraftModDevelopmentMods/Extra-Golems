package com.golems.entity;

import com.golems.items.ItemBedrockGolem;
import com.golems.main.Config;
import com.golems.main.GolemItems;
import com.golems.util.WeightedItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityBedrockGolem extends GolemBase {

	public EntityBedrockGolem(World world) {
		super(world, Config.BEDROCK.getBaseAttack(), Blocks.BEDROCK);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("bedrock");
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return super.attackEntityAsMob(entity);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource src) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		// creative players can "despawn" by using spawnBedrockGolem on this entity
		ItemStack itemstack = player.getHeldItem(hand);
		if (player.capabilities.isCreativeMode) {
			if (!itemstack.isEmpty() && itemstack.getItem() == GolemItems.spawnBedrockGolem) {
				player.swingArm(hand);
				if (!this.world.isRemote) {
					this.setDead();
				} else
					ItemBedrockGolem.spawnParticles(this.world, this.posX - 0.5D, this.posY + 0.1D,
							this.posZ - 0.5D, 0.1D);
			}
		}

		return super.processInteract(player, hand);
	}

	@Override
	protected void damageEntity(DamageSource source, float amount) {
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.BEDROCK.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel) {
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
