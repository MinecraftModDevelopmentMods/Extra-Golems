package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.InertGoal;
import com.mcmoddev.golems.entity.goal.LookAtWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.LookRandomlyWhenActiveGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This behavior allows an entity to use fuel, accept fuel items,
 * only move and attack while fueled, and save/load fuel
 **/
@Immutable
public class UseFuelBehavior extends Behavior {

	public static final Codec<UseFuelBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(1, Integer.MAX_VALUE).fieldOf("max_fuel").forGetter(UseFuelBehavior::getMaxFuel))
			.and(Codec.intRange(1, Integer.MAX_VALUE).fieldOf("burn_time").forGetter(UseFuelBehavior::getBurnTime))
			.apply(instance, UseFuelBehavior::new));

	protected static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(AbstractGolem.class, EntityDataSerializers.INT);

	/** The maximum amount of fuel the entity can hold **/
	protected final int maxFuel;
	/** The number of ticks it takes to deplete one unit of fuel **/
	protected final int burnTime;

	public UseFuelBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, int maxFuel, int burnTime) {
		super(variant, tooltipPredicate);
		this.maxFuel = maxFuel;
		this.burnTime = burnTime;
	}

	//// GETTERS ////

	public int getMaxFuel() {
		return maxFuel;
	}

	public int getBurnTime() {
		return burnTime;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.USE_FUEL.get();
	}

	//// METHODS ////

	@Override
	public void onAttachData(IExtraGolem entity) {
		entity.attachBehaviorData(new UseFuelBehaviorData(entity, FUEL));
	}

	@Override
	public void onRegisterSynchedData(IExtraGolem entity) {
		defineSynchedData(entity.asMob().getEntityData(), FUEL, 0);
	}

	@Override
	public void onRegisterGoals(final IExtraGolem entity) {
		final Mob mob = entity.asMob();
		// remove look goals
		removeGoal(mob, LookAtPlayerGoal.class);
		removeGoal(mob, RandomLookAroundGoal.class);
		// add inert goal
		mob.goalSelector.addGoal(0, new InertGoal(entity, getVariantBounds()));
		// add custom look goals
		mob.goalSelector.addGoal(7, new LookAtWhenActiveGoal(entity, Player.class, 6.0F, getVariantBounds()));
		mob.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal(entity, getVariantBounds()));
	}

	@Override
	public void onTick(IExtraGolem entity) {
		final Optional<UseFuelBehaviorData> oHelper = entity.getBehaviorData(UseFuelBehaviorData.class);
		if(oHelper.isPresent() && oHelper.get().hasFuel() && entity.asMob().tickCount % oHelper.get().getBurnTime() == 0) {
			oHelper.get().addFuel(-1);
		}
	}

	@Override
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) {
		if (!player.isCrouching() && !player.getItemInHand(hand).isEmpty()) {
			processInteractFuel(entity, player, hand);
		}
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		return ImmutableList.of(Component.translatable(PREFIX + "use_fuel").withStyle(ChatFormatting.GRAY));
	}

	//// NBT ////

	private static final String KEY_FUEL_HELPER = "FuelData";

	@Override
	public void onWriteData(final IExtraGolem entity, final CompoundTag tag) {
		entity.getBehaviorData(UseFuelBehaviorData.class).ifPresent(helper -> tag.put(KEY_FUEL_HELPER, helper.serializeNBT()));
	}

	@Override
	public void onReadData(final IExtraGolem entity, final CompoundTag tag) {
		entity.getBehaviorData(UseFuelBehaviorData.class).ifPresent(helper -> helper.deserializeNBT(tag.getCompound(KEY_FUEL_HELPER)));
	}

	//// HELPER METHODS ////

	/**
	 * Attempts to consume the player held item and add fuel. Also handles water buckets causing fuel reset.
	 *
	 * @param entity the Golem entity
	 * @param player the player
	 * @param hand   the player hand
	 * @return true if the held item was processed and the fuel changed
	 */
	protected boolean processInteractFuel(final IExtraGolem entity, final Player player, final InteractionHand hand) {
		// allow player to add fuel to the entity by clicking on them with a fuel item
		final Optional<UseFuelBehaviorData> oData = entity.getBehaviorData(UseFuelBehaviorData.class);
		if(oData.isEmpty()) {
			return false;
		}
		final UseFuelBehaviorData data = oData.get();
		ItemStack stack = player.getItemInHand(hand);
		final Mob mob = entity.asMob();
		int burnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * (player.isCrouching() ? stack.getCount() : 1);
		if (burnTime > 0 && (data.getFuel() + burnTime) <= getMaxFuel()) {
			if (player.isCrouching()) {
				// take entire ItemStack
				data.addFuel(burnTime * stack.getCount());
				stack = stack.getCraftingRemainingItem();
			} else {
				// take one item from ItemStack
				data.addFuel(burnTime);
				if (stack.getCount() > 1) {
					stack.shrink(1);
				} else {
					stack = stack.getCraftingRemainingItem();
				}
			}
			// update the player's held item
			player.setItemInHand(hand, stack);
			// play sound and send particles
			mob.playSound(SoundEvents.GENERIC_BURN, 1.0F, 0.8F + 0.4F * mob.getRandom().nextFloat());
			((ServerLevel)mob.level()).sendParticles(ParticleTypes.FLAME, mob.getX(), mob.getY(0.6D), mob.getZ(), 6, 0.5D, 0.25D, 0.5D, 0);
			return true;
		}

		// allow player to remove burn time by using a water bucket
		if (stack.getItem() == Items.WATER_BUCKET) {
			data.setFuel(0);
			player.setItemInHand(hand, stack.getCraftingRemainingItem());
			// play sound and send particles
			mob.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 0.8F + 0.4F * mob.getRandom().nextFloat());
			((ServerLevel)mob.level()).sendParticles(ParticleTypes.SMOKE, mob.getX(), mob.getY(0.6D), mob.getZ(), 6, 0.5D, 0.25D, 0.5D, 0);
			return true;
		}
		// no changes
		return false;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UseFuelBehavior)) return false;
		if (!super.equals(o)) return false;
		UseFuelBehavior that = (UseFuelBehavior) o;
		return maxFuel == that.maxFuel && burnTime == that.burnTime;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), maxFuel, burnTime);
	}
}
