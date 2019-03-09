package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.entity.ai.EntityAIDefendAgainstMonsters;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends EntityCreature implements IAnimal {

	private static final DataParameter<Boolean> BABY = EntityDataManager.<Boolean>createKey(GolemBase.class, DataSerializers.BOOLEAN);
	private static final String KEY_BABY = "isChild";
	public static final int WANDER_DISTANCE = 64;
	protected int attackTimer;
	protected boolean isPlayerCreated;
	protected ResourceLocation textureLoc;
	protected ResourceLocation lootTableLoc;
	protected ItemStack creativeReturn;
	Village villageObj;
	protected boolean hasHome = false;
	/**
	 * deincrements, and a distance-to-home check is done at 0.
	 **/
	private int homeCheckTimer = 180;
	// customizable variables with default values //
	protected double knockbackY = 0.4000000059604645D;
	/** Amount by which to multiply damage if it's a critical. **/
	protected float criticalModifier = 2.25F;
	/** Percent chance to multiply damage [0, 100]. **/
	protected int criticalChance = 5;
	protected boolean takesFallDamage = false;
	protected boolean canDrown = false;
	protected boolean isLeashable = true;

	protected final GolemContainer container;
	
	// swimming AI
	protected EntityAIBase swimmingAI = new EntityAISwimming(this);
	protected EntityAIBase wanderAvoidWater = null;
	protected EntityAIBase wander = null;

	/////////////// CONSTRUCTORS /////////////////

	/**
	 * Initializes this golem with the given World. 
	 * Also sets the following:
	 * <br>{@code setBaseAttackDamage} using the config
	 * <br>{@code takesFallDamage} to false
	 * <br>{@code canSwim} to false.
	 * <br>{@code creativeReturn} to the map result of {@code GolemLookup} with this golem.
	 * Defaults to the Golem Head if no block is found. Call {@link #setCreativeReturn(ItemStack)}
	 * if you want to return something different.
	 * @param world the entity world
	 **/

	public GolemBase(Class<? extends GolemBase> clazz, final World world) {
		super(GolemBase.getGolemType(clazz), world);
		container = GolemRegistrar.getContainer(clazz);
		this.setSize(1.4F, 2.9F);
		this.setCanTakeFallDamage(false);
		this.setCanSwim(false);
		Block pickBlock = container.validBuildingBlocks.get(0); //get the first valid building block
		this.setCreativeReturn(pickBlock);
		this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(container.attack);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(container.health);
		this.experienceValue = 4 + rand.nextInt(8);
	}


	////////////// BEHAVIOR OVERRIDES //////////////////

	@Override
	protected void initEntityAI() {
		// all of these tasks are copied from the Iron Golem and adjusted for movement speed
		this.tasks.addTask(1, new EntityAIAttackMelee(this, this.getBaseMoveSpeed() * 4.0D, true));
		this.tasks.addTask(2,
			new EntityAIMoveTowardsTarget(this, this.getBaseMoveSpeed() * 3.75D, 32.0F));
		this.tasks.addTask(3,
			new EntityAIMoveThroughVillage(this, this.getBaseMoveSpeed() * 2.25D, true));
		this.tasks.addTask(4,
			new EntityAIMoveTowardsRestriction(this, this.getBaseMoveSpeed() * 4.0D));
		//// Wander AI has been moved to setCanSwim(boolean)
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIDefendAgainstMonsters(this));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, (Class[]) new Class[0]));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this,
			EntityLiving.class, 10, false, true,
			e -> e != null && IMob.VISIBLE_MOB_SELECTOR.test(e) && !(e instanceof EntityCreeper)));
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.setTextureType(this.applyTexture());
		this.getDataManager().register(BABY, false);
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			.setBaseValue(container.attack);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(container.health);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
	}

	/**
	 * main AI tick function, replaces updateEntityActionState.
	 */
	@Override
	protected void updateAITasks() {
		if(this.homeCheckTimer > 0) {
			--homeCheckTimer;
		} else {
			// check for home village
			this.updateHomeVillage();
	        this.homeCheckTimer = 180;
		}

		super.updateAITasks();
	}

	/**
	 * Decrements the entity's air supply when underwater.
	 */
	@Override
	protected int decreaseAirSupply(final int i) {
		return this.canDrown ? super.decreaseAirSupply(i) : i;
	}

	@Override
	public boolean canBeLeashedTo(final EntityPlayer player) {
		return this.isLeashable && super.canBeLeashedTo(player);
	}

	@Override
	protected void collideWithEntity(final Entity entityIn) {
		if (entityIn instanceof IMob && entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityCreeper)
			&& this.getRNG().nextInt(20) == 0) {
			this.setAttackTarget((EntityLivingBase) entityIn);
		}

		super.collideWithEntity(entityIn);
	}


	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		if (this.attackTimer > 0) {
			--this.attackTimer;
		}
		//Ripped straight from EntityIronGolem
		if (this.motionX * this.motionX + this.motionZ * this.motionZ > (double) 2.5000003E-7F
			&& this.rand.nextInt(5) == 0) {
			int i = MathHelper.floor(this.posX);
			int j = MathHelper.floor(this.posY - (double) 0.2F);
			int k = MathHelper.floor(this.posZ);
			IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));
			if (!iblockstate.isAir()) {
				this.world.spawnParticle(new BlockParticleData(Particles.BLOCK, iblockstate),
					this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
					this.getBoundingBox().minY + 0.1D, this.posZ +
						((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
					4.0D * ((double) this.rand.nextFloat() - 0.5D), 0.5D,
					((double) this.rand.nextFloat() - 0.5D) * 4.0D);
			}
		}
	}

	/**
	 * Returns true if this entity can attack entities of the specified class.
	 */
	@Override
	public boolean canAttackClass(final Class<? extends EntityLivingBase> cls) {
		return (!this.isPlayerCreated() || !EntityPlayer.class.isAssignableFrom(cls)) && (cls != EntityCreeper.class && super.canAttackClass(cls));
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		// (0.0 ~ 1.0] lower number results in less variance
		final float VARIANCE = 0.8F;
		// calculate damage based on current attack damage and variance
		final float currentAttack = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			.getValue();
		float damage = currentAttack
			+ (float) (rand.nextDouble() - 0.5D) * VARIANCE * currentAttack;

		// try to increase damage if random critical chance succeeds
		if (rand.nextInt(100) < this.criticalChance) {
			damage *= this.criticalModifier;
		}

		this.attackTimer = 10;
		this.world.setEntityState(this, (byte) 4);
		final boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

		if (flag) {
			entity.motionY += knockbackY;
			this.applyEnchantments(this, entity);
		}

		this.playSound(this.getThrowSound(), 1.0F, 0.9F + rand.nextFloat() * 0.2F);
		return flag;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(final byte b) {
		if (b == 4) {
			this.attackTimer = 10;
			this.playSound(this.getThrowSound(), 1.0F, 0.9F + rand.nextFloat() * 0.2F);
		} else {
			super.handleStatusUpdate(b);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public int getAttackTimer() {
		return this.attackTimer;
	}

	/** Called when the mob is falling. Calculates and applies fall damage **/
	@Override
	public void fall(final float distance, final float damageMultiplier) {
		if (this.canTakeFallDamage()) {
			super.fall(distance, damageMultiplier);
		}
	}

	@Override
	public int getMaxFallHeight() {
		return this.canTakeFallDamage() ? super.getMaxFallHeight() : 64;
	}

	/** Plays sound of golem walking **/
	@Override
	protected void playStepSound(BlockPos pos, IBlockState blockIn) {
		this.playSound(this.getWalkingSound(), 0.76F, 0.9F + rand.nextFloat() * 0.2F);
	}

	/** Determines if an entity can be despawned, used on idle far away entities. **/
	@Override
	public boolean canDespawn() {
		return false;
	}

	/**
	 * Get number of ticks, at least during which the living entity will be silent.
	 */
	@Override
	public int getTalkInterval() {
		return 24000;
	}

	/**
	 * Called when a user uses the creative pick block button on this entity.
	 *
	 * @param target
	 *            The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
	 */
	@Override
	public ItemStack getPickedResult(final RayTraceResult target) {
		return this.creativeReturn;
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	@Override
	public void onDeath(final DamageSource src) {
		if (!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
			this.villageObj.modifyPlayerReputation(this.attackingPlayer.getUniqueID(), -5);
		}

		super.onDeath(src);
	}

	@Override
	public void writeAdditional(NBTTagCompound compound) {
		super.writeAdditional(compound);
        compound.setBoolean(KEY_BABY, this.isChild());
    }
	
	@Override
	public void readAdditional(NBTTagCompound compound) {
		super.readAdditional(compound);
		this.setChild(compound.getBoolean(KEY_BABY));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return this.lootTableLoc;
    }
	
	/** 
	 * Updates this golem's home position IF there is a nearby village.
	 * @return if the golem found a village home
	 **/
	public boolean updateHomeVillage() {
		// set home position based on nearest village ONLY if one is close enough
		this.villageObj = this.world.getVillageCollection().getNearestVillage(new BlockPos(this), WANDER_DISTANCE * 2);
        if (this.villageObj != null) {
        	final BlockPos home = this.villageObj.getCenter();
            final int wanderDistance = (int)((float)this.villageObj.getVillageRadius() * 0.8F);
            this.setHomePosAndDistance(home, wanderDistance);
            return true;
        }
        else return false;
	}

	/////////////// OTHER SETTERS AND GETTERS /////////////////
	
	/** 
	 * Called after golem has been spawned. Parameters are the exact IBlockStates used to
	 * make this golem (especially used with multi-textured golems)
	 **/
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) { }

	public void setLootTableLoc(final ResourceLocation lootTable) {
		this.lootTableLoc = lootTable;
	}

	public void setLootTableLoc(String modid, final String name) {
		this.lootTableLoc = new ResourceLocation(modid, "entities/" + name);
	}
	
	public void setLootTableLoc(final String name) {
		this.setLootTableLoc(ExtraGolems.MODID, name);
	}
	
	public void setTextureType(final ResourceLocation texturelocation) {
		this.textureLoc = texturelocation;
	}

	public ResourceLocation getTextureType() {
		return this.textureLoc;
	}

	public void setCreativeReturn(final Block blockToReturn) {
		this.setCreativeReturn(new ItemStack(blockToReturn, 1));
	}

	public void setCreativeReturn(final ItemStack blockToReturn) {
		this.creativeReturn = blockToReturn;
	}

	public ItemStack getCreativeReturn() {
		return this.creativeReturn;
	}

	public float getBaseAttackDamage() {
		return (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
	}

	public double getBaseMoveSpeed() {
		return this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
	}

	public Village getVillage() {
		return this.villageObj;
	}
	
	public void setChild(boolean isChild) {
		this.getDataManager().set(BABY, isChild);
	}
	
	@Override
	public boolean isChild() {
		return this.getDataManager().get(BABY).booleanValue();
	}

	public void setCanTakeFallDamage(final boolean toSet) {
		this.takesFallDamage = toSet;
	}

	public boolean canTakeFallDamage() {
		return this.takesFallDamage;
	}

	public void setCanSwim(final boolean canSwim) {
		this.getNavigator().setCanSwim(canSwim);
		if(null == wander) {
			wander = new EntityAIWander(this, this.getBaseMoveSpeed() * 2.25D);
		}
		if(null == wanderAvoidWater) {
			wanderAvoidWater = new EntityAIWanderAvoidWater(this, this.getBaseMoveSpeed() * 2.25D);
		}
		
		if (canSwim) {
			this.tasks.addTask(0, swimmingAI);
			this.tasks.addTask(5, wander);
			this.tasks.removeTask(wanderAvoidWater);
		} else {
			this.tasks.removeTask(swimmingAI);
			this.tasks.removeTask(wander);
			this.tasks.addTask(5, wanderAvoidWater);
		}
	}

	public void setPlayerCreated(final boolean bool) {
		this.isPlayerCreated = bool;
	}

	public boolean isPlayerCreated() {
		return this.isPlayerCreated;
	}

	public void setImmuneToFire(final boolean toSet) {
		this.isImmuneToFire = toSet;
	}

	/** Whether right-clicking on this entity triggers a texture change **/
	public boolean doesInteractChangeTexture() {
		return false;
	}

	public GolemContainer getGolemContainer() {
		return container;
	}
	
	/** The EntityType associated with this golem, or null if there is none **/
	@Nullable
	public static EntityType<?> getGolemType(Class<? extends GolemBase> golem) {
		return GolemRegistrar.getContainer(golem).entityType;
	}

	/** 
	 * Helper method for translating text into local language using {@code I18n}
	 **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}

	/////////////// TEXTURE HELPERS //////////////////

	/** Makes a texture on the assumption that MODID is 'golems'. **/
	@Deprecated
	public static ResourceLocation makeGolemTexture(final String texture) {
		return makeGolemTexture(ExtraGolems.MODID, texture);
	}

	/**
	 * Makes a ResourceLocation using the passed mod id and part of the texture name. Texture should
	 * be at 'assets/<b>MODID</b>/textures/entity/golem_<b>suffix</b>.png'
	 *
	 * @see {@link #applyTexture()}
	 **/
	@Deprecated
	public static ResourceLocation makeGolemTexture(final String modid, final String texture) {
		return makeTexture(modid, "golem_" + texture);
	}
	
	/**
	 * Makes a ResourceLocation using the passed mod id and part of the texture name. Texture should
	 * be at 'assets/[MODID]/textures/entity/[TEXTURE].png'
	 **/
	public static ResourceLocation makeTexture(final String MODID, final String TEXTURE) {
		return new ResourceLocation(MODID + ":textures/entity/" + TEXTURE + ".png");
	}

	///////////////////// SOUND OVERRIDES ////////////////////

	@Override
	protected SoundEvent getAmbientSound() {
		return getGolemSound();
	}

	protected SoundEvent getWalkingSound() {
		return getGolemSound();
	}

	/** Returns the sound this mob makes when it attacks. **/
	public SoundEvent getThrowSound() {
		return getGolemSound();
	}

	@Override
	protected SoundEvent getHurtSound(final DamageSource ignored) {
		return getGolemSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getGolemSound();
	}

	////////////////////////////////////////////////////////////
	// Override ALL OF THE FOLLOWING FUNCTIONS FOR EACH GOLEM //
	////////////////////////////////////////////////////////////

	/**
	 * Allows each golem to add special information to in-game info (eg, Waila, Hwyla, TOP, etc.).
	 * Typically checks if the Config allows this golem's special ability (if it has one) and adds a
	 * formatted String to the passed list.
	 *
	 * @param list The list to which the golem adds description strings (separate entries are separate lines)
	 * @return the passed list with or without this golem's added description
	 **/
	public List<String> addSpecialDesc(final List<String> list) { return list; }
	
	/**
	 * Called from {@link #registerData()} and used to set the texture type <b>before</b> the entity is
	 * fully constructed or rendered. Example implementation: texture is at
	 * 'assets/golems/textures/entity/golem_clay.png'
	 *
	 * <pre>
	 * {@code
	 * protected ResourceLocation applyTexture() {
	 * 	return this.makeGolemTexture("golems", "clay");
	 *}
	 * </pre>
	 *
	 * @return a ResourceLocation for this golem's texture
	 * @see #makeGolemTexture(String, String)
	 **/
	protected abstract ResourceLocation applyTexture();

	/** @return A SoundEvent to play when the golem is attacking, walking, hurt, and on death **/
	public abstract SoundEvent getGolemSound();
}
