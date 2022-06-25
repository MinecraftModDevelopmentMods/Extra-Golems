package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.container.behavior.parameter.ChangeTexturesBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.ChangeTextureGoal;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

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
	private final Optional<ChangeTexturesBehaviorParameter> tickTextures;

	/**
	 * The map to use when the entity is wet
	 **/
	private final Optional<ChangeTexturesBehaviorParameter> wetTextures;
	/**
	 * The map to use when the entity is dry
	 **/
	private final Optional<ChangeTexturesBehaviorParameter> dryTextures;

	/**
	 * The map to use when the entity has fuel. Only used when UseFuelBehavior is present
	 **/
	private final Optional<ChangeTexturesBehaviorParameter> fueledTextures;
	/**
	 * The map to use when the entity has no fuel. Only used when UseFuelBehavior is present
	 **/
	private final Optional<ChangeTexturesBehaviorParameter> emptyTextures;

	public ChangeTextureBehavior(final CompoundTag tag) {
		super(tag);
		tickTextures = tag.contains("tick") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("tick"))) : Optional.empty();
		wetTextures = tag.contains("wet") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("wet"))) : Optional.empty();
		dryTextures = tag.contains("dry") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("dry"))) : Optional.empty();
		fueledTextures = tag.contains("fuel") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("fuel"))) : Optional.empty();
		emptyTextures = tag.contains("fuel_empty") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("fuel_empty"))) : Optional.empty();
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		if (entity.getContainer().getMultitexture().isPresent()) {
			entity.goalSelector.addGoal(1, new ChangeTextureGoal<>(entity, tickTextures, wetTextures, dryTextures, fueledTextures, emptyTextures));
		}
	}
}
