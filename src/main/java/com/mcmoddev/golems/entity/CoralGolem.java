package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CoralBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class CoralGolem extends GolemMultiTextured {

	protected static final DataParameter<Boolean> DRY = EntityDataManager.createKey(CoralGolem.class, DataSerializers.BOOLEAN);
	protected static final String KEY_DRY = "isDry";
	
	public static final String[] VARIANTS = { "tube", "brain", "bubble", "fire", "horn" };
	public final ResourceLocation[] texturesDry;
	
	// the minimum amount of time before golem will change between "dry" and "wet"
	private static final int TIME_TO_CHANGE = 240;
	// the amount of time since this golem started changing between "dry" and "wet"
	private int timeChanging = 0;
	
	public CoralGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, VARIANTS);
		this.texturesDry = new ResourceLocation[VARIANTS.length];
		for (int n = 0, len = VARIANTS.length; n < len; n++) {
			// initialize "dead" textures
			this.texturesDry[n] = makeTexture(ExtraGolems.MODID, this.container.getName() + "/" + VARIANTS[n] + "_dead");
		}		
	}
	
	public boolean isDry() {
		return this.getDataManager().get(DRY).booleanValue();
	}
	
	public void setDry(boolean isDry) {
		if(this.getDataManager().get(DRY).booleanValue() != isDry) {
			this.getDataManager().set(DRY, Boolean.valueOf(isDry));
		}
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(DRY, Boolean.valueOf(false));
	}
	
	@Override
	public void livingTick() {
		super.livingTick();
		// update "dry" data if the golem has been "changing" state for long enough
		final boolean isChanging = this.isInWaterOrBubbleColumn() == this.isDry();
		if(isChanging) {
			if(!this.world.isRemote && ++timeChanging > TIME_TO_CHANGE) {
				this.setDry(!this.isInWaterOrBubbleColumn());
				this.timeChanging = 0;
			}
		} else {
			timeChanging = 0;
		}
		// heals randomly, but only when wet
		if (!this.isDry() && rand.nextInt(650) == 0) {
			this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 60, 1));
		}
	}

	@Override
	public void notifyDataManagerChange(final DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (DRY.equals(key)) {
			this.setDry(this.getDataManager().get(DRY).booleanValue());
			if (this.getDataManager().get(DRY).booleanValue()) {
				// truncate these values to one decimal place after modifying them from base values
				double dryHealth = Math.floor(container.getHealth() * 0.8D);
				double dryAttack = Math.floor(container.getAttack() * 1.8D * 10D) / 10D;
				double drySpeed = Math.floor(container.getSpeed() * 0.7D);
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(dryHealth);
				this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(dryAttack);
				this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(drySpeed);
				// particle effects to show that the golem is "drying out"
				ItemBedrockGolem.spawnParticles(this.world, this.posX - 0.5D, this.posY + 0.1D,
						this.posZ - 0.5D, 0.09D, ParticleTypes.SMOKE, 40);
			} else {
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(container.getHealth());
				this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(container.getAttack());
				this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(container.getSpeed());
			}
		}
	}
	
	@Override
	public void writeAdditional(final CompoundNBT nbt) {
		super.writeAdditional(nbt);
		nbt.putBoolean(KEY_DRY, this.isDry());
	}

	@Override
	public void readAdditional(final CompoundNBT nbt) {
		super.readAdditional(nbt);
		this.setDry(nbt.getBoolean(KEY_DRY));
	}
	
	@Override
	public ResourceLocation[] getTextureArray() {
		return this.isDry() ? this.texturesDry : this.textures;
	}
	
	@Override
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		// uses HashMap to determine which texture this golem should apply
		// based on the top-middle building block.
		this.setDry(!(body.getBlock() instanceof CoralBlock));
		final Map<Block, Byte> map = this.isDry() 
				? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL;
		byte textureNum = GolemTextureBytes.getByBlock(map, body.getBlock());
		this.setTextureNum(textureNum);
	}

	@Override
	public ItemStack getCreativeReturn(final RayTraceResult target) {
		return new ItemStack(GolemTextureBytes.getByByte(
				this.isDry() ? GolemTextureBytes.CORAL_DEAD : GolemTextureBytes.CORAL, 
				(byte)this.getTextureNum()));
	}	
}
