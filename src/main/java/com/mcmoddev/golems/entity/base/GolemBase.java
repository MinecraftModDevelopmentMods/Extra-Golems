package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.main.ExtraGolems;
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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Base class for all golems in this mod.
 **/
public abstract class GolemBase extends IronGolemEntity {
	
	// TODO protected final DataParameter<Boolean> CHILD = EntityDataManager.createKey(GolemBase.class, DataSerializers.BOOLEAN);
	// TODO protected final String KEY_CHILD = "isChild";
	
	protected final GolemContainer container;
	protected ResourceLocation textureLoc;
	private boolean canFall = false;
	private boolean canSwim = false;
	//type, world
	public GolemBase(EntityType<? extends GolemBase> type, World world) {
		super(type, world);
		this.container = GolemRegistrar.getContainer(type);
		this.navigator.setCanSwim(false);
	}

	/**
	 * Called after construction when a golem is built by a player
	 * @param body
	 * @param legs
	 * @param arm1
	 * @param arm2
	 */
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		//do nothing by default
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
	}

	@Override
	protected void registerData() {
		super.registerData();
		// TODO this.getDataManager().register(CHILD, Boolean.FALSE);
		this.setTextureType(this.applyTexture());
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
	 * Allows the golem to take fall damage.
	 * This is disabled by default.
	 **/
	public void enableFallDamage() {
		canFall = true;
	}
	
	/**
	 * @return whether or not this golem takes fall damage.
	 **/
	public boolean canFall() {
		return canFall;
	}
	
	/**
	 * Allows the golem to swim actively.
	 * This is disabled by default.
	 **/
	public void enableSwim() {
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
	public ItemStack getPickedResult(final RayTraceResult ray) {
		final Block block = this.container.getPrimaryBuildingBlock();
		return block != null ? new ItemStack(block) : ItemStack.EMPTY;
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

	@Override
	protected SoundEvent getHurtSound(final DamageSource ignored) {
		return getGolemSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getGolemSound();
	}

	/**
	 * Called from {@link #registerData()} and used to set the texture type <b>before</b> the entity is
	 * fully constructed or rendered. Example implementation: texture is at
	 * 'assets/golems/textures/entity/golem_clay.png'
	 *
	 * <pre>
	 * {@code
	 * protected ResourceLocation applyTexture() {
	 * 	return this.makeGolemTexture("golems", "clay");
	 * }
	 * </pre>
	 *
	 * @return a ResourceLocation for this golem's texture
	 * @see #makeGolemTexture(String, String)
	 **/
	protected abstract ResourceLocation applyTexture();

	/**
	 * @return A SoundEvent to play when the golem is attacking, walking, hurt, and on death
	 **/
	public abstract SoundEvent getGolemSound();

}
