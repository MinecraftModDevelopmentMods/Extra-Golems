package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.container.behavior.parameter.ChangeMaterialBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.concurrent.Immutable;
import java.util.Map;
import java.util.Optional;


/**
 * This behavior allows an entity to change its container
 * when an item is used on the entity or when it ticks based on several conditions
 **/
@Immutable
public class ItemUpdateGolemBehavior extends Behavior<GolemBase> {

	public static final Codec<ItemUpdateGolemBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(UpdateOnUseItem.CODEC.fieldOf("target").forGetter(ItemUpdateGolemBehavior::getUpdateOnUseItem))
			.apply(instance, ItemUpdateGolemBehavior::new));

	/** The golem variant combo to apply when using an item **/
	private final UpdateOnUseItem updateOnUseItem;

	public ItemUpdateGolemBehavior(MinMaxBounds.Ints variant, UpdateOnUseItem updateOnUseItem) {
		super(variant);
		this.updateOnUseItem = updateOnUseItem;
	}

	//// GETTERS ////


	public UpdateOnUseItem getUpdateOnUseItem() {
		return updateOnUseItem;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.ITEM_UPDATE_GOLEM.get();
	}

	//// METHODS ////

	@Override
	public void onMobInteract(GolemBase entity, Player player, InteractionHand hand) {
		if(!canApply(entity)) {
			return;
		}
		// determine held item
		ItemStack item = player.getItemInHand(hand);
		// load holder set
		final HolderSet<Item> holderSet = updateOnUseItem.getItems().get(BuiltInRegistries.ITEM);
		if(holderSet.contains(item.getItemHolder()) && entity.getRandom().nextDouble() < updateOnUseItem.getChance()) {
			if(updateOnUseItem.update(entity)) {
				player.swing(hand);
			}
		}
	}


	//// CLASSES ////

	public static class UpdateOnUseItem extends GolemVariantCombo {

		public static final Codec<UpdateOnUseItem> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
				.and(DeferredHolderSet.codec(BuiltInRegistries.ITEM.key()).fieldOf("item").forGetter(UpdateOnUseItem::getItems))
				.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(UpdateOnUseItem::getChance))
				.apply(instance, UpdateOnUseItem::new));

		/** The items associated with the update **/
		private final DeferredHolderSet<Item> items;
		/** The percent chance **/
		private final double chance;

		public UpdateOnUseItem(Optional<ResourceLocation> golem, Optional<Integer> variant, DeferredHolderSet<Item> items, double chance) {
			super(golem, variant);
			this.items = items;
			this.chance = chance;
		}

		//// GETTERS ////

		public DeferredHolderSet<Item> getItems() {
			return items;
		}

		public double getChance() {
			return chance;
		}
	}
}
