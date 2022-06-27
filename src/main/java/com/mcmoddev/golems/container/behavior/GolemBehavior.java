package com.mcmoddev.golems.container.behavior;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.ResourcePair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This is the base class for NBT-based behaviors.
 * Each GolemContainer has a set of behaviors that is
 * read from a CompoundTag. Each behavior can access
 * the entity at key points such as goal registration,
 * mob interaction, entity death, and entity NBT read/write.
 * The GolemBehavior does not change after loading its
 * initial parameters.
 * The GolemBehavior may make use of a BehaviorParameter
 * to easily load complicated settings from NBT.
 */
@Immutable
public abstract class GolemBehavior {

	/**
	 * The description text for an entity that lights enemies on fire
	 **/
	protected static final MutableComponent FIRE_DESC = new TranslatableComponent("entitytip.lights_mobs_on_fire").withStyle(ChatFormatting.GOLD);
	/**
	 * The description text for an entity that applies potion effects to itself
	 **/
	protected static final MutableComponent EFFECTS_SELF_DESC = new TranslatableComponent("entitytip.potion_effects_self").withStyle(ChatFormatting.LIGHT_PURPLE);
	/**
	 * The description text for an entity that applies potion effects its enemy
	 **/
	protected static final MutableComponent EFFECTS_ENEMY_DESC = new TranslatableComponent("entitytip.potion_effects_enemy").withStyle(ChatFormatting.LIGHT_PURPLE);

	protected final CompoundTag tag;

	public GolemBehavior(final CompoundTag tag) {
		this.tag = tag;
	}

	/**
	 * Called when the Golem registers goals
	 *
	 * @param entity the Golem
	 */
	public void onRegisterGoals(final GolemBase entity) {

	}

	/**
	 * Called when the Golem hurts an entity
	 *
	 * @param entity the Golem
	 * @param target the entity that was hurt
	 */
	public void onHurtTarget(final GolemBase entity, final Entity target) {

	}

	/**
	 * Called when the Golem is hurt
	 *
	 * @param entity the Golem
	 * @param source the source of the damage
	 * @param amount the amount of damage
	 */
	public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {

	}

	/**
	 * Called when a player interacts and the item was not a banner or heal item
	 *
	 * @param entity the Golem
	 * @param player the Player
	 * @param hand   the Player's hand
	 */
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {

	}

	/**
	 * Called when the Golem dies, before it is marked as removed
	 *
	 * @param entity the Golem
	 * @param source the DamageSource that killed the Golem
	 */
	public void onDie(final GolemBase entity, final DamageSource source) {

	}

	/**
	 * Called after reading additional data from NBT
	 *
	 * @param entity the Golem
	 * @param tag    the Golem NBT tag
	 */
	public void onWriteData(final GolemBase entity, final CompoundTag tag) {

	}

	/**
	 * Called after writing additional data to NBT
	 *
	 * @param entity the Golem
	 * @param tag    the Golem NBT tag
	 */
	public void onReadData(final GolemBase entity, final CompoundTag tag) {

	}

	/**
	 * Called when building the Golem Info Book to add descriptions
	 *
	 * @param list the current description list
	 */
	public void onAddDescriptions(List<Component> list) {

	}

	/**
	 * Attempts to send a particle packet from the server
	 * @param entity the entity
	 * @param particle the particle
	 * @param count the number of particles
	 */
	public static void sendParticles(final Entity entity, final ParticleOptions particle, final int count) {
		Vec3 pos = entity.position().add(0, entity.getBbHeight() * 0.5D, 0);
		if(entity.level instanceof ServerLevel) {
			((ServerLevel)entity.level).sendParticles(particle, pos.x, pos.y, pos.z, count, 0.5D, 0.5D, 0.5D, 0);
		}
	}

	/**
	 * Parses a compound tag that contains resource IDs or tags as keys,
	 * and behavior parameters as values
	 * @param items the compound tag
	 * @return a map of resource pairs to behavior parameters, may be empty
	 */
	protected static <T extends BehaviorParameter> Map<ResourcePair, T> readParameters(final CompoundTag items, Function<CompoundTag, T> constructor) {
		final ImmutableMap.Builder<ResourcePair, T> builder = new ImmutableMap.Builder<>();
		// iterate through all entries
		for(String resource : items.getAllKeys()) {
			// read resource pair (supports ID or tag)
			Optional<ResourcePair> pair = ResourcePair.read(resource).resultOrPartial(s ->
					ExtraGolems.LOGGER.error("Failed to parse item ID or tag from " + s));
			// read the behavior parameter from the compound
			if(pair.isPresent()) {
				CompoundTag paramTag = items.getCompound(resource);
				T param = constructor.apply(paramTag);
				// add behavior parameter to the map
				builder.put(pair.get(), param);
			}
		}
		// build the map
		return builder.build();
	}
}
