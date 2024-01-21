package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.BurnInSunBehavior;
import com.mcmoddev.golems.data.behavior.ExplodeBehavior;
import com.mcmoddev.golems.data.behavior.LightBehavior;
import com.mcmoddev.golems.data.behavior.PowerBehavior;
import com.mcmoddev.golems.data.behavior.ShootArrowsBehavior;
import com.mcmoddev.golems.data.behavior.UseFuelBehavior;
import com.mcmoddev.golems.data.golem.SwimAbility;
import com.mcmoddev.golems.entity.goal.GoToWaterGoal;
import com.mcmoddev.golems.entity.goal.SwimUpGoal;
import com.mcmoddev.golems.item.SpawnGolemItem;
import com.mcmoddev.golems.util.GolemAttributeManager;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Base class for all golems in this mod.
 **/
public class GolemBase extends IronGolem implements IExtraGolem {

	// SYNCED ENTITY DATA //
	private static final EntityDataAccessor<Optional<ResourceLocation>> GOLEM = SynchedEntityData.defineId(GolemBase.class, IExtraGolem.OPTIONAL_RESOURCE_LOCATION);
	private static final EntityDataAccessor<Boolean> CHILD = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Byte> VARIANT = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ARROWS = SynchedEntityData.defineId(GolemBase.class, EntityDataSerializers.INT);

	// KEYS //
	private static final String KEY_CHILD = "IsChild";

	// EVENTS //
	private static final byte LIGHT_FUSE_EVENT = (byte) 13;

	// CONTAINER //
	@Nullable
	private GolemContainer cachedContainer;

	// INVENTORY //
	private static final int INVENTORY_SIZE = 9;
	private SimpleContainer inventory;
	private @Nullable Player playerInMenu;

	// NAVIGATION //
	private final WaterBoundPathNavigation waterNavigator;
	private final GroundPathNavigation groundNavigator;
	private boolean swimmingUp;

	// RANDOM EXPLODER //
	private int fuse;
	private boolean fuseLit;

	// shoot arrows behavior
	private boolean isInventoryDirty;
	private final RangedAttackGoal aiArrowAttack;
	private final MeleeAttackGoal aiMeleeAttack;

	// color
	private int biomeColor = 0x83A05A;

