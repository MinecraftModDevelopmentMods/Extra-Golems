package com.mcmoddev.golems.container.behavior;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.container.behavior.parameter.ChangeTexturesBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.ChangeTextureGoal;
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
 * This behavior allows an entity to change its texture based on
 * several conditions, such as a random chance each tick or while
 * wet, dry, fueled, or empty of fuel.
 **/
@Immutable
public class ChangeTextureBehavior extends GolemBehavior {

	/**
	 * The map to use for per-tick changes. Accepts texture ID as string only
	 **/
	private final ChangeTexturesBehaviorParameter tickTextures;

	/**
	 * The map to use when the entity is wet
	 **/
	private final ChangeTexturesBehaviorParameter wetTextures;
	/**
	 * The map to use when the entity is dry
	 **/
	private final ChangeTexturesBehaviorParameter dryTextures;

	/**
	 * The map to use when the entity has fuel. Only used when UseFuelBehavior is present
	 **/
	private final ChangeTexturesBehaviorParameter fueledTextures;
	/**
	 * The map to use when the entity has no fuel. Only used when UseFuelBehavior is present
	 **/
	private final ChangeTexturesBehaviorParameter emptyTextures;
	/**
	 * A map of item IDs and tags to change texture when the item is used on the entity
	 */
	private final Map<ResourcePair, ChangeTexturesBehaviorParameter> itemParameters;

	public ChangeTextureBehavior(final CompoundTag tag) {
		super(tag);
		tickTextures = tag.contains("tick") ? new ChangeTexturesBehaviorParameter(tag.getCompound("tick")) : null;
		wetTextures = tag.contains("wet") ? new ChangeTexturesBehaviorParameter(tag.getCompound("wet")) : null;
		dryTextures = tag.contains("dry") ? new ChangeTexturesBehaviorParameter(tag.getCompound("dry")) : null;
		fueledTextures = tag.contains("fuel") ? new ChangeTexturesBehaviorParameter(tag.getCompound("fuel")) : null;
		emptyTextures = tag.contains("fuel_empty") ? new ChangeTexturesBehaviorParameter(tag.getCompound("fuel_empty")) : null;

		itemParameters = ImmutableMap.copyOf(readParameters(tag.getCompound("items"), ChangeTexturesBehaviorParameter::new));
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		if (entity.getContainer().getMultitexture().isPresent()) {
			entity.goalSelector.addGoal(1, new ChangeTextureGoal<>(entity, tickTextures, wetTextures, dryTextures, fueledTextures, emptyTextures));
		}
	}

	@Override
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
		// determine held item and current texture
		ItemStack item = player.getItemInHand(hand);
		int textureId = entity.getTextureId();
		// attempt to find the item ID in the map
		ResourcePair itemId = new ResourcePair(ForgeRegistries.ITEMS.getKey(item.getItem()), false);
		if (itemParameters.containsKey(itemId) && apply(entity, player, hand, itemParameters.get(itemId), textureId)) {
			return;
		}
		// attempt to find the item tag in the item
		for (Map.Entry<ResourcePair, ChangeTexturesBehaviorParameter> entry : itemParameters.entrySet()) {
			// skip resources that are not tags
			if (!entry.getKey().flag()) {
				continue;
			}
			// check if item contains the given tag
			TagKey<Item> tagKey = ForgeRegistries.ITEMS.tags().createTagKey(entry.getKey().resource());
			if (item.is(tagKey) && apply(entity, player, hand, entry.getValue(), textureId)) {
				return;
			}
		}
	}

	/**
	 * Attempts to change the texture of the given entity, with a random chance
	 *
	 * @param entity    the entity
	 * @param param     the change texture parameter
	 * @param textureId the current texture ID
	 * @return true if the texture was changed
	 */
	private boolean apply(final GolemBase entity, final Player player, final InteractionHand hand, final ChangeTexturesBehaviorParameter param, final int textureId) {
		if (entity.getRandom().nextFloat() < param.getChance()) {
			// determine next texture and update the entity
			int updateTextureId = param.getTextureId(String.valueOf(textureId), textureId);
			if (updateTextureId != textureId) {
				entity.setTextureId((byte) updateTextureId);
				sendParticles(entity, ParticleTypes.INSTANT_EFFECT, 12);
				player.swing(hand);
				return true;
			}
		}
		return false;
	}
}
