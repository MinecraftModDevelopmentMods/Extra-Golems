package com.mcmoddev.golems.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

public interface IFuelConsumer {

	String KEY_FUEL = "Fuel";

	/**
	 * @param fuel the new amount of fuel
	 **/
	void setFuel(final int fuel);

	/**
	 * @return the amount of fuel remaining
	 **/
	int getFuel();

	/**
	 * @return the maximum amount of fuel
	 **/
	int getMaxFuel();

	/**
	 * @param tag the CompoundTag to write to
	 **/
	default void saveFuel(final CompoundTag tag) {
		tag.putInt(KEY_FUEL, getFuel());
	}

	/**
	 * @param tag the CompoundTag to read from
	 **/
	default void loadFuel(final CompoundTag tag) {
		setFuel(tag.getInt(KEY_FUEL));
	}

	/**
	 * @return true if the fuel level is above zero
	 **/
	default boolean hasFuel() {
		return getFuel() > 0;
	}

	/**
	 * @return a number between 0.0 and 1.0 to indicate fuel level
	 **/
	default float getFuelPercentage() {
		return (float) getFuel() / (float) getMaxFuel();
	}

	/**
	 * @param toAdd the amount of fuel to add or subtract
	 **/
	default void addFuel(final int toAdd) {
		if (toAdd != 0) {
			setFuel(getFuel() + toAdd);
		}
	}

	/**
	 * Called on mob interaction. Attempts to consume the player's
	 * current item and add fuel. Also handles water buckets causing
	 * fuel reset.
	 *
	 * @param player the player
	 * @param hand   the player's hand
	 */
	default void consumeFuel(final Player player, final InteractionHand hand) {
		// allow player to add fuel to the entity by clicking on them with a fuel item
		ItemStack stack = player.getItemInHand(hand);
		int burnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * (player.isCrouching() ? stack.getCount() : 1);
		if (burnTime > 0 && (getFuel() + burnTime) <= getMaxFuel()) {
			if (player.isCrouching()) {
				// take entire ItemStack
				this.addFuel(burnTime * stack.getCount());
				stack = stack.getCraftingRemainingItem();
			} else {
				// take one item from ItemStack
				this.addFuel(burnTime);
				if (stack.getCount() > 1) {
					stack.shrink(1);
				} else {
					stack = stack.getCraftingRemainingItem();
				}
			}
			// update the player's held item
			player.setItemInHand(hand, stack);
			// add particles
//      ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.03D, ParticleTypes.FLAME, 10);
//      return InteractionResult.CONSUME;
		}

		// allow player to remove burn time by using a water bucket
		if (stack.getItem() == Items.WATER_BUCKET) {
			this.setFuel(0);
			player.setItemInHand(hand, stack.getCraftingRemainingItem());
//      ItemBedrockGolem.spawnParticles(this.level, pos.x, pos.y + this.getBbHeight() / 2.0D, pos.z, 0.1D, ParticleTypes.LARGE_SMOKE, 15);
//      return InteractionResult.CONSUME;
		}
	}
}
