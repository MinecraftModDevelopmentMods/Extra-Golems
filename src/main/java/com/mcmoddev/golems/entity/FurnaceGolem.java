package com.mcmoddev.golems.entity;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public final class FurnaceGolem extends GolemBase {

	private static final DataParameter<Integer> FUEL = EntityDataManager.createKey(FurnaceGolem.class,
			DataSerializers.VARINT);
	private static final String KEY_FUEL = "FuelRemaining";
	private static final int MAX_FUEL = 102400;
	
	private static final ResourceLocation ACTIVE = makeTexture(GolemNames.FURNACE_GOLEM + "/active");
	private static final ResourceLocation INERT = makeTexture(GolemNames.FURNACE_GOLEM + "/inert");
	
	public static final String FUEL_FACTOR = "Burn Time";
	protected final int fuelBurnTime;

	public FurnaceGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		fuelBurnTime = getConfigInt(FUEL_FACTOR);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(FUEL, Integer.valueOf(0));
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new InertGoal(this));
		this.goalSelector.addGoal(1, new UseFuelGoal(this));
		this.goalSelector.addGoal(2, new TemptGoal(this, 0.7D, Ingredient.fromTag(ItemTags.COALS), false));
	}
	
	@Override
	public void livingTick() {
		super.livingTick();
		if(this.world.isRemote && rand.nextInt(24) == 0) {
			// particle effects
			final double pMotion = 0.03D;
			world.addParticle(this.hasFuel() ? ParticleTypes.FLAME : ParticleTypes.SMOKE, 
					this.posX + world.rand.nextDouble() * 0.25D - 0.125D + this.getMotion().getX() * 8,
					this.posY + world.rand.nextDouble() * 0.5D + this.getHeight() / 2.0D, 
					this.posZ + world.rand.nextDouble() * 0.25D - 0.125D + this.getMotion().getZ() * 8,
					world.rand.nextDouble() * pMotion - pMotion * 0.5D,
					world.rand.nextDouble() * pMotion * 0.75D,
					world.rand.nextDouble() * pMotion - pMotion * 0.5D);
		}
		
		if(this.ticksExisted % 20 == 0) {
			// DEBUG
			System.out.println("Fuel is now " + getFuel());
		}
	}
	
	@Override
	public void readAdditional(final CompoundNBT tag) {
		super.readAdditional(tag);
		this.setFuel(tag.getInt(KEY_FUEL));
	}
	
	@Override
	public void writeAdditional(final CompoundNBT tag) {
		super.writeAdditional(tag);
		tag.putInt(KEY_FUEL, getFuel());
	}
	
	@Override
	protected boolean processInteract(final PlayerEntity player, final Hand hand) {
		// allow player to add fuel to the golem by clicking on them with a fuel item
		ItemStack stack = player.getHeldItem(hand);
		int burnTime = getFuelAmount(stack);
		if(burnTime > 0 && getFuel() < MAX_FUEL) {
			if(player.isSneaking()) {
				// take entire ItemStack
				this.addFuel(burnTime * stack.getCount());
				stack.setCount(0);
			} else {
				// take one item from ItemStack
				this.addFuel(burnTime);
				stack.shrink(1);
			}
			// update the player's held item
			player.setHeldItem(hand, stack.isEmpty() ? stack.getContainerItem() : stack);
			// add particles
			if(this.world.isRemote) {
				ItemBedrockGolem.spawnParticles(this.world, this.posX - 0.5D, this.posY + this.getHeight() / 3.0D,
						this.posZ - 0.5D, 0.03D, ParticleTypes.FLAME, 10);
			}
			return true;
		}
		
		// allow player to remove burn time by using a water bucket
		if(stack.getItem() == Items.WATER_BUCKET) {
			this.setFuel(0);
			player.setHeldItem(hand, stack.getContainerItem());
			return true;
		}
		
		return super.processInteract(player, hand);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return hasFuel() ? ACTIVE : INERT;
	}
	
	public boolean hasFuel() {
		return getFuel() > 0;
	}
	
	public int getFuel() {
		return this.getDataManager().get(FUEL).intValue();
	}
	
	public void setFuel(final int fuel) {
		if(getFuel() != fuel) {
			this.getDataManager().set(FUEL, Integer.valueOf(fuel));
		}
	}
	
	public void addFuel(final int toAdd) {
		if(toAdd != 0) {
			this.getDataManager().set(FUEL, getFuel() + toAdd);			
		}
	}
	
	private static int getFuelAmount(final ItemStack i) {
		return ForgeHooks.getBurnTime(i);
	}
	
	class UseFuelGoal extends Goal {
		
		private final FurnaceGolem golem;
		
		protected UseFuelGoal(final FurnaceGolem entity) {
			super();
			golem = entity;
		}

		@Override
		public boolean shouldExecute() {
			// only uses fuel every X ticks
			return golem.isServerWorld() && golem.getFuel() > 0 && golem.ticksExisted % golem.fuelBurnTime == 0;
		}
		
		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
		
		@Override
		public void startExecuting() {
			tick();
		}
		
		@Override
		public void tick() {
			golem.addFuel(-1);
		}
	}
	
	class InertGoal extends Goal {
		
		private final FurnaceGolem golem;
		
		protected InertGoal(final FurnaceGolem entity) {
			super();
			this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE, Flag.TARGET));
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
			tick();
		}
		
		@Override
		public void tick() {
			// freeze the golem and ai tasks
			golem.setMotion(golem.getMotion().mul(0, 1.0D, 0));
			golem.setMoveForward(0F);
			golem.setMoveStrafing(0F);
			golem.moveController.setMoveTo(golem.posX, golem.posY, golem.posZ, 0.1D);
			golem.setJumping(false);
			golem.setAttackTarget(null);
			golem.setRevengeTarget(null);
			golem.getNavigator().clearPath();
			golem.rotationPitch = (float)Math.toRadians(60D);
			golem.rotationYaw = golem.prevRotationYaw;
			// remove this golem from being targeted
			// MOVED TO EVENT HANDLER
//			if(golem.ticksExisted % 10 == 0) {
//				final List<MobEntity> list = golem.getEntityWorld()
//						.getEntitiesWithinAABB(MobEntity.class, golem.getBoundingBox().grow(20D, 8D, 20D), 
//								e -> e.getAttackTarget() == golem || e.getRevengeTarget() == golem);
//				for(final MobEntity m : list) {
//					m.setAttackTarget(null);
//					m.setRevengeTarget(null);
//				}
//			}
		}
		
	}
}
