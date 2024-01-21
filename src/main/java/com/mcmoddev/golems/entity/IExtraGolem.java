package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.data.GolemContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IExtraGolem extends IVariantProvider, IFuelConsumer, IArrowShooter,
		IRandomExploder, ILightProvider, IPowerProvider, IMenuProvider,
		InventoryCarrier, ContainerListener, IEntityAdditionalSpawnData {

	public static final EntityDataSerializer<Optional<ResourceLocation>> OPTIONAL_RESOURCE_LOCATION = EntityDataSerializer.optional(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);

	//// ENTITY ////

	/**
	 * @param <T> Pathfinder Mob & IExtraGolem
	 * @return the IExtraGolem as a mob
	 */
	<T extends PathfinderMob & IExtraGolem> T asMob();

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
