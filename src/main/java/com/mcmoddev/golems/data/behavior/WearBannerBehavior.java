package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;

import javax.annotation.concurrent.Immutable;

/**
 * This behavior allows an entity to wear or remove a banner in the chest slot
 **/
@Immutable
public class WearBannerBehavior extends Behavior {

	public static final WearBannerBehavior ANY = new WearBannerBehavior(MinMaxBounds.Ints.ANY, TooltipPredicate.HIDDEN);

	public static final Codec<WearBannerBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.apply(instance, WearBannerBehavior::new));

	public WearBannerBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate) {
		super(variant, tooltipPredicate);
	}

	//// GETTERS ////

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.WEAR_BANNER.get();
	}

	//// METHODS ////

	@Override
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) {
		final Mob mob = entity.asMob();
		final ItemStack stack = player.getItemInHand(hand);
		// Attempt to remove banner from the entity
		final ItemStack banner = mob.getItemBySlot(EquipmentSlot.CHEST);
		if (!banner.isEmpty() && stack.getItem() instanceof ShearsItem) {
			mob.spawnAtLocation(banner, mob.getBbHeight() * 0.9F);
			mob.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
		}
		// Attempt to place a banner on the entity
		if (stack.is(ItemTags.BANNERS)) {
			mob.setItemSlot(EquipmentSlot.CHEST, stack.split(1));
			mob.setDropChance(EquipmentSlot.CHEST, 1.0F);
		}
	}
}
