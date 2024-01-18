package com.mcmoddev.golems.container.behavior;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.container.behavior.parameter.ChangeMaterialBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.ResourcePair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * This behavior allows an entity to change its material (container)
 * when an item is used on the entity.
 **/
@Immutable
public class ChangeMaterialBehavior extends GolemBehavior {

	/**
	 * The material parameter to change per-tick
	 */
	private final ChangeMaterialBehaviorParameter tickMaterial;
	/**
	 * The material parameter to change when the entity is wet
	 */
	private final ChangeMaterialBehaviorParameter wetMaterial;
	/**
	 * The material parameter to change when the entity is dry
	 */
	private final ChangeMaterialBehaviorParameter dryMaterial;
	/**
	 * The material parameter to change when the entity has fuel. Only used when useFuelBehavior is present
	 */
	private final ChangeMaterialBehaviorParameter fueledMaterial;
	/**
	 * The material parameter to change when the entity has no fuel. Only used when UseFuelBehavior is present
	 */
	private final ChangeMaterialBehaviorParameter emptyMaterial;
	/**
	 * A map of item IDs and tags to change material when the item is used on the entity
	 */
	private final Map<ResourcePair, ChangeMaterialBehaviorParameter> itemParameters;

	public ChangeMaterialBehavior(final CompoundTag tag) {
		super(tag);
		tickMaterial = tag.contains("tick") ? new ChangeMaterialBehaviorParameter(tag.getCompound("tick")) : null;
		wetMaterial = tag.contains("wet") ? new ChangeMaterialBehaviorParameter(tag.getCompound("wet")) : null;
		dryMaterial = tag.contains("dry") ? new ChangeMaterialBehaviorParameter(tag.getCompound("dry")) : null;
		fueledMaterial = tag.contains("fuel") ? new ChangeMaterialBehaviorParameter(tag.getCompound("fuel")) : null;
		emptyMaterial = tag.contains("fuel_empty") ? new ChangeMaterialBehaviorParameter(tag.getCompound("fuel_empty")) : null;

		itemParameters = ImmutableMap.copyOf(readParameters(tag.getCompound("items"), ChangeMaterialBehaviorParameter::new));
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		entity.goalSelector.addGoal(1, new ChangeMaterialGoal<>(entity, tickMaterial, wetMaterial, dryMaterial, fueledMaterial, emptyMaterial));
	}

	@Override
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
		// determine held item
		ItemStack item = player.getItemInHand(hand);
		// attempt to find the item ID in the map
		ResourcePair itemId = new ResourcePair(ForgeRegistries.ITEMS.getKey(item.getItem()), false);
		if (itemParameters.containsKey(itemId) && apply(entity, player, hand, itemParameters.get(itemId))) {
			return;
		}
		// attempt to find the item tag in the item
		for (Map.Entry<ResourcePair, ChangeMaterialBehaviorParameter> entry : itemParameters.entrySet()) {
			// skip resources that are not tags
			if (!entry.getKey().flag()) {
				continue;
			}
			// check if item contains the given tag
			TagKey<Item> tagKey = ForgeRegistries.ITEMS.tags().createTagKey(entry.getKey().resource());
			if (item.is(tagKey) && apply(entity, player, hand, entry.getValue())) {
				return;
			}
		}
	}

	/**
	 * Attempts to change the material of the given entity, with a random chance
	 *
	 * @param entity            the entity
	 * @param behaviorParameter the change material parameter
	 * @return true if the material was changed
	 */
	private boolean apply(final GolemBase entity, final Player player, final InteractionHand hand, final ChangeMaterialBehaviorParameter behaviorParameter) {
		if (entity.getRandom().nextFloat() < behaviorParameter.getChance()) {
			entity.setMaterial(behaviorParameter.getMaterial());
			sendParticles(entity, ParticleTypes.INSTANT_EFFECT, 12);
			player.swing(hand);
			return true;
		}
		return false;
	}
}
