package com.golems.entity;

import java.util.List;

import com.golems.items.ItemBedrockGolem;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityFurnaceGolem extends GolemBase {

	private static final DataParameter<Integer> FUEL = EntityDataManager.createKey(EntityFurnaceGolem.class,
			DataSerializers.VARINT);
	private static final String KEY_FUEL = "FuelRemaining";

	private static final ResourceLocation LIT = makeTexture(ExtraGolems.MODID, GolemNames.FURNACE_GOLEM + "_lit");
	private static final ResourceLocation UNLIT = makeTexture(ExtraGolems.MODID, GolemNames.FURNACE_GOLEM + "_unlit");

	public static final String FUEL_FACTOR = "Burn Time";
	public static final int MAX_FUEL = 102400;
	public final int fuelBurnFactor;

	public EntityFurnaceGolem(final World world) {
		super(world);
		fuelBurnFactor = Math.max(1, getConfig(this).getInt(FUEL_FACTOR));
		this.setImmuneToFire(true);
		this.addHealItem(new ItemStack(Blocks.COBBLESTONE), 0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(FUEL, Integer.valueOf(0));
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(0, new InertGoal(this));
		this.tasks.addTask(1, new UseFuelGoal(this));
		this.tasks.addTask(1, new EntityAITempt(this, 0.7D, Items.COAL, false));
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.world.isRemote && rand.nextInt(24) == 0) {
			// particle effects
			final double pMotion = 0.03D;
			world.spawnParticle(this.hasFuel() ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_NORMAL,
					this.posX + world.rand.nextDouble() * 0.4D - 0.2D + this.motionX * 8,
					this.posY + world.rand.nextDouble() * 0.5D + this.height / 2.0D,
					this.posZ + world.rand.nextDouble() * 0.4D - 0.2D + this.motionZ * 8,
					world.rand.nextDouble() * pMotion - pMotion * 0.5D, world.rand.nextDouble() * pMotion * 0.75D,
					world.rand.nextDouble() * pMotion - pMotion * 0.5D);
		}
		if(!hasFuel() && this.getAttackTarget() != null) {
			this.setAttackTarget(null);
		}
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		this.setFuel(tag.getInteger(KEY_FUEL));
	}

	@Override
	public void writeEntityToNBT(final NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger(KEY_FUEL, getFuel());
	}

	/**
	 * Applies a velocity to the entities, to push them away from eachother.
	 */
	@Override
	public void applyEntityCollision(final Entity entityIn) {
		if (this.hasFuel()) {
			super.applyEntityCollision(entityIn);
		}
	}

	@Override
	public float getCollisionBorderSize() {
		return this.hasFuel() ? super.getCollisionBorderSize() : 0.0F;
	}
	
	@Override
	public boolean canAttackClass(final Class<? extends EntityLivingBase> cls) {
		return hasFuel() && super.canAttackClass(cls);
	}
	
	@Override
	public void setAttackTarget(final EntityLivingBase target) {
		if(hasFuel()) {
			super.setAttackTarget(target);
		}
	}

	@Override
	protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
		// allow player to add fuel to the golem by clicking on them with a fuel item
		ItemStack stack = player.getHeldItem(hand);
		int burnTime = getBurnAmount(stack) * (player.isSneaking() ? stack.getCount() : 1);
		if (burnTime > 0 && (getFuel() + burnTime) <= MAX_FUEL) {
			// add the fuel
			this.addFuel(burnTime);
			// reduce the itemstack
			if(stack.getCount() > 1 && !player.isSneaking()) {
				stack.shrink(1);
			} else {
				stack = stack.getItem().getContainerItem(stack);
			}
			// update the player's held item
			player.setHeldItem(hand, stack);
			// add particles
			ItemBedrockGolem.spawnParticles(this.world, this.posX, this.posY + this.height / 2.0D, this.posZ, 0.03D,
					EnumParticleTypes.FLAME, 10);
			return true;
		}

		// allow player to remove burn time by using a water bucket
		if (stack.getItem() == Items.WATER_BUCKET) {
			this.setFuel(0);
			player.setHeldItem(hand, stack.getItem().getContainerItem(stack));
			ItemBedrockGolem.spawnParticles(this.world, this.posX, this.posY + this.height / 2.0D, this.posZ, 0.1D,
					EnumParticleTypes.SMOKE_LARGE, 15);
			return true;
		}

		return super.processInteract(player, hand);
	}

	private static int getBurnAmount(final ItemStack i) {
		return TileEntityFurnace.getItemBurnTime(i);
	}

	@Override
	public ResourceLocation getTextureType() {
		return hasFuel() ? LIT : UNLIT;
	}

	@Override
	protected ResourceLocation applyTexture() {
		// apply TEMPORARY texture to avoid NPE. Actual texture is first applied in
		// onLivingUpdate
		return makeTexture(ExtraGolems.MODID, GolemNames.CLAY_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	public boolean hasFuel() {
		return getFuel() > 0;
	}

	/** @return the current fuel level as a number **/
	public int getFuel() {
		return this.getDataManager().get(FUEL).intValue();
	}

	/** @return a number between 0.0 and 1.0 to indicate fuel level **/
	public float getFuelPercentage() {
		return (float) getFuel() / (float) MAX_FUEL;
	}

	public void setFuel(final int fuel) {
		if (getFuel() != fuel) {
			this.getDataManager().set(FUEL, Integer.valueOf(fuel));
		}
	}

	public void addFuel(final int toAdd) {
		if (toAdd != 0) {
			this.getDataManager().set(FUEL, getFuel() + toAdd);
		}
	}

	/**
	 * Sets the head's yaw rotation of the entity.
	 */
	@Override
	public void setRotationYawHead(float rotation) {
		if (hasFuel()) {
			super.setRotationYawHead(rotation);
		}
	}

	@SideOnly(Side.CLIENT)
	public void turn(float yaw, float pitch) {
		if (this.hasFuel()) {
			super.turn(yaw, pitch);
		}
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		// add special
		list.add(TextFormatting.GRAY + trans("entitytip.use_fuel"));
		// add fuel amount to the tooltip
		final int fuel = this.getFuel();
		if (fuel > 0) {
			final int percentFuel = (int) Math.ceil(this.getFuelPercentage() * 100F);
			final TextFormatting color;
			if (percentFuel < 6) {
				color = TextFormatting.RED;
			} else if (percentFuel < 16) {
				color = TextFormatting.YELLOW;
			} else {
				color = TextFormatting.WHITE;
			}
			// if sneaking, show exact value, otherwise show percentage value
			final String fuelString = GuiScreen.isShiftKeyDown() ? Integer.toString(fuel)
					: (Integer.toString(percentFuel) + "%");
			// actually add the description
			list.add(TextFormatting.GRAY + trans("entitytip.fuel") + ": " + color + fuelString);
		}
		return list;
	}

	class UseFuelGoal extends EntityAIBase {

		private final EntityFurnaceGolem golem;

		protected UseFuelGoal(final EntityFurnaceGolem entity) {
			super();
			golem = entity;
		}

		@Override
		public boolean shouldExecute() {
			// only uses fuel every X ticks
			return golem.isServerWorld() && golem.getFuel() > 0 && golem.ticksExisted % golem.fuelBurnFactor == 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void startExecuting() {
			golem.addFuel(-1);
		}
	}

	class InertGoal extends EntityAIBase {

		private final EntityFurnaceGolem golem;

		protected InertGoal(final EntityFurnaceGolem entity) {
			super();
			this.setMutexBits(7);
			golem = entity;
		}

		@Override
		public boolean shouldExecute() {
			return !golem.hasFuel();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void startExecuting() {
			updateTask();
		}

		@Override
		public void updateTask() {
			// freeze the golem and ai tasks
			golem.motionX = 0D;
			golem.motionZ = 0D;
			golem.setMoveForward(0F);
			golem.setMoveStrafing(0F);
			golem.getMoveHelper().setMoveTo(golem.posX, golem.posY, golem.posZ, 0.1D);
			golem.setJumping(false);
			//golem.setAttackTarget(null);
			//golem.setRevengeTarget(null);
			golem.getNavigator().clearPath();
			golem.prevRotationYaw = -15F;
			golem.setRotation(golem.prevRotationYaw, prevRotationYaw);
			// set looking down
			final double lookX = golem.getLookVec().x;
			final double lookY = Math.toRadians(-15D);
			final double lookZ = golem.getLookVec().z;
			golem.getLookHelper().setLookPosition(lookX, lookY, lookZ, golem.getHorizontalFaceSpeed(),
					golem.getVerticalFaceSpeed());

		}
	}
}