	//// CONSTRUCTOR ////

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
		GolemBase golem = new GolemBase(EGRegistry.EntityReg.GOLEM.get(), world);
		golem.setGolemId(material);
		return golem;
	}

	//// EXTRA GOLEM ////

	@Override
	public GolemBase asMob() {
		return this;
	}

	/**
	 * @return the Material that can be used to look up the GolemContainer
	 **/
	@Override
	public Optional<ResourceLocation> getGolemId() {
		return this.getEntityData().get(GOLEM);
	}

	@Override
	public Optional<GolemContainer> getContainer(RegistryAccess registryAccess) {
		if(null == this.cachedContainer) {
			this.cachedContainer = IExtraGolem.super.getContainer(registryAccess).orElse(null);
		}
		return Optional.ofNullable(this.cachedContainer);
	}

	/**
	 * Gets or loads the cached {@link GolemContainer}
	 * @return the value of {@link #getContainer(RegistryAccess)}
	 **/
	public Optional<GolemContainer> getContainer() {
		return getContainer(level().registryAccess());
	}

	@Override
	public void setGolemId(final @Nullable ResourceLocation id) {
		// update golem and container
		this.getEntityData().set(GOLEM, Optional.ofNullable(id));
		clearCachedGolemContainer();
		if(null == id) {
			return;
		}
		// load container
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			// log error message when failing to load
			ExtraGolems.LOGGER.error("Failed to load golem container for '" + id + "'");
			return;
		}
		// container was loaded successfully
		final GolemContainer container = oContainer.get();
		this.attributes = GolemAttributeManager.getAttributes(this.level().registryAccess(), id);
		this.setInvulnerable(container.getAttributes().isInvulnerable());
		// update server data
		if (!this.level().isClientSide()) {
			// remove and re-instantiate goals
			this.goalSelector.getAvailableGoals().clear();
			this.registerGoals();
			// define behavior for the given swimming ability
			switch (container.getAttributes().getSwimAbility()) {
				case FLOAT:
					// basic swimming AI
					goalSelector.addGoal(0, new FloatGoal(this));
					break;
				case SWIM:
					// advanced swimming AI
					setMaxUpStep(1.0f);
					moveControl = new SwimmingMovementController(this);
					setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
					goalSelector.addGoal(1, new GoToWaterGoal(this, 14, 1.0D));
					goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8F, 200));
					goalSelector.addGoal(5, new SwimUpGoal(this, 1.0D, level().getSeaLevel() + 1));
					break;
				case SINK:
				default:
					// no swimming AI
					break;
			}
			// update pathfinding malus
			container.getAttributes().updatePathfinding(this);
			// update ground navigator
			this.groundNavigator.setAvoidSun(container.getBehaviors().hasBehavior(BurnInSunBehavior.class));

			// allow behaviors to register goals
			container.getBehaviors().forEach(b -> b.onRegisterGoals(this));
		}
	}

	public void clearCachedGolemContainer() {
		this.cachedContainer = null;
	}

	/** @return the {@link GolemContainer} that was created by ID **/
	public GolemContainer getGolemContainer() {
		return this.cachedContainer;
	}

	/** @return the current biome color (only updated client-side) **/
	public int getBiomeColor() {
		return biomeColor;
	}

	//// SYNCHED DATA ////

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(GOLEM, Optional.empty());
		this.getEntityData().define(CHILD, Boolean.FALSE);
		this.getEntityData().define(VARIANT, (byte) 0);
		this.getEntityData().define(FUEL, 0);
		this.getEntityData().define(ARROWS, 0);
	}

	@Override
	public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (GOLEM.equals(key)) {
			setGolemId(this.getEntityData().get(GOLEM).orElse(null));
		}
		Optional<GolemContainer> oContainer = getContainer();
		if (CHILD.equals(key)) {
			// recalculate size
			this.refreshDimensions();
			// process golem container
			if(oContainer.isPresent()) {
				final GolemContainer container = oContainer.get();
				if (this.isBaby()) {
					// truncate these values to one decimal place after reducing them from base values
					double childHealth = (Math.floor(container.getAttributes().getHealth() * 0.3D * 10D)) / 10D;
					double childAttack = (Math.floor(container.getAttributes().getAttack() * 0.6D * 10D)) / 10D;
					this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(childHealth);
					this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(childAttack);
					this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
					this.setMaxUpStep(0.6F);
				} else {
					// use full values for non-child entity
					this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(container.getAttributes().getHealth());
					this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(container.getAttributes().getAttack());
					this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(container.getAttributes().getKnockbackResistance());
					this.setMaxUpStep(1.0F);
				}
			}
		} else if (VARIANT.equals(key)) {
			this.setVariant((byte) this.getVariant());
		}
	}

	//// LIVING ENTITY ////

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevel, DifficultyInstance difficulty,
										MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
		this.setHealth(this.getMaxHealth());
		getContainer(serverLevel.registryAccess()).ifPresent(c -> this.setInvulnerable(c.getAttributes().isInvulnerable()));
		return super.finalizeSpawn(serverLevel, difficulty, mobSpawnType, spawnGroupData, tag);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
	}

	@Override
	public boolean canAttackType(final EntityType<?> type) {
		if (type == EntityType.PLAYER && this.isPlayerCreated()) {
			return ExtraGolems.CONFIG.enableFriendlyFire();
		}
		if (type == EntityType.VILLAGER || type == EGRegistry.EntityReg.GOLEM.get() || type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM) {
			return false;
		}
		return super.canAttackType(type);
	}

	@Override
	public ItemStack getPickedResult(final HitResult ray) {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return ItemStack.EMPTY;
		}
		final Collection<Block> blocks = oContainer.get().getGolem().getBlocks().get();
		if(blocks.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(blocks.toArray(new Block[blocks.size()])[0]);
	}

	@Override
	public float getLightLevelDependentMagicValue() {
		final int powerLevel = this.getPowerLevel();
		if(powerLevel > 0) {
			return powerLevel / 15.0F;
		}
		return super.getLightLevelDependentMagicValue();
	}

	@Override
	protected Component getTypeName() {
		final Optional<GolemContainer> container = getContainer(level().registryAccess());
		if(container.isPresent()) {
			return container.get().getTypeName();
		}
		return super.getTypeName();
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return super.getDefaultLootTable();
		}
		return oContainer.get().getLootTable();
	}

	@Override
	public boolean isBaby() {
		return this.getEntityData().get(CHILD).booleanValue();
	}

	/**
	 * Update whether this entity is 'child' and recalculate size
	 **/
	@Override
	public void setBaby(final boolean isChild) {
		if (this.getEntityData().get(CHILD).booleanValue() != isChild) {
			this.getEntityData().set(CHILD, Boolean.valueOf(isChild));
			this.refreshDimensions();
		}
	}

	@Override
	public boolean isSunBurnTick() {
		return super.isSunBurnTick();
	}

	//// LIGHT PROVIDER ////

	@Override
	public int getLightLevel() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return 0;
		}
		// collect light behaviors
		final List<LightBehavior> behaviors = oContainer.get().getBehaviors().getActiveBehaviors(LightBehavior.class, this);
		// determine max value from all collected light behaviors (defaults to zero if the list is empty)
		int max = 0;
		for(LightBehavior b : behaviors) {
			max = Math.max(max, b.getLightLevel());
		}
		return max;
	}

	//// POWER PROVIDER ////

	@Override
	public int getPowerLevel() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return 0;
		}
		// collect power behaviors
		final List<PowerBehavior> behaviors = oContainer.get().getBehaviors().getActiveBehaviors(PowerBehavior.class, this);
		// determine max value from all collected power behaviors (defaults to zero if the list is empty)
		int max = 0;
		for(PowerBehavior b : behaviors) {
			max = Math.max(max, b.getPowerLevel());
		}
		return max;
	}

	//// TICK ////

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		// process golem container
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return;
		}
		final GolemContainer container = oContainer.get();
		// process damage weakness
		final Registry<DamageType> registry = level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		// take damage from water
		if(this.isInWaterRainOrBubble() && container.getAttributes().isWeakTo(level().registryAccess(), DamageTypes.DROWN)) {
			this.hurt(this.damageSources().drown(), 1.0F);
		}
		// take damage from heat
		if(this.level().getBiome(this.blockPosition()).is(BiomeTags.SNOW_GOLEM_MELTS) && container.getAttributes().isWeakTo(level().registryAccess(), DamageTypes.ON_FIRE, DamageTypes.IN_FIRE)) {
			this.hurt(this.damageSources().onFire(), 1.0F);
		}
		// update behaviors
		container.getBehaviors().forEach(b -> b.onTick(this));
	}

	@Override
	public void tick() {
		super.tick();
		// client-side updates
		if (this.level().isClientSide()) {
			// update biome color
			if (this.tickCount % 15 == 1) {
				biomeColor = this.level().getBiome(this.blockPosition()).value().getFoliageColor();
			}
			// spawn fuse particles
			if (isFuseLit()) {
				this.level().addParticle(ParticleTypes.SMOKE, getX() + getRandom().nextDouble() - 0.5D, getY() + getEyeHeight(getPose()), getZ() + getRandom().nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);
			}
			// spawn golem container particles
			getContainer().ifPresent(container -> {
				final ParticleOptions options = container.getGolem().getParticle();
				if(options != null) {
					this.level().addParticle(options, getX() + getRandom().nextDouble() - 0.5D, getY() + (getRandom().nextDouble() * getEyeHeight()), getZ() + getRandom().nextDouble() - 0.5D,
							0.1D * (getRandom().nextDouble() - 0.5D), 0.1D * (getRandom().nextDouble() - 0.5D), 0.1D * (getRandom().nextDouble() - 0.5D));
				}
			});
		}
	}

	//// ATTRIBUTE HOOKS ////

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return super.causeFallDamage(distance, damageMultiplier, source);
		}
		// process weak to fall damage
		if(oContainer.get().getAttributes().isWeakTo(level().registryAccess(), DamageTypes.FALL)) {
			// this code is copied from the super.super.causeFallDamage method since IronGolem overrides it to do nothing
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
				this.hurt(this.damageSources().fall(), (float) i);
				return true;
			} else {
				return flag;
			}
		}
		return false;
	}

	@Override
	public boolean fireImmune() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return super.fireImmune();
		}
		return oContainer.get().getAttributes().isImmuneTo(level().registryAccess(), DamageTypes.IN_FIRE, DamageTypes.ON_FIRE);
	}

	@Override
	public boolean ignoreExplosion() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return super.ignoreExplosion();
		}
		return oContainer.get().getAttributes().isImmuneTo(level().registryAccess(), DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION);
	}

	@Override
	public boolean isSensitiveToWater() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return super.isSensitiveToWater();
		}
		return oContainer.get().getAttributes().isWeakTo(level().registryAccess(), DamageTypes.DROWN);
	}

	//// BEHAVIOR HOOKS ////

	@Override
	public boolean doHurtTarget(Entity target) {
		if (super.doHurtTarget(target)) {
			// process golem container
			getContainer().ifPresent(container -> {
				// use attack knockback stat
				final double knockback = container.getAttributes().getAttackKnockback();
				if (knockback > 0 && !isBaby()) {
					final Vec3 myPos = this.position();
					final Vec3 ePos = target.position();
					final double dX = Math.signum(ePos.x - myPos.x) * knockback;
					final double dZ = Math.signum(ePos.z - myPos.z) * knockback;
					target.setDeltaMovement(target.getDeltaMovement().add(dX, knockback / 2, dZ));
				}
				// allow behaviors to process doHurtTarget
				container.getBehaviors().forEach(b -> b.onHurtTarget(this, target));
			});
			return true;
		}
		return false;
	}

	@Override
	protected void actuallyHurt(DamageSource source, float amount) {
		super.actuallyHurt(source, amount);
		// allow behaviors to process actuallyHurt
		getContainer().ifPresent(container -> container.getBehaviors().forEach(b -> b.onActuallyHurt(this, source, amount)));
	}

	@Override
	public void thunderHit(ServerLevel pLevel, LightningBolt pLightning) {
		super.thunderHit(pLevel, pLightning);
		getContainer().ifPresent(container -> container.getBehaviors().forEach(b -> b.onStruckByLightning(this, pLightning)));
	}

	@Override
	public void die(final DamageSource source) {
		// allow behaviors to process die
		getContainer().ifPresent(container -> container.getBehaviors().forEach(b -> b.onDie(this, source)));
		super.die(source);
	}

	@Override
	protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		// Attempt to heal the entity
		final float repairAmount = getRepairAmount(stack);
		if (!stack.isEmpty() && repairAmount > 0 && processRepair(player, hand, stack, repairAmount)) {
			return InteractionResult.CONSUME;
		}
		// Allow behaviors to process mobInteract
		getContainer().ifPresent(container -> container.getBehaviors().forEach(b -> b.onMobInteract(this, player, hand)));
		return super.mobInteract(player, hand);
	}

	/**
	 * Called when the player uses an item that can repair this entity
	 *
	 * @param player     the player using the item
	 * @param hand       the player hand
	 * @param stack      the item being used
	 * @param repairAmount the amount of health this item will restore
	 * @return true if the item was consumed
	 */
	protected boolean processRepair(final Player player, final InteractionHand hand, final ItemStack stack, final float repairAmount) {
		if (ExtraGolems.CONFIG.enableHealGolems() && this.getHealth() < this.getMaxHealth()) {
			heal(repairAmount);
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
			SpawnGolemItem.spawnParticles(this.level(), pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.15D, ParticleTypes.INSTANT_EFFECT, 30);
			this.playSound(SoundEvents.STONE_PLACE, 0.85F, 1.1F + random.nextFloat() * 0.2F);
			return true;
		}
		return false;
	}

	/**
	 * @param itemStack the ItemStack being used to repair the entity
	 * @return the amount by which this item should repair the entity, in half-hearts, between 0 and 64.0
	 **/
	public float getRepairAmount(final ItemStack itemStack) {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return 0.0F;
		}
		float amount = (float) (this.getMaxHealth() * oContainer.get().getGolem().getRepairItems().getRepairAmount(itemStack));
		if (this.isBaby()) {
			amount *= 1.75F;
		}
		// max heal amount is 64, for no reason at all
		return Math.min(amount, 64.0F);
	}

	//// MENU ////

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
		return this.playerInMenu != null && this.position().closerThan(this.playerInMenu.position(), distance);
	}

	//// NBT ////

	@Override
	public void readAdditionalSaveData(final CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		// load golem ID
		if(tag.contains(KEY_GOLEM_ID, Tag.TAG_STRING)) {
			this.setGolemId(new ResourceLocation(tag.getString(KEY_GOLEM_ID)));
		}
		// load variant
		this.setVariant(tag.getByte(KEY_VARIANT));
		// load baby flag
		this.setBaby(tag.getBoolean(KEY_CHILD));
		// allow behaviors to process readData
		initArrowInventory();
		this.getContainer().ifPresent(container -> container.getBehaviors().forEach(b -> b.onReadData(this, tag)));
	}

	@Override
	public void addAdditionalSaveData(final CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		// save golem ID
		this.getGolemId().ifPresent(id -> tag.putString(KEY_GOLEM_ID, id.toString()));
		// save variant
		tag.putByte(KEY_VARIANT, (byte) this.getVariant());
		// save baby flag
		tag.putBoolean(KEY_CHILD, this.isBaby());
		// allow behaviors to process writeData
		this.getContainer().ifPresent(container -> container.getBehaviors().forEach(b -> b.onWriteData(this, tag)));
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		final Optional<ResourceLocation> oId = getGolemId();
		buffer.writeBoolean(oId.isPresent());
		oId.ifPresent(buffer::writeResourceLocation);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer) {
		boolean hasId = buffer.readBoolean();
		if(hasId) {
			ResourceLocation id = buffer.readResourceLocation();
			setGolemId(id);
			setHealth(getMaxHealth());
		}
	}

	//// SOUNDS ////

	@Override
	protected SoundEvent getAmbientSound() {
		return getSoundType().getStepSound();
	}

	@Override
	protected SoundEvent getHurtSound(final DamageSource ignored) {
		return getSoundType().getHitSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getSoundType().getBreakSound();
	}

	/**
	 * @return the SoundType of the golem
	 **/
	public final SoundType getSoundType() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return SoundType.STONE;
		}
		return oContainer.get().getAttributes().getSoundType();
	}

	//// VARIANT PROVIDER ////

	@Override
	public int getVariantCount() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return 1;
		}
		return oContainer.get().getGolem().getVariants();
	}

	@Override
	public void setVariant(int variant) {
		if (variant >= 0) {
			this.getEntityData().set(VARIANT, (byte)variant);
		}
	}

	@Override
	public int getVariant() {
		return this.getEntityData().get(VARIANT);
	}

	//// FUEL CONSUMER ////

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
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return 0;
		}
		final List<UseFuelBehavior> behaviors = oContainer.get().getBehaviors().getActiveBehaviors(UseFuelBehavior.class, this);
		if(behaviors.isEmpty()) {
			return 0;
		}
		return behaviors.get(0).getMaxFuel();
	}

	//// RANDOM EXPLODER ////

	@Override
	public int getMinFuse() {
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return 0;
		}
		final List<ExplodeBehavior> behaviors = oContainer.get().getBehaviors().getActiveBehaviors(ExplodeBehavior.class, this);
		if(behaviors.isEmpty()) {
			return 0;
		}
		return behaviors.get(0).getMinFuse();
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
		this.fuseLit = true;
	}

	@Override
	public boolean isFuseLit() {
		return this.fuseLit || this.getFuse() < getMinFuse();
	}

	//// ARROW SHOOTER ////

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
		this.isInventoryDirty = true;
	}

	@Override
	public double getArrowDamage() {
		// resolve container
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return 0;
		}
		// resolve behaviors
		final List<ShootArrowsBehavior> behaviors = oContainer.get().getBehaviors().getActiveBehaviors(ShootArrowsBehavior.class, this);
		if(behaviors.isEmpty()) {
			return 0;
		}
		// resolve damage from first behavior in the list
		double damage = behaviors.get(0).getDamage();
		// multiply damage by 0.5 when baby
		if(isBaby()) {
			damage *= 0.5D;
		}
		return damage;
	}

	@Override
	public boolean wantsToPickUp(ItemStack stack) {
		if(stack.isEmpty() || !(stack.getItem() instanceof ArrowItem)) {
			return false;
		}
		// resolve container
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty()) {
			return false;
		}
		// resolve behaviors
		if(!oContainer.get().getBehaviors().hasActiveBehavior(ShootArrowsBehavior.class, this)) {
			return false;
		}
		// validate inventory
		return getInventory().canAddItem(stack);
	}

	@Override
	public SimpleContainer getInventory() {
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
		Containers.dropContents(this.level(), this.blockPosition(), this.inventory);
	}

	@Override
	protected void pickUpItem(ItemEntity item) {
		InventoryCarrier.pickUpItem(this, this, item);
	}

	@Override
	public void onItemPickup(ItemEntity itemEntity) {
		super.onItemPickup(itemEntity);
		containerChanged(getInventory());
	}

	@Override
	public boolean canHoldItem(ItemStack item) {
		return false;
	}

	@Override
	public void containerChanged(Container container) {
		if(container == this.inventory) {
			this.isInventoryDirty = true;
		}
	}

	@Override
	public boolean isInventoryChanged() {
		return this.isInventoryDirty;
	}

	@Override
	public void resetInventoryChanged() {
		this.isInventoryDirty = false;
	}

	//// SWIMMING ////

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
		final Optional<GolemContainer> oContainer = getContainer();
		if(oContainer.isEmpty() || oContainer.get().getAttributes().getSwimAbility() != SwimAbility.SWIM) {
			super.updateSwimming();
			return;
		}
		// update navigation
		if (!this.level().isClientSide) {
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
	public boolean isPushedByFluid() {
		return !isSwimming();
	}

	public void setSwimmingUp(boolean isSwimmingUp) {
		final Optional<GolemContainer> oContainer = getContainer();
		final boolean canSwim = oContainer.isPresent() && oContainer.get().getAttributes().getSwimAbility() == SwimAbility.SWIM;
		this.swimmingUp = canSwim && isSwimmingUp;
	}

	public boolean isSwimmingUp() {
		final Optional<GolemContainer> oContainer = getContainer();
		final boolean canSwim = oContainer.isPresent() && oContainer.get().getAttributes().getSwimAbility() == SwimAbility.SWIM;
		if (!canSwim) {
			return false;
		}
		if (this.swimmingUp) {
			return true;
		}
		LivingEntity e = getTarget();
		return e != null && e.isInWater();
	}

	//// MOVE CONTROLLER ////

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
				if (!this.golem.onGround()) {
					this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
				}

				super.tick();
			}

		}
	}
}
