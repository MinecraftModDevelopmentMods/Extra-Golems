package com.mcmoddev.golems.entity.base;

import javax.annotation.Nullable;

import com.mcmoddev.golems.blocks.BlockUtility;
import com.mcmoddev.golems.entity.ai.EntityAIDefendAgainstMonsters;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.VillagePieces.Village;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends CreatureEntity {

	protected static final DataParameter<Boolean> BABY = EntityDataManager.<Boolean>createKey(
			GolemBase.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Boolean> PLAYER_CREATED = EntityDataManager.<Boolean>createKey(
			GolemBase.class, DataSerializers.BOOLEAN);
	private static final String KEY_BABY = "isChild";
	private static final String KEY_PLAYER_CREATED = "isPlayerCreated";
	public static final int WANDER_DISTANCE = 64;
	protected int attackTimer;
	protected ResourceLocation textureLoc;
	protected ItemStack creativeReturn;
	protected Village villageObj;
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
	protected Goal swimmingAI = new SwimGoal(this);

	/////////////// CONSTRUCTORS /////////////////

	/*
	 * Initializes this golem with the given World. 
	 * Also sets the following:
	 * <br>{@code setBaseAttackDamage} using the config
	 * <br>{@code takesFallDamage} to false
	 * <br>{@code canSwim} to false.
	 * <br>{@code creativeReturn} to the map result of {@code GolemLookup} with this golem.
	 * Defaults to the Golem Head if no block is found. Call {@link #setCreativeReturn(ItemStack)}
	 * if you want to return something different.
	 * @param world the entity world
	 */
	
//	public GolemBase(final String name, final World world) {
//		this(ExtraGolems.MODID, name, world);
//	}
//	
//	public GolemBase(final String modid, final String name, final World world) {
//		this(GolemRegistrar.getContainer(new ResourceLocation(modid, name)).entityType, world);
//	}
	
	public GolemBase(final EntityType<? extends GolemBase> type, final World world) {
		super(type, world);
		this.container = GolemRegistrar.getContainer(type.getRegistryName());
		Block pickBlock = container.getPrimaryBuildingBlock();
		this.setCreativeReturn(pickBlock != null ? pickBlock : GolemItems.GOLEM_HEAD);	
		this.setCanTakeFallDamage(false);
		this.setCanSwim(false);
		this.experienceValue = 4 + rand.nextInt(8);
	}


	////////////// BEHAVIOR OVERRIDES //////////////////

	@Override
	protected void registerGoals() {
		
		
		this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(1,
				new NearestAttackableTargetGoal<>(this, MobEntity.class, 10, true, false, (e) -> {
					return e instanceof IMob;
				}));
		
		

		// all of these tasks are copied from the Iron Golem and adjusted for movement speed
		//this.field_70714_bg.addTask(1, new AttackGoal(this, this.getBaseMoveSpeed() * 4.0D, true));
		this.targetSelector.addGoal(2,
			new MoveTowardsTargetGoal(this, this.getBaseMoveSpeed() * 3.75D, 32.0F));
		this.targetSelector.addGoal(3,
			new MoveThroughVillageGoal(this, this.getBaseMoveSpeed() * 2.25D, true, 2, () -> Boolean.valueOf(true)));
		this.targetSelector.addGoal(4,
			new MoveTowardsRestrictionGoal(this, this.getBaseMoveSpeed() * 4.0D));
		
		
		//// Wander AI has been moved to setCanSwim(boolean)
		this.goalSelector.addGoal(1, new EntityAIDefendAgainstMonsters(this));
//		this.field_70715_bh.addTask(2, new EntityAIHurtByTarget(this, false, (Class[]) new Class[0]));
//		this.field_70715_bh.addTask(3, new EntityAINearestAttackableTarget<>(this,
//			EntityLiving.class, 10, false, true,
//			e -> e != null && IMob.VISIBLE_MOB_SELECTOR.test(e) && !(e instanceof EntityCreeper)));
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.setTextureType(this.applyTexture());
		this.getDataManager().register(BABY, false);
		this.getDataManager().register(PLAYER_CREATED, false);
	}
	//NOTE: This is called before the constructor gets to adding the container
	@Override
	protected void registerAttributes() {
		GolemContainer golemContainer = GolemRegistrar.getContainer(this.getType());
		super.registerAttributes();
		this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			.setBaseValue(golemContainer.getAttack());
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(golemContainer.getHealth());
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(golemContainer.getSpeed());
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
	public boolean canBeLeashedTo(final PlayerEntity player) {
		return this.isLeashable && super.canBeLeashedTo(player);
	}

	@Override
	protected void collideWithEntity(final Entity entityIn) {
		if (entityIn instanceof IMob && entityIn instanceof LivingEntity && !(entityIn instanceof CreeperEntity)
			&& this.getRNG().nextInt(20) == 0) {
			this.setAttackTarget((LivingEntity) entityIn);
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
			int j = MathHelper.floor(this.posY - 0.200D);
			int k = MathHelper.floor(this.posZ);
			BlockPos pos = new BlockPos(i, j, k);
			BlockState BlockState = this.world.getBlockState(pos);
			if (BlockState.getMaterial() != Material.AIR && !BlockState.getMaterial().isLiquid()
					&& !(BlockState.getBlock() instanceof BlockUtility)) {
				this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState),
					this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.getWidth(),
					this.getBoundingBox().minY + 0.1D, this.posZ +
						((double) this.rand.nextFloat() - 0.5D) * (double) this.getWidth(),
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
		final boolean isAttackablePlayer = PlayerEntity.class.isAssignableFrom(cls) 
				&& (!this.isPlayerCreated() || ExtraGolemsConfig.enableFriendlyFire());
		final boolean isCreeper = cls == CreeperEntity.class;
		return !isCreeper && (isAttackablePlayer || super.canAttackClass(cls));
	}
	
	@Override
	protected void damageEntity(final DamageSource source, final float amount) {
		if (!this.isInvulnerableTo(source)) {
			float adjusted = amount;
			if (this.isPotionActive(Effects.LUCK)) {
				adjusted *= 0.89F;
			} else if (this.isPotionActive(Effects.UNLUCK)) {
				adjusted *= 1.25F;
			}
			super.damageEntity(source, adjusted);
		}
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		// (0.0 ~ 1.0] lower number results in less variance
		final float VARIANCE = 0.8F;
		// (0.0 ~ 1.0] based on luck / unluck and critical chance
		float multiplier = 1.0F;
		// try to increase damage if random critical chance succeeds
		if (this.isPotionActive(Effects.LUCK)) {
			multiplier += 0.5F * this.criticalModifier;
		} else if (this.isPotionActive(Effects.UNLUCK)) {
			multiplier -= 0.65F;
		} else if (rand.nextInt(100) < this.criticalChance) {
			multiplier = this.criticalModifier;
		}
		
		final float currentAttack = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			.getValue();
		// calculate damage based on current attack damage, variance, and luck/unluck/critical
		float damage = multiplier * (currentAttack
			+ (float) rand.nextInt((int)currentAttack + 1) * VARIANCE);
		
		this.attackTimer = 10;
		this.world.setEntityState(this, (byte) 4);
		final boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

		if (flag) {
			entity.addVelocity(0, knockbackY, 0);
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
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(this.getWalkingSound(), 0.76F, 0.9F + rand.nextFloat() * 0.2F);
	}

	/** Determines if an entity can be despawned, used on idle far away entities. **/
//	@Override
//	public boolean canDespawn() {
//		return false;
//	}

	/**
	 * Get number of ticks, at least during which the living entity will be silent.
	 */
	@Override
	public int getTalkInterval() {
		return 24000;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return !this.canDrown;
	}

	/**
	 * Called when a user uses the creative pick block button on this entity.
	 *
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should
	 *         be added.
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
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
        compound.putBoolean(KEY_BABY, this.isChild());
        compound.putBoolean(KEY_PLAYER_CREATED, this.isPlayerCreated());
    }
	
	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setChild(compound.getBoolean(KEY_BABY));
		this.setPlayerCreated(compound.getBoolean(KEY_PLAYER_CREATED));
	}

	@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return this.container.getLootTable();
    }
	
	/** 
	 * Updates this golem's home position IF there is a nearby village.
	 * @return if the golem found a village home
	 * @see #updateHomeVillageInRange(BlockPos, int)
	 **/
	public boolean updateHomeVillage() {
		final int radius = (WANDER_DISTANCE * 3) / 2;
		return updateHomeVillageInRange(new BlockPos(this), radius);
	}
	
	/** 
	 * Updates this golem's home position IF there is a nearby village
	 * in the specified radius
	 * @param POS the center of the radius to check for a village
	 * @param RADIUS the size of the area to check for a village
	 * @return if the golem found a village home
	 * @see #updateHomeVillage()
	 **/
	protected boolean updateHomeVillageInRange(final BlockPos POS, final int RADIUS) {
		// set home position based on nearest village ONLY if one is close enough
		this.villageObj = this.world.getVillageCollection().getNearestVillage(POS, RADIUS);
        if (this.villageObj != null) {
        	final BlockPos home = this.villageObj.getCenter();
            final int wanderDistance = (int)((float)this.villageObj.getVillageRadius() * 0.8F);
            this.setHomePosAndDistance(home, wanderDistance);
            return true;
        }
        return false;
	}

	/////////////// OTHER SETTERS AND GETTERS /////////////////
	
	/** 
	 * Called after golem has been spawned. Parameters are the exact IBlockStates used to
	 * make this golem (especially used with multi-textured golems)
	 **/
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) { }

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
//		if(null == wander) {
//			wander = new EntityAIWander(this, this.getBaseMoveSpeed() * 2.25D);
//		}
//		if(null == wanderAvoidWater) {
//			wanderAvoidWater = new EntityAIWanderAvoidWater(this, this.getBaseMoveSpeed() * 2.25D);
//		}
		
		if (canSwim) {
			this.goalSelector.addGoal(0, swimmingAI);
		} else {
			this.goalSelector.removeGoal(swimmingAI);
		}
	}

	public void setPlayerCreated(final boolean bool) {
		this.getDataManager().set(PLAYER_CREATED, bool);
	}

	public boolean isPlayerCreated() {
		return this.getDataManager().get(PLAYER_CREATED).booleanValue();
	}

	/** 
	 * Whether right-clicking on this entity triggers a texture change.
	 * @return True if this is a {@link GolemMultiTextured} or a 
	 * {@link GolemMultiColorized} AND the config option is enabled.
	 **/
	public boolean canInteractChangeTexture() {
		return ExtraGolemsConfig.enableTextureInteract()
				&& (GolemMultiTextured.class.isAssignableFrom(this.getClass()) 
					|| GolemMultiColorized.class.isAssignableFrom(this.getClass()));
	}
	
	/**
	 * Whether this golem provides light (by placing light source blocks).
	 * Does not change any behavior, but is used in the Light Block code
	 * to determine if it can stay (called AFTER light is placed).
	 * @see com.mcmoddev.golems.blocks.BlockUtilityGlow
	 **/
	public boolean isProvidingLight() {
		return false;
	}
	
	/**
	 * Whether this golem provides power (by placing power source blocks).
	 * Does not change any behavior, but is used in the Power Block code
	 * to determine if it can stay.
	 * @see com.mcmoddev.golems.blocks.BlockUtilityPower
	 **/
	public boolean isProvidingPower() {
		return false;
	}

	public GolemContainer getGolemContainer() {
		return container != null ? container : GolemRegistrar.getContainer(this.getClass());
	}
	
	public ForgeConfigSpec.ConfigValue getConfigValue(String name) {
		return (ExtraGolemsConfig.GOLEM_CONFIG.specials.get(this.getGolemContainer().specialContainers.get(name))).value;
	}
	
	public boolean getConfigBool(final String name) {
		return ((Boolean)getConfigValue(name).get()).booleanValue();
	}
	
	public int getConfigInt(final String name) {
		return ((Integer)getConfigValue(name).get()).intValue();
	}
	
	public double getConfigDouble(final String name) {
		return ((Double)getConfigValue(name).get()).doubleValue();
	}

	/////////////// TEXTURE HELPERS //////////////////
	
	public void setTextureType(final ResourceLocation texturelocation) {
		this.textureLoc = texturelocation;
	}

	public ResourceLocation getTextureType() {
		return this.textureLoc;
	}

	/**
	 * Calls {@link #makeTexture(String, String)} on the assumption that MODID is 'golems'.
	 * Texture should be at 'assets/golems/textures/entity/[TEXTURE].png'
	 **/
	public static ResourceLocation makeTexture(final String TEXTURE) {
		return makeTexture(ExtraGolems.MODID, TEXTURE);
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
