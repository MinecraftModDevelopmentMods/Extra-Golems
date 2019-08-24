package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class MagmaGolem extends GolemBase {

	public static final String ALLOW_FIRE_SPECIAL = "Allow Special: Burn Enemies";
	public static final String ALLOW_LAVA_SPECIAL = "Allow Special: Melt Cobblestone";
	public static final String ALLOW_SPLITTING = "Allow Special: Split";
	public static final String ALLOW_WATER_DAMAGE = "Enable Water Damage";
	public static final String MELT_DELAY = "Melting Delay";

	private static final String TEXTURE_LOC = ExtraGolems.MODID + ":textures/entity/magma/" + GolemNames.MAGMA_GOLEM;
	private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
		new ResourceLocation(TEXTURE_LOC + "_0.png"), new ResourceLocation(TEXTURE_LOC + "_1.png"),
		new ResourceLocation(TEXTURE_LOC + "_2.png"), new ResourceLocation(TEXTURE_LOC + "_3.png"),
		new ResourceLocation(TEXTURE_LOC + "_2.png"), new ResourceLocation(TEXTURE_LOC + "_1.png")
	};

	/**
	 * Golem should stand in one spot for number of ticks before affecting the block below it.
	 */
	private int ticksStandingStill;
	/**
	 * Helpers for "Standing Still" code
	 */
	private int stillX;
	private int stillZ;
	/**
	 * Whether this golem is hurt by water
	 */
	private boolean isHurtByWater;

	private boolean allowMelting;
	private int meltDelay;
	
	public MagmaGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.isHurtByWater = this.getConfigBool(ALLOW_WATER_DAMAGE);
		this.allowMelting = this.getConfigBool(ALLOW_LAVA_SPECIAL);
		this.meltDelay = this.getConfigInt(MELT_DELAY);
		this.ticksStandingStill = 0;
	}

	@Override
	public boolean canSwim() {
		return isHurtByWater;
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		// change stats if this is a child vs. an adult golem
		super.notifyDataManagerChange(key);
		if (CHILD.equals(key)) {
			if (this.isChild()) {
				// child golem can't use special abilities
				this.allowMelting = false;
				// truncate these values to one decimal place after reducing them from base values
				double childHealth = (Math.floor(container.getHealth() * 0.3D * 10D)) / 10D;
				double childAttack = (Math.floor(container.getAttack() * 0.6D * 10D)) / 10D;
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(childHealth);
				this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(childAttack);
			} else {
				this.allowMelting = this.getConfigBool(ALLOW_LAVA_SPECIAL);
				this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(container.getAttack());
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(container.getHealth());
			}
			// recalculate size just in case
			this.recalculateSize();
		}
	}

	@Override
	public ResourceLocation getTextureType() {
		final int changeInterval = 5;
		int textureNum = ((this.ticksExisted + this.getEntityId()) / changeInterval) % TEXTURES.length;
		return TEXTURES[textureNum];
	}

	/**
	 * Attack by lighting on fire as well.
	 */
	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			if (!this.isChild() && this.getConfigBool(ALLOW_FIRE_SPECIAL)) {
				entity.setFire(2 + rand.nextInt(5));
			}
			return true;
		}
		return false;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// take damage from water/rain
		if (this.isHurtByWater && this.isWet()) {
			this.attackEntityFrom(DamageSource.DROWN, 0.5F);
		}
		// check the cobblestone-melting math
		if (this.allowMelting && !this.isChild()) {
			final int x = MathHelper.floor(this.posX);
			final int y = MathHelper.floor(this.posY - 0.20000000298023224D);
			final int z = MathHelper.floor(this.posZ);
			final BlockPos below = new BlockPos(x, y, z);
			final Block b1 = this.world.getBlockState(below).getBlock();

			if (x == this.stillX && z == this.stillZ) {
				// check if it's been holding still long enough AND on top of cobblestone
				if (++this.ticksStandingStill >= this.meltDelay
						&& b1 == Blocks.COBBLESTONE && rand.nextInt(16) == 0) {
					BlockState replace = Blocks.MAGMA_BLOCK.getDefaultState();
					this.world.setBlockState(below, replace, 3);
					this.ticksStandingStill = 0;
				}
			} else {
				this.ticksStandingStill = 0;
				this.stillX = x;
				this.stillZ = z;
			}
		}
	}

	@Override
	protected SoundEvent getHurtSound(final DamageSource ignored) {
		return ignored == DamageSource.DROWN ? SoundEvents.BLOCK_LAVA_EXTINGUISH : this.getGolemSound();
	}

	@Override
	public void onDeath(final DamageSource source) {
		if(!this.world.isRemote && !this.isChild() && this.getConfigBool(ALLOW_SPLITTING)) {
			GolemBase child1 = GolemRegistrar.getContainer(MagmaGolem.class).getEntityType().create(this.world);
			GolemBase child2 = GolemRegistrar.getContainer(MagmaGolem.class).getEntityType().create(this.world);
			child1.setChild(true);
			child2.setChild(true);
			// copy attack target info
			if (this.getAttackTarget() != null) {
				child1.setAttackTarget(this.getAttackTarget());
				child2.setAttackTarget(this.getAttackTarget());
			}
			// set location
			child1.setLocationAndAngles(this.posX + rand.nextDouble() - 0.5D, this.posY,
					this.posZ + rand.nextDouble() - 0.5D, this.rotationYaw + rand.nextInt(20) - 10, 0);
			child2.setLocationAndAngles(this.posX + rand.nextDouble() - 0.5D, this.posY,
					this.posZ + rand.nextDouble() - 0.5D, this.rotationYaw + rand.nextInt(20) - 10, 0);
			// spawn the entities
			this.getEntityWorld().addEntity(child1);
			this.getEntityWorld().addEntity(child2);
		}

		super.onDeath(source);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return 15728880;
	}
}
