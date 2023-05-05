package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.GolemContainer.SwimMode;
import com.mcmoddev.golems.container.behavior.ExplodeBehavior;
import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.container.behavior.ShootArrowsBehavior;
import com.mcmoddev.golems.container.behavior.UseFuelBehavior;
import com.mcmoddev.golems.entity.goal.GoToWaterGoal;
import com.mcmoddev.golems.entity.goal.PlaceUtilityBlocksGoal;
import com.mcmoddev.golems.entity.goal.SwimUpGoal;
import com.mcmoddev.golems.item.SpawnGolemItem;
import com.mcmoddev.golems.util.GolemAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Base class for all golems in this mod.
 **/
public class GolemBase extends IronGolem implements IMultitextured, IFuelConsumer, IRandomTeleporter, IRandomExploder, IArrowShooter, IEntityAdditionalSpawnData {

	protected static final EntityDataAccessor<String> MATERIAL = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Boolean> CHILD = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Byte> TEXTURE = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Boolean> FUSE_LIT = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Integer> ARROWS = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.INT);

	public static final String KEY_MATERIAL = "Material";
	public static final String KEY_CHILD = "IsChild";
	private static final double MAX_ARMOR = ((RangedAttribute)Attributes.ARMOR).getMaxValue() - 0.01D;

	private ResourceLocation material = new ResourceLocation(ExtraGolems.MODID, "empty");
	private GolemContainer container = GolemContainer.EMPTY;

	protected Component description;
	protected boolean isMaterialDirty;

	// swimming helpers
	protected final WaterBoundPathNavigation waterNavigator;
	protected final GroundPathNavigation groundNavigator;
	protected boolean swimmingUp;

	// explode behavior
	protected int fuse;

	// shoot arrows behavior
	protected final RangedAttackGoal aiArrowAttack;
	protected final MeleeAttackGoal aiMeleeAttack;
	private SimpleContainer inventory;
	@Nullable
	private Player playerInMenu;

	// color
	protected int biomeColor = 8626266;

	public GolemBase(EntityType<? extends GolemBase> type, Level world) {
		super(type, world);
		// the following will be unused if swimming is not enabled
		this.waterNavigator = new WaterBoundPathNavigation(this, world);
		this.groundNavigator = new GroundPathNavigation(this, world);
		// the following will only be used if ShootArrowsBehavior is added
		aiArrowAttack = new RangedAttackGoal(this, 1.0D, 28, 32.0F);
		aiMeleeAttack = new MeleeAttackGoal(this, 1.0D, true);
		initArrowInventory();
	}

	public static GolemBase create(final Level world, final ResourceLocation material) {
		GolemBase golem = new GolemBase(EGRegistry.GOLEM.get(), world);
		golem.setMaterial(material);
		return golem;
	}

	public void setMaterial(final ResourceLocation materialIn) {
		this.isMaterialDirty = true;
		// update material and container
		this.getEntityData().set(MATERIAL, materialIn.toString());
		this.material = materialIn;
		// load container
		final Registry<GolemContainer> registry = level.registryAccess().registryOrThrow(ExtraGolems.Keys.GOLEM_CONTAINERS);
		final Optional<GolemContainer> oContainer = registry.getOptional(materialIn);
		if(!oContainer.isPresent()) {
			// log single error message when failing to load
			ExtraGolems.LOGGER.error("Failed to load golem container for '" + materialIn.toString() + "'");
			return;
		}
		// container was loaded successfully
		this.isMaterialDirty = false;
		this.container = oContainer.get();
		this.attributes = GolemAttributes.getAttributes(level.registryAccess(), materialIn);
		this.setInvulnerable(container.getAttributes().getArmor() > MAX_ARMOR);
		// clear description
		this.description = null;
		// update server data
		if (!level.isClientSide()) {
			// remove and re-instantiate goals
			this.goalSelector.removeAllGoals();
			this.registerGoals();
			// define behavior for the given swimming ability
			switch (container.getSwimAbility()) {
				case FLOAT:
					// basic swimming AI
					goalSelector.addGoal(0, new FloatGoal(this));
					break;
				case SWIM:
					// advanced swimming AI
					maxUpStep = 1.0F;
					moveControl = new SwimmingMovementController(this);
					setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
					goalSelector.addGoal(1, new GoToWaterGoal(this, 14, 1.0D));
					goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8F, 200));
					goalSelector.addGoal(5, new SwimUpGoal(this, 1.0D, level.getSeaLevel() + 1));
					break;
				case SINK:
				default:
					// no swimming AI
					break;
			}
			// define pathfinding priority
			if (container.getAttributes().isHurtByWater()) {
				this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
			}
			if(container.hasBehavior(GolemBehaviors.BURN_IN_SUN)) {
				this.groundNavigator.setAvoidSun(true);
			}
			// register goals
			registerGlowGoal();
			registerPowerGoal();

			// allow behaviors to register goals
			container.getBehaviors().values().forEach(list -> list.forEach(b -> b.onRegisterGoals(this)));
		}
	}

	/**
	 * @return the Material that can be used to look up the GolemContainer
	 **/
	public ResourceLocation getMaterial() {
		return this.material;
	}

	/**
	 * @return the GolemContainer that was looked up by Material
	 **/
	public GolemContainer getContainer() {
		return this.container;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(MATERIAL, GolemContainer.EMPTY_MATERIAL.toString());
		this.getEntityData().define(CHILD, Boolean.FALSE);
		this.getEntityData().define(TEXTURE, (byte) 0);
		this.getEntityData().define(FUEL, 0);
		this.getEntityData().define(FUSE_LIT, Boolean.FALSE);
		this.getEntityData().define(ARROWS, 0);
	}

	@Override
	public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (MATERIAL.equals(key)) {
			setMaterial(new ResourceLocation(this.getEntityData().get(MATERIAL)));
		}
		if (CHILD.equals(key)) {
			if (this.isBaby()) {
				// truncate these values to one decimal place after reducing them from base values
				double childHealth = (Math.floor(getContainer().getAttributes().getHealth() * 0.3D * 10D)) / 10D;
				double childAttack = (Math.floor(getContainer().getAttributes().getAttack() * 0.6D * 10D)) / 10D;
				this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(childHealth);
				this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(childAttack);
				this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
			} else {
				// use full values for non-child entity
				this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(getContainer().getAttributes().getHealth());
				this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getContainer().getAttributes().getAttack());
				this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(getContainer().getAttributes().getKnockbackResist());
			}
			// recalculate size
			this.refreshDimensions();
		} else if (TEXTURE.equals(key)) {
			this.setTextureId((byte) this.getTextureId());
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevel, DifficultyInstance difficulty,
										MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
		this.setHealth(this.getMaxHealth());
		this.setInvulnerable(getContainer().getAttributes().getArmor() > MAX_ARMOR);
		return super.finalizeSpawn(serverLevel, difficulty, mobSpawnType, spawnGroupData, tag);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
	}

	@Override
	public boolean isSunBurnTick() {
		return super.isSunBurnTick();
	}

	protected void registerGlowGoal() {
		// register light level AI if enabled
		int lightInt = getContainer().getMaxLightLevel();
		if (lightInt > 0) {
			final BlockState state = EGRegistry.UTILITY_LIGHT.get().defaultBlockState().setValue(GlowBlock.LIGHT_LEVEL, lightInt);
			this.goalSelector.addGoal(9, new PlaceUtilityBlocksGoal(this, state, GlowBlock.UPDATE_TICKS,
					true, (golem, pos) -> golem.isProvidingLight()));
		}
	}

	protected void registerPowerGoal() {
		// register power level AI if enabled
		int powerInt = getContainer().getMaxPowerLevel();
		if (powerInt > 0) {
			final BlockState state = EGRegistry.UTILITY_POWER.get().defaultBlockState().setValue(PowerBlock.POWER_LEVEL, powerInt);
			final int freq = PowerBlock.UPDATE_TICKS;
			this.goalSelector.addGoal(9, new PlaceUtilityBlocksGoal(this, state, freq, false, (golem, pos) -> golem.isProvidingPower()));
		}
	}

	/////////////// GOLEM UTILITY METHODS //////////////////

	/**
	 * Whether right-clicking on this entity triggers a texture change.
	 *
	 * @return True if this is a {@link IMultitextured} AND the config option
	 * is enabled.
	 **/
	public boolean canInteractChangeTexture() {
		return getContainer().getMultitexture().isPresent()
				&& getContainer().getMultitexture().get().canCycle();
	}

	/**
	 * Whether this entity provides light (by placing light source blocks). Does not
	 * change any behavior, but is used in the Light Block code to determine if it
	 * can stay (called AFTER light is placed).
	 *
	 * @see com.mcmoddev.golems.block.GlowBlock
	 **/
	public boolean isProvidingLight() {
		if (getContainer().getMultitexture().isPresent()) {
			return getContainer().getMultitexture().get().getLight(this) > 0;
		}
		return this.getContainer().getMaxLightLevel() > 0;
	}

	/**
	 * Whether this entity provides power (by placing power source blocks). Does not
	 * change any behavior, but is used in the Power Block code to determine if it
	 * can stay.
	 *
	 * @see com.mcmoddev.golems.block.PowerBlock
	 **/
	public boolean isProvidingPower() {
		return this.getContainer().getMaxPowerLevel() > 0;
	}

	/**
	 * @param i the ItemStack being used to heal the entity
	 * @return the amount by which this item should heal the entity, in half-hearts.
	 * Defaults to 25% of max health or 32.0, whichever is smaller
	 **/
	public float getHealAmount(final ItemStack i) {
		float amount = (float) (this.getMaxHealth() * this.getContainer().getHealAmount(i.getItem()));
		if (this.isBaby()) {
			amount *= 1.75F;
		}
		// max heal amount is 64, for no reason at all
		return Math.min(amount, 64.0F);
	}

	public BlockPos getBlockBelow() {
		return getBlockPosBelowThatAffectsMyMovement();
	}

	public ItemStack getBanner() {
		return this.getItemBySlot(EquipmentSlot.CHEST);
	}

	public void setBanner(final ItemStack bannerItem) {
		this.setItemSlot(EquipmentSlot.CHEST, bannerItem);
		if (bannerItem.getItem() instanceof BannerItem) {
			this.setDropChance(EquipmentSlot.CHEST, 1.0F);
		}
	}

	/////////////// OVERRIDEN BEHAVIOR //////////////////

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		// update material
		if(isMaterialDirty && tickCount > 0) {
			setMaterial(this.material);
		}
		// take damage from water
		if (getContainer().getAttributes().isHurtByWater() && this.isInWaterRainOrBubble()) {
			this.hurt(DamageSource.DROWN, 1.0F);
		}
		// take damage from heat
		if (getContainer().getAttributes().isHurtByHeat()) {
			final BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement().above(2);
			if (this.level.getBiome(pos).value().shouldSnowGolemBurn(pos)) {
				this.hurt(DamageSource.ON_FIRE, 1.0F);
			}
		}
		// update combat goal when arrows behavior is enabled
		if (getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS) && tickCount % 35 == 1) {
			final boolean forceMelee = (getTarget() != null && getTarget().distanceToSqr(this) < 8.0D);
			updateCombatTask(forceMelee);
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		// allow behaviors to update
		getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onTick(this)));
		// client-side updates
		if (level.isClientSide()) {
			// update biome color
			if (this.tickCount % 15 == 1) {
				biomeColor = this.level.getBiome(this.blockPosition()).value().getFoliageColor();
			}
			// spawn fuse particles
			if (isFuseLit()) {
				level.addParticle(ParticleTypes.SMOKE, getX() + getRandom().nextDouble() - 0.5D, getY() + (getRandom().nextDouble() * getBbHeight()), getZ() + getRandom().nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);
			}
			// spawn particles based on container
			getContainer().getParticle().ifPresent(particle -> {
				level.addParticle(particle, getX() + getRandom().nextDouble() - 0.5D, getY() + (getRandom().nextDouble() * getEyeHeight()), getZ() + getRandom().nextDouble() - 0.5D,
						0.1D * (getRandom().nextDouble() - 0.5D), 0.1D * (getRandom().nextDouble() - 0.5D), 0.1D * (getRandom().nextDouble() - 0.5D));
			});
		}
	}


	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
		if (!getContainer().getAttributes().isHurtByFall()) {
			return false;
		}

		float[] ret = net.minecraftforge.common.ForgeHooks.onLivingFall(this, distance, damageMultiplier);
		if (ret == null) return false;
		distance = ret[0];
		damageMultiplier = ret[1];

		boolean flag = super.causeFallDamage(distance, damageMultiplier, source);
		int i = this.calculateFallDamage(distance, damageMultiplier);
		if (i > 0) {
			SoundEvent sound = i > 4 ? this.getFallSounds().big() : this.getFallSounds().small();
			this.playSound(sound, 1.0F, 1.0F);
			this.playBlockFallSound();
			this.hurt(DamageSource.FALL, (float) i);
			return true;
		} else {
			return flag;
		}
	}

	@Override
	public boolean fireImmune() {
		return getContainer().getAttributes().hasFireImmunity();
	}

	@Override
	public boolean ignoreExplosion() {
		return getContainer().getAttributes().hasExplosionImmunity();
	}

	@Override
	public boolean canAttackType(final EntityType<?> type) {
		if (type == EntityType.PLAYER && this.isPlayerCreated()) {
			return ExtraGolems.CONFIG.enableFriendlyFire();
		}
		if (type == EntityType.VILLAGER || type == EGRegistry.GOLEM.get() || type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM) {
			return false;
		}
		return super.canAttackType(type);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (super.doHurtTarget(target)) {
			// use attack knockback stat
			final double knockback = getContainer().getAttributes().getAttackKnockback();
			if (knockback > 0 && !isBaby()) {
				final Vec3 myPos = this.position();
				final Vec3 ePos = target.position();
				final double dX = Math.signum(ePos.x - myPos.x) * knockback;
				final double dZ = Math.signum(ePos.z - myPos.z) * knockback;
				target.setDeltaMovement(target.getDeltaMovement().add(dX, knockback / 2, dZ));
			}
			// allow behaviors to process doHurtTarget
			this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onHurtTarget(this, target)));
			return true;
		}
		return false;
	}

	@Override
	protected void actuallyHurt(DamageSource source, float amount) {
		super.actuallyHurt(source, amount);
		// allow behaviors to process actuallyHurt
		this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onActuallyHurt(this, source, amount)));
	}

	@Override
	public ItemStack getPickedResult(final HitResult ray) {
		return container.hasBlocks() ? new ItemStack(container.getAllBlocks().toArray(new Block[0])[0]) : ItemStack.EMPTY;
	}

	@Override
	protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		// Attempt to remove banner from the entity
		if (!this.getBanner().isEmpty() && stack.getItem() instanceof ShearsItem) {
			this.spawnAtLocation(this.getBanner(), this.isBaby() ? 0.9F : 1.4F);
			this.setBanner(ItemStack.EMPTY);
			return InteractionResult.CONSUME;
		}
		// Attempt to place a banner on the entity
		if (stack.getItem() instanceof BannerItem && processInteractBanner(player, hand, stack)) {
			return InteractionResult.CONSUME;
		}
		// Attempt to heal the entity
		final float healAmount = getHealAmount(stack);
		if (!stack.isEmpty() && healAmount > 0 && processInteractHeal(player, hand, stack, healAmount)) {
			return InteractionResult.CONSUME;
		}
		// Cycle texture when server-side player interacts with the entity.
		// This only runs for one hand, whether or not the hand is empty,
		// to avoid double-interaction that causes double texture cycles.
		if (hand == InteractionHand.MAIN_HAND && !level.isClientSide() && !player.isCrouching()
				&& canInteractChangeTexture() && cycleTexture()) {
			player.swing(hand);
			return InteractionResult.CONSUME;
		}
		// Allow behaviors to process mobInteract
		this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onMobInteract(this, player, hand)));
		return super.mobInteract(player, hand);
	}

	/**
	 * Called when the player uses an item that might be a banner
	 *
	 * @param player the player using the item
	 * @param hand   the player hand
	 * @param stack  the item being used
	 * @return true if the item was consumed
	 */
	protected boolean processInteractBanner(final Player player, final InteractionHand hand, final ItemStack stack) {
		if (!this.getBanner().isEmpty()) {
			this.spawnAtLocation(this.getBanner(), this.isBaby() ? 0.9F : 1.4F);
		}
		setBanner(stack.split(1));
		return true;
	}

	/**
	 * Called when the player uses an item that can heal this entity
	 *
	 * @param player     the player using the item
	 * @param hand       the player hand
	 * @param stack      the item being used
	 * @param healAmount the amount of health this item will restore
	 * @return true if the item was consumed
	 */
	protected boolean processInteractHeal(final Player player, final InteractionHand hand, final ItemStack stack, final float healAmount) {
		if (ExtraGolems.CONFIG.enableHealGolems() && this.getHealth() < this.getMaxHealth()) {
			heal(healAmount);
			// update stack size/item
			if (!player.isCreative()) {
				if (stack.getCount() > 1) {
					stack.shrink(1);
				} else {
					// update the player's held item
					player.setItemInHand(hand, stack.getCraftingRemainingItem());
				}
			}
			// if currently attacking this player, stop
			if (this.getTarget() == player) {
				this.setLastHurtByMob(null);
				this.setTarget(null);
			}
			// spawn particles and play sound
			final Vec3 pos = this.position();
			SpawnGolemItem.spawnParticles(this.level, pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.15D, ParticleTypes.INSTANT_EFFECT, 30);
			this.playSound(SoundEvents.STONE_PLACE, 0.85F, 1.1F + random.nextFloat() * 0.2F);
			return true;
		}
		return false;
	}

	/**
	 * @param player the player who has opened a menu
	 */
	public void setPlayerInMenu(@Nullable final Player player) {
		this.playerInMenu = player;
	}

	/**
	 * @return the player who has opened a menu, if any
	 */
	@Nullable
	public Player getPlayerInMenu() {
		return playerInMenu;
	}

	/**
	 * @param distance the maximum distance from the player to this entity
	 * @return true if the player with an open menu exists and is within the given distance
	 */
	public boolean isPlayerInRangeForMenu(final double distance) {
		if(null == this.playerInMenu) {
			return false;
		}
		return this.distanceToSqr(this.playerInMenu) < distance * distance;
	}

	@Override
	public boolean isSensitiveToWater() {
		return this.getContainer().getAttributes().isHurtByWater();
	}

	@Override
	public float getLightLevelDependentMagicValue() {
		return (this.isProvidingLight() || this.isProvidingPower()) ? 1.0F : super.getLightLevelDependentMagicValue();
	}

	@Override
	protected Component getTypeName() {
		if (null == description) {
			description = Component.translatable("entity." + material.getNamespace() + ".golem." + material.getPath());
		}
		return description;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return getContainer().getLootTable(this);
	}

	@Override
	public boolean isBaby() {
		return this.getEntityData().get(CHILD).booleanValue();
	}

	/**
	 * Update whether this entity is 'child' and recalculate size
	 **/
	public void setBaby(final boolean isChild) {
		if (this.getEntityData().get(CHILD).booleanValue() != isChild) {
			this.getEntityData().set(CHILD, Boolean.valueOf(isChild));
			this.refreshDimensions();
		}
	}

	@Override
	public void die(final DamageSource source) {
		// allow behaviors to process die
		this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onDie(this, source)));
		super.die(source);
	}

	////////////////NBT /////////////////

	@Override
	public void readAdditionalSaveData(final CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setMaterial(new ResourceLocation(tag.getString(KEY_MATERIAL)));
		this.setBaby(tag.getBoolean(KEY_CHILD));
		getContainer().getMultitexture().ifPresent(m -> loadTextureId(tag));
		// allow behaviors to process readData
		initArrowInventory();
		this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onReadData(this, tag)));
	}

	@Override
	public void addAdditionalSaveData(final CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putString(KEY_MATERIAL, getMaterial().toString());
		tag.putBoolean(KEY_CHILD, this.isBaby());
		getContainer().getMultitexture().ifPresent(m -> saveTextureId(tag));
		// allow behaviors to process writeData
		this.getContainer().getBehaviors().values().forEach(list -> list.forEach(b -> b.onWriteData(this, tag)));
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(material);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer) {
		setMaterial(buffer.readResourceLocation());
		setHealth(getMaxHealth());
	}

	///////////////////// SOUND OVERRIDES ////////////////////

	@Override
	protected SoundEvent getAmbientSound() {
		return getGolemSound();
	}

	@Override
	protected SoundEvent getHurtSound(final DamageSource ignored) {
		return getGolemSound() == SoundEvents.GLASS_STEP ? SoundEvents.GLASS_HIT : getGolemSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getGolemSound() == SoundEvents.GLASS_STEP ? SoundEvents.GLASS_BREAK : getGolemSound();
	}

	/**
	 * @return A SoundEvent to play when the entity is attacking, walking, hurt, and
	 * on death
	 **/
	public final SoundEvent getGolemSound() {
		return getContainer().getSound();
	}

	///////////////////// MULTITEXTURE ///////////////////////////

	@Override
	public int getTextureCount() {
		return getContainer().getMultitexture().isPresent() ? container.getMultitexture().get().getTextureCount() : 0;
	}

	@Override
	public void setTextureId(byte toSet) {
		if (toSet >= 0) {
			this.getEntityData().set(TEXTURE, toSet);
		}
	}

	@Override
	public int getTextureId() {
		return this.getEntityData().get(TEXTURE);
	}

	/**
	 * Called after construction when a entity is built by a player
	 *
	 * @param body the body block
	 * @param legs the legs block
	 * @param arm1 the first arm block
	 * @param arm2 the second arm block
	 */
	public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
		getContainer().getMultitexture().ifPresent(m -> this.setTextureId((byte) m.getTextureFromBlock(body.getBlock())));
	}

	/**
	 * @return the current biome color (only updated client-side)
	 **/
	public int getBiomeColor() {
		return biomeColor;
	}

	///////////////////// FUEL ////////////////////////

	@Override
	public void setFuel(int fuel) {
		getEntityData().set(FUEL, fuel);
	}

	@Override
	public int getFuel() {
		return getEntityData().get(FUEL);
	}

	@Override
	public int getMaxFuel() {
		List<UseFuelBehavior> b = getContainer().getBehaviors(GolemBehaviors.USE_FUEL);
		return b.isEmpty() ? 0 : b.get(0).getMaxFuel();
	}


	///////////////////// EXPLODE ////////////////////////

	@Override
	public GolemBase getGolemEntity() {
		return this;
	}

	@Override
	public int getFuseLen() {
		List<ExplodeBehavior> b = getContainer().getBehaviors(GolemBehaviors.EXPLODE);
		return b.isEmpty() ? 0 : b.get(0).getFuseLen();
	}

	@Override
	public int getFuse() {
		return fuse;
	}

	@Override
	public void setFuse(int fuseIn) {
		fuse = fuseIn;
	}

	@Override
	public void setFuseLit(boolean litIn) {
		getEntityData().set(FUSE_LIT, litIn);
	}

	@Override
	public boolean isFuseLit() {
		return getEntityData().get(FUSE_LIT);
	}

	///////////////////// SHOOT ARROWS ////////////////////////

	@Override
	public void initArrowInventory() {
		SimpleContainer simplecontainer = this.inventory;
		this.inventory = new SimpleContainer(INVENTORY_SIZE);
		if (simplecontainer != null) {
			simplecontainer.removeListener(this);
			int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = simplecontainer.getItem(j);
				if (!itemstack.isEmpty()) {
					this.inventory.setItem(j, itemstack.copy());
				}
			}
		}

		this.inventory.addListener(this);
		this.containerChanged(this.inventory);
	}

	@Override
	public double getArrowDamage() {
		if (getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
			double multiplier = isBaby() ? 0.5D : 1.0D;
			return multiplier * getContainer().<ShootArrowsBehavior>getBehaviors(GolemBehaviors.SHOOT_ARROWS).get(0).getDamage();
		}
		return 0;
	}

	@Override
	public boolean wantsToPickUp(ItemStack stack) {
		if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ArrowItem
				&& getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
			// make sure the entity can pick up this stack
			for (int i = 0, l = getArrowInventory().getContainerSize(); i < l; i++) {
				final ItemStack invStack = getArrowInventory().getItem(i);
				if (invStack.isEmpty() || (invStack.getItem() == stack.getItem() && ItemStack.tagMatches(invStack, stack)
						&& invStack.getCount() + stack.getCount() <= invStack.getMaxStackSize())) {
					return true;
				}
			}
			return false;
		}
		return this.canHoldItem(stack);
	}

	@Override
	public boolean canPickUpLoot() {
		return getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS) || super.canPickUpLoot();
	}

	@Override
	public SimpleContainer getArrowInventory() {
		return inventory;
	}

	@Override
	public RangedAttackGoal getRangedGoal() {
		return aiArrowAttack;
	}

	@Override
	public MeleeAttackGoal getMeleeGoal() {
		return aiMeleeAttack;
	}

	@Override
	public int getArrowsInInventory() {
		return getEntityData().get(ARROWS);
	}

	@Override
	public void setArrowsInInventory(int count) {
		getEntityData().set(ARROWS, count);
	}

	@Override
	protected void dropEquipment() {
		super.dropEquipment();
		if (getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
			dropArrowInventory();
		}
	}

	@Override
	public boolean equipItemIfPossible(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem
				&& getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)
				&& getArrowInventory().canAddItem(stack)) {
			// attempt to add the arrows to the inventory
			getArrowInventory().addItem(stack);
			return true;
		} else {
			return super.equipItemIfPossible(stack);
		}
	}

	@Override
	public void onItemPickup(ItemEntity itemEntity) {
		super.onItemPickup(itemEntity);
		containerChanged(getArrowInventory());
	}

	@Override
	public boolean canHoldItem(ItemStack item) {
		return false;
	}

	///////////////////// SWIMMING BEHAVIOR ////////////////////////

	@Override
	public void travel(final Vec3 vec) {
		if (isEffectiveAi() && isInWater() && isSwimmingUp()) {
			moveRelative(0.01F, vec);
			move(MoverType.SELF, getDeltaMovement());
			setDeltaMovement(getDeltaMovement().scale(0.9D));
		} else {
			super.travel(vec);
		}
	}

	@Override
	public void updateSwimming() {
		if (getContainer().getSwimAbility() != SwimMode.SWIM) {
			super.updateSwimming();
			return;
		}
		if (!this.level.isClientSide) {
			if (isEffectiveAi() && isInWater() && isSwimmingUp()) {
				this.navigation = this.waterNavigator;
				setSwimming(true);
			} else {
				this.navigation = this.groundNavigator;
				setSwimming(false);
			}
		}
	}

	@Override
	protected float getWaterSlowDown() {
		return getContainer().getSwimAbility() == SwimMode.SWIM ? 0.88F : super.getWaterSlowDown();
	}

	@Override
	public boolean isPushedByFluid() {
		return !isSwimming();
	}

	public void setSwimmingUp(boolean isSwimmingUp) {
		this.swimmingUp = (isSwimmingUp && getContainer().getSwimAbility() == SwimMode.SWIM);
	}

	public boolean isSwimmingUp() {
		if (getContainer().getSwimAbility() != SwimMode.SWIM) {
			return false;
		}
		if (this.swimmingUp) {
			return true;
		}
		LivingEntity e = getTarget();
		return e != null && e.isInWater();
	}

	public static boolean isSwimmingUp(final GolemBase golem) {
		return golem.swimmingUp;
	}

	/**
	 * Referenced from {@link GoToWaterGoal}.
	 *
	 * @param target a location representing a water block
	 * @return true if the entity should move towards the water
	 **/
	public boolean shouldMoveToWater(final Vec3 target) {
		return getContainer().getSwimAbility() == SwimMode.SWIM;
	}

	static class SwimmingMovementController extends MoveControl {
		private final GolemBase golem;

		public SwimmingMovementController(GolemBase golemIn) {
			super(golemIn);
			this.golem = golemIn;
		}

		public void tick() {
			LivingEntity livingentity = this.golem.getTarget();
			if (this.golem.isSwimmingUp() && this.golem.isInWater()) {
				if (livingentity != null && livingentity.getY() > this.golem.getY() || this.golem.swimmingUp) {
					this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
				}

				if (this.operation != MoveControl.Operation.MOVE_TO || this.golem.getNavigation().isDone()) {
					this.golem.setSpeed(0.0F);
					return;
				}

				double d0 = this.wantedX - this.golem.getX();
				double d1 = this.wantedY - this.golem.getY();
				double d2 = this.wantedZ - this.golem.getZ();
				double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
				d1 = d1 / d3;
				float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
				this.golem.setYRot(this.rotlerp(this.golem.getYRot(), f, 90.0F));
				this.golem.yBodyRot = this.golem.getYRot();
				float f1 = (float) (this.speedModifier * this.golem.getAttributeValue(Attributes.MOVEMENT_SPEED));
				float f2 = Mth.lerp(0.125F, this.golem.getSpeed(), f1);
				this.golem.setSpeed(f2);
				this.golem.setDeltaMovement(
						this.golem.getDeltaMovement().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
			} else {
				if (!this.golem.onGround) {
					this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
				}

				super.tick();
			}

		}
	}
}
