package com.golems.entity;

import java.util.ArrayList;
import java.util.List;

import com.golems.entity.ai.EntityAIDefendAgainstMonsters;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.WeightedItem;
import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends EntityCreature implements IAnimals {

	protected int attackTimer;
	protected boolean isPlayerCreated;
	protected ResourceLocation textureLoc;
	protected ItemStack creativeReturn;
	Village villageObj;
	protected boolean hasHome = false;
	/** deincrements, and a distance-to-home check is done at 0. **/
	private int homeCheckTimer = 70;

	// customizable variables with default values //
	protected double knockbackY = 0.4000000059604645D;
	/** Amount by which to multiply damage if it's a critical. **/
	protected float criticalModifier = 2.25F;
	/** Percent chance to multiply damage [0, 100]. **/
	protected int criticalChance = 5;
	protected boolean takesFallDamage = false;
	protected boolean canDrown = false;
	protected boolean isLeashable = true;

	// swimming AI
	protected EntityAIBase swimmingAI = new EntityAISwimming(this);

	/////////////// CONSTRUCTORS /////////////////

	/* Private to force child classes to use other constructors */
	private GolemBase(final World world) {
		super(world);
		this.setSize(1.4F, 2.9F);
		this.setCanTakeFallDamage(false);
		this.setCanSwim(false);
	}

	/**
	 * 
	 * @param world
	 * @param attack
	 * @param pickBlock
	 */
	public GolemBase(final World world, final float attack, final ItemStack pickBlock) {
		this(world);
		this.setCreativeReturn(pickBlock);
		this.setBaseAttackDamage(attack);
		this.experienceValue = 4 + rand.nextInt((int) attack + 2);
	}

	public GolemBase(final World world, final float attack, final Block pickBlock) {
		this(world, attack, new ItemStack(pickBlock, 1, 0));
	}

	public GolemBase(final World world, final float attack) {
		this(world, attack, GolemItems.golemHead);
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
		this.tasks.addTask(5, new EntityAIWander(this, this.getBaseMoveSpeed() * 2.25D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIDefendAgainstMonsters(this));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, (Class[]) new Class[0]));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class,
				10, false, true, new Predicate<EntityLiving>() {

					public boolean apply(EntityLiving e) {
						return e != null && IMob.VISIBLE_MOB_SELECTOR.apply(e)
								&& !(e instanceof EntityCreeper);
					}
				}));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.setTextureType(this.applyTexture());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
				.setBaseValue(7);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
		this.applyAttributes();
	}

	/**
	 * main AI tick function, replaces updateEntityActionState.
	 */
	@Override
	protected void updateAITasks() {
		if (--this.homeCheckTimer <= 0) {
			this.homeCheckTimer = 70 + this.rand.nextInt(50);
			this.villageObj = this.world.getVillageCollection()
					.getNearestVillage(new BlockPos(this), 32);

			if (this.villageObj == null) {
				this.detachHome();
			} else {
				BlockPos blockpos = this.villageObj.getCenter();
				this.setHomePosAndDistance(blockpos,
						(int) ((float) this.villageObj.getVillageRadius() * 0.8F));
			}
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
		if (entityIn instanceof IMob && !(entityIn instanceof EntityCreeper)
				&& this.getRNG().nextInt(20) == 0) {
			// also copied from vanilla... seems like it will break if a non-EntityLivingBase
			// implements IMob
			this.setAttackTarget((EntityLivingBase) entityIn);
		}

		super.collideWithEntity(entityIn);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.attackTimer > 0) {
			--this.attackTimer;
		}

		// spawn block particles when this golem moves
		if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7D
				&& this.rand.nextInt(5) == 0) {
			final int i = MathHelper.floor(this.posX);
			final int j = MathHelper.floor(this.posY - 0.20000000298023224D);
			final int k = MathHelper.floor(this.posZ);
			final IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

			if (iblockstate.getMaterial() != Material.AIR) {
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
						this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
						this.getEntityBoundingBox().minY + 0.1D,
						this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
						4.0D * ((double) this.rand.nextFloat() - 0.5D), 0.5D,
						((double) this.rand.nextFloat() - 0.5D) * 4.0D,
						new int[] { Block.getStateId(iblockstate) });
			}
		}
	}

	/**
	 * Returns true if this entity can attack entities of the specified class.
	 */
	@Override
	public boolean canAttackClass(final Class<? extends EntityLivingBase> cls) {
		return this.isPlayerCreated() && EntityPlayer.class.isAssignableFrom(cls) ? false
				: (cls == EntityCreeper.class ? false : super.canAttackClass(cls));
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		// (0.0 ~ 1.0] lower number results in less variance
		final float VARIANCE = 0.8F;
		// calculate damage based on current attack damage and variance
		final float currentAttack = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
				.getAttributeValue();
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
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(final byte b) {
		if (b == 4) {
			this.attackTimer = 10;
			this.playSound(this.getThrowSound(), 1.0F, 0.9F + rand.nextFloat() * 0.2F);
		} else {
			super.handleStatusUpdate(b);
		}
	}

	@SideOnly(Side.CLIENT)
	public int getAttackTimer() {
		return this.attackTimer;
	}

	/**
	 * Called when the mob is falling. Calculates and applies fall damage.
	 */
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

	/**
	 * Plays sound of golem walking.
	 */
	@Override
	protected void playStepSound(final BlockPos pos, final Block block) {
		this.playSound(this.getWalkingSound(), 0.76F, 0.9F + rand.nextFloat() * 0.2F);
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities.
	 */
	@Override
	protected boolean canDespawn() {
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
	@SuppressWarnings("deprecation")
	@Override
	public void onDeath(final DamageSource src) {
		if (!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
			this.villageObj.modifyPlayerReputation(this.attackingPlayer.getName(), -5);
		}

		super.onDeath(src);
	}

	///////////////// ITEM DROP LOGIC /////////////////////

	/**
	 * Drop items of this living's type.
	 * 
	 * @param recentlyHit
	 *            - Whether this entity has recently been hit by a player.
	 * @param lootingLevel
	 *            - Level of Looting used to kill this mob.
	 */
	@Override
	protected void dropFewItems(final boolean recentlyHit, final int lootingLevel) {
		// make and populate a list of WeightedItem instances
		final List<WeightedItem> drops = new ArrayList<>();
		this.addDrop(drops, rand.nextBoolean() ? Blocks.YELLOW_FLOWER : Blocks.RED_FLOWER, 0, 1, 2,
				65);
		this.addDrop(drops, Items.REDSTONE, 0, 1, 1, 20 + lootingLevel * 10);

		this.addGolemDrops(drops, recentlyHit, lootingLevel);

		// drop every item in the list if it passes a percent-chance check
		for (final WeightedItem w : drops) {
			if (w != null && w.shouldDrop(this.rand)) {
				final ItemStack drop = w.makeStack(this.rand);
				this.entityDropItem(drop, 0.0F);
			}
		}
	}

	/** Adds an ItemStack to the list of golem drops. **/
	protected boolean addDrop(final List<WeightedItem> dropList, final ItemStack stack, final int percentChance) {
		return dropList.add(new WeightedItem(stack, percentChance));
	}

	/** Adds an Item to the list of golem drops. **/
	protected boolean addDrop(final List<WeightedItem> dropList, final Item item, final int meta, final int min, final int max,
			final int percentChance) {
		return dropList.add(new WeightedItem(item, meta, min, max, percentChance));
	}

	/** Adds a Block to the list of golem drops. **/
	protected boolean addDrop(final List<WeightedItem> dropList, final Block block, final int meta, final int min, final int max,
			int percentChance) {
		return dropList
				.add(new WeightedItem(Item.getItemFromBlock(block), meta, min, max, percentChance));
	}

	/**
	 * Iterates through a list of drops and removes any entries that contain the given item and
	 * metadata. Useful for removing the default drops, which include redstone, yellow flowers, and
	 * red flowers. Pass {@code OreDictionary.WILDCARD_VALUE} to ignore metadata.
	 **/
	protected boolean removeFromList(final List<WeightedItem> list, final Item in, final int meta) {
		boolean flag = false;
		for (final WeightedItem w : list) {
			if (w != null && w.item == in
					&& (meta == OreDictionary.WILDCARD_VALUE || w.meta == meta)) {
				list.remove(w);
				flag = true;
			}
		}
		return flag;
	}

	/////////////// OTHER SETTERS AND GETTERS /////////////////

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

	private void setBaseAttackDamage(final float f) {
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(f);
	}

	// unused
	public float getBaseAttackDamage() {
		return (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
				.getBaseValue();
	}

	public double getBaseMoveSpeed() {
		return this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
	}

	public Village getVillage() {
		return this.villageObj;
	}

	public void setCanTakeFallDamage(final boolean toSet) {
		this.takesFallDamage = toSet;
	}

	public boolean canTakeFallDamage() {
		return this.takesFallDamage;
	}

	public void setCanSwim(final boolean canSwim) {
		((PathNavigateGround) this.getNavigator()).setCanSwim(canSwim);
		if (canSwim) {
			this.tasks.addTask(0, swimmingAI);
		} else {
			this.tasks.removeTask(swimmingAI);
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

	/** Not used in this project. Will be used in the WAILA addon **/
	public boolean doesInteractChangeTexture() {
		return false;
	}

	/////////////// TEXTURE HELPERS //////////////////

	/** Makes a texture on the assumption that MODID is 'golems'. **/
	public static ResourceLocation makeGolemTexture(final String texture) {
		return makeGolemTexture(ExtraGolems.MODID, texture);
	}

	/**
	 * Makes a ResourceLocation using the passed mod id and part of the texture name. Texture should
	 * be at 'assets/<b>MODID</b>/textures/entity/golem_<b>suffix</b>.png'
	 * 
	 * @see {@link #applyTexture()}
	 **/
	public static ResourceLocation makeGolemTexture(final String modid, final String texture) {
		return new ResourceLocation(modid + ":textures/entity/golem_" + texture + ".png");
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
	 * Called from applyEntityAttributes. Use this to adjust health, speed, knockback resistance,
	 * etc.
	 **/
	protected abstract void applyAttributes();

	/**
	 * Called from {@code entityInit} and used to set the texture type <b>before</b> the entity is
	 * fully constructed or rendered. Example implementation: texture is at
	 * 'assets/golems/textures/entity/golem_clay.png'
	 * 
	 * <pre>
	 * {@code
	 * protected ResourceLocation applyTexture()
	 *{
	 * 	return this.makeGolemTexture("golems", "clay"); 
	 *}
	 * </pre>
	 * 
	 * @return a ResourceLocation for this golem's texture
	 * 
	 * @see #makeGolemTexture(String, String)
	 **/
	protected abstract ResourceLocation applyTexture();

	/// **
	// * Called each time a golem dies. Passes a list of drops already containing some defaults.
	// * You can add entries using
	// * {@link #addDropEntry(dropList, item, meta, minAmount, maxAmount, percentChance)} or
	// * {@link #addDropEntry(dropList, ItemStack, percentChance)}
	// * @see WeightedItem
	// **/
	// TODO: Fix me!
	public abstract void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit,
			final int lootingLevel);

	/**
	 * @return A SoundEvent to play when the golem is attacking, walking, hurt, and on death
	 **/
	public abstract SoundEvent getGolemSound();
}