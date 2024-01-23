package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.data.IBehaviorData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public interface IExtraGolem extends IVariantProvider, ILightProvider, IPowerProvider,
		IInventoryProvider, ContainerListener, RangedAttackMob, IEntityAdditionalSpawnData {

	public static final EntityDataSerializer<Optional<ResourceLocation>> OPTIONAL_RESOURCE_LOCATION = EntityDataSerializer.optional(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);

	//// ENTITY ////

	/**
	 * @param <T> AbstractGolem Mob & IExtraGolem
	 * @return the IExtraGolem as a mob
	 */
	<T extends AbstractGolem & IExtraGolem> T asMob();

	/** Set up the inventory **/
	void setupInventory();

	//// CONTAINER ////

	/**
	 * @param id the axolootl variant ID, can be null
	 */
	void setGolemId(@Nullable final ResourceLocation id);

	/**
	 * @return the axolootl variant ID, if any is defined
	 */
	Optional<ResourceLocation> getGolemId();

	/**
	 * @param registryAccess the registry access
	 * @return the cached golem container, if any
	 */
	default Optional<GolemContainer> getContainer(final RegistryAccess registryAccess) {
		// load variant ID
		final Optional<ResourceLocation> oId = getGolemId();
		if(oId.isEmpty()) {
			return Optional.empty();
		}
		// all checks passed
		return Optional.of(GolemContainer.getOrCreate(registryAccess, oId.get()));
	}

	//// GOLEM HELPER ////

	Map<Class<? extends IBehaviorData>, IBehaviorData> getBehaviorData();

	default <T extends IBehaviorData> void attachBehaviorData(final T data) {
		getBehaviorData().put(data.getClass(), data);
	}

	default <T extends IBehaviorData> Optional<T> getBehaviorData(final Class<T> clazz) {
		return Optional.ofNullable((T)getBehaviorData().get(clazz));
	}

	//// GOLEM ////

	boolean isSunBurnTick();

	/**
	 * Called after construction when a entity is built by a player
	 *
	 * @param body the body block
	 * @param legs the legs block
	 * @param arm1 the first arm block
	 * @param arm2 the second arm block
	 * @param player the player who built the entity, if any
	 */
	default void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2, @Nullable Entity player) {}

	//// NBT ////

	public static final String KEY_GOLEM_ID = "Golem";

	default void writeContainer(CompoundTag pCompound) {
		getGolemId().ifPresent(id -> pCompound.putString(KEY_GOLEM_ID, id.toString()));
	}

	default void readContainer(CompoundTag pCompound) {
		// read golem ID
		if(pCompound.contains(KEY_GOLEM_ID, Tag.TAG_STRING)) {
			setGolemId(new ResourceLocation(pCompound.getString(KEY_GOLEM_ID)));
		}
		// read legacy golem ID
		if(pCompound.contains("Material", Tag.TAG_STRING)) {
			setGolemId(new ResourceLocation(pCompound.getString("Material")));
		}
	}
}
