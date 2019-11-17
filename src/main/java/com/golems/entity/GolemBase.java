package com.golems.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.golems.items.ItemBedrockGolem;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemLookup;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends EntityIronGolem {

	private static final DataParameter<Boolean> BABY = EntityDataManager.<Boolean>createKey(GolemBase.class, DataSerializers.BOOLEAN);
	private static final String KEY_BABY = "isChild";
	public static final int WANDER_DISTANCE = 64;
	
	protected ResourceLocation textureLoc;
	protected ResourceLocation lootTableLoc;

	// customizable variables with default values //
	protected double knockbackY = 0.4000000059604645D;
	/** Amount by which to multiply damage if it's a critical. **/
	protected float criticalModifier = 2.25F;
	/** Percent chance to multiply damage [0, 100]. **/
	protected int criticalChance = 5;
	protected boolean takesFallDamage = false;
	protected boolean canDrown = false;
	
	protected EntityAIBase swimmingAi = new EntityAISwimming(this);

	/////////////// CONSTRUCTORS /////////////////

	/**
	 * Initializes this golem with the given World. 
	 * Also sets the following:
	 * <br>{@code SharedMonsterAttributes.ATTACK_DAMAGE} using the config
	 * <br>{@code SharedMonsterAttributes.MAX_HEALTH} using the config
	 * <br>{@code takesFallDamage} to false
	 * <br>{@code canSwim} to false.
	 * <br>{@code creativeReturn} to the map result of {@code GolemLookup} with this golem.
	 * Defaults to the Golem Head if no block is found. Call {@link #setCreativeReturn(ItemStack)}
	 * if you want to return something different.
	 * @param world the entity world
	 **/
	public GolemBase(final World world) {
		super(world);
		this.setSize(1.4F, 2.9F);
		this.setCanTakeFallDamage(false);
		this.setCanSwim(false);
		GolemConfigSet cfg = getConfig(this);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(cfg.getBaseAttack());
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(cfg.getMaxHealth());
		this.experienceValue = 4 + rand.nextInt((int)8);
	}

	////////////// BEHAVIOR OVERRIDES //////////////////

	@Override
	protected void entityInit() {
		super.entityInit();
		this.setTextureType(this.applyTexture());
		this.getDataManager().register(BABY, Boolean.valueOf(false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		GolemConfigSet cfg = getConfig(this);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			.setBaseValue(cfg.getBaseAttack());
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(cfg.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.4D);
	}

	/**
	 * Returns true if this entity can attack entities of the specified class.
	 */
	@Override
	public boolean canAttackClass(final Class<? extends EntityLivingBase> cls) {
		
		if(this.isPlayerCreated() && EntityPlayer.class.isAssignableFrom(cls)) {
			return Config.enableFriendlyFire();
		}
		if(cls == EntityVillager.class || GolemBase.class.isAssignableFrom(cls)) {
			return false;
		}
		return super.canAttackClass(cls);
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		// calculate damage based on current attack damage and variance
		final float currentAttack = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			.getAttributeValue();
		float damage = currentAttack + (float) (rand.nextDouble() - 0.5D) * 0.75F * currentAttack;
		// try to increase damage if random critical chance succeeds
		if (rand.nextInt(100) < this.criticalChance) {
			damage *= this.criticalModifier;
		}
		// use reflection to reset 'attackTimer' field
		ReflectionHelper.setPrivateValue(EntityIronGolem.class, this, 10, "field_70855_f", "attackTimer");
		this.worldObj.setEntityState(this, (byte) 4);
		final boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

		if (flag) {
			entity.motionY += knockbackY;
			this.applyEnchantments(this, entity);
		}

		this.playSound(this.getThrowSound(), 1.0F, 0.9F + rand.nextFloat() * 0.2F);
		return flag;
	}

	/** Called when the mob is falling. Calculates and applies fall damage **/
	@Override
	public void fall(float distance, float damageMultiplier) {
		if (!this.canTakeFallDamage()) {
			return;
		}
		float[] ret = net.minecraftforge.common.ForgeHooks.onLivingFall(this, distance, damageMultiplier);
		if (ret == null) {
			return;
		}
		distance = ret[0];
		damageMultiplier = ret[1];
		super.fall(distance, damageMultiplier);
		PotionEffect potioneffect = this.getActivePotionEffect(MobEffects.JUMP_BOOST);
		float f = potioneffect == null ? 0.0F : (float) (potioneffect.getAmplifier() + 1);
		int i = MathHelper.ceiling_float_int((distance - 3.0F - f) * damageMultiplier);

		if (i > 0) {
			this.playSound(this.getFallSound(i), 1.0F, 1.0F);
			this.attackEntityFrom(DamageSource.fall, (float) i);
			final BlockPos below = getBlockBelow();
			IBlockState iblockstate = this.worldObj.getBlockState(below);

			if (iblockstate.getMaterial() != Material.AIR) {
				SoundType soundtype = iblockstate.getBlock().getSoundType(iblockstate, worldObj, below, this);
				this.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
			}
		}
	}

	@Override
	public int getMaxFallHeight() {
		return this.canTakeFallDamage() ? super.getMaxFallHeight() : 64;
	}

	/** Plays sound of golem walking **/
	@Override
	protected void playStepSound(final BlockPos pos, final Block block) {
		this.playSound(this.getWalkingSound(), 0.76F, 0.9F + rand.nextFloat() * 0.2F);
	}

	/**
	 * Called when a user uses the creative pick block button on this entity.
	 *
	 * @param target
	 *            The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
	 */
	@Override
	@Nullable
	public ItemStack getPickedResult(final RayTraceResult target) {
		Block pickBlock = GolemLookup.getFirstBuildingBlock(this.getClass());
		return pickBlock != null ? new ItemStack(pickBlock) : null;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean(KEY_BABY, this.isChild());
    }
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setChild(compound.getBoolean(KEY_BABY));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return this.lootTableLoc;
    }
	
	@Override
    public EnumActionResult applyPlayerInteraction(final EntityPlayer player, final Vec3d vec, 
    		@Nullable final ItemStack stack, final EnumHand hand) {
		if(Config.enableHealGolems() && this.getHealth() < this.getMaxHealth() && stack != null && isHealingItem(stack)) {
			heal(getHealAmount(stack));
			stack.stackSize--;
			if(stack.stackSize <= 0) {
				player.setHeldItem(hand, null);
			}
			// if currently attacking this player, stop
			if(this.getAttackTarget() == player) {
				this.setRevengeTarget(null);
				this.setAttackTarget(null);
			}
			// spawn particles and play sound
			if(this.worldObj.isRemote) {
				ItemBedrockGolem.spawnParticles(this.worldObj, this.posX, this.posY + this.height / 2.0D,
						this.posZ, 0.12D, EnumParticleTypes.VILLAGER_HAPPY, 20);
			}
			this.playSound(SoundEvents.BLOCK_STONE_PLACE, 0.85F, 1.1F + rand.nextFloat() * 0.2F);
			return EnumActionResult.SUCCESS;
		}
		return super.applyPlayerInteraction(player, vec, stack, hand);
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
	
	public float getBaseAttackDamage() {
		return (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
	}

	public double getBaseMoveSpeed() {
		return this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
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
		((PathNavigateGround) this.getNavigator()).setCanSwim(canSwim);
		if (canSwim) {
			this.tasks.addTask(0, swimmingAi);
		} else {
			this.tasks.removeTask(swimmingAi);
		}
	}

	public void setImmuneToFire(final boolean toSet) {
		this.isImmuneToFire = toSet;
	}

	/**
	 * Whether right-clicking on this entity triggers a texture change.
	 *
	 * @return True if this is a {@link GolemMultiTextured} or a
	 * {@link GolemMultiColorized} AND the config option is enabled.
	 **/
	public boolean doesInteractChangeTexture() {
		return Config.interactChangesTexture()
			&& (GolemMultiTextured.class.isAssignableFrom(this.getClass())
			|| GolemColorizedMultiTextured.class.isAssignableFrom(this.getClass()));
	}
	
	/**
	 * Does not change behavior, but is required when the
	 * utility block checks for valid golems
	 **/
	public boolean isProvidingLight() {
		return false;
	}
	
	/**
	 * Does not change behavior, but is required when the
	 * utility block checks for valid golems
	 **/
	public boolean isProvidingPower() {
		return false;
	}

	/** @return The Blocks used to build this golem, or null if there is none **/
	@Nullable
	public static Block[] getBuildingBlocks(GolemBase golem) {
		return GolemLookup.getBuildingBlocks(golem.getClass());
	}

	/** The GolemConfigSet associated with this golem, or the empty GCS if there is none **/
	@Nonnull
	public static GolemConfigSet getConfig(GolemBase golem) {
		return golem != null && GolemLookup.hasConfig(golem.getClass()) ? GolemLookup.getConfig(golem.getClass()) : GolemConfigSet.EMPTY;
	}
	
	/**
	 * @param i the ItemStack being applied to the golem
	 * @return true if the golem can be built the given item-block
	 **/
	public boolean isHealingItem(final ItemStack i) {
		final Block[] blocks = getBuildingBlocks(this);
		if(i != null && blocks != null && i.getItem() instanceof ItemBlock) {
			for(final Block b : blocks) {
				if(i.getItem() == (new ItemStack(b)).getItem()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param i the ItemStack being used to heal the golem
	 * @return the amount by which this item should heal the golem,
	 * in half-hearts. Defaults to 25% of max health or 32.0, 
	 * whichever is smaller
	 **/
	public float getHealAmount(final ItemStack i) {
		return this.isChild() ? (this.getMaxHealth() * 0.5F) : Math.min(this.getMaxHealth() * 0.25F, 32.0F);
	}
	
	/**
	 * @return the BlockPos directly below the golem
	 **/
	public BlockPos getBlockBelow() {
		int j = MathHelper.floor_double(this.posX);
		int k = MathHelper.floor_double(this.posY - 0.20000000298023224D);
		int l = MathHelper.floor_double(this.posZ);
		return new BlockPos(j, k, l);
	}
	
	/** 
	 * Helper method for translating text into local language using {@code I18n}
	 * @see addSpecialDesc 
	 **/
	protected static String trans(final String s, final Object... strings) {
		return new TextComponentTranslation(s, strings).getFormattedText();
	}

	/////////////// TEXTURE HELPERS //////////////////
	
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
	protected SoundEvent getHurtSound() {
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
	 * Called from {@link #entityInit()} and used to set the texture type <b>before</b> the entity is
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
