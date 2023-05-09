package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class GolemBehaviors {

	/**
	 * Behavior registry map
	 **/
	public static final Map<ResourceLocation, Function<CompoundTag, ? extends GolemBehavior>> BEHAVIORS = new HashMap<>();

	public static final GolemBehaviorKey<AoeDryBehavior> AOE_DRY = register("aoe_dry", AoeDryBehavior.class, AoeDryBehavior::new);
	public static final GolemBehaviorKey<AoeFreezeBehavior> AOE_FREEZE = register("aoe_freeze", AoeFreezeBehavior.class, AoeFreezeBehavior::new);
	public static final GolemBehaviorKey<AoeGrowBehavior> AOE_GROW = register("aoe_grow", AoeGrowBehavior.class, AoeGrowBehavior::new);
	public static final GolemBehaviorKey<BurnInSunBehavior> BURN_IN_SUN = register("burn_in_sun", BurnInSunBehavior.class, BurnInSunBehavior::new);
	public static final GolemBehaviorKey<ChangeTextureBehavior> CHANGE_TEXTURE = register("change_texture", ChangeTextureBehavior.class, ChangeTextureBehavior::new);
	public static final GolemBehaviorKey<ChangeMaterialBehavior> CHANGE_MATERIAL = register("change_material", ChangeMaterialBehavior.class, ChangeMaterialBehavior::new);
	public static final GolemBehaviorKey<CraftingMenuBehavior> CRAFTING_MENU = register("crafting_menu", CraftingMenuBehavior.class, CraftingMenuBehavior::new);
	public static final GolemBehaviorKey<ExplodeBehavior> EXPLODE = register("explode", ExplodeBehavior.class, ExplodeBehavior::new);
	public static final GolemBehaviorKey<FollowBehavior> FOLLOW = register("follow", FollowBehavior.class, FollowBehavior::new);
	public static final GolemBehaviorKey<OnActuallyHurtBehavior> ON_ACTUALLY_HURT = register("hurt", OnActuallyHurtBehavior.class, OnActuallyHurtBehavior::new);
	public static final GolemBehaviorKey<OnHurtTargetBehavior> ON_HURT_TARGET = register("attack", OnHurtTargetBehavior.class, OnHurtTargetBehavior::new);
	public static final GolemBehaviorKey<PlaceBlocksBehavior> PLACE_BLOCKS = register("place_blocks", PlaceBlocksBehavior.class, PlaceBlocksBehavior::new);
	public static final GolemBehaviorKey<PassiveEffectBehavior> PASSIVE_EFFECT = register("passive_effect", PassiveEffectBehavior.class, PassiveEffectBehavior::new);
	public static final GolemBehaviorKey<ShootArrowsBehavior> SHOOT_ARROWS = register("shoot_arrows", ShootArrowsBehavior.class, ShootArrowsBehavior::new);
	public static final GolemBehaviorKey<SplitBehavior> SPLIT_ON_DEATH = register("split_on_death", SplitBehavior.class, SplitBehavior::new);
	public static final GolemBehaviorKey<TeleportBehavior> TELEPORT = register("teleport", TeleportBehavior.class, TeleportBehavior::new);
	public static final GolemBehaviorKey<TemptBehavior> TEMPT = register("tempt", TemptBehavior.class, TemptBehavior::new);
	public static final GolemBehaviorKey<UseFuelBehavior> USE_FUEL = register("use_fuel", UseFuelBehavior.class, UseFuelBehavior::new);

	private GolemBehaviors() {
	}

	// Required to classload on both server and client
	public static void init() {
	}

	public static Optional<? extends GolemBehavior> create(final ResourceLocation name, final CompoundTag tag) {
		if (BEHAVIORS.containsKey(name)) {
			return Optional.of(BEHAVIORS.get(name).apply(tag));
		}
		return Optional.empty();
	}

	private static <T extends GolemBehavior> GolemBehaviorKey<T> register(final String name, final Class<T> clazz, final Function<CompoundTag, T> function) {
		final ResourceLocation id = new ResourceLocation(ExtraGolems.MODID, name);
		BEHAVIORS.put(id, function);
		return new GolemBehaviorKey<>(id, clazz);
	}

}
