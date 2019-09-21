package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.main.ExtraGolemsEntities;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends IronGolemEntity {
	
	protected static final DataParameter<Boolean> CHILD = EntityDataManager.createKey(GolemBase.class, DataSerializers.BOOLEAN);
	protected static final String KEY_CHILD = "isChild";
	
	protected final GolemContainer container;
	private boolean canFall = false;
	private boolean canSwim = false;
	//type, world
	public GolemBase(EntityType<? extends GolemBase> type, World world) {
		super(type, world);
		this.container = GolemRegistrar.getContainer(type);
		canFall = container.takesFallDamage();
		if(container.canSwim()) {
			this.enableSwim();
		}
	}

	/**
	 * Called after construction when a golem is built by a player
	 * @param body
	 * @param legs
	 * @param arm1
	 * @param arm2
	 */
	public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
		// do nothing
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		//Called in super constructor; this.container == null
		GolemContainer golemContainer = GolemRegistrar.getContainer(this.getType());
		this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			.setBaseValue(golemContainer.getAttack());
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(golemContainer.getHealth());
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(golemContainer.getSpeed());
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(golemContainer.getKnockbackResist());
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(CHILD, Boolean.valueOf(false));
	}

	/**
	 * Whether right-clicking on this entity triggers a texture change.
	 *
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
	 *
	 * @see com.mcmoddev.golems.blocks.BlockUtilityGlow
	 **/
	public boolean isProvidingLight() {
		return false;
	}

	/**
	 * Whether this golem provides power (by placing power source blocks).
	 * Does not change any behavior, but is used in the Power Block code
	 * to determine if it can stay.
	 *
	 * @see com.mcmoddev.golems.blocks.BlockUtilityPower
	 **/
	public boolean isProvidingPower() {
		return false;
	}
	
	/**
	 * Allows the golem to swim actively.
	 * This is disabled by default.
	 **/
	private void enableSwim() {
		this.canSwim = true;
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.navigator.setCanSwim(true);
	}
	
	@Override
	public boolean canSwim() {
		return canSwim;
	}

	public GolemContainer getGolemContainer() {
		return container != null ? container : GolemRegistrar.getContainer(this.getType().getRegistryName());
	}

	public ForgeConfigSpec.ConfigValue getConfigValue(String name) {
		return (ExtraGolemsConfig.GOLEM_CONFIG.specials.get(this.getGolemContainer().specialContainers.get(name))).value;
	}

	public boolean getConfigBool(final String name) {
		return (Boolean) getConfigValue(name).get();
	}

	public int getConfigInt(final String name) {
		return (Integer) getConfigValue(name).get();
	}

	public double getConfigDouble(final String name) {
		return (Double) getConfigValue(name).get();
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		if(!canFall) return;
		float[] ret = net.minecraftforge.common.ForgeHooks.onLivingFall(this, distance, damageMultiplier);
		if (ret == null) return;
		distance = ret[0]; damageMultiplier = ret[1];
		super.fall(distance, damageMultiplier);
		EffectInstance effectinstance = this.getActivePotionEffect(Effects.JUMP_BOOST);
		float f = effectinstance == null ? 0.0F : (float)(effectinstance.getAmplifier() + 1);
		int i = MathHelper.ceil((distance - 3.0F - f) * damageMultiplier);
		if (i > 0) {
			this.playSound(this.getFallSound(i), 1.0F, 1.0F);
			this.attackEntityFrom(DamageSource.FALL, (float)i);
			int j = MathHelper.floor(this.posX);
			int k = MathHelper.floor(this.posY - (double)0.2F);
			int l = MathHelper.floor(this.posZ);
			BlockState blockstate = this.world.getBlockState(new BlockPos(j, k, l));
			if (!blockstate.isAir()) {
				SoundType soundtype = blockstate.getSoundType(world, new BlockPos(j, k, l), this);
				this.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
			}
		}
	}
	
	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		// Copy Iron Golem behavior but allow for custom attack damage
		double baseAttack = this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
		
		// Deal damage between 100% and 175% of current attack power
		double damage = baseAttack + (this.rand.nextDouble() * 0.75D) * baseAttack;
		final boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)damage);
		if (flag) {
			entity.setMotion(entity.getMotion().add(0.0D, 0.4D, 0.0D));
			this.applyEnchantments(this, entity);
		}
		
		// Set fields and play sound so the client knows the golem is attacking an Entity
		this.attackTimer = 10;
		this.world.setEntityState(this, (byte) 4);
		this.playSound(this.getGolemSound(), 1.0F, 0.9F + rand.nextFloat() * 0.2F);
		return flag;
	}
	
	@Override
	public boolean canAttack(final EntityType<?> type) {
		if(type == EntityType.PLAYER && this.isPlayerCreated()) {
			return ExtraGolemsConfig.enableFriendlyFire();
		}
		if(type == EntityType.VILLAGER || type.getRegistryName().toString().contains("golem")) {
			return false;
		}
		return super.canAttack(type);
	}
	
	@Override
	public ItemStack getPickedResult(final RayTraceResult ray) {
		final Block block = this.container.getPrimaryBuildingBlock();
		return block != null ? new ItemStack(block) : ItemStack.EMPTY;
	}
	
	@Override
	public boolean isChild() {
		return this.getDataManager().get(CHILD).booleanValue();
	}
	
	/** Update whether this entity is 'child' and recalculate size **/
	public void setChild(final boolean isChild) {
		if(this.getDataManager().get(CHILD).booleanValue() != isChild) {
			this.getDataManager().set(CHILD, Boolean.valueOf(isChild));
			this.recalculateSize();
		}
	}
	
	@Override
	public void readAdditional(final CompoundNBT tag) {
		super.readAdditional(tag);
		this.setChild(tag.getBoolean(KEY_CHILD));
	}
	
	@Override
	public void writeAdditional(final CompoundNBT tag) {
		super.writeAdditional(tag);
		tag.putBoolean(KEY_CHILD, this.isChild());
	}

	/////////////// TEXTURE HELPERS //////////////////

	/**
	 * This method is called from the golem Render code
	 * and should return the current texture (skin) of
	 * the golem. Defaults to querying the container for
	 * a texture.
	 * @return a ResourceLocation to use for rendering
	 **/
	public ResourceLocation getTexture() {
		return this.container.getTexture();
	}

	/**
	 * Calls {@link #makeTexture(String, String)} on the assumption that MODID is 'golems'.
	 * Texture should be at 'assets/golems/textures/entity/[TEXTURE].png'
	 * <br>For most golems, set the texture when building the GolemContainer using
	 * {@link GolemContainer.Builder#setTexture(ResourceLocation)} or
	 * {@link GolemContainer.Builder#basicTexture()}
	 **/
	protected static ResourceLocation makeTexture(final String TEXTURE) {
		return ExtraGolemsEntities.makeTexture(TEXTURE);
	}

	/**
	 * Makes a ResourceLocation using the passed mod id and the texture name. Texture should
	 * be at 'assets/[MODID]/textures/entity/[TEXTURE].png'
	 * <br>For most golems, set the texture when building the GolemContainer using
	 * {@link GolemContainer.Builder#setTexture(ResourceLocation)} or
	 * {@link GolemContainer.Builder#basicTexture()}
	 * @see #makeTexture(String)
	 **/
	protected static ResourceLocation makeTexture(final String MODID, final String TEXTURE) {
		return ExtraGolemsEntities.makeTexture(MODID, TEXTURE);
	}

	///////////////////// SOUND OVERRIDES ////////////////////

	@Override
	protected SoundEvent getAmbientSound() {
		return getGolemSound();
	}

	@Override
	protected SoundEvent getHurtSound(final DamageSource ignored) {
		return getGolemSound() == SoundEvents.BLOCK_GLASS_STEP ? SoundEvents.BLOCK_GLASS_HIT : getGolemSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getGolemSound() == SoundEvents.BLOCK_GLASS_STEP ? SoundEvents.BLOCK_GLASS_BREAK : getGolemSound();
	}
	
	/**
	 * @return A SoundEvent to play when the golem is attacking, walking, hurt, and on death
	 **/
	public final SoundEvent getGolemSound() {
		return this.container.getSound();
	}
}
