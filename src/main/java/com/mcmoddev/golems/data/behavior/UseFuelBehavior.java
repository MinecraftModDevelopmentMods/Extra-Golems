package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.InertGoal;
import com.mcmoddev.golems.entity.goal.LookAtWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.LookRandomlyWhenActiveGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

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

	/** The maximum amount of fuel the entity can hold **/
	protected final int maxFuel;
	/** The number of ticks it takes to deplete one unit of fuel **/
	protected final int burnTime;

	public UseFuelBehavior(MinMaxBounds.Ints variant, int maxFuel, int burnTime) {
		super(variant);
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
	public void onRegisterGoals(final IExtraGolem entity) {
		final Mob mob = entity.asMob();
		removeGoal(mob, LookAtPlayerGoal.class);
		removeGoal(mob, RandomLookAroundGoal.class);
		// TODO adjust goals to account for texture variant
		mob.goalSelector.addGoal(0, new InertGoal<>(entity));
		mob.goalSelector.addGoal(7, new LookAtWhenActiveGoal<>(entity, Player.class, 6.0F));
		mob.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal<>(entity));
	}

	@Override
	public void onTick(IExtraGolem entity) {
		if(entity.hasFuel() && entity.asMob().tickCount % burnTime == 0) {
			entity.addFuel(-1);
		}
	}

	@Override
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) {
		if (!player.isCrouching() && !player.getItemInHand(hand).isEmpty()) {
			processInteractFuel(entity, player, hand);
		}
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.use_fuel").withStyle(ChatFormatting.GRAY));
	}

	//// NBT ////

	private static final String KEY_FUEL = "Fuel";

	@Override
	public void onWriteData(final IExtraGolem entity, final CompoundTag tag) {
		tag.putInt(KEY_FUEL, entity.getFuel());
	}

	@Override
	public void onReadData(final IExtraGolem entity, final CompoundTag tag) {
		entity.setFuel(tag.getInt(KEY_FUEL));
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
		ItemStack stack = player.getItemInHand(hand);
		int burnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * (player.isCrouching() ? stack.getCount() : 1);
		if (burnTime > 0 && (entity.getFuel() + burnTime) <= getMaxFuel()) {
			if (player.isCrouching()) {
				// take entire ItemStack
				entity.addFuel(burnTime * stack.getCount());
				stack = stack.getCraftingRemainingItem();
			} else {
				// take one item from ItemStack
				entity.addFuel(burnTime);
				if (stack.getCount() > 1) {
					stack.shrink(1);
				} else {
					stack = stack.getCraftingRemainingItem();
				}
			}
			// update the player's held item
			player.setItemInHand(hand, stack);
			// TODO spawn particles
			return true;
		}

		// allow player to remove burn time by using a water bucket
		if (stack.getItem() == Items.WATER_BUCKET) {
			entity.setFuel(0);
			player.setItemInHand(hand, stack.getCraftingRemainingItem());
			// TODO spawn particles, play extinguish sound
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
